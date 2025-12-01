package services;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción:
 */
import models.Paciente;
import repository.PacienteRepository;
import repository.PacienteRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PacienteServiceImpl implements PacienteService {

    private PacienteRepository repository;

    public PacienteServiceImpl(Connection conn) {
        // Casteo necesario si tu variable repository es de la Interfaz pero instancias la Impl
        this.repository = new PacienteRepositoryImpl(conn);
    }

    @Override
    public List<Paciente> listar() {
        try {
            return repository.listar();
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }


    @Override
    public List<Paciente> listarInactivos() {
        try {

            return  repository.listarInactivos();
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Optional<Paciente> porId(int id) {
        try {
            return Optional.ofNullable(repository.porId(id));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Optional<Paciente> porCedula(String cedula) {
        try {
            return Optional.ofNullable(repository.porCedula(cedula));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void guardar(Paciente paciente) {
        try {
            // Validación de Cédula duplicada
            Paciente pacienteExistente = repository.porCedula(paciente.getCedula());

            if (pacienteExistente != null) {
                if (paciente.getIdPaciente() == 0) {
                    // Si existe y estamos creando uno nuevo
                    throw new ServiceJdbcException("La cédula " + paciente.getCedula() + " ya está registrada.");
                } else {
                    // Si existe y estamos editando (verificamos que no sea el mismo ID)
                    if (pacienteExistente.getIdPaciente() != paciente.getIdPaciente()) {
                        throw new ServiceJdbcException("La cédula " + paciente.getCedula() + " ya pertenece a otro paciente.");
                    }
                }
            }
            repository.guardar(paciente);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void eliminar(int id) {
        try {
            repository.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void activar(int id) {
        try {
            repository.activar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }
}