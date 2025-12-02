package services;


import models.Odontologo;
import java.util.List;
import java.util.Optional;

public interface OdontologoService {
    List<Odontologo> listar();
    Optional<Odontologo> porId(int id);
}