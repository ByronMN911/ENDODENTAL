package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para la gestión de Odontólogos.
 * Esta clase actúa como intermediario entre los controladores (OdontologoServlet, CitaServlet)
 * y la capa de acceso a datos (OdontologoRepository).
 * Se encarga de coordinar la recuperación de información profesional del personal médico.
 */

import models.Odontologo;
import repository.OdontologoRepository;
import repository.OdontologoRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class OdontologoServiceImpl implements OdontologoService {

    // Dependencia del repositorio para el acceso a la base de datos
    private OdontologoRepository repository;

    /**
     * Constructor que inicializa el servicio inyectando la conexión a la base de datos.
     * Utiliza el patrón de inyección manual para instanciar la implementación concreta del repositorio.
     *
     * @param conn La conexión JDBC activa proveniente del filtro o controlador.
     */
    public OdontologoServiceImpl(Connection conn) {
        this.repository = new OdontologoRepositoryImpl(conn);
    }

    /**
     * Recupera el listado completo de odontólogos activos.
     *
     * @return Una lista de objetos {@link Odontologo} que incluye datos del usuario asociado.
     * @throws ServiceJdbcException Si ocurre un error de base de datos durante la consulta.
     */
    @Override
    public List<Odontologo> listar() {
        try {
            return repository.listar();
        } catch (SQLException e) {
            // Captura la excepción SQL (técnica) y la relanza como una excepción de servicio (negocio)
            // para mantener el desacoplamiento entre capas.
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Busca un odontólogo específico por su ID primario (tabla 'odontologos').
     *
     * @param id El ID del odontólogo a buscar.
     * @return Un {@link Optional} con el odontólogo si existe, o vacío si no.
     * @throws ServiceJdbcException Si ocurre un error en la consulta SQL.
     */
    @Override
    public Optional<Odontologo> porId(int id) {
        try {
            return Optional.ofNullable(repository.porId(id));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Busca el perfil profesional de un odontólogo basándose en su ID de Usuario (Login).
     * Este método es esencial para el dashboard del médico, ya que permite identificar
     * qué doctor ha iniciado sesión para mostrarle solo sus citas correspondientes.
     *
     * @param idUsuario El ID de la tabla 'usuarios' asociado al doctor.
     * @return Un {@link Optional} con el perfil del odontólogo asociado.
     * @throws ServiceJdbcException Si ocurre un error técnico.
     */
    @Override
    public Optional<Odontologo> porIdUsuario(int idUsuario) {
        try {
            /*
             * Nota Técnica:
             * Si el método 'porIdUsuario' no está definido en la interfaz genérica del Repositorio,
             * realizamos un casteo explícito a la implementación concreta (OdontologoRepositoryImpl)
             * para acceder a este método especializado.
             * Lo ideal en una arquitectura estricta es agregar el método a la interfaz OdontologoRepository.
             */
            return Optional.ofNullable(((OdontologoRepositoryImpl) repository).porIdUsuario(idUsuario));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }
}