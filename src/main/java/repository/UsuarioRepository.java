package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato de Acceso a Datos (DAO) para la entidad Usuario.
 * Establece las operaciones necesarias para la gestión de seguridad y administración
 * del personal de la clínica (Administradores, Secretarias, Odontólogos).
 *
 * Sus funciones principales son permitir la autenticación (Login) y la gestión
 * del ciclo de vida de las cuentas de usuario.
 */

import models.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface UsuarioRepository {

    /**
     * Busca un usuario por su nombre de usuario (username).
     * Este método es CRÍTICO para el proceso de Login. Se utiliza para recuperar
     * la contraseña encriptada y el estado del usuario antes de validar el acceso.
     *
     * @param username El nombre de usuario ingresado en el login.
     * @return El objeto Usuario con sus datos y rol, o null si no existe.
     * @throws SQLException Si ocurre un error de conexión o consulta.
     */
    Usuario porUsername(String username) throws SQLException;

    // --- MÉTODOS DE GESTIÓN (ADMINISTRACIÓN) ---

    /**
     * Recupera el listado de todos los usuarios activos en el sistema.
     * Utilizado en el Dashboard del Administrador para ver el personal actual.
     * Filtra por 'estado = 1'.
     *
     * @return Lista de usuarios activos.
     * @throws SQLException Si ocurre un error SQL.
     */
    List<Usuario> listar() throws SQLException;

    /**
     * Recupera el listado de usuarios desactivados.
     * Permite al administrador ver quiénes han sido dados de baja (Papelera).
     * Filtra por 'estado = 0'.
     *
     * @return Lista de usuarios inactivos.
     * @throws SQLException Si ocurre un error SQL.
     */
    List<Usuario> listarInactivos() throws SQLException;

    /**
     * Busca un usuario por su identificador único (PK).
     * Necesario para cargar los datos en el formulario de edición de usuario.
     *
     * @param id El ID del usuario.
     * @return El objeto Usuario encontrado.
     * @throws SQLException Si ocurre un error SQL.
     */
    Usuario porId(int id) throws SQLException;

    /**
     * Persiste un usuario en la base de datos (Crear o Actualizar).
     *
     * IMPORTANTE: Este método debe retornar el ID generado (clave primaria)
     * después de una inserción. Esto es vital cuando se crea un 'Odontólogo',
     * ya que necesitamos el ID del nuevo usuario para crear inmediatamente
     * su registro correspondiente en la tabla 'odontologos'.
     *
     * @param usuario El objeto Usuario con los datos a guardar.
     * @return El ID (int) del usuario guardado o actualizado.
     * @throws SQLException Si ocurre un error (ej: username duplicado).
     */
    int guardar(Usuario usuario) throws SQLException;

    /**
     * Realiza la baja lógica de un usuario (Soft Delete).
     * Cambia el estado a 0, impidiendo que el usuario inicie sesión en el futuro,
     * pero manteniendo su historial de acciones en el sistema.
     *
     * @param id El ID del usuario a desactivar.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    void eliminar(int id) throws SQLException;

    /**
     * Reactiva un usuario previamente desactivado.
     * Restaura el acceso al sistema permitiendo el login nuevamente.
     *
     * @param id El ID del usuario a activar.
     * @throws SQLException Si ocurre un error al actualizar.
     */
    void activar(int id) throws SQLException;
}