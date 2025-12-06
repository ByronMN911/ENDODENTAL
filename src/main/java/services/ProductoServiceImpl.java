package services;

/*
 * Autor: Mathew Lara
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para la gestión del Inventario de Productos.
 * Esta clase actúa como intermediario entre el controlador (ProductoServlet) y la capa de datos (ProductoRepository).
 * Se encarga de coordinar las operaciones CRUD para los insumos y productos de venta (ej: Pastas, Cepillos, Anestesia),
 * gestionando también el ciclo de vida de los registros (activo/inactivo) y el manejo de errores.
 */

import models.Producto;
import repository.ProductoRepository;
import repository.ProductoRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoServiceImpl implements ProductoService {

    // Dependencia del repositorio para el acceso a la base de datos
    private ProductoRepository repository;

    /**
     * Constructor que inicializa el servicio inyectando la conexión a la base de datos.
     * Utiliza el patrón de inyección manual para instanciar la implementación concreta del repositorio.
     *
     * @param conn La conexión JDBC activa proveniente del filtro o controlador.
     */
    public ProductoServiceImpl(Connection conn) {
        this.repository = new ProductoRepositoryImpl(conn);
    }

    /**
     * Recupera el listado de productos activos en el inventario.
     *
     * @return Una lista de objetos {@link Producto} cuyo estado es activo (1).
     * @throws ServiceJdbcException Si ocurre un error de base de datos (SQL) durante la consulta.
     */
    @Override
    public List<Producto> listar() {
        try {
            return repository.listar();
        } catch (SQLException e) {
            // Capturamos la excepción técnica SQL y la re-lanzamos como una excepción de servicio
            // para mantener el desacoplamiento y un manejo de errores uniforme.
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Busca un producto específico por su identificador único.
     *
     * @param id El ID del producto a buscar.
     * @return Un {@link Optional} que contiene el producto si existe, o vacío si no.
     * @throws ServiceJdbcException Si ocurre un error en la consulta SQL.
     */
    @Override
    public Optional<Producto> porId(int id) {
        try {
            // Utilizamos Optional.ofNullable para manejar elegantemente el caso de que no se encuentre el producto
            return Optional.ofNullable(repository.porId(id));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Registra un nuevo producto o actualiza los datos de uno existente.
     * La lógica de determinar si es INSERT o UPDATE reside en el repositorio basándose en el ID.
     *
     * @param producto El objeto Producto con los datos a persistir (nombre, stock, precio, etc.).
     * @throws ServiceJdbcException Si ocurre un error al intentar guardar en la base de datos.
     */
    @Override
    public void guardar(Producto producto) {
        try {
            // Aquí se podrían agregar validaciones de negocio adicionales antes de guardar
            // Por ejemplo: validar que el stock no sea negativo o que el precio sea mayor a 0.
            repository.guardar(producto);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Realiza la baja lógica (desactivación) de un producto.
     * El producto deja de estar visible en las listas principales pero se conserva en la base de datos.
     *
     * @param id El ID del producto a archivar.
     * @throws ServiceJdbcException Si ocurre un error técnico durante la actualización del estado.
     */
    @Override
    public void eliminar(int id) {
        try {
            repository.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Recupera el listado de productos que han sido archivados (Papelera de reciclaje).
     * Útil para auditoría o para recuperar ítems eliminados accidentalmente.
     *
     * @return Una lista de objetos {@link Producto} con estado inactivo (0).
     */
    @Override
    public List<Producto> listarInactivos() {
        try {
            return repository.listarInactivos();
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Restaura un producto que estaba en la papelera, volviéndolo a poner disponible para la venta/uso.
     * Cambia el estado del registro de 0 a 1.
     *
     * @param id El ID del producto a reactivar.
     */
    @Override
    public void activar(int id) {
        try {
            repository.activar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }
}