package services;

/*
 * Autor: Mathew Lara
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio del Inventario.
 * Establece las operaciones disponibles para gestionar los productos e insumos
 * de la clínica (ej: Materiales dentales, Kits de limpieza, Medicamentos de venta).
 * Permite el desacoplamiento entre los controladores web y la implementación concreta del servicio.
 */

import models.Producto;
import java.util.List;
import java.util.Optional;

public interface ProductoService {

    /**
     * Recupera el listado de todos los productos disponibles en el inventario.
     * Generalmente filtra solo aquellos cuyo estado es 'Activo'.
     *
     * @return Una lista de objetos {@link Producto} listos para ser mostrados o vendidos.
     */
    List<Producto> listar();

    /**
     * Recupera el listado de productos que han sido desactivados o archivados.
     * Permite gestionar la "Papelera de Reciclaje" del inventario.
     *
     * @return Una lista de objetos {@link Producto} con estado inactivo (0).
     */
    List<Producto> listarInactivos();

    /**
     * Busca un producto específico por su identificador único.
     *
     * @param id El ID del producto a buscar.
     * @return Un {@link Optional} que contiene el producto si se encuentra, o vacío si no existe.
     */
    Optional<Producto> porId(int id);

    /**
     * Registra un nuevo producto en el inventario o actualiza los datos de uno existente.
     * La implementación debe decidir si es una inserción o actualización (usualmente basado en si el ID > 0).
     *
     * @param producto El objeto Producto con la información a persistir (nombre, precio, stock, etc.).
     */
    void guardar(Producto producto);

    /**
     * Realiza la baja lógica de un producto.
     * No elimina el registro físicamente de la base de datos, sino que cambia su estado
     * para que no aparezca en las listas de venta o selección.
     *
     * @param id El ID del producto a desactivar.
     */
    void eliminar(int id);

    /**
     * Restaura un producto previamente eliminado (archivado).
     * Vuelve a cambiar su estado a activo para que esté disponible nuevamente.
     *
     * @param id El ID del producto a reactivar.
     */
    void activar(int id);
}