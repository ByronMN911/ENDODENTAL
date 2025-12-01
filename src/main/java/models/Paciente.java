package models;

import java.time.LocalDateTime;

public class Paciente {
    private int idPaciente;
    private String cedula;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String email;
    private String alergias;
    private LocalDateTime fechaRegistro;
    private int estado;

    public Paciente() {
    }

    // 1. Constructor completo (BDD)
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

    // 2. Constructor para Nuevo Paciente (Sin ID ni fechaRegistro ni estado)
    //  la BDD pone el estado en 1 por defecto
    public Paciente(String cedula, String nombres, String apellidos, String telefono, String email, String alergias) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.alergias = alergias;
    }

    // Getters y Setters

    // ... (Getters y Setters existentes) ...

    public int getIdPaciente() { return idPaciente; }
    public void setIdPaciente(int idPaciente) { this.idPaciente = idPaciente; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}