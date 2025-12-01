package repository;

/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.1 (Con soporte para Borrado Lógico)
 */
import models.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteRepositoryImpl implements PacienteRepository {

    private Connection conn;

    public PacienteRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Paciente> listar() throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        // CAMBIO: Filtramos solo los ACTIVOS (estado = 1)
        String sql = "SELECT * FROM pacientes WHERE estado = 1 ORDER BY apellidos, nombres";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pacientes.add(crearPaciente(rs));
            }
        }
        return pacientes;
    }

    // Metodo para ver usuarios inactivos

    public List<Paciente> listarInactivos() throws SQLException {
        List<Paciente> pacientes = new ArrayList<>();
        // Filtramos solo los INACTIVOS (estado = 0)
        String sql = "SELECT * FROM pacientes WHERE estado = 0 ORDER BY apellidos, nombres";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pacientes.add(crearPaciente(rs));
            }
        }
        return pacientes;
    }

    @Override
    public Paciente porId(int id) throws SQLException {
        Paciente paciente = null;

        String sql = "SELECT * FROM pacientes WHERE id_paciente = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = crearPaciente(rs);
                }
            }
        }
        return paciente;
    }

    @Override
    public Paciente porCedula(String cedula) throws SQLException {
        Paciente paciente = null;

        String sql = "SELECT * FROM pacientes WHERE cedula = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cedula);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    paciente = crearPaciente(rs);
                }
            }
        }
        return paciente;
    }

    @Override
    public void guardar(Paciente paciente) throws SQLException {
        String sql;
        if (paciente.getIdPaciente() > 0) {
            // UPDATE: No tocamos el estado aqui, si edito el nombre, no quiero que se active/desactive solo.
            sql = "UPDATE pacientes SET cedula=?, nombres=?, apellidos=?, telefono=?, email=?, alergias=? WHERE id_paciente=?";
        } else {
            // INSERT: No mencionamos 'estado' ni 'fecha_registro'.
            // MySQL usará los DEFAULT (1 y NOW()) definidos en la tabla.
            sql = "INSERT INTO pacientes (cedula, nombres, apellidos, telefono, email, alergias) VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, paciente.getCedula());
            stmt.setString(2, paciente.getNombres());
            stmt.setString(3, paciente.getApellidos());
            stmt.setString(4, paciente.getTelefono());
            stmt.setString(5, paciente.getEmail());
            stmt.setString(6, paciente.getAlergias());

            if (paciente.getIdPaciente() > 0) {
                stmt.setInt(7, paciente.getIdPaciente());
            }

            stmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        // SOFT DELETE: Ponemos estado en 0
        String sql = "UPDATE pacientes SET estado = 0 WHERE id_paciente = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Metodo extra para reactivar
    public void activar(int id) throws SQLException {
        String sql = "UPDATE pacientes SET estado = 1 WHERE id_paciente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Paciente crearPaciente(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setIdPaciente(rs.getInt("id_paciente"));
        p.setCedula(rs.getString("cedula"));
        p.setNombres(rs.getString("nombres"));
        p.setApellidos(rs.getString("apellidos"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setAlergias(rs.getString("alergias"));
        p.setEstado(rs.getInt("estado"));

        Timestamp fechaTimestamp = rs.getTimestamp("fecha_registro");
        if (fechaTimestamp != null) {
            p.setFechaRegistro(fechaTimestamp.toLocalDateTime());
        }

        return p;
    }
}