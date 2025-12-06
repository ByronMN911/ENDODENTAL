package models;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa un Servicio Médico ofrecido por la clínica.
 * Actúa como la tabla de catálogo de precios y descripciones para los servicios intangibles
 * (ej: Profilaxis, Endodoncia).
 * * Arquitectura:
 * Utiliza BigDecimal para manejar el precio base con precisión financiera.
 */
import java.math.BigDecimal;

public class Servicio {
    // Identificador único del servicio (Primary Key)
    private int idServicio;
    // Nombre descriptivo del servicio (Ej: "Limpieza Profunda")
    private String nombre;
    // Descripción detallada del servicio o procedimiento
    private String descripcion;
    // Precio base del servicio (utiliza BigDecimal para precisión monetaria)
    private BigDecimal precioBase;

    /**
     * Constructor por defecto, requerido por frameworks y la capa de mapeo JDBC.
     */
    public Servicio() {
    }

    /**
     * Constructor completo para inicializar el objeto desde la base de datos.
     *
     * @param idServicio Identificador único.
     * @param nombre Nombre del servicio.
     * @param descripcion Descripción del servicio.
     * @param precioBase Precio base (BigDecimal).
     */
    public Servicio(int idServicio, String nombre, String descripcion, BigDecimal precioBase) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
    }

    // Getters y Setters

    /**
     * @return El identificador único del servicio.
     */
    public int getIdServicio() { return idServicio; }
    public void setIdServicio(int idServicio) { this.idServicio = idServicio; }

    /**
     * @return El nombre del servicio.
     */
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * @return La descripción del servicio.
     */
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * @return El precio base del servicio (valor BigDecimal).
     */
    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }
}