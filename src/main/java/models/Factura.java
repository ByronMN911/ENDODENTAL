package models;

/*
 * Autor: Génesis Escobar
 * Fecha: 05/12/2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa la Cabecera de una Factura de Venta.
 * Almacena los datos del cliente, los montos totales (subtotal, IVA, total) y la vinculación
 * con la cita que originó el cobro. Es el registro fiscal y financiero de la transacción.
 */
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Factura {
    // Identificador único de la factura (Primary Key)
    private int idFactura;
    // Fecha y hora en que se emitió la factura
    private LocalDateTime fechaEmision;
    // Número de identificación del cliente (RUC, Cédula o Pasaporte)
    private String identificacionCliente;
    // Nombre completo o Razón Social del cliente a facturar
    private String nombreClienteFactura;
    // Dirección fiscal del cliente
    private String direccionCliente;
    // Monto total sin impuestos (Suma de los subtotales de los detalles)
    private BigDecimal subtotal;
    // Monto correspondiente al Impuesto al Valor Agregado (IVA)
    private BigDecimal montoIva;
    // Monto final a pagar (subtotal + IVA)
    private BigDecimal totalPagar;
    // Método de pago utilizado (Ej: Efectivo, Tarjeta, Transferencia)
    private String metodoPago;

    // Relación de Composición: La factura pertenece a una Cita específica
    private Cita cita;

    // Relación Agregación: Lista de los ítems o detalles de la factura
    private List<DetalleFactura> detalles;


    /**
     * Constructor por defecto requerido por frameworks y la capa de mapeo.
     */
    public Factura() {
    }

    /**
     * Constructor completo para inicializar la cabecera de la factura desde la base de datos.
     */
    public Factura(int idFactura, LocalDateTime fechaEmision, String identificacionCliente, String nombreClienteFactura, String direccionCliente, BigDecimal subtotal, BigDecimal montoIva, BigDecimal totalPagar, String metodoPago, Cita cita) {
        this.idFactura = idFactura;
        this.fechaEmision = fechaEmision;
        this.identificacionCliente = identificacionCliente;
        this.nombreClienteFactura = nombreClienteFactura;
        this.direccionCliente = direccionCliente;
        this.subtotal = subtotal;
        this.montoIva = montoIva;
        this.totalPagar = totalPagar;
        this.metodoPago = metodoPago;
        this.cita = cita;
    }

    // Getters y Setters

    /**
     * @return El identificador único de la factura.
     */
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }

    /**
     * @return La fecha y hora de emisión (LocalDateTime).
     */
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    /**
     * @return La identificación del cliente (RUC/Cédula).
     */
    public String getIdentificacionCliente() { return identificacionCliente; }
    public void setIdentificacionCliente(String identificacionCliente) { this.identificacionCliente = identificacionCliente; }

    /**
     * @return El nombre o razón social del cliente.
     */
    public String getNombreClienteFactura() { return nombreClienteFactura; }
    public void setNombreClienteFactura(String nombreClienteFactura) { this.nombreClienteFactura = nombreClienteFactura; }

    /**
     * @return La dirección del cliente.
     */
    public String getDireccionCliente() { return direccionCliente; }
    public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }

    /**
     * @return El subtotal (valor BigDecimal).
     */
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    /**
     * @return El monto del IVA (valor BigDecimal).
     */
    public BigDecimal getMontoIva() { return montoIva; }
    public void setMontoIva(BigDecimal montoIva) { this.montoIva = montoIva; }

    /**
     * @return El total final a pagar (valor BigDecimal).
     */
    public BigDecimal getTotalPagar() { return totalPagar; }
    public void setTotalPagar(BigDecimal totalPagar) { this.totalPagar = totalPagar; }

    /**
     * @return El método de pago utilizado.
     */
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    /**
     * @return El objeto Cita que originó esta factura.
     */
    public Cita getCita() { return cita; }
    public void setCita(Cita cita) { this.cita = cita; }

    /**
     * @return La lista de detalles (ítems) de esta factura.
     */
    public List<DetalleFactura> getDetalles() {
        return detalles;
    }

    /**
     * @param detalles La lista de detalles (ítems) a asociar a la cabecera.
     */
    public void setDetalles(List<DetalleFactura> detalles) {
        this.detalles = detalles;
    }

}