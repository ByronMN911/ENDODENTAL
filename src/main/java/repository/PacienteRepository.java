package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Interfaz que define el contrato de Acceso a Datos (DAO) para la entidad Paciente.
 * Establece las operaciones necesarias para la gestión de la información de los pacientes
 * en la base de datos, incluyendo consultas, persistencia y manejo de estados lógicos (activo/inactivo).
 */

import models.Paciente;
import java.sql.SQLException;
import java.util.List;

public interface PacienteRepository {

    /**
     * Recupera el listado de todos los pacientes que se encuentran activos en el sistema.
     * Filtra los registros donde la columna 'estado' es 1.
     *
     * @return Una lista de objetos Paciente activos.
     * @throws SQLException Si ocurre un error de conexión o consulta en la base de datos.
     */
    List<Paciente> listar() throws SQLException;

    /**
     * Recupera el listado de pacientes que han sido desactivados o eliminados lógicamente.
     * Filtra los registros donde la columna 'estado' es 0 (Papelera de reciclaje).
     *
     * @return Una lista de objetos Paciente inactivos.
     * @throws SQLException Si ocurre un error SQL.
     */
    List<Paciente> listarInactivos() throws SQLException;

    /**
     * Busca un paciente específico por su identificador único (Primary Key).
     * Esencial para cargar los datos en el formulario de edición.
     *
     * @param id El ID único del paciente.
     * @return El objeto Paciente encontrado o null si no existe.
     * @throws SQLException Si ocurre un error SQL.
     */
    Paciente porId(int id) throws SQLException;

    /**
     * Busca un paciente por su número de cédula.
     * Este método es crítico para evitar duplicidad de registros y para el buscador rápido.
     *
     * @param cedula El número de cédula a buscar.
     * @return El objeto Paciente encontrado o null si no existe.
     * @throws SQLException Si ocurre un error SQL.
     */
    Paciente porCedula(String cedula) throws SQLException;

    /**
     * Persiste un paciente en la base de datos.
     * Este método maneja tanto la creación de nuevos registros (INSERT) como la
     * actualización de existentes (UPDATE), dependiendo del ID del objeto.
     *
     * @param paciente El objeto Paciente con los datos a guardar.
     * @throws SQLException Si ocurre un error al guardar (ej: restricción única violada).
     */
    void guardar(Paciente paciente) throws SQLException;

    /**
     * Realiza la eliminación lógica de un paciente.
     * No borra el registro físicamente, sino que actualiza su estado a 0 (Inactivo).
     *
     * @param id El ID del paciente a desactivar.
     * @throws SQLException Si ocurre un error al actualizar el estado.
     */
    void eliminar(int id) throws SQLException;

    /**
     * Restaura un paciente previamente eliminado.
     * Actualiza el estado del registro a 1 (Activo), haciéndolo visible nuevamente en las listas principales.
     *
     * @param id El ID del paciente a reactivar.
     * @throws SQLException Si ocurre un error al actualizar el estado.
     */
    void activar(int id) throws SQLException;
}