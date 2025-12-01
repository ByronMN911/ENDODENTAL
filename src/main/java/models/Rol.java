package models;
/*
* Autor: Byron Melo
* Fecha: 30-11-2025
* Versión: 1.0
* Descripción: Entidad que representa los roles del sistema identificados por una ID y mostrando
* el valor que puede ser: administrador, odontólogo o secretaria.
 */
public class Rol {
    private int idRol;
    private String nombreRol;

    public Rol() {
    }

    public Rol(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    // Getters y Setters
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}