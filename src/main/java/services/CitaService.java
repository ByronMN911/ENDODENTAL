package services;

import models.Cita;
import java.util.List;
import java.util.Optional;

public interface CitaService {
    List<Cita> listar();
    List<Cita> listarHoy();
    Optional<Cita> porId(int id);
    void agendarCita(Cita cita);
}