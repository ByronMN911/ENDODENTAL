package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Interfaz que define el contrato de Acceso a Datos (DAO) para la entidad Odontólogo.
 * Esta interfaz establece las operaciones necesarias para recuperar la información
 * profesional del personal médico desde la base de datos.
 *
 * Es fundamental para vincular la información de autenticación (Usuario) con el perfil
 * médico (Odontólogo) y para poblar los selectores en la gestión de citas.
 */

import models.Odontologo;
import java.sql.SQLException;
import java.util.List;

public interface OdontologoRepository {

    /**
     * Recupera el listado completo de todos los odontólogos registrados y activos.
     * Utiliza una consulta con JOIN para traer también la información del usuario asociado
     * (nombre completo, email), lo cual es necesario para mostrar datos legibles en la interfaz.
     *
     * @return Una lista de objetos Odontologo con sus datos poblados.
     * @throws SQLException Si ocurre un error de conexión o en la ejecución de la consulta SQL.
     */
    List<Odontologo> listar() throws SQLException;

    /**
     * Busca un odontólogo específico por su identificador único (Primary Key).
     * Este método es útil cuando ya se conoce el ID del doctor, por ejemplo, al editar una cita
     * para pre-seleccionar el doctor en el formulario.
     *
     * @param id El ID único del odontólogo en la tabla 'odontologos'.
     * @return El objeto Odontologo encontrado o null si no existe.
     * @throws SQLException Si ocurre un error técnico en la base de datos.
     */
    Odontologo porId(int id) throws SQLException;

    /**
     * Busca el perfil de odontólogo asociado a un ID de Usuario específico.
     * Este método es CRÍTICO para el proceso de Login y Dashboard del doctor.
     * Permite determinar si el usuario que acaba de iniciar sesión es un médico y,
     * de ser así, recuperar su ID de doctor para filtrar sus citas pendientes.
     *
     * @param idUsuario El ID de la tabla 'usuarios' (FK en la tabla odontologos).
     * @return El objeto Odontologo asociado a ese usuario.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    Odontologo porIdUsuario(int idUsuario) throws SQLException;
}