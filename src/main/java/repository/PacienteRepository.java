package repository;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción:
 */
import models.Paciente;
import java.sql.SQLException;
import java.util.List;

public interface PacienteRepository {
    List<Paciente> listar() throws SQLException;
    List<Paciente> listarInactivos() throws SQLException;
    Paciente porId(int id) throws SQLException;
    Paciente porCedula(String cedula) throws SQLException;
    void guardar(Paciente paciente) throws SQLException; // Sirve para Crear y Editar
    void eliminar(int id) throws SQLException;
    void activar(int id) throws SQLException;
}