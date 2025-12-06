package services;

/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.1
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio relacionada con los Pacientes.
 * Establece las operaciones disponibles para gestionar el expediente clínico y administrativo
 * de los pacientes, asegurando el desacoplamiento entre los controladores web y la
 * implementación concreta del servicio.
 */

import models.Paciente;
import java.util.List;
import java.util.Optional;

public interface PacienteService {

    /**
     * Recupera el listado completo de pacientes activos en el sistema.
     * Útil para poblar la tabla principal de gestión en el dashboard de secretaría.
     *
     * @return Una lista de objetos {@link Paciente} cuyo estado es activo (1).
     */
    List<Paciente> listar();

    /**
     * Recupera el listado de pacientes que han sido desactivados o archivados.
     * Permite gestionar la "Papelera de Reciclaje" de pacientes para auditoría o restauración.
     *
     * @return Una lista de objetos {@link Paciente} con estado inactivo (0).
     */
    List<Paciente> listarInactivos();

    /**
     * Busca un paciente específico por su identificador único (Primary Key).
     *
     * @param id El ID del paciente a buscar.
     * @return Un {@link Optional} que contiene el paciente si existe, o vacío si no se encuentra.
     */
    Optional<Paciente> porId(int id);

    /**
     * Busca un paciente por su número de cédula de identidad.
     * Método crítico para evitar duplicidad de registros antes de crear uno nuevo.
     *
     * @param cedula El número de cédula a buscar (debe ser válido según el algoritmo).
     * @return Un {@link Optional} con el paciente si ya está registrado.
     */
    Optional<Paciente> porCedula(String cedula);

    /**
     * Registra un nuevo paciente o actualiza los datos de uno existente.
     * La implementación debe manejar validaciones de negocio como la unicidad de la cédula
     * y la validación del formato ecuatoriano.
     *
     * @param paciente El objeto Paciente con la información personal y de contacto.
     */
    void guardar(Paciente paciente);

    /**
     * Realiza la baja lógica de un paciente.
     * Cambia el estado del registro a inactivo (0) para ocultarlo de las listas principales
     * sin perder su historial clínico o financiero.
     *
     * @param id El ID del paciente a archivar.
     */
    void eliminar(int id);

    /**
     * Restaura un paciente previamente eliminado (archivado).
     * Vuelve a cambiar su estado a activo (1) para permitir nuevas citas o facturación.
     *
     * @param id El ID del paciente a reactivar.
     */
    void activar(int id);
}