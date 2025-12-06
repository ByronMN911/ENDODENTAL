package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para la gestión de Citas Médicas.
 * Esta clase es el corazón operativo de la agenda. Sus responsabilidades incluyen:
 * 1. Coordinar el flujo de estados de una cita (Pendiente -> Atendida -> Facturada).
 * 2. Aplicar reglas de negocio estrictas para evitar conflictos de horario (doble agendamiento).
 * 3. Proveer vistas filtradas específicas para cada rol (Secretaria, Odontólogo).
 * 4. Gestionar la persistencia a través del CitaRepository.
 */

import models.Cita;
import repository.CitaRepository;
import repository.CitaRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CitaServiceImpl implements CitaService {

    // Dependencia del repositorio para el acceso a datos
    // Se utiliza la implementación concreta para acceder a métodos especializados de filtrado
    private CitaRepositoryImpl repository;

    /**
     * Constructor que inicializa el servicio.
     * Inyecta la conexión JDBC para asegurar que todas las operaciones de este servicio
     * ocurran dentro del mismo contexto transaccional.
     *
     * @param conn La conexión a la base de datos.
     */
    public CitaServiceImpl(Connection conn) {
        this.repository = new CitaRepositoryImpl(conn);
    }

    // =========================================================================
    // MÉTODOS DE VISUALIZACIÓN PARA LA SECRETARIA (PESTAÑAS DE AGENDA)
    // =========================================================================

    /**
     * Recupera la "Agenda Operativa" para una fecha específica.
     * Muestra las citas que requieren acción inmediata o gestión.
     * Incluye estados:
     * - 'Pendiente': Citas por atender o confirmar.
     * - 'Atendida': Citas que ya pasaron por el doctor y esperan facturación.
     *
     * @param fecha La fecha de consulta (YYYY-MM-DD).
     * @return Lista de citas activas del día.
     */
    public List<Cita> listarAgendaPorFecha(String fecha) {
        try {
            return repository.listarPorFechaYEstados(fecha, List.of("Pendiente", "Atendida"));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Recupera el historial de citas que ya han completado su ciclo (Facturadas).
     * Útil para auditoría de caja y revisión de ingresos del día.
     *
     * @param fecha La fecha de consulta.
     * @return Lista de citas con estado 'Facturada'.
     */
    public List<Cita> listarFacturadasPorFecha(String fecha) {
        try {
            return repository.listarPorFechaYEstados(fecha, List.of("Facturada"));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Recupera el historial de citas canceladas o fallidas.
     * Permite analizar el ausentismo o liberar cupos si se reactivan.
     *
     * @param fecha La fecha de consulta.
     * @return Lista de citas con estado 'Cancelada'.
     */
    public List<Cita> listarCanceladasPorFecha(String fecha) {
        try {
            return repository.listarPorFechaYEstados(fecha, List.of("Cancelada"));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    // =========================================================================
    // MÉTODOS ESTÁNDAR DE LA INTERFAZ
    // =========================================================================

    /**
     * Recupera todas las citas históricas del sistema.
     *
     * @return Lista completa de citas.
     */
    @Override
    public List<Cita> listar() {
        try {
            return repository.listar();
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Acceso directo a la agenda del día actual (Hoy).
     * Utiliza la fecha del servidor para filtrar.
     *
     * @return Lista de citas operativas de hoy.
     */
    @Override
    public List<Cita> listarHoy() {
        try {
            String hoy = LocalDate.now().toString();
            return repository.listarPorFecha(hoy);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Implementación del método genérico de listado por fecha.
     * Delega a la lógica por defecto del repositorio (generalmente Agenda Operativa).
     */
    @Override
    public List<Cita> listarPorFecha(String fecha) {
        try {
            return repository.listarPorFecha(fecha);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Busca el historial clínico/citas de un paciente por su cédula.
     *
     * @param cedula Número de identificación del paciente.
     * @return Lista de todas las citas asociadas a ese paciente.
     */
    @Override
    public List<Cita> buscarPorCedula(String cedula) {
        try {
            return repository.listarPorCedula(cedula);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Obtiene una lista global de todas las citas que están listas para cobro ('Atendida').
     * Nota: Este método busca en todo el histórico, no solo por fecha.
     */
    @Override
    public List<Cita> listarAtendidas() {
        try {
            return repository.listarPorEstado("Atendida");
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Obtiene una lista global de todas las citas canceladas.
     */
    @Override
    public List<Cita> listarCanceladas() {
        try {
            return repository.listarPorEstado("Cancelada");
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Busca una cita por su ID primario.
     * Esencial para cargar los datos en el formulario de edición.
     */
    @Override
    public Optional<Cita> porId(int id) {
        try {
            return Optional.ofNullable(repository.porId(id));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Actualiza manualmente el estado de una cita.
     *
     * @param id ID de la cita.
     * @param estado Nuevo estado (ej: 'Facturada').
     */
    @Override
    public void cambiarEstado(int id, String estado) {
        try {
            repository.actualizarEstado(id, estado);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Registra o actualiza una cita en la agenda.
     * Aplica validaciones críticas de negocio antes de persistir.
     *
     * Reglas:
     * 1. No se pueden crear citas nuevas en fechas/horas pasadas.
     * 2. (Validación de Disponibilidad) Un odontólogo no puede tener dos citas activas
     * en el mismo bloque horario.
     *
     * @param cita El objeto Cita con los datos del formulario.
     */
    @Override
    public void agendarCita(Cita cita) {
        try {
            // Validaciones exclusivas para NUEVAS citas (ID == 0)
            // Si es edición, permitimos guardar aunque sea fecha pasada (corrección de datos)
            // y asumimos que el usuario gestiona el conflicto de horario conscientemente.
            if (cita.getIdCita() == 0) {

                // Regla 1: Validar fecha futura
                if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
                    throw new ServiceJdbcException("No se pueden agendar citas en el pasado.");
                }

                // Regla 2: Validar disponibilidad del doctor
                boolean ocupado = repository.existeCitaEnHorario(
                        cita.getOdontologo().getIdOdontologo(),
                        cita.getFechaHora()
                );

                if (ocupado) {
                    throw new ServiceJdbcException("El odontólogo ya tiene una cita agendada en ese horario.");
                }
            }

            // Persistencia
            repository.guardar(cita);

        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al guardar la cita: " + e.getMessage(), e);
        }
    }

    /**
     * Cancela una cita.
     * Realiza un Soft Delete cambiando el estado a 'Cancelada'.
     * Esto libera el horario del doctor para nuevas citas.
     */
    @Override
    public void cancelarCita(int id) {
        try {
            repository.actualizarEstado(id, "Cancelada");
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al cancelar la cita: " + e.getMessage(), e);
        }
    }

    /**
     * Marca una cita como atendida por el doctor.
     * Este es el paso previo a la facturación.
     */
    @Override
    public void finalizarCita(int id) {
        try {
            repository.actualizarEstado(id, "Atendida");
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al finalizar la cita: " + e.getMessage(), e);
        }
    }

    // =========================================================================
    // MÉTODOS ESPECIALIZADOS PARA OTROS MÓDULOS
    // =========================================================================

    /**
     * Recupera las citas pendientes de cobro para el módulo de Facturación.
     * Filtra específicamente las que tienen estado 'Atendida'.
     *
     * @return Lista de citas listas para facturar.
     */
    public List<Cita> listarPendientesDeFacturacion() {
        try {
            return repository.listarPorEstado("Atendida");
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Recupera la lista de trabajo para el Dashboard del Odontólogo.
     * Filtra las citas que:
     * 1. Pertenecen al doctor logueado.
     * 2. Corresponden a la fecha seleccionada.
     * 3. Están en estado 'Pendiente' (aún no atendidas).
     *
     * @param idOdontologo ID del doctor.
     * @param fecha Fecha de consulta.
     * @return Lista de pacientes en espera.
     */
    public List<Cita> listarPendientesDeAtencion(int idOdontologo, String fecha) {
        try {
            return repository.listarPendientesPorDoctor(idOdontologo, fecha);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }
}