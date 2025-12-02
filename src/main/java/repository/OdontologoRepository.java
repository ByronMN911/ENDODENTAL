package repository;

import models.Odontologo;
import java.sql.SQLException;
import java.util.List;

public interface OdontologoRepository {
    List<Odontologo> listar() throws SQLException;
    Odontologo porId(int id) throws SQLException;
}