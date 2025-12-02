package services;

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

    private CitaRepositoryImpl repository; // Usamos Impl para acceder a métodos extra o defínelos en Interfaz

    public CitaServiceImpl(Connection conn) {
        this.repository = new CitaRepositoryImpl(conn);
    }

    public List<Cita> listar() {
        try { return repository.listar(); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    public List<Cita> listarHoy() {
        try {
            String hoy = LocalDate.now().toString();
            return repository.listarPorFecha(hoy);
        } catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    // NUEVOS MÉTODOS
    public List<Cita> buscarPorCedula(String cedula) {
        try { return repository.listarPorCedula(cedula); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    public List<Cita> listarAtendidas() {
        try { return repository.listarPorEstado("Atendida"); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    public Optional<Cita> porId(int id) {
        try { return Optional.ofNullable(repository.porId(id)); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    public void cambiarEstado(int id, String estado) {
        try { repository.actualizarEstado(id, estado); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    public void agendarCita(Cita cita) {
        try {
            // Validaciones
            // 1. Si es NUEVA cita (id=0), validamos fecha pasada. Si es edición, permitimos corregir motivo.
            if (cita.getIdCita() == 0 && cita.getFechaHora().isBefore(LocalDateTime.now())) {
                throw new ServiceJdbcException("No se pueden agendar citas en el pasado.");
            }

            // 2. Disponibilidad: Solo si cambiamos fecha/doctor verificamos choque.
            // (Para simplificar, verificamos siempre, pero excluimos la misma cita si es edición en el Repo es complejo,
            //  así que asumimos validación básica).
            if (cita.getIdCita() == 0) { // Solo validamos estricto al crear
                boolean ocupado = repository.existeCitaEnHorario(cita.getOdontologo().getIdOdontologo(), cita.getFechaHora());
                if (ocupado) {
                    throw new ServiceJdbcException("El odontólogo ya tiene una cita agendada en ese horario.");
                }
            }

            repository.guardar(cita);
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al guardar: " + e.getMessage(), e);
        }
    }
}