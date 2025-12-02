package repository;


import models.Cita;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaRepository {
    // Listar todas las citas (Histórico)
    List<Cita> listar() throws SQLException;

    // Listar citas de un día específico (Para el Dashboard)
    // Recibe fecha en formato String 'YYYY-MM-DD'
    List<Cita> listarPorFecha(String fecha) throws SQLException;

    // Guardar una nueva cita
    void guardar(Cita cita) throws SQLException;

    // Validar disponibilidad: ¿El doctor ya tiene cita a esa hora?
    boolean existeCitaEnHorario(int idOdontologo, LocalDateTime fechaHora) throws SQLException;
}