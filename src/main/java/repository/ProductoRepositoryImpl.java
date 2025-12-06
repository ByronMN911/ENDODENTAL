package repository;

/*
 * Autor: Mathew Lara
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación concreta del Repositorio de Productos utilizando JDBC.
 * Esta clase maneja la persistencia de datos para el inventario, incluyendo
 * operaciones CRUD, filtrado por estado (activo/inactivo) y gestión crítica del stock.
 */

import models.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {

    // Conexión compartida inyectada para mantener la sesión de base de datos activa
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado por el filtro o servicio principal.
     */
    public ProductoRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Recupera el listado de productos activos.
     * Filtra por 'estado = 1' para mostrar solo los ítems disponibles para uso o venta.
     *
     * @return Lista de productos activos ordenados por nombre.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    @Override
    public List<Producto> listar() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE estado = 1 ORDER BY nombre";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(crearProducto(rs));
            }
        }
        return productos;
    }

    /**
     * Recupera el listado de productos inactivos (Papelera).
     * Filtra por 'estado = 0'. Útil para auditoría o restauración de ítems.
     *
     * @return Lista de productos inactivos.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    @Override
    public List<Producto> listarInactivos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE estado = 0 ORDER BY nombre";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(crearProducto(rs));
            }
        }
        return productos;
    }

    /**
     * Busca un producto por su ID único.
     * Recupera toda la información del producto sin importar su estado.
     *
     * @param id ID del producto.
     * @return Objeto Producto o null si no existe.
     * @throws SQLException Si ocurre un error SQL.
     */
    @Override
    public Producto porId(int id) throws SQLException {
        Producto p = null;
        String sql = "SELECT * FROM productos WHERE id_producto=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    p = crearProducto(rs);
                }
            }
        }
        return p;
    }

    /**
     * Persiste un producto en la base de datos.
     * Implementa lógica "Upsert":
     * - Si ID > 0: Actualiza (UPDATE) los campos editables.
     * - Si ID = 0: Inserta (INSERT) un nuevo registro.
     *
     * @param producto El objeto con los datos a guardar.
     * @throws SQLException Si ocurre un error al guardar.
     */
    @Override
    public void guardar(Producto producto) throws SQLException {
        String sql;
        if (producto.getIdProducto() > 0) {
            // UPDATE: Actualizamos información básica y stock
            sql = "UPDATE productos SET nombre=?, marca=?, descripcion=?, precio_venta=?, stock=?, stock_minimo=? WHERE id_producto=?";
        } else {
            // INSERT: El estado se define por defecto en 1 en la BD (o implícitamente activo)
            sql = "INSERT INTO productos (nombre, marca, descripcion, precio_venta, stock, stock_minimo) VALUES (?,?,?,?,?,?)";
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, producto.getNombre());
            stmt.setString(2, producto.getMarca());
            stmt.setString(3, producto.getDescripcion());
            stmt.setBigDecimal(4, producto.getPrecioVenta());
            stmt.setInt(5, producto.getStock());
            stmt.setInt(6, producto.getStockMinimo());

            if (producto.getIdProducto() > 0) {
                stmt.setInt(7, producto.getIdProducto());
            }
            stmt.executeUpdate();
        }
    }

    /**
     * Realiza un Borrado Lógico (Soft Delete).
     * Cambia el estado a 0 para "eliminar" visualmente el producto sin perder historial.
     *
     * @param id ID del producto a desactivar.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "UPDATE productos SET estado = 0 WHERE id_producto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Reactiva un producto previamente eliminado.
     * Cambia el estado a 1.
     *
     * @param id ID del producto a restaurar.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    @Override
    public void activar(int id) throws SQLException {
        String sql = "UPDATE productos SET estado = 1 WHERE id_producto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para mapear el ResultSet a un objeto Producto.
     * Centraliza la conversión de tipos SQL a Java.
     */
    private Producto crearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setNombre(rs.getString("nombre"));
        p.setMarca(rs.getString("marca"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        p.setStock(rs.getInt("stock"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setEstado(rs.getInt("estado"));
        return p;
    }

    /**
     * Actualiza el stock de forma atómica (incremental).
     * Es crítico para la facturación: resta (si es venta) o suma (si es compra)
     * directamente en la base de datos para evitar condiciones de carrera.
     *
     * @param idProducto ID del producto.
     * @param cantidadCambio Cantidad a sumar (positivo) o restar (negativo).
     * @throws SQLException Si ocurre un error.
     */
    @Override
    public void actualizarStock(int idProducto, int cantidadCambio) throws SQLException {
        // La consulta "stock = stock + ?" es segura para concurrencia básica
        String sql = "UPDATE productos SET stock = stock + ? WHERE id_producto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidadCambio);
            stmt.setInt(2, idProducto);
            stmt.executeUpdate();
        }
    }
}