package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para el proceso de Autenticación (Login).
 * Esta clase es responsable de orquestar la validación de credenciales, asegurando que:
 * 1. El usuario exista en la base de datos.
 * 2. El usuario tenga permiso de acceso (Estado Activo).
 * 3. La contraseña ingresada coincida criptográficamente con la almacenada.
 */

import models.Usuario;
import repository.UsuarioRepository;
import repository.UsuarioRepositoryImpl;
import util.PasswordUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class LoginServiceImpl implements LoginService {

    // Dependencia del repositorio para acceder a los datos del usuario
    private UsuarioRepository usuarioRepository;

    /**
     * Constructor que inicializa el servicio.
     * Inyecta la conexión a la base de datos en el repositorio correspondiente.
     *
     * @param conexion La conexión JDBC activa para esta petición.
     */
    public LoginServiceImpl(Connection conexion) {
        this.usuarioRepository = new UsuarioRepositoryImpl(conexion);
    }

    /**
     * Ejecuta el proceso de inicio de sesión.
     *
     * @param username El nombre de usuario proporcionado.
     * @param password La contraseña en texto plano proporcionada.
     * @return Un Optional con el Usuario si la autenticación es exitosa, o vacío si falla.
     * @throws ServiceJdbcException Si ocurre un error técnico en la base de datos.
     */
    @Override
    public Optional<Usuario> login(String username, String password) {
        try {
            // Logs de depuración para rastrear el intento de acceso (Solo para desarrollo)
            System.out.println("--- DEBUG LOGIN ---");
            System.out.println("Intento de: " + username);

            // PASO 1: Búsqueda del usuario
            // Consultamos a la base de datos si existe un registro con ese username.
            Usuario usuario = usuarioRepository.porUsername(username);

            // PASO 2: Validación de Existencia
            if (usuario != null) {
                System.out.println("Usuario encontrado en BD. ID: " + usuario.getIdUsuario());
                System.out.println("Estado: " + usuario.getEstado());

                // PASO 3: Validación de Estado (Regla de Negocio)
                // Si el usuario fue eliminado lógicamente (estado = 0), no permitimos el acceso
                // aunque la contraseña sea correcta. Esto es vital para la seguridad post-despido.
                if (usuario.getEstado() == 0) {
                    System.out.println("RECHAZADO: Usuario inactivo.");
                    // Retornamos vacío. Por seguridad, no especificamos si el fallo es por
                    // contraseña o por estado inactivo en el mensaje final al usuario.
                    return Optional.empty();
                }

                // PASO 4: Validación de Credenciales (Criptografía)
                // La BD almacena el HASH de la contraseña (ej: SHA-256), no el texto plano.
                // Por lo tanto, debemos encriptar la contraseña que acaba de ingresar el usuario
                // y comparar HASH contra HASH.
                String passwordInputHash = PasswordUtil.encriptar(password);

                // DEBUG: Comparación visual de hashes (Eliminar en producción)
                // System.out.println("Hash BD:    " + usuario.getPassword());
                // System.out.println("Hash Input: " + passwordInputHash);

                if (usuario.getPassword().equals(passwordInputHash)) {
                    System.out.println("¡LOGIN EXITOSO!");
                    return Optional.of(usuario);
                } else {
                    System.out.println("RECHAZADO: Contraseña incorrecta (Hash no coincide).");
                }
            } else {
                System.out.println("RECHAZADO: Usuario no encontrado en BD (Posible fallo en JOIN con roles).");
            }

            // Si llegamos aquí, alguna validación falló.
            return Optional.empty();

        } catch (SQLException e) {
            e.printStackTrace();
            // Relanzamos la excepción técnica como una de negocio para el Servlet
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }
}