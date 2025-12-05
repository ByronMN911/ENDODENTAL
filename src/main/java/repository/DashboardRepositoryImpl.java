package repository;

import java.sql.*;

public class DashboardRepositoryImpl implements DashboardRepository {
    private Connection conn;

    public DashboardRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public int contarCitasDia(String fecha) throws SQLException {
        // Contamos citas del día que no estén canceladas
        String sql = "SELECT COUNT(*) FROM citas WHERE DATE(fecha_hora) = ? AND estado != 'Cancelada'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public int contarPacientesActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM pacientes WHERE estado = 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    @Override
    public double sumarFacturadoDia(String fecha) throws SQLException {
        // Sumamos el total de las facturas emitidas hoy
        String sql = "SELECT SUM(total_pagar) FROM facturas WHERE DATE(fecha_emision) = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fecha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        }
        return 0.0;
    }
}