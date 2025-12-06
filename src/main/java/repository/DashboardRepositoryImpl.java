package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Implementación concreta del Repositorio del Dashboard.
 * Esta clase ejecuta las consultas SQL de agregación (COUNT, SUM) directamente contra la base de datos via JDBC.
 * Se enfoca en obtener métricas de rendimiento para la pantalla de inicio de manera eficiente.
 */

import java.sql.*;

public class DashboardRepositoryImpl implements DashboardRepository {

    // Conexión compartida inyectada para mantener la sesión de base de datos activa
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado por el filtro o servicio principal.
     */
    public DashboardRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Cuenta las citas válidas para una fecha específica.
     * Ejecuta un SELECT COUNT(*) filtrando por fecha y excluyendo las canceladas.
     *
     * @param fecha Fecha en formato 'YYYY-MM-DD'.
     * @return Cantidad de citas operativas del día.
     * @throws SQLException Si hay error en la consulta SQL.
     */
    @Override
    public int contarCitasDia(String fecha) throws SQLException {
        // Consulta SQL: Usamos la función DATE() de MySQL para extraer solo la parte de la fecha del campo DATETIME
        // y filtramos para no contar las citas con estado 'Cancelada'.
        String sql = "SELECT COUNT(*) FROM citas WHERE DATE(fecha_hora) = ? AND estado != 'Cancelada'";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha); // Asignación segura del parámetro fecha

            try (ResultSet rs = stmt.executeQuery()) {
                // La función COUNT siempre devuelve una fila, incluso si es 0
                if (rs.next()) {
                    return rs.getInt(1); // Retorna el valor de la primera columna (el conteo)
                }
            }
        }
        return 0; // Retorno por defecto defensivo
    }

    /**
     * Cuenta el total de pacientes registrados que se encuentran activos.
     * Filtra por la columna 'estado' = 1.
     *
     * @return Total de pacientes activos.
     * @throws SQLException Si hay error en la consulta SQL.
     */
    @Override
    public int contarPacientesActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pacientes WHERE estado = 1";

        // Usamos Statement simple ya que la consulta es estática y no tiene parámetros externos (sin riesgo de inyección)
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Suma el total monetario facturado en una fecha específica.
     * Utiliza la función de agregación SUM() sobre la columna 'total_pagar'.
     *
     * @param fecha Fecha de emisión de las facturas.
     * @return Suma total en formato double.
     * @throws SQLException Si hay error en la consulta SQL.
     */
    @Override
    public double sumarFacturadoDia(String fecha) throws SQLException {
        String sql = "SELECT SUM(total_pagar) FROM facturas WHERE DATE(fecha_emision) = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);

            try (ResultSet rs = stmt.executeQuery()) {
                // Si hay facturas, retorna la suma.
                // Si no hay facturas ese día, SUM devuelve NULL en SQL, que JDBC traduce a 0.0 al usar getDouble, lo cual es correcto.
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }
}