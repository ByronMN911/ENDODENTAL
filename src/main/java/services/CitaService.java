package services;

import models.Cita;
import java.util.List;
import java.util.Optional;

public interface CitaService {
    List<Cita> listar();
    List<Cita> listarHoy();
    List<Cita> listarPorFecha(String fecha);
    List<Cita> listarCanceladas();
    List<Cita> listarAtendidas(); // Asegúrate de tener este también para el historial
    List<Cita> buscarPorCedula(String cedula); // Y este para la búsqueda
    Optional<Cita> porId(int id);
    void agendarCita(Cita cita);
    void cancelarCita(int id);
    void finalizarCita(int id);
    void cambiarEstado(int id, String estado);
}