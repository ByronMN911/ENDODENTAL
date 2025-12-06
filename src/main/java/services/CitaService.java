package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio relacionada con las Citas.
 * Establece las operaciones necesarias para gestionar el ciclo de vida completo de una cita médica:
 * Agendamiento -> Atención (Odontólogo) -> Finalización/Facturación (Secretaria) o Cancelación.
 * Actúa como puente entre los controladores y la capa de persistencia.
 */

import models.Cita;
import java.util.List;
import java.util.Optional;

public interface CitaService {

    /**
     * Recupera el historial completo de citas registradas en el sistema.
     * Útil para reportes generales o auditoría.
     *
     * @return Una lista de todas las citas históricas ordenadas por fecha descendente.
     */
    List<Cita> listar();

    /**
     * Obtiene la agenda operativa del día actual.
     * Filtra las citas programadas para la fecha del servidor, mostrando
     * principalmente aquellas en estado 'Pendiente' o 'Atendida'.
     *
     * @return Una lista de citas para el día de hoy.
     */
    List<Cita> listarHoy();

    /**
     * Recupera la agenda filtrada por una fecha específica.
     * Permite a la secretaria o al doctor consultar la disponibilidad o carga de trabajo
     * de días futuros o pasados.
     *
     * @param fecha La fecha a consultar en formato String 'YYYY-MM-DD'.
     * @return Una lista de citas correspondientes a esa fecha.
     */
    List<Cita> listarPorFecha(String fecha);

    /**
     * Recupera el historial de citas que han sido canceladas.
     * Permite visualizar las horas que quedaron libres o gestionar reagendamientos.
     *
     * @return Una lista de citas con estado 'Cancelada'.
     */
    List<Cita> listarCanceladas();

    /**
     * Recupera las citas que ya han sido atendidas por el odontólogo pero aún no se facturan.
     * Este método es crítico para el módulo de Facturación, ya que llena el selector de
     * "Citas por Cobrar".
     *
     * @return Una lista de citas con estado 'Atendida'.
     */
    List<Cita> listarAtendidas();

    /**
     * Busca el historial de citas de un paciente específico mediante su cédula.
     * Fundamental para la atención al cliente cuando un paciente pregunta "¿Cuándo es mi cita?".
     *
     * @param cedula El número de cédula del paciente.
     * @return Una lista de citas asociadas a ese documento de identidad.
     */
    List<Cita> buscarPorCedula(String cedula);

    /**
     * Busca una cita específica por su identificador único.
     * Utilizado principalmente para cargar los datos en el formulario de edición o
     * para recuperar la información antes de generar una factura.
     *
     * @param id El ID de la cita.
     * @return Un {@link Optional} que contiene la cita si existe.
     */
    Optional<Cita> porId(int id);

    /**
     * Registra una nueva cita en la agenda.
     * Debe implementar validaciones de negocio críticas:
     * 1. No permitir citas en fechas pasadas (para nuevos registros).
     * 2. Verificar que el odontólogo no tenga otra cita ocupada en ese mismo horario.
     *
     * @param cita El objeto Cita con los datos del paciente, doctor, fecha y hora.
     */
    void agendarCita(Cita cita);

    /**
     * Cancela una cita programada.
     * Realiza un cambio de estado lógico a 'Cancelada' para liberar el horario del doctor,
     * manteniendo el registro histórico.
     *
     * @param id El ID de la cita a cancelar.
     */
    void cancelarCita(int id);

    /**
     * Marca una cita como 'Atendida' una vez que el odontólogo ha completado la consulta.
     * Este cambio de estado habilita la cita para ser procesada en el módulo de facturación.
     *
     * @param id El ID de la cita que se acaba de atender.
     */
    void finalizarCita(int id);

    /**
     * Método genérico para cambiar el estado de una cita.
     * Puede ser utilizado por procesos internos del sistema (ej: al facturar, cambiar a 'Facturada').
     *
     * @param id El ID de la cita.
     * @param estado El nuevo estado a asignar (ej: 'Facturada', 'No Asistio').
     */
    void cambiarEstado(int id, String estado);
}