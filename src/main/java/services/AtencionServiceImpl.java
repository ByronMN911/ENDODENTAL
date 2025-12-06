package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Servicio encargado de la lógica de negocio para la Atención Médica.
 * Gestiona el registro del diagnóstico y tratamiento realizado por el odontólogo.
 * Su función principal es cerrar el ciclo clínico y abrir el ciclo administrativo (Facturación)
 * mediante la actualización de estados.
 */

import models.Atencion;
import repository.AtencionRepositoryImpl;
import repository.CitaRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;

public class AtencionServiceImpl {

    // Repositorio para persistir los detalles médicos (diagnóstico, tratamiento)
    private AtencionRepositoryImpl atencionRepo;
    // Repositorio de citas para actualizar el estado del turno
    private CitaRepositoryImpl citaRepo;

    /**
     * Constructor que inyecta la conexión a la base de datos.
     * Inicializa los repositorios necesarios para guardar la atención y actualizar la cita
     * dentro de la misma transacción lógica.
     *
     * @param conn La conexión JDBC activa.
     */
    public AtencionServiceImpl(Connection conn) {
        this.atencionRepo = new AtencionRepositoryImpl(conn);
        this.citaRepo = new CitaRepositoryImpl(conn);
    }

    /**
     * Registra una nueva atención médica y actualiza el estado de la cita.
     * Este método es ejecutado cuando el odontólogo hace clic en "Finalizar Consulta".
     *
     * Flujo de Datos:
     * 1. Se guarda el detalle clínico (Diagnóstico, Tratamiento) en la tabla 'atenciones'.
     * 2. Se actualiza el estado de la cita en la tabla 'citas' a 'Atendida'.
     *
     * Impacto en el Sistema:
     * - La cita desaparece de la lista de pendientes del doctor.
     * - La cita aparece automáticamente en la lista de "Pendientes de Facturación" de la secretaría.
     *
     * @param atencion El objeto con los datos del diagnóstico y tratamiento.
     * @throws ServiceJdbcException Si ocurre un error en la persistencia de datos.
     */
    public void registrarAtencion(Atencion atencion) {
        try {
            // 1. Guardar la hoja de atención clínica
            atencionRepo.guardar(atencion);

            // 2. CAMBIO DE ESTADO (Regla de Negocio Crítica):
            // De 'Pendiente' a 'Atendida'.
            // Este cambio de estado es el gatillo que habilita el proceso de cobro.
            citaRepo.actualizarEstado(atencion.getCita().getIdCita(), "Atendida");

        } catch (SQLException e) {
            // Captura de errores SQL y re-lanzamiento como excepción de servicio controlada
            throw new ServiceJdbcException("Error al registrar atención: " + e.getMessage(), e);
        }
    }
}