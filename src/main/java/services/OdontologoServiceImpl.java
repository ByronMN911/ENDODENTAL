package services;


import models.Odontologo;
import repository.OdontologoRepository;
import repository.OdontologoRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OdontologoServiceImpl implements OdontologoService {

    private OdontologoRepository repository;

    public OdontologoServiceImpl(Connection conn) {
        this.repository = new OdontologoRepositoryImpl(conn);
    }

    @Override
    public List<Odontologo> listar() {
        try {
            return repository.listar();
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public Optional<Odontologo> porId(int id) {
        try {
            return Optional.ofNullable(repository.porId(id));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }
}