package services;

import models.Factura;
import models.DetalleFactura;
import models.Producto; // Import necesario
import repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FacturaServiceImpl implements FacturaService {

    private static final BigDecimal TASA_IVA = new BigDecimal("0.15");
    private final FacturaRepository facturaRepo;
    private final DetalleFacturaRepository detalleRepo;
    private final ProductoRepository productoRepo;
    private final CitaRepository citaRepo;

    public FacturaServiceImpl(Connection conn) {
        this.facturaRepo = new FacturaRepositoryImpl(conn);
        this.detalleRepo = new DetalleFacturaRepositoryImpl(conn);
        this.productoRepo = new ProductoRepositoryImpl(conn);
        this.citaRepo = new CitaRepositoryImpl(conn);
    }

    // ... (listar, porId, calcularTotales se mantienen igual) ...
    @Override
    public List<Factura> listar() {
        try { return facturaRepo.listar(); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    @Override
    public Optional<Factura> porId(int id) {
        try {
            Factura f = facturaRepo.porId(id);
            if (f != null) {
                f.setDetalles(detalleRepo.listarPorFactura(id));
                return Optional.of(f);
            }
            return Optional.empty();
        } catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    @Override
    public void calcularTotales(Factura factura) {
        BigDecimal subtotal = BigDecimal.ZERO;
        if (factura.getDetalles() != null) {
            for (DetalleFactura d : factura.getDetalles()) {
                if (d.getSubtotalItem() == null) {
                    BigDecimal cant = new BigDecimal(d.getCantidad());
                    d.setSubtotalItem(d.getPrecioUnitario().multiply(cant));
                }
                subtotal = subtotal.add(d.getSubtotalItem());
            }
        }
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);

        factura.setSubtotal(subtotal);
        factura.setMontoIva(iva);
        factura.setTotalPagar(total);
    }

    @Override
    public int guardar(Factura factura) {
        try {
            // 1. VALIDACIÓN PREVIA DE STOCK (CRÍTICO)
            // Antes de guardar nada, verificamos si alcanza el inventario para TODOS los productos
            if (factura.getDetalles() != null) {
                for (DetalleFactura d : factura.getDetalles()) {
                    if ("Producto".equalsIgnoreCase(d.getTipoItem()) && d.getProducto() != null) {
                        // Consultamos el stock actual en tiempo real
                        Producto pActual = productoRepo.porId(d.getProducto().getIdProducto());

                        if (pActual == null) {
                            throw new ServiceJdbcException("El producto con ID " + d.getProducto().getIdProducto() + " no existe.");
                        }

                        if (pActual.getStock() < d.getCantidad()) {
                            throw new ServiceJdbcException("Stock insuficiente para: " + pActual.getNombre() +
                                    ". Disponible: " + pActual.getStock() +
                                    ", Solicitado: " + d.getCantidad());
                        }
                    }
                }
            }

            // 2. Calcular Totales
            calcularTotales(factura);

            // 3. Guardar Cabecera
            int idFactura = facturaRepo.guardar(factura);

            // 4. Guardar Detalles y Descontar Stock
            if (factura.getDetalles() != null) {
                detalleRepo.guardar(idFactura, factura.getDetalles());

                for (DetalleFactura d : factura.getDetalles()) {
                    if ("Producto".equalsIgnoreCase(d.getTipoItem()) && d.getProducto() != null) {
                        // Aquí ya es seguro descontar porque validamos arriba
                        productoRepo.actualizarStock(d.getProducto().getIdProducto(), -d.getCantidad());
                    }
                }
            }

            // 5. Actualizar Cita
            if (factura.getCita() != null && factura.getCita().getIdCita() > 0) {
                citaRepo.actualizarEstado(factura.getCita().getIdCita(), "Facturada");
            }

            return idFactura;

        } catch (SQLException e) {
            throw new ServiceJdbcException("Error crítico al facturar: " + e.getMessage(), e);
        }
    }
}