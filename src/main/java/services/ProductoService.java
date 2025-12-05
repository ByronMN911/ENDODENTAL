package services;

import models.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> listar();
    List<Producto> listarInactivos();
    Optional<Producto> porId(int id);
    void guardar(Producto producto);
    void eliminar(int id);
    void activar(int id);
}