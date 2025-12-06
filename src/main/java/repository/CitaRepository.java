package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato de Acceso a Datos (DAO) para la entidad Cita.
 * Establece las operaciones CRUD y consultas especializadas necesarias para
 * gestionar la agenda, verificar disponibilidad y filtrar el historial médico.
 *
 * Esta interfaz desacopla la lógica de negocio (Service) de la tecnología
 * de persistencia (JDBC/SQL).
 */

import models.Cita;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository {

    /**
     * Recupera el listado histórico completo de todas las citas registradas.
     * Útil para auditoría o reportes generales.
     *
     * @return Lista de todas las citas ordenadas cronológicamente (descendente).
     * @throws SQLException Si ocurre un error en la consulta a la base de datos.
     */
    List<Cita> listar() throws SQLException;

    /**
     * Recupera las citas programadas para una fecha específica.
     * Utilizado principalmente para mostrar la "Agenda del Día".
     * Por defecto, suele filtrar citas activas (Pendientes/Atendidas).
     *
     * @param fecha La fecha a consultar en formato String (YYYY-MM-DD).
     * @return Lista de citas para ese día.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    List<Cita> listarPorFecha(String fecha) throws SQLException;

    /**
     * Método avanzado para filtrar citas por fecha y una lista de estados específicos.
     * Permite vistas personalizadas como "Solo Facturadas de Hoy" o "Solo Pendientes de Hoy".
     *
     * @param fecha La fecha a consultar.
     * @param estados Lista de estados permitidos (ej: ["Pendiente", "Atendida"]).
     * @return Lista de citas filtradas.
     * @throws SQLException Si ocurre un error SQL.
     */
    List<Cita> listarPorFechaYEstados(String fecha, List<String> estados) throws SQLException;

    /**
     * Busca citas asociadas a un paciente específico mediante su número de cédula.
     * Realiza una búsqueda parcial (LIKE) o exacta para facilitar la ubicación de turnos.
     *
     * @param cedula Número de identidad del paciente.
     * @return Lista de citas del paciente.
     * @throws SQLException Si ocurre un error SQL.
     */
    List<Cita> listarPorCedula(String cedula) throws SQLException;

    /**
     * Filtra citas globalmente por su estado actual.
     * Útil para reportes de "Citas Canceladas" o "Pendientes de Cobro".
     *
     * @param estado El estado a filtrar (ej: 'Atendida', 'Cancelada').
     * @return Lista de citas que coinciden con el estado.
     * @throws SQLException Si ocurre un error SQL.
     */
    List<Cita> listarPorEstado(String estado) throws SQLException;

    /**
     * Busca una cita específica por su ID único.
     * Esencial para cargar los datos en formularios de edición o procesos de facturación.
     *
     * @param id El identificador de la cita.
     * @return El objeto Cita encontrado o null si no existe.
     * @throws SQLException Si ocurre un error SQL.
     */
    Cita porId(int id) throws SQLException;

    /**
     * Persiste una cita en la base de datos.
     * Debe manejar tanto la creación de nuevos registros (INSERT) como la actualización
     * de existentes (UPDATE), dependiendo de si el objeto tiene un ID válido.
     *
     * @param cita El objeto con los datos de la cita (fecha, motivo, paciente, doctor).
     * @throws SQLException Si ocurre un error al guardar (ej: restricción de integridad).
     */
    void guardar(Cita cita) throws SQLException;

    /**
     * Verifica si un odontólogo ya tiene una cita asignada en un horario específico.
     * Regla de negocio crítica para evitar el doble agendamiento (citas superpuestas).
     *
     * @param idOdontologo ID del doctor.
     * @param fechaHora La fecha y hora exacta que se desea reservar.
     * @return true si el horario está ocupado, false si está libre.
     * @throws SQLException Si ocurre un error en la consulta de validación.
     */
    boolean existeCitaEnHorario(int idOdontologo, LocalDateTime fechaHora) throws SQLException;

    /**
     * Actualiza únicamente el estado de una cita existente.
     * Método optimizado para transiciones rápidas de ciclo de vida (ej: Cancelar, Finalizar).
     *
     * @param idCita ID de la cita a modificar.
     * @param nuevoEstado El nuevo estado a asignar (ej: 'Cancelada', 'Facturada').
     * @throws SQLException Si ocurre un error al actualizar.
     */
    void actualizarEstado(int idCita, String nuevoEstado) throws SQLException;

    /**
     * Recupera las citas pendientes asignadas a un doctor específico para una fecha dada.
     * Utilizado en el Dashboard del Odontólogo para mostrar su carga de trabajo diaria.
     *
     * @param idOdontologo ID del doctor logueado.
     * @param fecha La fecha de consulta.
     * @return Lista de citas pendientes para ese doctor en esa fecha.
     * @throws SQLException Si ocurre un error SQL.
     */
    List<Cita> listarPendientesPorDoctor(int idOdontologo, String fecha) throws SQLException;
}