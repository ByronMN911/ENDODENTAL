package repository;

/*
 * Autor: Génesis Escobar
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Implementación concreta del Repositorio de Facturas utilizando JDBC.
 * Esta clase maneja la persistencia de los datos financieros de la clínica.
 *
 * Características Técnicas:
 * 1. Inserción de registros con recuperación de claves primarias autogeneradas (IDs).
 * 2. Consultas con JOINs para vincular facturas con sus citas correspondientes.
 * 3. Mapeo manual de ResultSet a objetos Factura.
 */

import models.Factura;
import models.Cita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaRepositoryImpl implements FacturaRepository {

    // Conexión compartida inyectada para mantener la sesión de base de datos activa
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado por el filtro o servicio principal.
     */
    public FacturaRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Recupera el listado histórico de todas las facturas emitidas.
     * Utiliza un LEFT JOIN con la tabla de citas para obtener información contextual si es necesario.
     *
     * @return Lista de facturas ordenadas por fecha de emisión descendente (las más recientes primero).
     * @throws SQLException Si ocurre un error en la consulta.
     */
    @Override
    public List<Factura> listar() throws SQLException {
        List<Factura> facturas = new ArrayList<>();
        // Query: Traemos datos de la factura y datos básicos de la cita asociada
        String sql = "SELECT f.*, c.motivo FROM facturas f LEFT JOIN citas c ON f.id_cita = c.id_cita ORDER BY f.fecha_emision DESC";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                facturas.add(crearFactura(rs));
            }
        }
        return facturas;
    }

    /**
     * Busca una factura específica por su ID.
     *
     * @param id El identificador único de la factura.
     * @return El objeto Factura poblado con sus datos, o null si no existe.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    @Override
    public Factura porId(int id) throws SQLException {
        Factura factura = null;
        String sql = "SELECT * FROM facturas WHERE id_factura = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    factura = crearFactura(rs);
                }
            }
        }
        return factura;
    }

    /**
     * Guarda la cabecera de una nueva factura en la base de datos.
     *
     * IMPORTANTE: Utiliza Statement.RETURN_GENERATED_KEYS.
     * Esto es vital porque necesitamos saber qué ID le asignó la base de datos a esta factura
     * para poder usarlo inmediatamente después al guardar los detalles (ítems) en la tabla 'detalles_factura'.
     *
     * @param factura El objeto Factura con los datos del cliente y totales.
     * @return El ID (int) generado automáticamente por la base de datos.
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    @Override
    public int guardar(Factura factura) throws SQLException {
        String sql = "INSERT INTO facturas (id_cita, fecha_emision, identificacion_cliente, nombre_cliente_factura, direccion_cliente, subtotal, monto_iva, total_pagar, metodo_pago) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Preparamos el statement solicitando las claves generadas
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Asignación de parámetros
            stmt.setInt(1, factura.getCita().getIdCita());
            // Conversión de LocalDateTime a Timestamp de SQL
            stmt.setTimestamp(2, Timestamp.valueOf(factura.getFechaEmision()));
            stmt.setString(3, factura.getIdentificacionCliente());
            stmt.setString(4, factura.getNombreClienteFactura());
            stmt.setString(5, factura.getDireccionCliente());
            stmt.setBigDecimal(6, factura.getSubtotal());
            stmt.setBigDecimal(7, factura.getMontoIva());
            stmt.setBigDecimal(8, factura.getTotalPagar());
            stmt.setString(9, factura.getMetodoPago());

            // Ejecutamos la inserción
            stmt.executeUpdate();

            // Recuperamos el ID generado (Auto-increment)
            try (ResultSet rsKeys = stmt.getGeneratedKeys()) {
                if (rsKeys.next()) {
                    return rsKeys.getInt(1); // Retornamos el ID para usarlo en los detalles
                } else {
                    throw new SQLException("No se obtuvo ID para la factura.");
                }
            }
        }
    }

    /**
     * Método auxiliar (Helper) para convertir una fila del ResultSet en un objeto Java.
     * Centraliza la lógica de mapeo para evitar duplicidad de código.
     *
     * @param rs El ResultSet posicionado en la fila actual.
     * @return Un objeto Factura con sus atributos llenos.
     * @throws SQLException Si ocurre un error al leer las columnas.
     */
    private Factura crearFactura(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setIdFactura(rs.getInt("id_factura"));
        // Conversión inversa: Timestamp SQL -> LocalDateTime Java
        f.setFechaEmision(rs.getTimestamp("fecha_emision").toLocalDateTime());
        f.setIdentificacionCliente(rs.getString("identificacion_cliente"));
        f.setNombreClienteFactura(rs.getString("nombre_cliente_factura"));
        f.setDireccionCliente(rs.getString("direccion_cliente"));

        // Uso de BigDecimal para precisión monetaria
        f.setSubtotal(rs.getBigDecimal("subtotal"));
        f.setMontoIva(rs.getBigDecimal("monto_iva"));
        f.setTotalPagar(rs.getBigDecimal("total_pagar"));
        f.setMetodoPago(rs.getString("metodo_pago"));

        // Reconstruimos parcialmente el objeto Cita (solo el ID es necesario para referencia)
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        f.setCita(c);

        return f;
    }
}