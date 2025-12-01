package services;

/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.1
 */
import models.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteService {
    List<Paciente> listar();

    // Para ver la papelera de reciclaje
    List<Paciente> listarInactivos();

    Optional<Paciente> porId(int id);
    Optional<Paciente> porCedula(String cedula);
    void guardar(Paciente paciente);
    void eliminar(int id);

    // para recuperar un paciente eliminado
    void activar(int id);
}