package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.1
 * Descripción:
 * Implementación concreta del repositorio de Pacientes utilizando JDBC.
 * Esta clase gestiona todas las operaciones de base de datos relacionadas con los pacientes,
 * incluyendo la lógica para distinguir entre inserciones y actualizaciones,
 * así como el manejo del ciclo de vida (activo/inactivo) mediante borrado lógico.
 */

import models.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteRepositoryImpl implements PacienteRepository {

    // Conexión compartida inyectada para mantener la sesión de base de datos activa
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado por el filtro o servicio principal.
     */
    public PacienteRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Recupera la lista de pacientes activos.
     * Filtra explícitamente por la columna 'estado = 1' para no mostrar los eliminados
     * en la lista principal de gestión.
     *
     * @return Lista de pacientes activos ordenados por apellido.
     * @throws SQLException Si ocurre un error en la consulta.
     */
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

    /**
     * Recupera la lista de pacientes inactivos (Papelera de Reciclaje).
     * Filtra por 'estado = 0'. Permite al administrador o secretaria restaurar
     * expedientes cerrados accidentalmente.
     *
     * @return Lista de pacientes inactivos.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    @Override
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

    /**
     * Busca un paciente por su ID primario.
     * No filtra por estado, permitiendo recuperar datos de pacientes inactivos si es necesario
     * (por ejemplo, para mostrar historial en una cita antigua).
     *
     * @param id ID del paciente.
     * @return Objeto Paciente o null.
     * @throws SQLException Si ocurre un error SQL.
     */
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

    /**
     * Busca un paciente por su cédula de identidad.
     * Crítico para validar duplicados antes de insertar uno nuevo.
     *
     * @param cedula Número de cédula.
     * @return Objeto Paciente o null.
     * @throws SQLException Si ocurre un error SQL.
     */
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

    /**
     * Persiste un paciente en la base de datos.
     * Implementa la lógica "Upsert" (Update o Insert):
     * - Si el ID > 0: Se trata de una edición -> UPDATE.
     * - Si el ID es 0: Se trata de un nuevo registro -> INSERT.
     *
     * @param paciente El objeto con los datos a guardar.
     * @throws SQLException Si ocurre un error (ej: cédula duplicada).
     */
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
                // Si es UPDATE seteamos el ID en el último parámetro (7)
                stmt.setInt(7, paciente.getIdPaciente());
            }

            stmt.executeUpdate();
        }
    }

    /**
     * Realiza un Borrado Lógico (Soft Delete).
     * En lugar de DELETE, actualiza el campo 'estado' a 0.
     * Esto preserva el historial clínico del paciente.
     *
     * @param id ID del paciente a desactivar.
     * @throws SQLException Si ocurre un error SQL.
     */
    @Override
    public void eliminar(int id) throws SQLException {
        // SOFT DELETE: Ponemos estado en 0
        String sql = "UPDATE pacientes SET estado = 0 WHERE id_paciente = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Reactiva un paciente previamente eliminado.
     * Actualiza el campo 'estado' a 1.
     *
     * @param id ID del paciente a restaurar.
     * @throws SQLException Si ocurre un error SQL.
     */
    @Override
    public void activar(int id) throws SQLException {
        String sql = "UPDATE pacientes SET estado = 1 WHERE id_paciente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para mapear una fila del ResultSet a un objeto Paciente.
     * Centraliza la conversión de tipos de datos SQL a Java.
     *
     * @param rs ResultSet posicionado en la fila actual.
     * @return Objeto Paciente poblado.
     * @throws SQLException Si ocurre un error de lectura.
     */
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

        // Conversión crítica: Timestamp (SQL) -> LocalDateTime (Java)
        // Es necesario verificar si es null antes de convertir
        Timestamp fechaTimestamp = rs.getTimestamp("fecha_registro");
        if (fechaTimestamp != null) {
            p.setFechaRegistro(fechaTimestamp.toLocalDateTime());
        }

        return p;
    }
}