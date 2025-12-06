package services;

/*
 * Autor: Génesis Escobar
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio relacionada con los Servicios Médicos.
 * Establece las operaciones disponibles para gestionar el catálogo de servicios ofrecidos
 * por la clínica (ej: Consultas, Limpiezas, Cirugías), asegurando el desacoplamiento
 * entre el controlador y la implementación concreta.
 */

import models.Servicio;
import java.util.List;
import java.util.Optional;

public interface ServicioService {

    /**
     * Recupera el listado completo de servicios médicos disponibles en el sistema.
     * Útil para poblar selectores en la agenda, facturación o tablas de administración.
     *
     * @return Una lista de objetos {@link Servicio} con la información del catálogo.
     */
    List<Servicio> listar();

    /**
     * Busca un servicio específico por su identificador único (Primary Key).
     *
     * @param id El ID del servicio a buscar.
     * @return Un {@link Optional} que contiene el servicio si existe, o vacío si no.
     */
    Optional<Servicio> porId(int id);

    /**
     * Registra un nuevo servicio o actualiza los datos de uno existente.
     * La implementación debe determinar si es una inserción o actualización basada en el ID.
     *
     * @param servicio El objeto Servicio con los datos a persistir (nombre, precio, descripción).
     */
    void guardar(Servicio servicio);

    /**
     * Elimina un servicio del sistema.
     *
     * @param id El ID del servicio a eliminar.
     */
    void eliminar(int id);
}