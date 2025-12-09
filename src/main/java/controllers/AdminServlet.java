package controllers;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.1
 * Descripción:
 * Controlador (Servlet) encargado del Módulo de Administración.
 * Gestiona todas las peticiones HTTP relacionadas con la administración de usuarios del sistema
 * (Secretarias, Odontólogos, Administradores).
 *
 * Responsabilidades:
 * 1. Listar usuarios activos e inactivos.
 * 2. Gestionar el formulario de creación/edición de usuarios.
 * 3. Procesar la baja lógica (desactivación) y reactivación de cuentas.
 * 4. Coordinar la creación de perfiles profesionales (Odontólogos) junto con el usuario base.
 */

import models.*;
import services.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.regex.Pattern; // Import necesario para validaciones

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    /**
     * Maneja las peticiones GET para la navegación y recuperación de datos.
     * Controla qué vista se muestra al administrador (Listado, Papelera, Formulario de Edición).
     *
     * @param req  La solicitud HTTP.
     * @param resp La respuesta HTTP.
     * @throws ServletException Error en el Servlet.
     * @throws IOException Error de E/S.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Recuperación de la conexión inyectada por el filtro
        Connection conn = (Connection) req.getAttribute("conn");

        // 2. Instanciación de servicios necesarios
        UsuarioService userService = new UsuarioServiceImpl(conn);
        OdontologoService odoService = new OdontologoServiceImpl(conn);

        // 3. Determinación de la acción a realizar (Default: listar)
        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        // Variables para configurar la vista
        String titulo = "Gestión de Personal";

        try {
            switch (accion) {
                case "listar":
                    // Muestra todos los usuarios activos (estado = 1)
                    req.setAttribute("usuarios", userService.listar());
                    req.setAttribute("titulo", "Personal Activo");
                    req.setAttribute("esPapelera", false); // Configura los botones de acción en la tabla
                    break;

                case "inactivos":
                    // Muestra la papelera de reciclaje (estado = 0)
                    req.setAttribute("usuarios", userService.listarInactivos());
                    req.setAttribute("titulo", "Personal Inactivo");
                    req.setAttribute("esPapelera", true); // Cambia botones a "Reactivar"
                    break;

                case "eliminar":
                    // Acción de Soft Delete (Desactivar usuario)
                    int idElim = Integer.parseInt(req.getParameter("id"));
                    userService.eliminar(idElim);
                    // Redirección para evitar reenvío de formularios y refrescar la lista
                    resp.sendRedirect(req.getContextPath() + "/admin");
                    return; // Importante: Detener la ejecución tras redirigir

                case "activar":
                    // Acción de Restauración
                    int idAct = Integer.parseInt(req.getParameter("id"));
                    userService.activar(idAct);
                    resp.sendRedirect(req.getContextPath() + "/admin?accion=inactivos");
                    return;

                case "editar":
                    // Preparación del Modal para Edición
                    int idEdit = Integer.parseInt(req.getParameter("id"));
                    Usuario u = userService.porId(idEdit).orElse(null);

                    // Si el usuario a editar es un Odontólogo, necesitamos cargar sus datos extra
                    // para rellenar los campos de Especialidad y Código Médico en el modal.
                    if(u != null && "Odontologo".equals(u.getRol().getNombreRol())){
                        req.setAttribute("odontologoExtra", odoService.porIdUsuario(u.getIdUsuario()).orElse(null));
                    }

                    // Enviamos los objetos al JSP para que rellene el formulario
                    req.setAttribute("usuarioEditar", u);
                    req.setAttribute("mostrarModal", true); // Bandera para que el JS abra el modal automáticamente

                    // Recargamos la lista de fondo para que la pantalla no quede vacía detrás del modal
                    req.setAttribute("usuarios", userService.listar());
                    req.setAttribute("esPapelera", false);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
        }

        // 4. Despacho a la vista JSP principal
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/admin/dashboard.jsp").forward(req, resp);
    }

    /**
     * Maneja las peticiones POST para el procesamiento de formularios (Crear/Editar).
     * Recibe los datos del usuario y, si es odontólogo, también sus datos profesionales.
     *
     * @param req  La solicitud HTTP con los datos del formulario.
     * @param resp La respuesta HTTP.
     * @throws ServletException Error en el Servlet.
     * @throws IOException Error de E/S.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        UsuarioService userService = new UsuarioServiceImpl(conn);

        String accion = req.getParameter("accion");

        if ("guardar".equals(accion)) {
            try {
                // 1. Recolección de Datos Comunes (Usuario Base)
                int idUsuario = Integer.parseInt(req.getParameter("idUsuario")); // 0 si es nuevo
                String nombre = req.getParameter("nombre");
                String username = req.getParameter("username");
                String email = req.getParameter("email");
                String pass = req.getParameter("password"); // Puede venir vacía en edición (se maneja en Service)
                int idRol = Integer.parseInt(req.getParameter("idRol"));

                // --- VALIDACIÓN DE NOMBRE (SOLO LETRAS Y ESPACIOS) ---
                if (nombre == null || !Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", nombre)) {
                    throw new ServiceJdbcException("El nombre completo solo puede contener letras y espacios.");
                }

                // 2. Construcción de Objetos
                Rol rol = new Rol();
                rol.setIdRol(idRol);

                // Asignación manual de nombre de rol para lógica interna (idealmente vendría de BD, pero esto es eficiente)
                // Esto asegura que el UsuarioServiceImpl sepa si debe guardar datos de odontólogo
                if(idRol == 1) rol.setNombreRol("Administrador");
                else if(idRol == 2) rol.setNombreRol("Secretaria");
                else if(idRol == 3) rol.setNombreRol("Odontologo");

                Usuario u = new Usuario();
                u.setIdUsuario(idUsuario);
                u.setNombreCompleto(nombre);
                u.setUsername(username);
                u.setEmail(email);
                u.setPassword(pass); // El servicio decidirá si encriptarla o mantener la anterior si es null/vacía
                u.setRol(rol);

                // 3. Recolección de Datos Específicos (Si es Odontólogo)
                Odontologo odo = null;
                if (idRol == 3) { // 3 corresponde a Odontólogo
                    odo = new Odontologo();
                    odo.setEspecialidad(req.getParameter("especialidad"));
                    odo.setCodigoMedico(req.getParameter("codigo"));
                }

                // 4. Guardado Transaccional (Usuario + Perfil Médico)
                // El servicio se encarga de insertar/actualizar ambas tablas si es necesario
                userService.guardarUsuario(u, odo);

                // 5. Redirección con mensaje de éxito (PRG Pattern)
                resp.sendRedirect(req.getContextPath() + "/admin?exito=true");

            } catch (Exception e) {
                e.printStackTrace();
                // Si falla, redirigimos mostrando el error
                resp.sendRedirect(req.getContextPath() + "/admin?error=" + e.getMessage());
            }
        }
    }
}