package controllers;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Controlador (Servlet) encargado de la gestión del Dashboard Principal.
 * Este servlet actúa como el punto de entrada para la pantalla de inicio de la aplicación
 * (generalmente para roles administrativos y de secretaría).
 *
 * Su responsabilidad es orquestar la recuperación de indicadores clave de rendimiento (KPIs)
 * y preparar los datos para ser visualizados en la vista JSP.
 */

import services.DashboardService;
import services.DashboardServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

/**
 * Servlet mapeado a la ruta "/dashboard".
 * Gestiona la carga inicial de la aplicación post-login.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    /**
     * Procesa las solicitudes HTTP GET.
     * Se invoca cuando el usuario accede al enlace "Inicio" o es redirigido tras iniciar sesión.
     *
     * @param req  La solicitud HTTP que contiene los atributos de sesión y conexión.
     * @param resp La respuesta HTTP utilizada para enviar la vista al cliente.
     * @throws ServletException Si ocurre un error en el ciclo de vida del Servlet.
     * @throws IOException Si hay errores de entrada/salida al despachar la vista.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*
         * 1. INYECCIÓN DE DEPENDENCIAS
         * Recuperamos la conexión a la base de datos que fue abierta e inyectada
         * por el 'ConexionFilter' en el alcance del request.
         * Esto asegura que usemos una conexión válida y gestionada transaccionalmente.
         */
        Connection conn = (Connection) req.getAttribute("conn");

        /*
         * 2. INSTANCIACIÓN DEL SERVICIO
         * Creamos una instancia de la lógica de negocio (Service) pasándole la conexión.
         * DashboardService encapsula las consultas complejas de agregación (conteos y sumas).
         */
        DashboardService service = new DashboardServiceImpl(conn);

        /*
         * 3. RECUPERACIÓN DE DATOS (MODELO)
         * Llamamos a los métodos del servicio para obtener los datos en tiempo real.
         */
        // Cantidad de citas activas para el día actual
        int citasHoy = service.getCitasHoy();

        // Total histórico de pacientes registrados y activos
        int pacientesTotal = service.getPacientesTotales();

        // Suma monetaria de lo facturado en el día actual
        double ingresosHoy = service.getIngresosHoy();

        /*
         * 4. PREPARACIÓN DE LA VISTA
         * Guardamos los datos recuperados en el alcance del request (Request Scope).
         * El JSP utilizará estos atributos para renderizar las tarjetas informativas.
         */
        req.setAttribute("citasHoy", citasHoy);
        req.setAttribute("pacientesTotal", pacientesTotal);
        req.setAttribute("ingresosHoy", ingresosHoy);

        /*
         * 5. DESPACHO A LA VISTA (VIEW)
         * Transferimos el control al JSP ubicado en la carpeta protegida WEB-INF.
         * Usamos 'forward' para mantener la misma URL y los objetos request/response.
         */
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/dashboard.jsp").forward(req, resp);
    }
}