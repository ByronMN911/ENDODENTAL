package models;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa una Cita o turno programado en la clínica.
 * Contiene la información temporal (fecha y hora), el motivo y el estado actual de la cita
 * dentro del flujo de trabajo (Pendiente, Atendida, Facturada, Cancelada).
 *
 * Arquitectura:
 * Implementa el patrón de Composición para vincular objetos complejos (Paciente y Odontologo).
 */
import java.time.LocalDateTime;

public class Cita {
    // Identificador único de la cita (Primary Key)
    private int idCita;
    // Fecha y hora exactas de la cita (DATETIME en la BD)
    private LocalDateTime fechaHora;
    // Razón de la consulta o procedimiento
    private String motivo;
    // Estado actual de la cita (ENUM en la BD: Pendiente, Atendida, Facturada, etc.)
    private String estado;

    // Relaciones (Composición): Objetos completos para fácil acceso a los datos
    private Paciente paciente;
    private Odontologo odontologo;

    /**
     * Constructor por defecto requerido por frameworks (ej. Servlets) y la capa de mapeo.
     */
    public Cita() {
    }

    /**
     * Constructor Completo (Utilizado para mapear datos leídos de la BD).
     *
     * @param idCita Identificador de la cita.
     * @param fechaHora Fecha y hora del turno.
     * @param motivo Razón de la consulta.
     * @param estado Estado actual del turno.
     * @param paciente Objeto Paciente vinculado.
     * @param odontologo Objeto Odontologo que atiende.
     */
    public Cita(int idCita, LocalDateTime fechaHora, String motivo, String estado, Paciente paciente, Odontologo odontologo) {
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
        this.paciente = paciente;
        this.odontologo = odontologo;
    }

    /**
     * Constructor para la Creación de una NUEVA CITA (Agendamiento).
     * No incluye ID (auto-incrementado) ni Estado (se asume 'Pendiente' por defecto en la BD).
     *
     * @param fechaHora Fecha y hora del turno.
     * @param motivo Razón de la consulta.
     * @param paciente Objeto Paciente a vincular.
     * @param odontologo Objeto Odontologo que atenderá.
     */
    public Cita(LocalDateTime fechaHora, String motivo, Paciente paciente, Odontologo odontologo) {
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.paciente = paciente;
        this.odontologo = odontologo;
    }

    // Getters y Setters

    /**
     * @return El identificador único de la cita.
     */
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    /**
     * @return La fecha y hora exacta del turno (LocalDateTime).
     */
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    /**
     * @return El motivo de la consulta.
     */
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    /**
     * @return El estado actual de la cita (Ej: Pendiente, Atendida, Facturada).
     */
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    /**
     * @return El objeto Paciente asociado a esta cita.
     */
    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    /**
     * @return El objeto Odontólogo asociado a esta cita.
     */
    public Odontologo getOdontologo() { return odontologo; }
    public void setOdontologo(Odontologo odontologo) { this.odontologo = odontologo; }
}