package models;

import java.time.LocalDateTime;

public class Cita {
    private int idCita;
    private LocalDateTime fechaHora;
    private String motivo;
    private String estado; // Pendiente, Atendida, etc.

    // Relaciones (Composición)
    private Paciente paciente;
    private Odontologo odontologo;

    public Cita() {
    }

    // 1. Constructor Completo (Lectura desde BDD)
    // Aquí SÍ necesitamos el estado para mostrarlo en el historial
    public Cita(int idCita, LocalDateTime fechaHora, String motivo, String estado, Paciente paciente, Odontologo odontologo) {
        this.idCita = idCita;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.estado = estado;
        this.paciente = paciente;
        this.odontologo = odontologo;
    }

    // 2. Constructor para NUEVA CITA (Agendar)
    // NO pedimos estado (la BDD pone 'Pendiente'). NO pedimos ID (es auto-increment).
    public Cita(LocalDateTime fechaHora, String motivo, Paciente paciente, Odontologo odontologo) {
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.paciente = paciente;
        this.odontologo = odontologo;
    }

    // Getters y Setters
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Odontologo getOdontologo() { return odontologo; }
    public void setOdontologo(Odontologo odontologo) { this.odontologo = odontologo; }
}