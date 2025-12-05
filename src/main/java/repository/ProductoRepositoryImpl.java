package repository;

import models.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositoryImpl implements ProductoRepository {
    private Connection conn;

    public ProductoRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

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
}