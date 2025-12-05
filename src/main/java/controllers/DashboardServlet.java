package controllers;

import services.DashboardService;
import services.DashboardServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        DashboardService service = new DashboardServiceImpl(conn);

        // Obtenemos los datos reales
        int citasHoy = service.getCitasHoy();
        int pacientesTotal = service.getPacientesTotales();
        double ingresosHoy = service.getIngresosHoy();

        // Enviamos a la vista
        req.setAttribute("citasHoy", citasHoy);
        req.setAttribute("pacientesTotal", pacientesTotal);
        req.setAttribute("ingresosHoy", ingresosHoy);

        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/dashboard.jsp").forward(req, resp);
    }
}