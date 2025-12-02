package controllers;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.1
 */
import models.Paciente;
import services.PacienteService;
import services.PacienteServiceImpl;
import services.ServiceJdbcException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList; // Importante para listas vacías
import java.util.Optional;
import java.util.regex.Pattern; // Importante para validar regex

@WebServlet("/pacientes")
public class PacienteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Connection conn = (Connection) req.getAttribute("conn");
        PacienteService service = new PacienteServiceImpl(conn);

        String accion = req.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        switch (accion) {
            case "listar":
                listarPacientes(service, req, resp);
                break;

            case "inactivos":
                listarInactivos(service, req, resp);
                break;

            case "form":
                mostrarFormulario(service, req, resp);
                break;

            case "eliminar":
                eliminarPaciente(service, req, resp);
                break;

            case "activar":
                activarPaciente(service, req, resp);
                break;

            case "buscar":
                buscarPaciente(service, req, resp);
                break;

            default:
                listarPacientes(service, req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Connection conn = (Connection) req.getAttribute("conn");
        PacienteService service = new PacienteServiceImpl(conn);

        String idStr = req.getParameter("idPaciente");
        String cedula = req.getParameter("cedula");
        String nombres = req.getParameter("nombres");
        String apellidos = req.getParameter("apellidos");
        String telefono = req.getParameter("telefono");
        String email = req.getParameter("email");
        String alergias = req.getParameter("alergias");

        int id = (idStr == null || idStr.isEmpty()) ? 0 : Integer.parseInt(idStr);

        Paciente p = new Paciente();
        p.setIdPaciente(id);
        p.setCedula(cedula);
        p.setNombres(nombres);
        p.setApellidos(apellidos);
        p.setTelefono(telefono);
        p.setEmail(email);
        p.setAlergias(alergias);

        try {
            service.guardar(p);
            resp.sendRedirect(req.getContextPath() + "/pacientes");

        } catch (ServiceJdbcException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("paciente", p);
            getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/form.jsp").forward(req, resp);
        }
    }

    // METODOS AUXILIARES

    private void listarPacientes(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Paciente> lista = service.listar();
        req.setAttribute("pacientes", lista);
        req.setAttribute("titulo", "Listado de Pacientes Activos");
        req.setAttribute("esPapelera", false);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/lista.jsp").forward(req, resp);
    }

    private void listarInactivos(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Paciente> lista = service.listarInactivos();
        req.setAttribute("pacientes", lista);
        req.setAttribute("titulo", "Pacientes Archivados (Inactivos)");
        req.setAttribute("esPapelera", true);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/lista.jsp").forward(req, resp);
    }

    private void mostrarFormulario(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        Paciente p = new Paciente();

        if (idStr != null && !idStr.isEmpty()) {
            int id = Integer.parseInt(idStr);
            Optional<Paciente> o = service.porId(id);
            if (o.isPresent()) {
                p = o.get();
            }
        }

        req.setAttribute("paciente", p);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/form.jsp").forward(req, resp);
    }

    private void eliminarPaciente(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            try {
                service.eliminar(id);
            } catch (ServiceJdbcException e) {
                e.printStackTrace();
            }
        }
        resp.sendRedirect(req.getContextPath() + "/pacientes");
    }

    private void activarPaciente(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            try {
                service.activar(id);
            } catch (ServiceJdbcException e) {
                e.printStackTrace();
            }
        }
        resp.sendRedirect(req.getContextPath() + "/pacientes?accion=inactivos");
    }

    // --- MÉTODO DE BÚSQUEDA CORREGIDO Y VALIDADO ---
    private void buscarPaciente(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String texto = req.getParameter("busqueda");
        List<Paciente> lista;
        String titulo = "Resultados de búsqueda: " + texto;

        // 1. Validación: ¿El texto es nulo o vacío?
        if (texto == null || texto.trim().isEmpty()) {
            // Si está vacío, mostramos todo
            lista = service.listar();
            titulo = "Gestión de Pacientes";
        }
        // 2. Validación: ¿Contiene SOLO números? (Regex \\d+)
        else if (Pattern.matches("\\d+", texto)) {
            // Si es numérico, procedemos a buscar en la BD
            Optional<Paciente> p = service.porCedula(texto);

            if (p.isPresent()) {
                lista = new ArrayList<>();
                lista.add(p.get());
            } else {
                lista = new ArrayList<>(); // Lista vacía
                // Enviamos mensaje de error al JSP para avisar que no hubo coincidencias
                req.setAttribute("error", "No se encontró ningún paciente con la cédula: " + texto);
            }
        }
        // 3. Caso Inválido: Tiene letras o símbolos
        else {
            // No hacemos consulta a la base de datos para protegerla
            lista = service.listar(); // Mostramos la lista por defecto o una vacía
            req.setAttribute("error", "Formato inválido. Por favor ingrese solo números en el buscador.");
        }

        req.setAttribute("pacientes", lista);
        req.setAttribute("titulo", titulo);
        req.setAttribute("esPapelera", false);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/lista.jsp").forward(req, resp);
    }
}