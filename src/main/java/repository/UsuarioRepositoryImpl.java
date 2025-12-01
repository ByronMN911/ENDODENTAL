package repository;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción:
 */
import models.Rol;
import models.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * Implementación concreta del repositorio de Usuarios usando JDBC y MySQL.
 */
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private Connection conn;

    public UsuarioRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Usuario porUsername(String username) throws SQLException {
        Usuario usuario = null;

        /*
         * QUERY ACTUALIZADA CON INNER JOIN:
         * Traemos los datos de la tabla 'usuarios' (u) Y los datos de la tabla 'roles' (r).
         * Esto es necesario para llenar el objeto Rol dentro de Usuario.
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
                    usuario = crearUsuario(rs);
                }
            }
        }
        return usuario;
    }

    /*
     * Método Helper para mapear el ResultSet a un Objeto Usuario COMPLETO.
     * Aquí llenamos tanto el Usuario como su Rol interno.
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
        Rol rol = new Rol();
        rol.setIdRol(rs.getInt("id_rol"));       // Viene de la tabla roles (o usuarios, es la FK)
        rol.setNombreRol(rs.getString("nombre_rol")); // Viene de la tabla roles gracias al JOIN

        // Asignamos el objeto Rol al Usuario
        u.setRol(rol);

        return u;
    }
}