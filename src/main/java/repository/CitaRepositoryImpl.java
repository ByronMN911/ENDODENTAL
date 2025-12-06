package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 3.0
 * Descripción:
 * Implementación concreta del Repositorio de Citas utilizando JDBC.
 * Esta clase se encarga de toda la interacción directa con la base de datos para la entidad Cita.
 *
 * Características Técnicas:
 * 1. Mapeo Objeto-Relacional (ORM) manual mediante JOINs para reconstruir objetos complejos (Cita -> Paciente/Odontologo).
 * 2. Manejo avanzado de fechas Java (LocalDate/LocalDateTime) vs SQL (Timestamp).
 * 3. Consultas dinámicas para filtros flexibles (Por fecha y múltiples estados).
 */

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

    // Conexión compartida inyectada desde el servicio/filtro
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Conexión JDBC gestionada externamente.
     */
    public CitaRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Método Maestro para listado flexible.
     * Permite buscar citas en un día específico que cumplan con una lista de estados.
     * Ejemplo: "Traer todas las citas de Hoy que sean 'Pendiente' o 'Atendida'".
     *
     * @param fechaStr La fecha en formato String (YYYY-MM-DD).
     * @param estados Lista de estados permitidos (ej: List.of("Pendiente", "Atendida")).
     * @return Lista de citas que coinciden con los criterios.
     * @throws SQLException Si ocurre un error al construir o ejecutar la query dinámica.
     */
    @Override
    public List<Cita> listarPorFechaYEstados(String fechaStr, List<String> estados) throws SQLException {
        // 1. Conversión de Fechas: Definimos el rango exacto del día (00:00 a 23:59)
        // Esto soluciona problemas de zona horaria donde DATE() podría fallar.
        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);

        // 2. Construcción Dinámica del SQL para la cláusula IN (...)
        // Generamos tantos signos de interrogación (?) como estados haya en la lista.
        StringBuilder inClause = new StringBuilder();
        for (int i = 0; i < estados.size(); i++) {
            inClause.append("?");
            if (i < estados.size() - 1) inClause.append(",");
        }

        // 3. Query con JOINs para traer toda la información relacionada (Nombres, Especialidad)
        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.cedula AS p_ced, " +
                "o.especialidad, u.nombre_completo AS doc_nom " +
                "FROM citas c " +
                "INNER JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "INNER JOIN odontologos o ON c.id_odontologo = o.id_odontologo " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE c.fecha_hora BETWEEN ? AND ? " +
                "AND c.estado IN (" + inClause.toString() + ") " +
                "ORDER BY c.fecha_hora ASC";

        List<Cita> citas = new ArrayList<>();

        // 4. Preparación y Ejecución de la Sentencia
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Seteamos el rango de fechas (Parámetros 1 y 2)
            stmt.setTimestamp(1, Timestamp.valueOf(inicioDia));
            stmt.setTimestamp(2, Timestamp.valueOf(finDia));

            // Seteamos los estados dinámicamente (Parámetros 3 en adelante)
            for (int i = 0; i < estados.size(); i++) {
                stmt.setString(3 + i, estados.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    citas.add(crearCitaCompleta(rs));
                }
            }
        }
        return citas;
    }

    /**
     * Recupera el historial completo de citas.
     * @return Lista de todas las citas ordenadas por fecha descendente.
     */
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

    /**
     * Recupera citas por fecha (Método Legacy/Simplificado).
     * Por defecto asume que queremos ver la agenda operativa (Pendientes/Confirmadas).
     */
    @Override
    public List<Cita> listarPorFecha(String fechaStr) throws SQLException {
        // Delegamos al método maestro con los estados por defecto
        return listarPorFechaYEstados(fechaStr, List.of("Pendiente", "Confirmada", "Atendida"));
    }

    /**
     * Busca el historial de citas de un paciente por su cédula.
     * Utiliza LIKE para permitir búsquedas parciales.
     */
    @Override
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

    /**
     * Filtra citas globalmente por un estado específico.
     * Útil para reportes de "Citas Canceladas" o "Pendientes de Cobro".
     */
    @Override
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

    /**
     * Busca una cita específica por su ID.
     * Usado para cargar datos en el formulario de edición.
     */
    @Override
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

    /**
     * Verifica disponibilidad de horario para un doctor.
     * Regla de Negocio: Un doctor no puede tener dos citas activas (no canceladas) a la misma hora exacta.
     */
    @Override
    public boolean existeCitaEnHorario(int idOdontologo, LocalDateTime fechaHora) throws SQLException {
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

    /**
     * Persiste una cita (Crear o Editar).
     * Determina si es INSERT o UPDATE basándose en si el ID de la cita es mayor a 0.
     */
    @Override
    public void guardar(Cita cita) throws SQLException {
        String sql;
        if (cita.getIdCita() > 0) {
            // UPDATE: Actualizamos todos los campos editables
            sql = "UPDATE citas SET fecha_hora=?, motivo=?, id_paciente=?, id_odontologo=?, estado=? WHERE id_cita=?";
        } else {
            // INSERT: Creamos nuevo registro
            sql = "INSERT INTO citas (fecha_hora, motivo, id_paciente, id_odontologo, estado) VALUES (?, ?, ?, ?, ?)";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(cita.getFechaHora()));
            stmt.setString(2, cita.getMotivo());
            stmt.setInt(3, cita.getPaciente().getIdPaciente());
            stmt.setInt(4, cita.getOdontologo().getIdOdontologo());

            // Manejo de estado por defecto 'Pendiente' si es nulo
            String estado = (cita.getEstado() == null) ? "Pendiente" : cita.getEstado();
            stmt.setString(5, estado);

            if (cita.getIdCita() > 0) {
                stmt.setInt(6, cita.getIdCita());
            }
            stmt.executeUpdate();
        }
    }

    /**
     * Actualización rápida de estado.
     * Usado para transiciones de ciclo de vida (Cancelar, Finalizar/Atender, Facturar)
     * sin necesidad de cargar y guardar todo el objeto Cita.
     */
    @Override
    public void actualizarEstado(int idCita, String nuevoEstado) throws SQLException {
        String sql = "UPDATE citas SET estado = ? WHERE id_cita = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idCita);
            stmt.executeUpdate();
        }
    }

    /**
     * Método especializado para el Dashboard del Odontólogo.
     * Recupera solo las citas 'Pendiente' asignadas a un doctor específico en una fecha dada.
     */
    @Override
    public List<Cita> listarPendientesPorDoctor(int idOdontologo, String fechaStr) throws SQLException {
        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);

        String sql = "SELECT c.*, p.nombres AS p_nom, p.apellidos AS p_ape, p.cedula AS p_ced, " +
                "o.especialidad, u.nombre_completo AS doc_nom " +
                "FROM citas c " +
                "INNER JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "INNER JOIN odontologos o ON c.id_odontologo = o.id_odontologo " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE c.id_odontologo = ? " +
                "AND c.fecha_hora BETWEEN ? AND ? " +
                "AND c.estado = 'Pendiente' " +
                "ORDER BY c.fecha_hora ASC";

        return ejecutarConsultaParametrizada(sql, stmt -> {
            stmt.setInt(1, idOdontologo);
            stmt.setTimestamp(2, Timestamp.valueOf(inicioDia));
            stmt.setTimestamp(3, Timestamp.valueOf(finDia));
        });
    }

    /*
     * -------------------------------------------------------------------------
     * SECCIÓN DE MÉTODOS PRIVADOS Y HELPERS
     * -------------------------------------------------------------------------
     */

    /**
     * Interfaz funcional para permitir pasar lógica de seteo de parámetros
     * al método genérico 'ejecutarConsultaParametrizada'.
     */
    @FunctionalInterface
    interface SqlConsumer {
        void accept(PreparedStatement stmt) throws SQLException;
    }

    // Ejecuta una consulta SQL simple sin parámetros
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

    // Ejecuta una consulta SQL preparada con parámetros definidos por el consumidor
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

    /**
     * Mapea una fila del ResultSet a un objeto Cita completo.
     * Reconstruye la jerarquía de objetos (Cita -> Paciente, Cita -> Odontólogo -> Usuario).
     *
     * @param rs El ResultSet posicionado en la fila actual.
     * @return El objeto Cita poblado.
     */
    private Cita crearCitaCompleta(ResultSet rs) throws SQLException {
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        // Conversión Timestamp SQL -> LocalDateTime Java
        c.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        c.setMotivo(rs.getString("motivo"));
        c.setEstado(rs.getString("estado"));

        // Reconstrucción del Paciente
        Paciente p = new Paciente();
        p.setIdPaciente(rs.getInt("id_paciente"));
        p.setNombres(rs.getString("p_nom"));
        p.setApellidos(rs.getString("p_ape"));
        p.setCedula(rs.getString("p_ced"));
        c.setPaciente(p);

        // Reconstrucción del Odontólogo y su Usuario asociado
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