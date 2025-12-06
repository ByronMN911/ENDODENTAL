package models;

/*
 * Autor: Mathew Lara
 * Fecha: 05/12/2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa un producto o insumo dentro del inventario de la clínica.
 * Almacena información sobre el stock, el precio de venta y el estado de actividad.
 *
 * Arquitectura:
 * Actúa como un POJO (Plain Old Java Object) para transferir datos entre las capas de la aplicación.
 * Utiliza BigDecimal para manejar valores monetarios con alta precisión.
 */
import java.math.BigDecimal;

public class Producto {
    // Identificador único del producto (Primary Key)
    private int idProducto;
    // Nombre descriptivo del producto (Ej: Resina A2, Cepillo dental)
    private String nombre;
    // Marca del producto
    private String marca;
    // Descripción detallada
    private String descripcion;
    // Precio al que se vende el producto (BigDecimal para precisión financiera)
    private BigDecimal precioVenta;
    // Cantidad actual disponible en el inventario (Stock)
    private int stock;
    // Cantidad mínima requerida para disparar una alerta de "Bajo Stock"
    private int stockMinimo;
    // Estado de actividad (1: Activo/Disponible, 0: Inactivo/Archivado)
    private int estado;

    /**
     * Constructor por defecto, requerido para el mapeo de la capa JDBC.
     */
    public Producto() {
    }

    /**
     * Constructor completo (Utilizado para mapear datos leídos de la BD).
     *
     * @param idProducto Identificador único.
     * @param nombre Nombre del producto.
     * @param marca Marca.
     * @param descripcion Descripción.
     * @param precioVenta Precio de venta (BigDecimal).
     * @param stock Stock actual.
     * @param stockMinimo Stock mínimo de alerta.
     * @param estado Estado de actividad.
     */
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

    /**
     * @return El identificador único del producto.
     */
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    /**
     * @return El nombre del producto.
     */
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * @return La marca del producto.
     */
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    /**
     * @return La descripción del producto.
     */
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * @return El precio de venta (valor BigDecimal).
     */
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    /**
     * @return La cantidad actual en inventario.
     */
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    /**
     * @return La cantidad mínima requerida para alerta.
     */
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    /**
     * @return El estado de actividad (1=Activo, 0=Inactivo).
     */
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}