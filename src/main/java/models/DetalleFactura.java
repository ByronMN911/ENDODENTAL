package models;

/*
 * Autor: Génesis Escobar
 * Fecha: 05/12/2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa una línea de detalle dentro de una factura.
 * Es el componente que lista los ítems individuales que se han cobrado.
 *
 * Arquitectura:
 * Maneja relaciones polimórficas (un detalle se vincula a un Servicio O a un Producto).
 * Almacena valores financieros transaccionales (precio unitario y subtotal final).
 */
import java.math.BigDecimal;

public class DetalleFactura {
    // Identificador único de la línea de detalle (Primary Key)
    private int idDetalle;
    // Tipo de ítem cobrado, usado para la lógica de stock (Valor: "SERVICIO" o "PRODUCTO")
    private String tipoItem;
    // Cantidad del ítem vendido/realizado
    private int cantidad;
    // Precio unitario del ítem en el momento de la venta (Valor histórico, BigDecimal para precisión)
    private BigDecimal precioUnitario;
    // Subtotal de la línea (cantidad * precioUnitario)
    private BigDecimal subtotalItem;
    // Campo específico para odontología: diente o zona tratada (Ej: Muela 18, General).
    private String dienteOZona;

    // Relaciones (Composición)
    private Factura factura;  // Referencia a la cabecera de la factura a la que pertenece
    private Servicio servicio; // Objeto Servicio (será null si es un Producto)
    private Producto producto; // Objeto Producto (será null si es un Servicio)

    /**
     * Constructor por defecto requerido por frameworks y la capa de mapeo.
     */
    public DetalleFactura() {
    }

    /**
     * Constructor completo para inicializar el objeto desde la base de datos.
     */
    public DetalleFactura(int idDetalle, String tipoItem, int cantidad, BigDecimal precioUnitario, BigDecimal subtotalItem, String dienteOZona, Factura factura, Servicio servicio, Producto producto) {
        this.idDetalle = idDetalle;
        this.tipoItem = tipoItem;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotalItem = subtotalItem;
        this.dienteOZona = dienteOZona;
        this.factura = factura;
        this.servicio = servicio;
        this.producto = producto;
    }

    // Getters y Setters

    /**
     * @return El identificador único del detalle.
     */
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    /**
     * @return El tipo de ítem ("SERVICIO" o "PRODUCTO").
     */
    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }

    /**
     * @return La cantidad vendida.
     */
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    /**
     * @return El precio unitario de venta (valor BigDecimal).
     */
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    /**
     * @return El subtotal calculado para esta línea.
     */
    public BigDecimal getSubtotalItem() { return subtotalItem; }
    public void setSubtotalItem(BigDecimal subtotalItem) { this.subtotalItem = subtotalItem; }

    /**
     * @return La descripción de la zona dental asociada (opcional).
     */
    public String getDienteOZona() { return dienteOZona; }
    public void setDienteOZona(String dienteOZona) { this.dienteOZona = dienteOZona; }

    /**
     * @return El objeto Factura padre.
     */
    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }

    /**
     * @return El objeto Servicio asociado (null si es producto).
     */
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    /**
     * @return El objeto Producto asociado (null si es servicio).
     */
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }
}