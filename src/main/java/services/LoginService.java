package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio relacionada con la Autenticación.
 * Establece el método único necesario para validar las credenciales de un usuario
 * (Administrador, Secretaria u Odontólogo) y permitir su ingreso al sistema.
 * Desacopla el controlador (LoginServlet) de la implementación concreta de la validación.
 */

import models.Usuario;
import java.util.Optional;

public interface LoginService {

    /**
     * Valida las credenciales de un usuario para permitir el acceso al sistema.
     *
     * Este método debe encargarse de:
     * 1. Buscar al usuario por su nombre de usuario (username).
     * 2. Verificar que el usuario esté activo.
     * 3. Comparar la contraseña ingresada (texto plano) con la contraseña almacenada (hash).
     *
     * @param username El nombre de usuario ingresado en el formulario de login.
     * @param password La contraseña en texto plano ingresada por el usuario.
     * @return Un {@link Optional} que contiene el objeto {@link Usuario} completo (con rol) si las credenciales son válidas,
     * o un Optional vacío (Optional.empty()) si la autenticación falla.
     * El uso de Optional evita retornar 'null' y previene NullPointerExceptions en el controlador.
     */
    Optional<Usuario> login(String username, String password);
}