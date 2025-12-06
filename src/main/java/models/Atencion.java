package models;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 1.0
 * Descripción:
 * Entidad que representa el registro de la Atención Médica realizada por el odontólogo.
 * Almacena los hallazgos clínicos (diagnóstico) y los procedimientos realizados (tratamiento)
 * asociados a un turno específico (Cita). Es la hoja de evolución del expediente.
 */

public class Atencion {
    // Identificador único de la atención médica (Primary Key)
    private int idAtencion;

    // Hallazgo clínico principal (Ej: Caries profunda, Gingivitis).
    private String diagnostico;

    // Descripción de los procedimientos realizados (Ej: Resina compuesta, Profilaxis).
    private String tratamientoRealizado;

    // Notas adicionales, indicaciones o medicación recetada.
    private String notasAdicionales;

    // Relación de Composición: Vincula esta atención con el objeto Cita al que pertenece.
    private Cita cita;

    /**
     * Constructor por defecto requerido por frameworks y utilidades de Java.
     */
    public Atencion() {
    }

    /**
     * Constructor completo para inicializar el objeto desde la base de datos o lógica de negocio.
     * * @param idAtencion Identificador único de la atención.
     * @param diagnostico Detalle del diagnóstico.
     * @param tratamientoRealizado Procedimiento realizado.
     * @param notasAdicionales Notas u observaciones adicionales.
     * @param cita Objeto Cita asociado a esta atención.
     */
    public Atencion(int idAtencion, String diagnostico, String tratamientoRealizado, String notasAdicionales, Cita cita) {
        this.idAtencion = idAtencion;
        this.diagnostico = diagnostico;
        this.tratamientoRealizado = tratamientoRealizado;
        this.notasAdicionales = notasAdicionales;
        this.cita = cita;
    }

    // Getters y Setters

    /**
     * @return El identificador único de la atención.
     */
    public int getIdAtencion() { return idAtencion; }

    /**
     * @param idAtencion El nuevo ID de la atención.
     */
    public void setIdAtencion(int idAtencion) { this.idAtencion = idAtencion; }

    /**
     * @return El diagnóstico clínico registrado.
     */
    public String getDiagnostico() { return diagnostico; }

    /**
     * @param diagnostico El nuevo diagnóstico.
     */
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    /**
     * @return El tratamiento realizado.
     */
    public String getTratamientoRealizado() { return tratamientoRealizado; }

    /**
     * @param tratamientoRealizado El nuevo tratamiento.
     */
    public void setTratamientoRealizado(String tratamientoRealizado) { this.tratamientoRealizado = tratamientoRealizado; }

    /**
     * @return Las notas adicionales de la atención.
     */
    public String getNotasAdicionales() { return notasAdicionales; }

    /**
     * @param notasAdicionales Las nuevas notas.
     */
    public void setNotasAdicionales(String notasAdicionales) { this.notasAdicionales = notasAdicionales; }

    /**
     * @return El objeto Cita vinculado a esta atención (contiene idCita).
     */
    public Cita getCita() { return cita; }

    /**
     * @param cita El objeto Cita para vincular la atención.
     */
    public void setCita(Cita cita) { this.cita = cita; }
}