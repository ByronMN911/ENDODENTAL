package models;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Entidad que representa la información fundamental de un Paciente.
 * Almacena datos personales, de contacto, historial de alergias y el estado
 * de actividad del paciente en el sistema (utilizado para el borrado lógico).
 * * Arquitectura:
 * Actúa como un POJO (Plain Old Java Object) o Java Bean, transfiriendo datos
 * entre las capas de la aplicación (Repository, Service y View).
 */
import java.time.LocalDateTime;

public class Paciente {
    // Identificador único del paciente (Primary Key)
    private int idPaciente;
    // Cédula de identidad (clave única, utilizada para búsquedas y validación)
    private String cedula;
    // Nombres del paciente
    private String nombres;
    // Apellidos del paciente
    private String apellidos;
    // Número de teléfono
    private String telefono;
    // Correo electrónico
    private String email;
    // Notas sobre alergias (campo de texto libre, importante para la atención médica)
    private String alergias;
    // Fecha y hora del registro del paciente en el sistema
    private LocalDateTime fechaRegistro;
    // Estado de actividad (1: Activo, 0: Inactivo/Archivado - para borrado lógico)
    private int estado;

    /**
     * Constructor por defecto, esencial para el mapeo de la capa JDBC.
     */
    public Paciente() {
    }

    /**
     * Constructor completo (Utilizado para mapear datos leídos de la BD).
     *
     * @param idPaciente Identificador único.
     * @param cedula Número de cédula.
     * @param nombres Nombres.
     * @param apellidos Apellidos.
     * @param telefono Teléfono.
     * @param email Correo electrónico.
     * @param alergias Alergias.
     * @param fechaRegistro Fecha de registro.
     * @param estado Estado de actividad.
     */
    public Paciente(int idPaciente, String cedula, String nombres, String apellidos, String telefono, String email, String alergias, LocalDateTime fechaRegistro, int estado) {
        this.idPaciente = idPaciente;
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.alergias = alergias;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
    }

    /**
     * Constructor para la Creación de Nuevo Paciente.
     * No incluye ID, fechaRegistro ni estado, ya que estos valores son generados
     * automáticamente por la base de datos (ID, CURRENT_TIMESTAMP y estado por defecto 1).
     *
     * @param cedula Número de cédula.
     * @param nombres Nombres.
     * @param apellidos Apellidos.
     * @param telefono Teléfono.
     * @param email Correo electrónico.
     * @param alergias Alergias.
     */
    public Paciente(String cedula, String nombres, String apellidos, String telefono, String email, String alergias) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.alergias = alergias;
    }

    // Getters y Setters

    /**
     * @return El identificador único del paciente.
     */
    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    /**
     * @return El número de cédula.
     */
    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    /**
     * @return Los nombres del paciente.
     */
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    /**
     * @return Los apellidos del paciente.
     */
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    /**
     * @return El número de teléfono.
     */
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    /**
     * @return El correo electrónico.
     */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * @return Las alergias registradas.
     */
    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    /**
     * @return La fecha y hora de registro.
     */
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    /**
     * @return El estado de actividad (1=Activo, 0=Inactivo).
     */
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}