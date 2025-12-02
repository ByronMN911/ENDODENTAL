package repository;

import models.Odontologo;
import models.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OdontologoRepositoryImpl implements OdontologoRepository {

    private Connection conn;

    public OdontologoRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Odontologo> listar() throws SQLException {
        List<Odontologo> lista = new ArrayList<>();
        // INNER JOIN para traer el nombre del usuario asociado al odontólogo
        // Filtramos usuarios.estado = 1 para traer solo doctores activos
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

    // Helper para mapear el ResultSet a Objetos
    private Odontologo crearOdontologo(ResultSet rs) throws SQLException {
        Odontologo o = new Odontologo();
        o.setIdOdontologo(rs.getInt("id_odontologo"));
        o.setEspecialidad(rs.getString("especialidad"));
        o.setCodigoMedico(rs.getString("codigo_medico"));

        // Llenamos el objeto Usuario interno
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setEmail(rs.getString("email"));

        o.setUsuario(u); // Asignamos el usuario al odontólogo
        return o;
    }
}