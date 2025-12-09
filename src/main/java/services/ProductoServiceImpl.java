package services;

/*
 * Autor: Mathew Lara
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para la gestión del Inventario de Productos.
 * ...
 */

import models.Producto;
import repository.ProductoRepository;
import repository.ProductoRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoServiceImpl implements ProductoService {

    private ProductoRepository repository;

    public ProductoServiceImpl(Connection conn) {
        this.repository = new ProductoRepositoryImpl(conn);
    }

    // ... (listar, porId, guardar, eliminar, listarInactivos, activar se mantienen igual) ...

    @Override
    public List<Producto> listar() {
        try { return repository.listar(); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e.getCause()); }
    }

    @Override
    public Optional<Producto> porId(int id) {
        try { return Optional.ofNullable(repository.porId(id)); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e.getCause()); }
    }

    @Override
    public void guardar(Producto producto) {
        try {
            repository.guardar(producto);
        } catch (SQLException e) {
            // El catch aquí atrapará la excepción de stock insuficiente lanzada por el Repositorio
            throw new ServiceJdbcException("Error al guardar producto: " + e.getMessage(), e.getCause());
        }
    }

    @Override
    public void eliminar(int id) {
        try { repository.eliminar(id); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e.getCause()); }
    }

    @Override
    public List<Producto> listarInactivos() {
        try { return repository.listarInactivos(); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e.getCause()); }
    }

    @Override
    public void activar(int id) {
        try { repository.activar(id); }
        catch (SQLException e) { throw new ServiceJdbcException(e.getMessage(), e.getCause()); }
    }
}