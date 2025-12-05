package models;

import java.math.BigDecimal;

public class Producto {
    private int idProducto;
    private String nombre;
    private String marca;
    private String descripcion;
    private BigDecimal precioVenta;
    private int stock;
    private int stockMinimo;
    private int estado; // <--- NUEVO CAMPO

    public Producto() {
    }

    public Producto(int idProducto, String nombre, String marca, String descripcion, BigDecimal precioVenta, int stock, int stockMinimo, int estado) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.marca = marca;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public int getEstado() { return estado; } // <--- NUEVO GETTER
    public void setEstado(int estado) { this.estado = estado; } // <--- NUEVO SETTER
}