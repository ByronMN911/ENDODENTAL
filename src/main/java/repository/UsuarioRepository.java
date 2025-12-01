package repository;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción:
 */
import models.Usuario;
import java.sql.SQLException;

/*
 * Interfaz que define las operaciones disponibles para la entidad Usuario.
 * Esto nos permite desacoplar la implementación (JDBC) de la lógica de negocio.
 */
public interface UsuarioRepository {

    // Método vital para el Login: busca un usuario por su nombre de usuario
    Usuario porUsername(String username) throws SQLException;

    // Aquí podrías agregar más métodos a futuro, ej:
    // void guardar(Usuario usuario) throws SQLException;
    // Usuario porId(int id) throws SQLException;
}