package repository;

/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 3.0
 * Descripción:
 * Implementación concreta del Repositorio de Usuarios utilizando JDBC.
 * Esta clase gestiona la persistencia y recuperación de datos de los usuarios del sistema
 * (Administradores, Secretarias, Odontólogos).
 *
 * Características Técnicas:
 * 1. Uso de sentencias preparadas (PreparedStatement) para seguridad.
 * 2. Mapeo relacional manual (ResultSet -> Objeto Java) incluyendo relaciones (Rol).
 * 3. Gestión de claves foráneas y autogeneradas.
 */

import models.Rol;
import models.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepositoryImpl implements UsuarioRepository {

    // Conexión compartida inyectada para mantener la sesión de base de datos activa
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado por el filtro o servicio principal.
     */
    public UsuarioRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Busca un usuario por su nombre de usuario (login).
     * Utiliza un INNER JOIN con la tabla de roles para construir el objeto Usuario completo
     * con su Rol asociado en una sola consulta.
     *
     * @param username El nombre de usuario a buscar.
     * @return El objeto Usuario poblado o null si no se encuentra.
     * @throws SQLException Si ocurre un error en la consulta SQL.
     */
    @Override
    public Usuario porUsername(String username) throws SQLException {
        Usuario usuario = null;

        /*
         * QUERY ACTUALIZADA CON INNER JOIN:
         * Traemos los datos de la tabla 'usuarios' (u) Y los datos de la tabla 'roles' (r).
         * Esto es necesario para llenar el objeto Rol dentro de Usuario (Composición).
         */
        String sql = "SELECT u.id_usuario, u.username, u.password, u.nombre_completo, " +
                "u.email, u.estado, r.id_rol, r.nombre_rol " +
                "FROM usuarios u " +
                "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                "WHERE u.username = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Si encontramos el registro, delegamos el mapeo al método helper
                    usuario = crearUsuario(rs);
                }
            }
        }
        return usuario;
    }

    /**
     * Método Helper para mapear el ResultSet a un Objeto Usuario COMPLETO.
     * Centraliza la lógica de conversión de filas SQL a objetos Java.
     *
     * @param rs El ResultSet posicionado en la fila actual.
     * @return Un objeto Usuario con sus atributos y objeto Rol anidado.
     * @throws SQLException Si hay error al leer las columnas.
     */
    private Usuario crearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setEmail(rs.getString("email"));
        u.setEstado(rs.getInt("estado"));

        // CREACIÓN DEL OBJETO ROL (Composición)
        // Reconstruimos el objeto Rol a partir de las columnas obtenidas en el JOIN
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));       // Viene de la tabla roles (o usuarios, es la FK)
        rol.setNombreRol(rs.getString("nombre_rol")); // Viene de la tabla roles gracias al JOIN

        // Asignamos el objeto Rol al Usuario
        u.setRol(rol);

        return u;
    }

    /**
     * Recupera la lista de usuarios activos.
     * Filtra por 'estado = 1' y ordena alfabéticamente por nombre completo.
     */
    @Override
    public List<Usuario> listar() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u INNER JOIN roles r ON u.id_rol = r.id_rol WHERE u.estado = 1 ORDER BY u.nombre_completo";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) usuarios.add(crearUsuario(rs));
        }
        return usuarios;
    }

    /**
     * Recupera la lista de usuarios inactivos (Papelera de Reciclaje).
     * Filtra por 'estado = 0'.
     */
    @Override
    public List<Usuario> listarInactivos() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u INNER JOIN roles r ON u.id_rol = r.id_rol WHERE u.estado = 0 ORDER BY u.nombre_completo";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) usuarios.add(crearUsuario(rs));
        }
        return usuarios;
    }

    /**
     * Busca un usuario por su ID primario.
     * Incluye el JOIN con roles para devolver el objeto completo.
     */
    @Override
    public Usuario porId(int id) throws SQLException {
        String sql = "SELECT u.*, r.nombre_rol FROM usuarios u INNER JOIN roles r ON u.id_rol = r.id_rol WHERE u.id_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return crearUsuario(rs);
            }
        }
        return null;
    }

    /**
     * Persiste un usuario en la base de datos.
     * Implementa lógica "Upsert":
     * - UPDATE: Si el usuario tiene un ID > 0.
     * - INSERT: Si el usuario es nuevo (ID 0).
     *
     * @param usuario El objeto usuario a guardar.
     * @return El ID del usuario guardado (útil para relaciones posteriores, ej: crear Odontólogo).
     */
    @Override
    public int guardar(Usuario usuario) throws SQLException {
        String sql;
        // Determinamos si es actualización o inserción
        if (usuario.getIdUsuario() > 0) {
            // Update (Si password viene vacío, se asume que se manejó en el servicio para no sobreescribirlo con nulo)
            sql = "UPDATE usuarios SET username=?, nombre_completo=?, email=?, id_rol=?, password=? WHERE id_usuario=?";
        } else {
            // Insert: El estado por defecto es 1 (Activo)
            sql = "INSERT INTO usuarios (username, nombre_completo, email, id_rol, password, estado) VALUES (?, ?, ?, ?, ?, 1)";
        }

        // Usamos RETURN_GENERATED_KEYS para obtener el ID autogenerado en caso de INSERT
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getNombreCompleto());
            stmt.setString(3, usuario.getEmail());
            stmt.setInt(4, usuario.getRol().getIdRol());
            stmt.setString(5, usuario.getPassword());

            if (usuario.getIdUsuario() > 0) {
                // Caso UPDATE: El ID es el último parámetro del WHERE
                stmt.setInt(6, usuario.getIdUsuario());
                stmt.executeUpdate();
                return usuario.getIdUsuario(); // Retornamos el mismo ID
            } else {
                // Caso INSERT
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) return rs.getInt(1); // Retornamos el nuevo ID generado
                }
            }
        }
        return 0;
    }

    /**
     * Realiza el borrado lógico de un usuario.
     * Actualiza el campo 'estado' a 0.
     */
    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "UPDATE usuarios SET estado = 0 WHERE id_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Reactiva un usuario previamente eliminado.
     * Actualiza el campo 'estado' a 1.
     */
    @Override
    public void activar(int id) throws SQLException {
        String sql = "UPDATE usuarios SET estado = 1 WHERE id_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

}