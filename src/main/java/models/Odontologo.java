package models;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa el perfil profesional de un Odontólogo.
 * Esta clase almacena los datos específicos necesarios para el ejercicio médico
 * que no están incluidos en la entidad base 'Usuario', como su especialidad y código de licencia.
 *
 * Arquitectura:
 * Implementa el patrón de Composición para vincularse con la entidad Usuario,
 * permitiendo acceder a los datos de autenticación y nombre del doctor.
 */
import java.time.LocalDateTime;

public class Odontologo {
    // Identificador único del perfil del odontólogo (Primary Key)
    private int idOdontologo;
    // Campo que describe la especialidad del doctor (Ej: Ortodoncia, Endodoncia)
    private String especialidad;
    // Número de licencia o registro profesional
    private String codigoMedico;

    // COMPOSICIÓN: Un odontólogo TIENE UN Usuario asociado (datos de login y nombre)
    private Usuario usuario;

    /**
     * Constructor por defecto requerido por frameworks y la capa de mapeo.
     */
    public Odontologo() {
    }

    /**
     * Constructor Completo (Utilizado para mapear datos leídos de la BD).
     *
     * @param idOdontologo Identificador del perfil profesional.
     * @param especialidad Especialidad médica del doctor.
     * @param codigoMedico Número de licencia profesional.
     * @param usuario Objeto Usuario que contiene los datos de acceso (asociación 1:1).
     */
    public Odontologo(int idOdontologo, String especialidad, String codigoMedico, Usuario usuario) {
        this.idOdontologo = idOdontologo;
        this.especialidad = especialidad;
        this.codigoMedico = codigoMedico;
        this.usuario = usuario;
    }

    /**
     * Constructor simple (Utilizado para enviar solo el ID al guardar citas).
     * Al agendar, solo necesitamos el ID del doctor.
     *
     * @param idOdontologo El ID del doctor.
     */
    public Odontologo(int idOdontologo) {
        this.idOdontologo = idOdontologo;
    }

    // Getters y Setters

    /**
     * @return El identificador único del odontólogo.
     */
    public int getIdOdontologo() { return idOdontologo; }
    public void setIdOdontologo(int idOdontologo) { this.idOdontologo = idOdontologo; }

    /**
     * @return La especialidad del doctor.
     */
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    /**
     * @return El código de licencia médica.
     */
    public String getCodigoMedico() { return codigoMedico; }
    public void setCodigoMedico(String codigoMedico) { this.codigoMedico = codigoMedico; }

    /**
     * @return El objeto Usuario asociado (para obtener el nombre y credenciales).
     */
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}