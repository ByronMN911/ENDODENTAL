package controllers;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Controlador (Servlet) encargado de gestionar el proceso de Autenticación y Autorización.
 * Actúa como la puerta de entrada al sistema, validando credenciales y dirigiendo
 * a los usuarios a sus respectivos módulos según su rol (RBAC).
 * También gestiona el ciclo de vida de la sesión HTTP (Creación y Destrucción/Logout).
 */

import models.Usuario;
import services.LoginService;
import services.LoginServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;

/**
 * Servlet mapeado a la URL "/login".
 * Maneja las peticiones POST para iniciar sesión y GET para cerrar sesión o mostrar el formulario.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * Procesa la solicitud de inicio de sesión (Login).
     * Recibe las credenciales, las valida contra el servicio y establece la sesión del usuario.
     *
     * @param req  Objeto HttpServletRequest que contiene los parámetros del formulario (username, password).
     * @param resp Objeto HttpServletResponse para enviar la redirección o el error.
     * @throws ServletException Si ocurre un error en la ejecución del Servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Recuperación de parámetros del formulario HTML
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 2. Inyección de Dependencias
        // Obtenemos la conexión a la base de datos gestionada por el filtro (ConexionFilter)
        Connection conn = (Connection) req.getAttribute("conn");

        // Instanciamos el servicio de login inyectando la conexión necesaria para los repositorios
        LoginService service = new LoginServiceImpl(conn);

        // 3. Ejecución de la Lógica de Negocio
        // El servicio verifica existencia, estado activo y coincidencia de hash de contraseña
        Optional<Usuario> usuarioLogueado = service.login(username, password);

        if (usuarioLogueado.isPresent()) {
            // --- ESCENARIO: LOGIN EXITOSO ---

            // 4. Gestión de Sesión HTTP
            // Obtenemos la sesión actual o creamos una nueva si no existe
            HttpSession session = req.getSession();
            // Guardamos el objeto usuario completo en la sesión para persistencia entre peticiones
            session.setAttribute("usuario", usuarioLogueado.get());

            Usuario u = usuarioLogueado.get();

            // Validación defensiva del rol para evitar NullPointerException
            String nombreRol = (u.getRol() != null) ? u.getRol().getNombreRol().trim() : "";

            // 5. Redirección basada en Roles (Router)
            // Utilizamos 'sendRedirect' en lugar de 'forward' para implementar el patrón PRG (Post-Redirect-Get).
            // Esto evita que el usuario reenvíe el formulario de login si refresca la página.
            switch (nombreRol) {
                case "Administrador":
                    // Redirige al controlador del módulo de administración
                    resp.sendRedirect(req.getContextPath() + "/admin");
                    break;
                case "Odontologo":
                    // Redirige al controlador del módulo médico
                    resp.sendRedirect(req.getContextPath() + "/odontologo");
                    break;
                case "Secretaria":
                default:
                    // Redirige al dashboard general operativo
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                    break;
            }

        } else {
            // --- ESCENARIO: LOGIN FALLIDO ---

            // Establecemos un atributo de error para mostrar la alerta en el JSP
            req.setAttribute("error", "Credenciales incorrectas.");

            // Usamos 'forward' para volver al JSP de login manteniendo la misma petición (y el mensaje de error)
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    /**
     * Procesa las solicitudes GET.
     * Principalmente utilizado para cerrar la sesión (Logout) o mostrar la página de login inicial.
     *
     * @param req  Objeto de solicitud.
     * @param resp Objeto de respuesta.
     * @throws ServletException Error del Servlet.
     * @throws IOException Error de E/S.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Verificamos si hay una acción específica (ej: logout)
        String action = req.getParameter("action");

        if ("logout".equals(action)) {
            // 1. Recuperar la sesión sin crear una nueva (false)
            HttpSession session = req.getSession(false);
            if (session != null) {
                // 2. Invalidar la sesión: Borra todos los atributos y desconecta al usuario del servidor
                session.invalidate();
            }
            // 3. Redirigir al formulario de login limpio
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        } else {
            // Si no hay acción, simplemente mostramos la vista de login
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}