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

        // 2. Instanciamos el Servicio (CAMBIO APLICADO: Pasamos directo la conexión)
        // El servicio se encarga internamente de crear su repositorio.
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
            switch (nombreRol) {
                case "Administrador":
                    getServletContext().getRequestDispatcher("/WEB-INF/vistas/admin/dashboard.jsp").forward(req, resp);
                    break;
                case "Secretaria":
                    getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/dashboard.jsp").forward(req, resp);
                    break;
                case "Odontologo":
                    getServletContext().getRequestDispatcher("/WEB-INF/vistas/odontologo/dashboard.jsp").forward(req, resp);
                    break;
                default:
                    req.setAttribute("error", "Usuario sin rol asignado o rol desconocido.");
                    getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
            }

        } else {
            // --- LOGIN FALLIDO ---
            req.setAttribute("error", "Credenciales incorrectas.");
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    //Este se ejecuta cuando el usuario da click en iniciar sesión en el ícono del index.jsp
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        } else {
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}