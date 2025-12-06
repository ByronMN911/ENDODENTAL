package repository;

/*
 * Autor: Mathew Lara
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato de Acceso a Datos (DAO) para la entidad Producto.
 * Gestiona el inventario de la clínica, permitiendo realizar operaciones CRUD,
 * control de stock y manejo de estados (activo/inactivo) para insumos y productos de venta.
 */

import models.Producto;
import java.sql.SQLException;
import java.util.List;

public interface ProductoRepository {

    /**
     * Recupera el listado de productos que están activos y disponibles para la venta o uso.
     * Filtra los registros donde el estado es 1.
     *
     * @return Una lista de objetos Producto activos.
     * @throws SQLException Si ocurre un error en la consulta a la base de datos.
     */
    List<Producto> listar() throws SQLException;

    /**
     * Recupera el listado de productos que han sido desactivados o eliminados lógicamente.
     * Permite visualizar la "Papelera de Reciclaje" del inventario.
     *
     * @return Una lista de objetos Producto inactivos (estado 0).
     * @throws SQLException Si ocurre un error SQL.
     */
    List<Producto> listarInactivos() throws SQLException;

    /**
     * Busca un producto específico por su identificador único.
     *
     * @param id El ID del producto a buscar.
     * @return El objeto Producto encontrado o null si no existe.
     * @throws SQLException Si ocurre un error SQL.
     */
    Producto porId(int id) throws SQLException;

    /**
     * Persiste un producto en la base de datos.
     * Maneja tanto la inserción de nuevos productos como la actualización de existentes.
     *
     * @param producto El objeto Producto con la información a guardar.
     * @throws SQLException Si ocurre un error al guardar.
     */
    void guardar(Producto producto) throws SQLException;

    /**
     * Realiza la baja lógica de un producto (Soft Delete).
     * Cambia el estado del producto a 0 para ocultarlo de las listas principales
     * sin perder el historial de ventas.
     *
     * @param id El ID del producto a desactivar.
     * @throws SQLException Si ocurre un error al actualizar el estado.
     */
    void eliminar(int id) throws SQLException;

    /**
     * Restaura un producto previamente eliminado.
     * Cambia el estado del producto a 1 (Activo).
     *
     * @param id El ID del producto a reactivar.
     * @throws SQLException Si ocurre un error al actualizar el estado.
     */
    void activar(int id) throws SQLException;

    /**
     * Actualiza la cantidad de stock de un producto de forma atómica.
     * Este método es crítico para el módulo de facturación.
     * Permite restar stock (venta) o sumar stock (compra/reposición).
     *
     * @param id El ID del producto a modificar.
     * @param cantidad La cantidad a sumar (positivo) o restar (negativo).
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    void actualizarStock(int id, int cantidad) throws SQLException;
}