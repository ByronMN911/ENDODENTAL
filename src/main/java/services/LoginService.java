package services;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción: Entidad que representa los roles del sistema identificados por una ID y mostrando
 * el valor que puede ser: administrador, odontólogo o secretaria.
 */
import models.Usuario;

import java.util.Optional;

/*
 * Interfaz que define la lógica de negocio para la autenticación.
 * Usamos Optional<Usuario> para evitar retornar 'null' y causar NullPointerExceptions.
 */
public interface LoginService {
    Optional<Usuario> login(String username, String password);
}
