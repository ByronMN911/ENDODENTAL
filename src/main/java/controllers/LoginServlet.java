package controllers;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción:
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 1. Obtenemos la conexión del filtro
        Connection conn = (Connection) req.getAttribute("conn");

        // 2. Instanciamos el Servicio
        LoginService service = new LoginServiceImpl(conn);

        // 3. Ejecutar lógica
        Optional<Usuario> usuarioLogueado = service.login(username, password);

        if (usuarioLogueado.isPresent()) {
            // --- LOGIN EXITOSO ---
            HttpSession session = req.getSession();
            session.setAttribute("usuario", usuarioLogueado.get());

            Usuario u = usuarioLogueado.get();
            // Validamos que el rol no sea nulo para evitar NullPointerException
            String nombreRolDB = (u.getRol() != null) ? u.getRol().getNombreRol() : "";
            String nombreRol = nombreRolDB.trim();

            // CORRECCIÓN IMPORTANTE: Usamos sendRedirect en lugar de forward
            // Esto cambia la petición de POST a GET y evita el error 405
            switch (nombreRol) {
                case "Administrador":
                    // Si tuvieras un dashboard distinto, aquí iría la ruta.
                    // Por ahora redirigimos al mismo dashboard general o al específico si existe.
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                    break;
                case "Secretaria":
                    // Redirige al Servlet del Dashboard (GET)
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                    break;
                case "Odontologo":
                    // Redirige al Servlet del Dashboard (GET)
                    resp.sendRedirect(req.getContextPath() + "/dashboard");
                    break;
                default:
                    req.setAttribute("error", "Usuario sin rol asignado o rol desconocido.");
                    getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
            }

        } else {
            // --- LOGIN FALLIDO ---
            // Aquí SÍ usamos forward porque queremos mantenernos en la misma petición para mostrar el error
            req.setAttribute("error", "Credenciales incorrectas.");
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    // Este se ejecuta cuando el usuario da click en iniciar sesión en el ícono del index.jsp
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
        } else {
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}