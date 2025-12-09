package repository;

/*
 * Autor: Mathew Lara
 * Fecha: 05/12/2025
 * Versión: 3.1
 * Descripción:
 * Implementación concreta del Repositorio de Productos utilizando JDBC.
 * ...
 */

import models.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {
    private Connection conn;

    public ProductoRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    // ... (listar, listarInactivos, porId, guardar, eliminar, activar, crearProducto se mantienen igual) ...

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

    @Override
    public void guardar(Producto producto) throws SQLException {
        String sql;
        if (producto.getIdProducto() > 0) {
            sql = "UPDATE productos SET nombre=?, marca=?, descripcion=?, precio_venta=?, stock=?, stock_minimo=? WHERE id_producto=?";
        } else {
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

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "UPDATE productos SET estado = 0 WHERE id_producto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void activar(int id) throws SQLException {
        String sql = "UPDATE productos SET estado = 1 WHERE id_producto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

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
     * Actualiza el stock de forma atómica (incremental), con validación de suficiencia.
     * Es CRÍTICO para la facturación.
     *
     * @param idProducto ID del producto.
     * @param cantidadCambio Cantidad a sumar (positivo) o restar (negativo, para venta).
     * @throws SQLException Si ocurre un error, incluyendo stock insuficiente.
     */
    @Override
    public void actualizarStock(int idProducto, int cantidadCambio) throws SQLException {
        // Obtenemos el stock actual antes de ejecutar el UPDATE
        String sqlCheck = "SELECT stock FROM productos WHERE id_producto = ?";
        int stockActual = 0;

        try(PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck)) {
            stmtCheck.setInt(1, idProducto);
            try(ResultSet rs = stmtCheck.executeQuery()) {
                if (rs.next()) {
                    stockActual = rs.getInt("stock");
                }
            }
        }

        int nuevoStock = stockActual + cantidadCambio;

        // VALIDACIÓN DE INTEGRIDAD: Si el nuevo stock es menor que cero, lanzamos excepción.
        if (nuevoStock < 0) {
            // NOTA: El Servicio atrapará esta excepción SQL, la envolverá en ServiceJdbcException, y la mostrará.
            throw new SQLException("El stock resultante es negativo. Stock actual: " + stockActual + ", Se intenta descontar: " + (-cantidadCambio));
        }

        // Si la validación pasa, ejecutamos la actualización atómica
        String sqlUpdate = "UPDATE productos SET stock = stock + ? WHERE id_producto = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
            stmt.setInt(1, cantidadCambio); // El valor ya es negativo si es una venta
            stmt.setInt(2, idProducto);
            stmt.executeUpdate();
        }
    }
}