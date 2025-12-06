package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para la gestión de Usuarios.
 * Esta clase actúa como intermediario entre el controlador (Servlet) y la capa de datos (Repository).
 * Se encarga de orquestar operaciones complejas como la creación de usuarios con roles específicos
 * (ej. Odontólogos) y la seguridad (encriptación de contraseñas).
 */

import models.Usuario;
import models.Odontologo;
import repository.*;
import util.PasswordUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UsuarioServiceImpl implements UsuarioService {

    // Dependencias de Repositorios para acceso a datos
    private UsuarioRepository usuarioRepo;
    private OdontologoRepository odontologoRepo;

    /**
     * Constructor que inyecta la conexión a la base de datos.
     * Utiliza el patrón de "Constructor Injection" manual para inicializar los repositorios.
     *
     * @param conn La conexión JDBC activa para esta petición (Scope: Request).
     */
    public UsuarioServiceImpl(Connection conn) {
        this.usuarioRepo = new UsuarioRepositoryImpl(conn);
        // Instanciamos también el repo de odontólogos para guardar detalles profesionales si es necesario
        this.odontologoRepo = new OdontologoRepositoryImpl(conn);
    }

    /**
     * Obtiene la lista de todos los usuarios activos en el sistema.
     *
     * @return Lista de objetos Usuario con estado activo.
     */
    @Override
    public List<Usuario> listar() {
        try {
            return usuarioRepo.listar();
        } catch (SQLException e) {
            // Envolvemos la excepción SQL en una RuntimeException personalizada para no ensuciar la firma
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Obtiene la lista de usuarios que han sido desactivados (Papelera de reciclaje).
     *
     * @return Lista de objetos Usuario con estado inactivo.
     */
    @Override
    public List<Usuario> listarInactivos() {
        try {
            return usuarioRepo.listarInactivos();
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Busca un usuario por su identificador único.
     *
     * @param id El ID del usuario a buscar.
     * @return Un Optional que contiene el Usuario si existe, o vacío si no.
     */
    @Override
    public Optional<Usuario> porId(int id) {
        try {
            return Optional.ofNullable(usuarioRepo.porId(id));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Guarda un usuario en la base de datos (Creación o Actualización).
     * Este método maneja lógica crítica de seguridad y consistencia de datos.
     *
     * 1. Encriptación: Si la contraseña cambió o es nueva, se aplica SHA-256.
     * 2. Transacción Implícita: Si el usuario es Odontólogo, se guardan también sus datos profesionales.
     *
     * @param usuario El objeto Usuario con los datos básicos (nombre, user, pass, rol).
     * @param datosOdontologo Objeto con especialidad y código médico (solo si el rol es Odontólogo).
     */
    @Override
    public void guardarUsuario(Usuario usuario, Odontologo datosOdontologo) {
        try {
            /*
             * LÓGICA DE SEGURIDAD (ENCRIPTACIÓN)
             * ---------------------------------------------------------
             * Verificamos si viene una contraseña nueva para encriptarla.
             * Si estamos editando y el campo password vino vacío, recuperamos la
             * contraseña anterior de la BD para no perderla.
             */
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                // Caso 1: Nuevo usuario o cambio de contraseña -> Encriptar
                String hash = PasswordUtil.encriptar(usuario.getPassword());
                usuario.setPassword(hash);
            } else if (usuario.getIdUsuario() > 0) {
                // Caso 2: Edición sin cambio de contraseña -> Mantener la actual
                Usuario viejo = usuarioRepo.porId(usuario.getIdUsuario());
                if (viejo != null) {
                    usuario.setPassword(viejo.getPassword());
                }
            }

            /*
             * PERSISTENCIA DEL USUARIO
             * Guardamos los datos en la tabla 'usuarios' y obtenemos el ID generado.
             */
            int idUsuario = usuarioRepo.guardar(usuario);
            usuario.setIdUsuario(idUsuario);

            /*
             * PERSISTENCIA DE DATOS PROFESIONALES (SI APLICA)
             * Si el rol seleccionado es 'Odontologo' y vienen datos extra,
             * los guardamos en la tabla 'odontologos' vinculándolos con el ID del usuario.
             */
            if ("Odontologo".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
                if (datosOdontologo != null) {
                    datosOdontologo.setUsuario(usuario); // Vinculación FK
                    // Casteamos a la implementación concreta para acceder al método específico guardar
                    ((OdontologoRepositoryImpl) odontologoRepo).guardar(datosOdontologo);
                }
            }

        } catch (SQLException e) {
            throw new ServiceJdbcException("Error al guardar el usuario y sus detalles: " + e.getMessage(), e);
        }
    }

    /**
     * Realiza un borrado lógico del usuario (Desactivación).
     * No elimina el registro físicamente para mantener la integridad referencial.
     *
     * @param id El ID del usuario a desactivar.
     */
    @Override
    public void eliminar(int id) {
        try {
            usuarioRepo.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }

    /**
     * Reactiva un usuario que estaba en la papelera.
     *
     * @param id El ID del usuario a activar.
     */
    @Override
    public void activar(int id) {
        try {
            usuarioRepo.activar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e);
        }
    }
}