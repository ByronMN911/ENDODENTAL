package models;
/*
* Autor: Byron Melo
* Fecha: 30-11-2025
* Versión: 1.0
* Descripción: Entidad que representa a un usuario del sistema (Admin, Odontólogo, Secretaria).
Espejo de la tabla 'usuarios' en la base de datos.
 */

public class Usuario {

    private int idUsuario;
    private String username;
    private String password;
    private String nombreCompleto;
    private String email;
    // usamos el Objeto Rol
    private Rol rol;
    private int estado;

    public Usuario() {
    }

    // Constructor completo con Objeto Rol
    public Usuario(int idUsuario, String username, String password, String nombreCompleto, String email, Rol rol, int estado) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
    }

    // Constructor sin ID (para registros nuevos)
    public Usuario(String username, String password, String nombreCompleto, String email, Rol rol, int estado) {
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
    }

    // Getters y Setters Actualizados
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Getter y Setter para el OBJETO Rol
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}