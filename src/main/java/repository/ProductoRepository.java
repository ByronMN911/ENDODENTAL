package repository;

import models.Producto;
import java.sql.SQLException;
import java.util.List;

public interface ProductoRepository {
    List<Producto> listar() throws SQLException;
    // Agregamos estos métodos a la interfaz para no tener que castear después
    List<Producto> listarInactivos() throws SQLException;
    Producto porId(int id) throws SQLException;
    void guardar(Producto producto) throws SQLException;
    void eliminar(int id) throws SQLException;
    void activar(int id) throws SQLException;
}
