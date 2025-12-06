package models;

/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa los roles de usuario dentro del sistema (RBAC - Role-Based Access Control).
 * Esta clase es esencial para la seguridad, ya que determina los permisos de acceso y
 * la navegación de cada usuario (Administrador, Odontólogo, Secretaria).
 * * Arquitectura:
 * Actúa como una tabla de catálogo (lookup table) para la entidad Usuario.
 */
public class Rol {
    // Identificador único del rol (Primary Key en la tabla 'roles').
    private int idRol;

    // Nombre descriptivo del rol (Ej: "Administrador", "Secretaria").
    private String nombreRol;

    /**
     * Constructor por defecto, esencial para la capa de mapeo de datos (JDBC).
     */
    public Rol() {
    }

    /**
     * Constructor completo para inicializar el objeto.
     * * @param idRol Identificador único del rol.
     * @param nombreRol Nombre asignado al rol.
     */
    public Rol(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    // Getters y Setters

    /**
     * @return El identificador único del rol (ID).
     */
    public int getIdRol() { return idRol; }

    /**
     * @param idRol El nuevo identificador del rol.
     */
    public void setIdRol(int idRol) { this.idRol = idRol; }

    /**
     * @return El nombre descriptivo del rol.
     */
    public String getNombreRol() { return nombreRol; }

    /**
     * @param nombreRol El nuevo nombre descriptivo del rol.
     */
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}