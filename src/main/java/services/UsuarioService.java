package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio relacionada con los Usuarios.
 * Establece las operaciones disponibles para gestionar el ciclo de vida de los usuarios
 * (Administradores, Secretarias, Odontólogos) en el sistema, asegurando el desacoplamiento
 * entre el controlador y la implementación concreta.
 */

import models.Usuario;
import models.Odontologo;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    /**
     * Recupera el listado completo de usuarios que se encuentran activos en el sistema.
     * Útil para poblar tablas de gestión en el dashboard.
     *
     * @return Una lista de objetos {@link Usuario} con estado activo (1).
     */
    List<Usuario> listar();

    /**
     * Recupera el listado de usuarios que han sido desactivados lógicamente.
     * Permite visualizar la "Papelera de Reciclaje" de personal.
     *
     * @return Una lista de objetos {@link Usuario} con estado inactivo (0).
     */
    List<Usuario> listarInactivos();

    /**
     * Busca un usuario específico por su identificador único (Primary Key).
     *
     * @param id El ID del usuario a buscar.
     * @return Un {@link Optional} que contiene el usuario si es encontrado, o vacío si no existe.
     */
    Optional<Usuario> porId(int id);

    /**
     * Operación transaccional para registrar o actualizar un usuario.
     * Maneja la lógica compleja de guardar la información base del usuario y,
     * si el rol es 'Odontólogo', guarda también sus datos profesionales específicos.
     *
     * @param usuario El objeto con los datos de acceso y personales.
     * @param datosOdontologo Objeto con especialidad y código médico (puede ser null si no es doctor).
     */
    void guardarUsuario(Usuario usuario, Odontologo datosOdontologo);

    /**
     * Desactiva un usuario del sistema (Borrado Lógico).
     * El registro no se elimina físicamente de la base de datos para mantener
     * la integridad histórica de citas y auditoría.
     *
     * @param id El ID del usuario a desactivar.
     */
    void eliminar(int id);

    /**
     * Restaura el acceso a un usuario que estaba desactivado.
     *
     * @param id El ID del usuario a reactivar.
     */
    void activar(int id);
}