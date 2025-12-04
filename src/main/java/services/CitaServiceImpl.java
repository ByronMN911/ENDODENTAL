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

    private CitaRepositoryImpl repository;

    public CitaServiceImpl(Connection conn) {
        this.repository = new CitaRepositoryImpl(conn);
    }

    @Override
    public List<Cita> listar() {
        try { return repository.listar(); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    @Override
    public List<Cita> listarHoy() {
        try {
            String hoy = LocalDate.now().toString();
            return repository.listarPorFecha(hoy);
        } catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    // --- IMPLEMENTACIÓN DEL MÉTODO FALTANTE ---
    @Override
    public List<Cita> listarPorFecha(String fecha) {
        try {
            // Delegamos directamente al repositorio que ya tiene el SQL con el WHERE DATE(...)
            return repository.listarPorFecha(fecha);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }
    // ------------------------------------------

    @Override
    public List<Cita> buscarPorCedula(String cedula) {
        try { return repository.listarPorCedula(cedula); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    @Override
    public List<Cita> listarAtendidas() {
        try { return repository.listarPorEstado("Atendida"); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    @Override
    public List<Cita> listarCanceladas() {
        try {
            return repository.listarPorEstado("Cancelada");
        }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    @Override
    public Optional<Cita> porId(int id) {
        try { return Optional.ofNullable(repository.porId(id)); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    @Override
    public void cambiarEstado(int id, String estado) {
        try { repository.actualizarEstado(id, estado); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e); }
    }

    @Override
    public void agendarCita(Cita cita) {
        try {
            if (cita.getIdCita() == 0 && cita.getFechaHora().isBefore(LocalDateTime.now())) {
                throw new ServiceJdbcException("No se pueden agendar citas en el pasado.");
            }

            if (cita.getIdCita() == 0) {
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

    @Override
    public void cancelarCita(int id) {
        try {
            repository.actualizarEstado(id, "Cancelada");
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al cancelar la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public void finalizarCita(int id) {
        try {
            repository.actualizarEstado(id, "Atendida");
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al finalizar la cita: " + e.getMessage(), e);
        }
    }
}