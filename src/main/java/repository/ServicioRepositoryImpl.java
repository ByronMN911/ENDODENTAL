package repository;

/*
 * Autor: Génesis Escobar
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación concreta del Repositorio de Servicios utilizando JDBC.
 * Esta clase gestiona la persistencia de los datos del catálogo de servicios médicos
 * (ej: Consultas, Limpiezas, Cirugías) interactuando directamente con la base de datos.
 *
 * Características Técnicas:
 * 1. Uso de PreparedStatement para prevenir Inyección SQL.
 * 2. Manejo eficiente de recursos con try-with-resources.
 * 3. Mapeo manual de ResultSet a objetos de dominio (Servicio).
 */

import models.Servicio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioRepositoryImpl implements ServicioRepository {

    // Conexión compartida inyectada para mantener la sesión de base de datos activa
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado por el filtro o servicio principal.
     */
    public ServicioRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Recupera el listado completo de servicios médicos disponibles.
     * Ejecuta una consulta SELECT simple ordenada alfabéticamente por nombre.
     *
     * @return Una lista de objetos Servicio poblados con datos de la BD.
     * @throws SQLException Si ocurre un error de conexión o ejecución.
     */
    @Override
    public List<Servicio> listar() throws SQLException {
        List<Servicio> servicios = new ArrayList<>();
        // Ordenamos por nombre para que el Select se vea ordenado en la interfaz
        String sql = "SELECT * FROM servicios ORDER BY nombre";

        // try-with-resources asegura el cierre automático del Statement y ResultSet
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                servicios.add(crearServicio(rs));
            }
        }
        return servicios;
    }

    /**
     * Busca un servicio específico por su ID único.
     *
     * @param id El ID del servicio a buscar.
     * @return El objeto Servicio encontrado o null si no existe.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    @Override
    public Servicio porId(int id) throws SQLException {
        Servicio servicio = null;
        String sql = "SELECT * FROM servicios WHERE id_servicio = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    servicio = crearServicio(rs);
                }
            }
        }
        return servicio;
    }

    /**
     * Persiste un servicio en la base de datos.
     * Implementa la lógica "Upsert":
     * - Si el ID > 0: Se trata de una edición -> UPDATE.
     * - Si el ID es 0: Se trata de un nuevo registro -> INSERT.
     *
     * @param servicio El objeto Servicio con los datos a guardar.
     * @throws SQLException Si ocurre un error al guardar (ej: nombre duplicado).
     */
    @Override
    public void guardar(Servicio servicio) throws SQLException {
        String sql;
        // Determinamos la operación basada en la existencia del ID
        if (servicio.getIdServicio() > 0) {
            sql = "UPDATE servicios SET nombre=?, descripcion=?, precio_base=? WHERE id_servicio=?";
        } else {
            sql = "INSERT INTO servicios (nombre, descripcion, precio_base) VALUES (?, ?, ?)";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Asignación segura de parámetros
            stmt.setString(1, servicio.getNombre());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setBigDecimal(3, servicio.getPrecioBase());

            if (servicio.getIdServicio() > 0) {
                // Si es UPDATE, el ID es el cuarto parámetro (WHERE id = ?)
                stmt.setInt(4, servicio.getIdServicio());
            }

            stmt.executeUpdate();
        }
    }

    /**
     * Elimina un servicio del catálogo.
     * En este caso, se implementa un Borrado Físico (DELETE) directo.
     * Nota: Esto fallará si el servicio ya ha sido usado en facturas (Integridad Referencial).
     *
     * @param id El ID del servicio a eliminar.
     * @throws SQLException Si ocurre un error al eliminar.
     */
    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM servicios WHERE id_servicio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar (Helper) para mapear una fila del ResultSet a un objeto Servicio.
     * Centraliza la conversión de tipos SQL a Java para evitar repetición de código.
     *
     * @param rs El ResultSet posicionado en la fila actual.
     * @return Un objeto Servicio con sus atributos llenos.
     * @throws SQLException Si hay error al leer columnas.
     */
    private Servicio crearServicio(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setIdServicio(rs.getInt("id_servicio"));
        s.setNombre(rs.getString("nombre"));
        s.setDescripcion(rs.getString("descripcion"));
        // Mapeo de DECIMAL(SQL) a BigDecimal(Java) para precisión monetaria
        s.setPrecioBase(rs.getBigDecimal("precio_base"));
        return s;
    }
}