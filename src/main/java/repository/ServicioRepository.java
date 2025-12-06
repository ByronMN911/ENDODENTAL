package repository;

/*
 * Autor: Génesis Escobar
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato de Acceso a Datos (DAO) para la entidad Servicio.
 * Esta interfaz establece las operaciones necesarias para administrar el catálogo de
 * servicios médicos que ofrece la clínica (ej: Consultas, Limpiezas, Cirugías).
 *
 * Diferencia clave con Productos: Los servicios son intangibles y no manejan stock.
 */

import models.Servicio;
import java.sql.SQLException;
import java.util.List;

public interface ServicioRepository {

    /**
     * Recupera el listado completo de servicios médicos disponibles.
     * Este método se utiliza para poblar los selectores en el módulo de facturación
     * y para mostrar la lista de precios en el administrador.
     *
     * @return Una lista de objetos {@link Servicio} ordenada alfabéticamente.
     * @throws SQLException Si ocurre un error de conexión o consulta.
     */
    List<Servicio> listar() throws SQLException;

    /**
     * Busca un servicio específico por su identificador único (Primary Key).
     * Utilizado para recuperar los detalles (precio, descripción) de un servicio
     * al momento de agregarlo a una factura o editarlo.
     *
     * @param id El ID del servicio a buscar.
     * @return El objeto Servicio encontrado o null si no existe.
     * @throws SQLException Si ocurre un error técnico en la base de datos.
     */
    Servicio porId(int id) throws SQLException;

    /**
     * Persiste un servicio en la base de datos.
     * Este método debe ser capaz de manejar tanto la creación de nuevos servicios (INSERT)
     * como la actualización de la información de servicios existentes (UPDATE),
     * como cambios de precio o descripción.
     *
     * @param servicio El objeto Servicio con los datos a guardar.
     * @throws SQLException Si ocurre un error al guardar (ej: nombre duplicado).
     */
    void guardar(Servicio servicio) throws SQLException;

    /**
     * Elimina un servicio del catálogo.
     * Dependiendo de la implementación, esto puede ser una eliminación física o lógica.
     *
     * @param id El ID del servicio a eliminar.
     * @throws SQLException Si ocurre un error (ej: restricción de integridad si ya ha sido facturado).
     */
    void eliminar(int id) throws SQLException;
}