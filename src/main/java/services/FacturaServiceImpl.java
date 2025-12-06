package services;

/*
 * Autor: Génesis Escobar
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para el módulo de Facturación.
 * Esta clase centraliza las reglas financieras y operativas del cobro de servicios, incluyendo:
 * 1. Cálculos matemáticos precisos para impuestos y totales.
 * 2. Orquestación de la persistencia de datos (Cabecera + Detalles).
 * 3. Actualización de inventario (descuento de stock).
 * 4. Gestión del ciclo de vida de la cita (cambio a estado 'Facturada').
 */

import models.Factura;
import models.DetalleFactura;
import repository.*; // Importamos Repositorios e Implementaciones
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FacturaServiceImpl implements FacturaService {

    // Constante para el cálculo del Impuesto al Valor Agregado (15% en Ecuador)
    // Usamos BigDecimal para evitar errores de precisión en operaciones financieras.
    private static final BigDecimal TASA_IVA = new BigDecimal("0.15");

    // Dependencias de Repositorios para interactuar con distintas tablas
    private final FacturaRepository facturaRepo;      // Para tabla 'facturas'
    private final DetalleFacturaRepository detalleRepo; // Para tabla 'detalles_factura'
    private final ProductoRepository productoRepo;    // Para actualizar stock en 'productos'
    private final CitaRepository citaRepo;            // Para actualizar estado en 'citas'

    /**
     * Constructor que inyecta la conexión a la base de datos.
     * Inicializa todos los repositorios necesarios compartiendo la misma conexión
     * para garantizar la integridad transaccional (ACID).
     *
     * @param conn La conexión JDBC activa.
     */
    public FacturaServiceImpl(Connection conn) {
        this.facturaRepo = new FacturaRepositoryImpl(conn);
        this.detalleRepo = new DetalleFacturaRepositoryImpl(conn);
        this.productoRepo = new ProductoRepositoryImpl(conn);
        this.citaRepo = new CitaRepositoryImpl(conn);
    }

    /**
     * Recupera el historial completo de facturas emitidas.
     *
     * @return Lista de facturas ordenadas cronológicamente.
     */
    @Override
    public List<Factura> listar() {
        try {
            return facturaRepo.listar();
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al listar facturas", e);
        }
    }

    /**
     * Busca una factura por su ID y carga sus detalles asociados.
     * Es fundamental cargar los detalles aquí para poder generar el PDF o ver el reporte completo.
     *
     * @param id ID de la factura.
     * @return Optional con la factura completa.
     */
    @Override
    public Optional<Factura> porId(int id) {
        try {
            Factura f = facturaRepo.porId(id);
            if (f != null) {
                // Lógica de carga ansiosa (Eager Loading) manual:
                // Recuperamos los ítems de la factura y se los asignamos al objeto padre.
                f.setDetalles(detalleRepo.listarPorFactura(id));
                return Optional.of(f);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al obtener factura", e);
        }
    }

    /**
     * Realiza los cálculos financieros de la factura.
     * Itera sobre los detalles para sumar subtotales, aplica el IVA y calcula el total final.
     * Se utiliza 'RoundingMode.HALF_UP' (Redondeo comercial estándar).
     *
     * @param factura El objeto factura con la lista de detalles a procesar.
     */
    @Override
    public void calcularTotales(Factura factura) {
        BigDecimal subtotal = BigDecimal.ZERO;

        // 1. Sumar el costo de cada ítem (Servicio o Producto)
        if (factura.getDetalles() != null) {
            for (DetalleFactura d : factura.getDetalles()) {
                // Si el subtotal del ítem no vino calculado desde la vista, lo calculamos aquí
                if (d.getSubtotalItem() == null) {
                    BigDecimal cant = new BigDecimal(d.getCantidad());
                    // Precio * Cantidad
                    d.setSubtotalItem(d.getPrecioUnitario().multiply(cant));
                }
                // Acumulamos al subtotal general
                subtotal = subtotal.add(d.getSubtotalItem());
            }
        }

        // 2. Aplicar redondeo a 2 decimales para el subtotal
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        // 3. Calcular Impuestos (IVA)
        BigDecimal iva = subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);

        // 4. Calcular Total a Pagar
        BigDecimal total = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);

        // 5. Asignar valores calculados al objeto Factura
        factura.setSubtotal(subtotal);
        factura.setMontoIva(iva);
        factura.setTotalPagar(total);
    }

    /**
     * Operación Transaccional de Guardado.
     * Ejecuta una serie de pasos críticos que deben ocurrir atómicamente:
     * 1. Calcular montos.
     * 2. Insertar Factura.
     * 3. Insertar Detalles.
     * 4. Descontar Stock.
     * 5. Actualizar Estado de Cita.
     *
     * @param factura Objeto con datos de cabecera y detalles.
     * @return El ID de la nueva factura generada.
     */
    @Override
    public int guardar(Factura factura) {
        try {
            // PASO 1: Garantizar integridad financiera recalculando totales en el servidor
            calcularTotales(factura);

            // PASO 2: Persistir la cabecera de la factura y obtener su ID autogenerado
            int idFactura = facturaRepo.guardar(factura);

            // PASO 3: Procesar los detalles (Ítems)
            if (factura.getDetalles() != null) {
                // Guardado por lotes (Batch) de los detalles
                detalleRepo.guardar(idFactura, factura.getDetalles());

                // LÓGICA DE INVENTARIO:
                // Recorremos los ítems para identificar Productos (Tangibles) y descontar stock.
                for (DetalleFactura d : factura.getDetalles()) {
                    if ("Producto".equalsIgnoreCase(d.getTipoItem()) && d.getProducto() != null) {
                        // Llamamos al repositorio de productos para restar la cantidad vendida.
                        // Pasamos la cantidad en negativo (-d.getCantidad()) para reducir el stock.
                        productoRepo.actualizarStock(d.getProducto().getIdProducto(), -d.getCantidad());
                    }
                }
            }

            // PASO 4: ACTUALIZACIÓN DE FLUJO DE NEGOCIO
            // Cambiamos el estado de la cita de 'Atendida' a 'Facturada'.
            // Esto hace que la cita deje de aparecer en la lista de "Pendientes de Cobro".
            if (factura.getCita() != null && factura.getCita().getIdCita() > 0) {
                // Utilizamos el método de actualización de estado del repositorio de citas
                // Casting a la implementación para acceder al método específico si no está en la interfaz genérica
                ((CitaRepositoryImpl) citaRepo).actualizarEstado(factura.getCita().getIdCita(), "Facturada");
            }

            return idFactura;

        } catch (SQLException e) {
            // Capturamos cualquier error SQL (ej: fallo de conexión, stock insuficiente, error de sintaxis)
            // y lanzamos una excepción de servicio para ser manejada por el controlador.
            throw new ServiceJdbcException("Error crítico al procesar la facturación: " + e.getMessage(), e);
        }
    }
}