package controllers;

import models.Cita;
import models.Odontologo;
import models.Paciente;
import services.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@WebServlet("/citas")
public class CitaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");

        // Servicios
        CitaServiceImpl citaService = new CitaServiceImpl(conn);
        PacienteService pacienteService = new PacienteServiceImpl(conn);
        OdontologoService odontologoService = new OdontologoServiceImpl(conn);

        // 1. Cargar Listas para los Modals (Selects)
        req.setAttribute("listaPacientes", pacienteService.listar());
        req.setAttribute("listaOdontologos", odontologoService.listar());

        // 2. Lógica de Visualización (Qué citas muestro)
        String accion = req.getParameter("accion");
        List<Cita> listaMostrar = citaService.listarHoy();
        String tituloTabla = "Agenda del Día";

        if ("buscar".equals(accion)) {
            String cedula = req.getParameter("busqueda");
            if (cedula != null && Pattern.matches("\\d+", cedula)) {
                listaMostrar = citaService.buscarPorCedula(cedula); // Método que añadimos en el paso anterior
                tituloTabla = "Resultados para la cédula: " + cedula;
            } else {
                req.setAttribute("error", "Cédula inválida.");
                listaMostrar = citaService.listarHoy();
            }
        } else if ("historial".equals(accion)) {
            listaMostrar = citaService.listarAtendidas(); // O listar todas
            tituloTabla = "Historial Completo";

        }else if ("canceladas".equals(accion)) { // <--- NUEVO CASO
                listaMostrar = citaService.listarCanceladas();
                tituloTabla = "Historial de Citas Canceladas";

        }
        else if ("filtrarFecha".equals(accion)) {
            String fecha = req.getParameter("fecha");
            if (fecha != null && !fecha.isEmpty()) {
                listaMostrar = citaService.listarPorFecha(fecha); // Asegúrate de tener este método en el Service
                tituloTabla = "Agenda del: " + fecha;
            } else {
                listaMostrar = citaService.listarHoy();
            }
        }

        req.setAttribute("citas", listaMostrar);
        req.setAttribute("titulo", tituloTabla);

        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/citas_gestion.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        CitaServiceImpl citaService = new CitaServiceImpl(conn);
        String accion = req.getParameter("accion");

        try {
            if ("guardar".equals(accion)) {
                // GUARDAR O EDITAR
                int idCita = Integer.parseInt(req.getParameter("idCita")); // 0 si es nueva
                int idPaciente = Integer.parseInt(req.getParameter("idPaciente"));
                int idOdontologo = Integer.parseInt(req.getParameter("idOdontologo"));
                String fecha = req.getParameter("fecha");
                String hora = req.getParameter("hora");
                String motivo = req.getParameter("motivo");

                LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora + ":00");

                Paciente p = new Paciente(); p.setIdPaciente(idPaciente);
                Odontologo o = new Odontologo(); o.setIdOdontologo(idOdontologo);

                // Mantenemos el estado original si es edición, o null si es nueva
                String estadoOriginal = req.getParameter("estadoActual");

                Cita cita = new Cita(idCita, fechaHora, motivo, estadoOriginal, p, o);

                citaService.agendarCita(cita);
                resp.sendRedirect(req.getContextPath() + "/citas?exito=guardada");
            }
            else if ("cancelar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                citaService.cancelarCita(id);
                resp.sendRedirect(req.getContextPath() + "/citas?exito=cancelada");
            }
            else if ("finalizar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                citaService.finalizarCita(id);
                resp.sendRedirect(req.getContextPath() + "/citas?exito=finalizada");
            }

        } catch (ServiceJdbcException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error inesperado.");
            doGet(req, resp);
        }
    }
}