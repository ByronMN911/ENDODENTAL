package repository;

import models.Cita;
import models.Odontologo;
import models.Paciente;
import models.Usuario;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CitaRepositoryImpl implements CitaRepository {
    private Connection conn;

    public CitaRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Cita> listar() throws SQLException {
        return ejecutarConsulta("SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.cedula AS p_ced, " +
                "o.especialidad, u.nombre_completo AS doc_nom " +
                "FROM citas c " +
                "INNER JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "INNER JOIN odontologos o ON c.id_odontologo = o.id_odontologo " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "ORDER BY c.fecha_hora DESC");
    }
      /*
    // LISTAR HOY
    @Override
    public List<Cita> listarPorFecha(String fecha) throws SQLException {
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.cedula AS p_ced, " +
                "o.especialidad, u.nombre_completo AS doc_nom " +
                "FROM citas c " +
                "INNER JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "INNER JOIN odontologos o ON c.id_odontologo = o.id_odontologo " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE DATE(c.fecha_hora) = ? " +
                "ORDER BY c.fecha_hora ASC";
        return ejecutarConsultaParametrizada(sql, stmt -> stmt.setString(1, fecha));
    }
    */


    // LISTAR POR FECHA (CORREGIDO CON RANGO DE TIEMPO)
    @Override
    public List<Cita> listarPorFecha(String fechaStr) throws SQLException {
        // 1. Convertimos el String (YYYY-MM-DD) a objetos de Java Time
        LocalDate fecha = LocalDate.parse(fechaStr);

        // 2. Definimos el inicio del día (00:00:00) y el fin del día (23:59:59)
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);

        // 3. Usamos BETWEEN en lugar de DATE(). Esto es más seguro y eficiente (Sargable).
        // También mantenemos el filtro de no mostrar Canceladas/Atendidas si es la vista diaria
        // (Según tu lógica anterior, mostramos Pendientes y Confirmadas en la agenda)
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.cedula AS p_ced, " +
                "o.especialidad, u.nombre_completo AS doc_nom " +
                "FROM citas c " +
                "INNER JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "INNER JOIN odontologos o ON c.id_odontologo = o.id_odontologo " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE c.fecha_hora BETWEEN ? AND ? " +
                "AND c.estado IN ('Pendiente', 'Confirmada') " +
                "ORDER BY c.fecha_hora ASC";

        return ejecutarConsultaParametrizada(sql, stmt -> {
            stmt.setTimestamp(1, Timestamp.valueOf(inicioDia));
            stmt.setTimestamp(2, Timestamp.valueOf(finDia));
        });
    }

    // NUEVO: BUSCAR POR CÉDULA
    public List<Cita> listarPorCedula(String cedula) throws SQLException {
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.cedula AS p_ced, " +
                "o.especialidad, u.nombre_completo AS doc_nom " +
                "FROM citas c " +
                "INNER JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "INNER JOIN odontologos o ON c.id_odontologo = o.id_odontologo " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE p.cedula LIKE ? " +
                "ORDER BY c.fecha_hora DESC";
        return ejecutarConsultaParametrizada(sql, stmt -> stmt.setString(1, "%" + cedula + "%"));
    }

    // NUEVO: LISTAR POR ESTADO (Para historial de atendidas)
    public List<Cita> listarPorEstado(String estado) throws SQLException {
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.cedula AS p_ced, " +
                "o.especialidad, u.nombre_completo AS doc_nom " +
                "FROM citas c " +
                "INNER JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "INNER JOIN odontologos o ON c.id_odontologo = o.id_odontologo " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE c.estado = ? " +
                "ORDER BY c.fecha_hora DESC";
        return ejecutarConsultaParametrizada(sql, stmt -> stmt.setString(1, estado));
    }

    // NUEVO: OBTENER POR ID (Para editar)
    public Cita porId(int id) throws SQLException {
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.cedula AS p_ced, " +
                "o.especialidad, u.nombre_completo AS doc_nom " +
                "FROM citas c " +
                "INNER JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "INNER JOIN odontologos o ON c.id_odontologo = o.id_odontologo " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE c.id_cita = ?";
        List<Cita> lista = ejecutarConsultaParametrizada(sql, stmt -> stmt.setInt(1, id));
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public boolean existeCitaEnHorario(int idOdontologo, LocalDateTime fechaHora) throws SQLException {
        // Validación: Ignoramos citas canceladas
        String sql = "SELECT COUNT(*) FROM citas WHERE id_odontologo = ? AND fecha_hora = ? AND estado != 'Cancelada'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idOdontologo);
            stmt.setTimestamp(2, Timestamp.valueOf(fechaHora));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // ACTUALIZADO: Maneja INSERT y UPDATE
    @Override
    public void guardar(Cita cita) throws SQLException {
        String sql;
        if (cita.getIdCita() > 0) {
            // UPDATE (Edición)
            sql = "UPDATE citas SET fecha_hora=?, motivo=?, id_paciente=?, id_odontologo=?, estado=? WHERE id_cita=?";
        } else {
            // INSERT (Nueva) - Estado por defecto 'Pendiente'
            sql = "INSERT INTO citas (fecha_hora, motivo, id_paciente, id_odontologo, estado) VALUES (?, ?, ?, ?, ?)";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(cita.getFechaHora()));
            stmt.setString(2, cita.getMotivo());
            stmt.setInt(3, cita.getPaciente().getIdPaciente());
            stmt.setInt(4, cita.getOdontologo().getIdOdontologo());

            // Si es nueva, forzamos "Pendiente" si viene nulo, o usamos el que tenga
            String estado = (cita.getEstado() == null) ? "Pendiente" : cita.getEstado();
            stmt.setString(5, estado);

            if (cita.getIdCita() > 0) {
                stmt.setInt(6, cita.getIdCita());
            }
            stmt.executeUpdate();
        }
    }

    // NUEVO: Método rápido para cambiar estado
    public void actualizarEstado(int idCita, String nuevoEstado) throws SQLException {
        String sql = "UPDATE citas SET estado = ? WHERE id_cita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idCita);
            stmt.executeUpdate();
        }
    }


    // --- Helpers para reducir código repetido ---
    @FunctionalInterface
    interface SqlConsumer {
        void accept(PreparedStatement stmt) throws SQLException;
    }

    private List<Cita> ejecutarConsulta(String sql) throws SQLException {
        List<Cita> citas = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                citas.add(crearCitaCompleta(rs));
            }
        }
        return citas;
    }

    private List<Cita> ejecutarConsultaParametrizada(String sql, SqlConsumer setParams) throws SQLException {
        List<Cita> citas = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParams.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    citas.add(crearCitaCompleta(rs));
                }
            }
        }
        return citas;
    }

    private Cita crearCitaCompleta(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        c.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        c.setMotivo(rs.getString("motivo"));
        c.setEstado(rs.getString("estado"));

        Paciente p = new Paciente();
        p.setIdPaciente(rs.getInt("id_paciente"));
        p.setNombres(rs.getString("p_nom"));
        p.setApellidos(rs.getString("p_ape"));
        p.setCedula(rs.getString("p_ced"));
        c.setPaciente(p);

        Odontologo o = new Odontologo();
        o.setIdOdontologo(rs.getInt("id_odontologo"));
        o.setEspecialidad(rs.getString("especialidad"));

        Usuario u = new Usuario();
        u.setNombreCompleto(rs.getString("doc_nom"));
        o.setUsuario(u);

        c.setOdontologo(o);
        return c;
    }
}