package repository;

/*
 * Autor: Génesis Escobar
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Interfaz que define el contrato de Acceso a Datos (DAO) para la entidad Factura.
 * Establece las operaciones fundamentales para la persistencia y recuperación
 * de la información financiera (cabeceras de facturas).
 */

import models.Factura;
import java.sql.SQLException;
import java.util.List;

public interface FacturaRepository {

    /**
     * Recupera el listado histórico completo de todas las facturas emitidas.
     * Útil para mostrar el historial en el dashboard o reportes.
     *
     * @return Una lista de objetos Factura.
     * @throws SQLException Si ocurre un error de conexión o consulta en la base de datos.
     */
    List<Factura> listar() throws SQLException;

    /**
     * Busca una factura específica por su identificador único.
     * Utilizado para recuperar los datos de una factura antes de generar su PDF o ver detalles.
     *
     * @param id El ID de la factura a buscar.
     * @return El objeto Factura encontrado (puede ser null si no existe).
     * @throws SQLException Si ocurre un error SQL durante la búsqueda.
     */
    Factura porId(int id) throws SQLException;

    /**
     * Guarda una nueva factura en la base de datos.
     * Este método debe encargarse de insertar los datos de la cabecera (cliente, fecha, totales)
     * y retornar el ID generado automáticamente por la base de datos para poder
     * vincular los detalles (ítems) posteriormente.
     *
     * @param factura El objeto Factura con la información a persistir.
     * @return El ID (clave primaria) generado para la nueva factura.
     * @throws SQLException Si ocurre un error durante la inserción (ej: restricción de clave foránea).
     */
    int guardar(Factura factura) throws SQLException;
}