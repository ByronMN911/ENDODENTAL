package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio relacionada con los Odontólogos.
 * Establece las operaciones disponibles para gestionar al personal médico de la clínica.
 * * Esta interfaz es fundamental para desacoplar la capa web (Servlets) de la capa de datos,
 * permitiendo realizar búsquedas especializadas necesarias para la gestión de citas y
 * la autenticación del personal médico.
 */

import models.Odontologo;
import java.util.List;
import java.util.Optional;

public interface OdontologoService {

    /**
     * Recupera el listado completo de odontólogos activos en el sistema.
     * Este método es utilizado principalmente para poblar los selectores (dropdowns)
     * en los formularios de agendamiento de citas, permitiendo a la secretaria elegir
     * con qué doctor se atenderá el paciente.
     *
     * @return Una lista de objetos {@link Odontologo} que incluye la información profesional y de usuario.
     */
    List<Odontologo> listar();

    /**
     * Busca un odontólogo específico por su identificador único de la tabla 'odontologos'.
     * Útil para operaciones puntuales donde se conoce el ID del profesional, por ejemplo,
     * al cargar los datos para editar una cita existente.
     *
     * @param id El ID primario del odontólogo.
     * @return Un {@link Optional} que contiene al odontólogo si existe, o vacío si no.
     */
    Optional<Odontologo> porId(int id);

    /**
     * Busca el perfil profesional de un odontólogo basándose en su ID de Usuario (Login).
     * * Este método es CRÍTICO para el módulo del Odontólogo:
     * Cuando un doctor inicia sesión, el sistema solo conoce su 'id_usuario'.
     * Este método permite "traducir" ese usuario genérico al perfil específico de Odontólogo,
     * obteniendo su 'id_odontologo' necesario para filtrar sus citas pendientes en el dashboard.
     *
     * @param idUsuario El ID de la tabla 'usuarios' asociado al doctor.
     * @return Un {@link Optional} con el perfil del odontólogo asociado a ese usuario.
     */
    Optional<Odontologo> porIdUsuario(int idUsuario);
}