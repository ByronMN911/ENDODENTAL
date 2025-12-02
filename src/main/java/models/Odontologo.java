package models;

public class Odontologo {
    private int idOdontologo;
    private String especialidad;
    private String codigoMedico;

    // COMPOSICIÓN: Un odontólogo ES un usuario, así accedemos a su nombre.
    private Usuario usuario;

    public Odontologo() {
    }

    // Constructor Completo (Lectura desde BDD)
    public Odontologo(int idOdontologo, String especialidad, String codigoMedico, Usuario usuario) {
        this.idOdontologo = idOdontologo;
        this.especialidad = especialidad;
        this.codigoMedico = codigoMedico;
        this.usuario = usuario;
    }

    // Constructor simple (Solo ID, útil para cuando guardamos una cita y solo tenemos el ID del select)
    public Odontologo(int idOdontologo) {
        this.idOdontologo = idOdontologo;
    }

    // Getters y Setters
    public int getIdOdontologo() { return idOdontologo; }
    public void setIdOdontologo(int idOdontologo) { this.idOdontologo = idOdontologo; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getCodigoMedico() { return codigoMedico; }
    public void setCodigoMedico(String codigoMedico) { this.codigoMedico = codigoMedico; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}