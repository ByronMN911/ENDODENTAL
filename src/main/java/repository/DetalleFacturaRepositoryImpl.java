package repository;

/*
 * Autor: Génesis Escobar
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Implementación concreta del Repositorio de Detalles de Factura.
 * Esta clase maneja la persistencia de los ítems individuales que componen una factura,
 * ya sean Servicios Médicos o Productos de Inventario.
 *
 * Características Técnicas:
 * 1. Uso de Batch Update para inserciones masivas eficientes.
 * 2. Manejo de relaciones polimórficas en base de datos (Servicio/Producto).
 * 3. Reconstrucción de objetos anidados mediante consultas con JOIN.
 */

import models.DetalleFactura;
import models.Producto;
import models.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class DetalleFacturaRepositoryImpl implements DetalleFacturaRepository {

    // Conexión compartida inyectada para mantener la sesión de base de datos activa
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado por el filtro o servicio principal.
     */
    public DetalleFacturaRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Guarda una lista completa de detalles asociados a una factura.
     * Implementa el patrón "Batch Processing" para enviar múltiples inserciones
     * en una sola llamada de red a la base de datos, mejorando significativamente el rendimiento.
     *
     * @param idFactura ID de la factura padre recién creada.
     * @param detalles Lista de ítems a insertar.
     * @throws SQLException Si ocurre un error durante el proceso de batch.
     */
    @Override
    public void guardar(int idFactura, List<DetalleFactura> detalles) throws SQLException {
        String sql = "INSERT INTO detalles_factura (id_factura, id_servicio, id_producto, tipo_item, cantidad, precio_unitario, subtotal_item, diente_o_zona) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Iteramos sobre cada ítem de la factura
            for (DetalleFactura d : detalles) {
                // 1. Vinculación con la Factura Padre
                stmt.setInt(1, idFactura);

                // 2. Lógica para Referencia Cruzada (Servicio vs Producto)
                // Un detalle puede ser un Servicio O un Producto, pero no ambos.
                // Guardamos el ID en la columna correspondiente y NULL en la otra.

                if (d.getServicio() != null && d.getServicio().getIdServicio() > 0) {
                    stmt.setInt(2, d.getServicio().getIdServicio());
                } else {
                    stmt.setNull(2, Types.INTEGER); // Null explícito
                }

                if (d.getProducto() != null && d.getProducto().getIdProducto() > 0) {
                    stmt.setInt(3, d.getProducto().getIdProducto());
                } else {
                    stmt.setNull(3, Types.INTEGER); // Null explícito
                }

                // 3. Datos Transaccionales
                stmt.setString(4, d.getTipoItem()); // "Servicio" o "Producto"
                stmt.setInt(5, d.getCantidad());
                stmt.setBigDecimal(6, d.getPrecioUnitario());
                stmt.setBigDecimal(7, d.getSubtotalItem());

                // 4. Dato Específico Odontológico (Solo aplica a servicios)
                if (d.getDienteOZona() != null) {
                    stmt.setString(8, d.getDienteOZona());
                } else {
                    stmt.setNull(8, Types.VARCHAR);
                }

                // Agregamos la sentencia al "lote" de ejecución
                stmt.addBatch();
            }

            // Ejecutamos todas las inserciones acumuladas de una sola vez
            stmt.executeBatch();
        }
    }

    /**
     * Recupera los detalles de una factura específica.
     * Utiliza LEFT JOIN para traer los nombres descriptivos de los servicios o productos
     * en una sola consulta, evitando el problema de N+1 consultas.
     *
     * @param idFactura ID de la factura a consultar.
     * @return Lista de objetos DetalleFactura poblados.
     * @throws SQLException Si ocurre un error de lectura.
     */
    @Override
    public List<DetalleFactura> listarPorFactura(int idFactura) throws SQLException {
        // Query optimizada: Trae datos de la tabla intermedia y nombres de las tablas maestras
        String sql = "SELECT df.*, s.nombre AS s_nombre, p.nombre AS p_nombre FROM detalles_factura df " +
                "LEFT JOIN servicios s ON df.id_servicio = s.id_servicio " +
                "LEFT JOIN productos p ON df.id_producto = p.id_producto " +
                "WHERE df.id_factura = ?";

        List<DetalleFactura> lista = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFactura);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleFactura d = new DetalleFactura();

                    // Mapeo básico de columnas
                    d.setIdDetalle(rs.getInt("id_detalle"));
                    d.setTipoItem(rs.getString("tipo_item"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    d.setSubtotalItem(rs.getBigDecimal("subtotal_item"));
                    d.setDienteOZona(rs.getString("diente_o_zona"));

                    // Reconstrucción condicional de objetos relacionados
                    // Verificamos cuál de las dos FKs tiene valor

                    int idServ = rs.getInt("id_servicio");
                    if (!rs.wasNull()) { // Si id_servicio NO es NULL en BD
                        Servicio s = new Servicio();
                        s.setIdServicio(idServ);
                        s.setNombre(rs.getString("s_nombre")); // Nombre traído por el JOIN
                        d.setServicio(s);
                    }

                    int idProd = rs.getInt("id_producto");
                    if (!rs.wasNull()) { // Si id_producto NO es NULL en BD
                        Producto p = new Producto();
                        p.setIdProducto(idProd);
                        p.setNombre(rs.getString("p_nombre")); // Nombre traído por el JOIN
                        d.setProducto(p);
                    }

                    lista.add(d);
                }
            }
        }
        return lista;
    }
}