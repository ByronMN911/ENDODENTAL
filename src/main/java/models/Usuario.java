package models;

/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa a un usuario del sistema (personal de la clínica).
 * Actúa como espejo de la tabla 'usuarios' de la base de datos.
 * Esta clase es fundamental para la autenticación y la gestión de permisos (Roles).
 *
 * Arquitectura:
 * Implementa composición al contener el objeto 'Rol' asociado.
 */

public class Usuario {

    // Identificador único del usuario (Primary Key)
    private int idUsuario;
    // Nombre de usuario utilizado para el inicio de sesión
    private String username;
    // Contraseña encriptada (hash) almacenada en la base de datos
    private String password;
    // Nombre completo legible del empleado
    private String nombreCompleto;
    // Correo electrónico de contacto
    private String email;
    // Relación de Composición: Objeto Rol que contiene el ID y el nombre del rol del usuario
    private Rol rol;
    // Estado de actividad (1: Activo, 0: Inactivo/Desactivado)
    private int estado;

    /**
     * Constructor por defecto, requerido por frameworks y la capa de mapeo (JDBC).
     */
    public Usuario() {
    }

    /**
     * Constructor completo para inicializar el objeto (Utilizado para mapear la lectura desde la BD).
     *
     * @param idUsuario ID del usuario.
     * @param username Nombre de usuario.
     * @param password Contraseña encriptada.
     * @param nombreCompleto Nombre completo.
     * @param email Correo electrónico.
     * @param rol Objeto Rol asociado.
     * @param estado Estado de actividad.
     */
    public Usuario(int idUsuario, String username, String password, String nombreCompleto, String email, Rol rol, int estado) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.password = password;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
    }

    /**
     * Constructor para nuevos registros (No incluye ID, generado por la BD).
     *
     * @param username Nombre de usuario.
     * @param password Contraseña en texto plano (será encriptada por el Service).
     * @param nombreCompleto Nombre completo.
     * @param email Correo electrónico.
     * @param rol Objeto Rol asociado.
     * @param estado Estado de actividad (normalmente 1).
     */
    public Usuario(String username, String password, String nombreCompleto, String email, Rol rol, int estado) {
        this.username = username;
        this.password = password; // Se asume encriptación en la capa Service
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.estado = estado;
    }

    // Getters y Setters Actualizados

    /**
     * @return El identificador único del usuario.
     */
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    /**
     * @return El nombre de usuario (para login).
     */
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    /**
     * @return La contraseña (almacenada como hash SHA-256).
     */
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    /**
     * @return El nombre completo del empleado.
     */
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    /**
     * @return El correo electrónico del usuario.
     */
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    /**
     * @return El objeto Rol asociado al usuario (permisos).
     */
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    /**
     * @return El estado de actividad (1: Activo, 0: Inactivo).
     */
    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}