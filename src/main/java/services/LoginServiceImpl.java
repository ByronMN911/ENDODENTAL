package services;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción:
 */
import models.Usuario;
import repository.UsuarioRepository;
import repository.UsuarioRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class LoginServiceImpl implements LoginService {

    private UsuarioRepository usuarioRepository;

    // Inyección de dependencias por constructor
    // El Service necesita al Repositorio para hablar con la BD
    public LoginServiceImpl(Connection conexion) {


        this.usuarioRepository = new UsuarioRepositoryImpl(conexion);
    }

    @Override
    public Optional<Usuario> login(String username, String password) {
        try {
            // Buscamos al usuario por su username
            // Usamos .porUsername del DAO que ya creamos
            Usuario usuario = usuarioRepository.porUsername(username);

            // 2. Validamos si existe y si la contraseña coincide
            if (usuario != null) {
                // COMPARACIÓN DE CONTRASEÑA
                // En producción usaríamos: BCrypt.checkpw(password, usuario.getPassword())
                if (usuario.getPassword().equals(password)) {
                    // Si todo es correcto, retornamos el usuario envuelto en Optional
                    return Optional.of(usuario);
                }
            }
            // Si el usuario no existe o la contraseña está mal, retornamos vacío.
            return Optional.empty();

        } catch (SQLException e) {
            // Convertimos la excepción técnica (SQL) en una de negocio (Nuestra excepción personalizada)
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }
}
