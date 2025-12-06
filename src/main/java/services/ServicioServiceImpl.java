package services;

/*
 * Autor: Génesis Escobar
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para la gestión de Servicios Médicos (Intangibles).
 * Esta clase actúa como intermediario entre los controladores y la capa de acceso a datos (Repository).
 * Se encarga de coordinar las operaciones CRUD para el catálogo de servicios (ej: Limpieza, Extracción, etc.)
 * y de gestionar el manejo de excepciones técnicas.
 */

import models.Servicio;
import repository.ServicioRepository;
import repository.ServicioRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ServicioServiceImpl implements ServicioService {

    // Dependencia del repositorio para el acceso a la base de datos
    private ServicioRepository repository;

    /**
     * Constructor que inicializa el servicio inyectando la conexión a la base de datos.
     * Utiliza el patrón de inyección manual para instanciar la implementación concreta del repositorio.
     *
     * @param conn La conexión JDBC activa proveniente del filtro o controlador.
     */
    public ServicioServiceImpl(Connection conn) {
        this.repository = new ServicioRepositoryImpl(conn);
    }

    /**
     * Recupera el catálogo completo de servicios médicos disponibles.
     *
     * @return Una lista de objetos {@link Servicio} ordenados (generalmente por nombre).
     * @throws ServiceJdbcException Si ocurre un error de base de datos (SQL) durante la consulta.
     */
    @Override
    public List<Servicio> listar() {
        try {
            return repository.listar();
        } catch (SQLException e) {
            // Envolvemos la excepción técnica (SQL) en una excepción de tiempo de ejecución (Runtime)
            // para mantener limpias las capas superiores y centralizar el manejo de errores.
            throw new ServiceJdbcException("Error al listar servicios: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un servicio específico por su identificador único.
     *
     * @param id El ID del servicio a buscar.
     * @return Un {@link Optional} que contiene el servicio si se encuentra, o vacío si no existe.
     * @throws ServiceJdbcException Si ocurre un error en la consulta SQL.
     */
    @Override
    public Optional<Servicio> porId(int id) {
        try {
            // Usamos Optional.ofNullable para manejar elegantemente el caso en que el repositorio retorne null
            return Optional.ofNullable(repository.porId(id));
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al buscar servicio por ID: " + e.getMessage(), e);
        }
    }

    /**
     * Registra un nuevo servicio o actualiza uno existente.
     * La distinción entre crear y editar se maneja típicamente verificando si el ID del objeto es mayor a 0.
     *
     * @param servicio El objeto Servicio con los datos a persistir (nombre, descripción, precio).
     * @throws ServiceJdbcException Si ocurre un error al intentar guardar en la base de datos.
     */
    @Override
    public void guardar(Servicio servicio) {
        try {
            repository.guardar(servicio);
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al guardar servicio: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un servicio del sistema.
     * Nota: Dependiendo de la implementación del repositorio, esto puede ser un borrado físico (DELETE)
     * o un borrado lógico (UPDATE estado).
     *
     * @param id El ID del servicio a eliminar.
     * @throws ServiceJdbcException Si ocurre un error al eliminar (ej: violación de integridad referencial).
     */
    @Override
    public void eliminar(int id) {
        try {
            repository.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al eliminar servicio: " + e.getMessage(), e);
        }
    }
}