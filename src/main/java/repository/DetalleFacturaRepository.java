package repository;

/*
 * Autor: Génesis Escobar
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Interfaz que define el contrato de Acceso a Datos (DAO) para los detalles de la factura.
 * Esta interfaz maneja la persistencia de los ítems individuales (servicios o productos)
 * que componen una factura.
 *
 * Su función principal es permitir el guardado en lote (batch) de los detalles y
 * la recuperación de los mismos para visualización o reimpresión de facturas.
 */

import models.DetalleFactura;
import java.sql.SQLException;
import java.util.List;

public interface DetalleFacturaRepository {

    /**
     * Guarda una lista completa de detalles asociados a una factura específica.
     * Este método suele implementarse utilizando procesamiento por lotes (Batch Processing)
     * para optimizar el rendimiento al insertar múltiples filas en una sola transacción.
     *
     * @param idFactura El ID de la factura padre a la que pertenecen estos detalles.
     * @param detalles La lista de objetos DetalleFactura que contienen la información del ítem (precio, cantidad, tipo).
     * @throws SQLException Si ocurre un error al intentar insertar los registros en la base de datos.
     */
    void guardar(int idFactura, List<DetalleFactura> detalles) throws SQLException;

    /**
     * Recupera todos los ítems (detalles) asociados a una factura.
     * Es fundamental para reconstruir la factura completa al momento de generar el PDF
     * o visualizar el historial.
     *
     * @param idFactura El ID de la factura de la cual se quieren obtener los detalles.
     * @return Una lista de objetos DetalleFactura poblados con la información de la base de datos.
     * @throws SQLException Si ocurre un error en la consulta SQL.
     */
    List<DetalleFactura> listarPorFactura(int idFactura) throws SQLException;
}