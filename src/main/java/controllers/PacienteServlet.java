package controllers;
/*
 * Autor: Byron Melo
 * Fecha: 30-11-2025
 * Versión: 1.0
 * Descripción: Controlador para la gestión de Pacientes.
 * Ruta: /secretaria
 * Funcionalidades:
 * - GET: Listar, Mostrar Formulario (Nuevo/Editar), Eliminar.
 * - POST: Guardar (Insertar o Actualizar).
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
import java.util.Optional;


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

            case "inactivos": //Ver papelera
                listarInactivos(service, req, resp);
                break;

            case "form":
                mostrarFormulario(service, req, resp);
                break;

            case "eliminar":
                eliminarPaciente(service, req, resp);
                break;

            case "activar": // Reactivar paciente
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
        // Nota: Al guardar desde el form, el estado se maneja en BDD (default 1) o se mantiene si es update.

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
        // Variable para saber qué botones mostrar en el JSP
        req.setAttribute("esPapelera", false);

        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/lista.jsp").forward(req, resp);
    }

    // METODO AUXILIAR
    private void listarInactivos(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Paciente> lista = service.listarInactivos();
        req.setAttribute("pacientes", lista);
        req.setAttribute("titulo", "Pacientes Archivados (Inactivos)");
        // Variable para activar modo papelera en JSP
        req.setAttribute("esPapelera", true);

        // Reutilizamos el MISMO JSP, pero se verá diferente gracias al c:if
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
                service.eliminar(id); // Esto hace el soft delete (estado = 0)
            } catch (ServiceJdbcException e) {
                e.printStackTrace();
            }
        }
        // Redirigimos al listado normal
        resp.sendRedirect(req.getContextPath() + "/pacientes");
    }

    // METODO AUXILIAR
    private void activarPaciente(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            try {
                service.activar(id); // Esto pone estado = 1
            } catch (ServiceJdbcException e) {
                e.printStackTrace();
            }
        }
        // Redirigimos a la papelera para ver que desapareció de ahí o podriamos ir a listar
        resp.sendRedirect(req.getContextPath() + "/pacientes?accion=inactivos");
    }

    // METODO AUXILIAR
    private void buscarPaciente(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String texto = req.getParameter("busqueda");
        List<Paciente> lista;

        // Si la caja de texto está vacía, mostramos todos
        if (texto == null || texto.trim().isEmpty()) {
            lista = service.listar();
        } else {
            // Intentamos buscar por Cédula (Exacta)
            // Nota: Si quisieras buscar por Nombre ("Juan"), necesitarías crear un método
            // "buscarPorNombreLike" en tu DAO que use "WHERE nombres LIKE '%texto%'".
            // Por ahora usamos lo que tenemos: porCedula.
            Optional<Paciente> p = service.porCedula(texto);

            if (p.isPresent()) {
                lista = List.of(p.get()); // Creamos una lista con el único resultado
            } else {
                lista = List.of(); // Lista vacía si no encuentra nada
            }
        }

        req.setAttribute("pacientes", lista);
        req.setAttribute("titulo", "Resultados de Búsqueda");
        req.setAttribute("esPapelera", false); // Asumimos búsqueda en activos
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/lista.jsp").forward(req, resp);
    }
}