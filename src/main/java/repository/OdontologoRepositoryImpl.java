package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Implementación concreta del Repositorio de Odontólogos usando JDBC.
 * Se encarga de la persistencia de datos profesionales de los doctores,
 * gestionando tanto la tabla 'odontologos' como su relación con 'usuarios'.
 *
 * Características Técnicas:
 * 1. Uso de INNER JOIN para recuperar datos combinados (Perfil Médico + Datos de Usuario).
 * 2. Mapeo manual de ResultSet a objetos complejos (Composición).
 * 3. Manejo de transacciones implícitas para actualización de perfiles.
 */

import models.Odontologo;
import models.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OdontologoRepositoryImpl implements OdontologoRepository {

    // Conexión compartida inyectada
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado externamente.
     */
    public OdontologoRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Recupera la lista de todos los odontólogos activos en el sistema.
     * Realiza un INNER JOIN con la tabla de usuarios para obtener el nombre real
     * y el correo, filtrando solo aquellos usuarios que tienen estado activo (1).
     *
     * @return Lista de objetos Odontologo con sus datos de usuario poblados.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    @Override
    public List<Odontologo> listar() throws SQLException {
        List<Odontologo> lista = new ArrayList<>();
        // Query: Trae datos de la tabla odontologos (o) y usuarios (u) donde el usuario esté activo.
        String sql = "SELECT o.id_odontologo, o.especialidad, o.codigo_medico, " +
                "u.id_usuario, u.nombre_completo, u.email " +
                "FROM odontologos o " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE u.estado = 1 " +
                "ORDER BY u.nombre_completo";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(crearOdontologo(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un odontólogo por su ID primario (id_odontologo).
     *
     * @param id El ID del odontólogo.
     * @return El objeto Odontologo encontrado o null.
     * @throws SQLException Si ocurre un error SQL.
     */
    @Override
    public Odontologo porId(int id) throws SQLException {
        Odontologo odontologo = null;
        String sql = "SELECT o.id_odontologo, o.especialidad, o.codigo_medico, " +
                "u.id_usuario, u.nombre_completo, u.email " +
                "FROM odontologos o " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE o.id_odontologo = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    odontologo = crearOdontologo(rs);
                }
            }
        }
        return odontologo;
    }

    /**
     * Método Auxiliar de Mapeo (Helper).
     * Convierte una fila del ResultSet (que trae columnas de dos tablas mezcladas)
     * en un objeto Odontologo que contiene dentro un objeto Usuario.
     *
     * @param rs El ResultSet posicionado en la fila actual.
     * @return Objeto Odontologo reconstruido.
     * @throws SQLException Si hay error al leer columnas.
     */
    private Odontologo crearOdontologo(ResultSet rs) throws SQLException {
        Odontologo o = new Odontologo();
        o.setIdOdontologo(rs.getInt("id_odontologo"));
        o.setEspecialidad(rs.getString("especialidad"));
        o.setCodigoMedico(rs.getString("codigo_medico"));

        // Reconstrucción del objeto Usuario interno (Composición)
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setEmail(rs.getString("email"));

        o.setUsuario(u); // Asignamos el usuario al odontólogo
        return o;
    }

    /**
     * Busca el perfil de odontólogo asociado a un ID de Usuario (Login).
     * Esencial para que el sistema sepa "quién es el doctor" cuando alguien se loguea.
     *
     * @param idUsuario El ID de la tabla usuarios.
     * @return El perfil de odontólogo asociado.
     * @throws SQLException Si ocurre un error SQL.
     */
    public Odontologo porIdUsuario(int idUsuario) throws SQLException {
        Odontologo odontologo = null;
        String sql = "SELECT o.id_odontologo, o.especialidad, o.codigo_medico, " +
                "u.id_usuario, u.nombre_completo, u.email " +
                "FROM odontologos o " +
                "INNER JOIN usuarios u ON o.id_usuario = u.id_usuario " +
                "WHERE u.id_usuario = ?"; // Filtramos por el ID del usuario

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    odontologo = crearOdontologo(rs);
                }
            }
        }
        return odontologo;
    }

    /**
     * Guarda o actualiza la información profesional de un odontólogo.
     * Este método se usa en el módulo de Administración al crear/editar usuarios.
     *
     * Lógica de Upsert (Update o Insert):
     * Primero verifica si ya existe un registro en 'odontologos' para ese usuario.
     * - Si existe -> UPDATE (Actualiza especialidad/código).
     * - Si no existe -> INSERT (Crea el perfil médico).
     *
     * @param o El objeto Odontologo con los datos a guardar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public void guardar(Odontologo o) throws SQLException {
        // 1. Verificación de existencia previa
        String sqlCheck = "SELECT COUNT(*) FROM odontologos WHERE id_usuario = ?";
        boolean existe = false;
        try(PreparedStatement stmt = conn.prepareStatement(sqlCheck)) {
            stmt.setInt(1, o.getUsuario().getIdUsuario());
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next() && rs.getInt(1) > 0) existe = true;
            }
        }

        // 2. Decisión de sentencia SQL
        String sql;
        if (existe) {
            sql = "UPDATE odontologos SET especialidad=?, codigo_medico=? WHERE id_usuario=?";
        } else {
            sql = "INSERT INTO odontologos (especialidad, codigo_medico, id_usuario) VALUES (?, ?, ?)";
        }

        // 3. Ejecución
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, o.getEspecialidad());
            stmt.setString(2, o.getCodigoMedico());
            stmt.setInt(3, o.getUsuario().getIdUsuario());
            stmt.executeUpdate();
        }
    }
}