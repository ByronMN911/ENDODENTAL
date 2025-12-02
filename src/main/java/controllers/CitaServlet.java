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

@WebServlet("/citas")
public class CitaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        CitaServiceImpl citaService = new CitaServiceImpl(conn);
        PacienteService pacienteService = new PacienteServiceImpl(conn);
        OdontologoService odontologoService = new OdontologoServiceImpl(conn);

        // 1. CARGAR LISTAS PARA EL FORMULARIO (Selects)
        req.setAttribute("listaPacientes", pacienteService.listar());
        req.setAttribute("listaOdontologos", odontologoService.listar());

        // 2. LOGICA DE NAVEGACIÓN
        String accion = req.getParameter("accion");
        List<Cita> listaMostrar;
        String tituloTabla = "Agenda del Día";

        if ("buscar".equals(accion)) {
            String cedula = req.getParameter("busqueda");
            listaMostrar = citaService.buscarPorCedula(cedula);
            tituloTabla = "Resultados de búsqueda: " + cedula;
        } else if ("historial".equals(accion)) {
            listaMostrar = citaService.listarAtendidas();
            tituloTabla = "Historial de Citas Atendidas";
        } else if ("editar".equals(accion)) {
            // Cargar datos para editar
            int id = Integer.parseInt(req.getParameter("id"));
            Optional<Cita> citaOpt = citaService.porId(id);
            if (citaOpt.isPresent()) {
                req.setAttribute("citaEditar", citaOpt.get());
            }
            // Mantenemos la lista del día de fondo
            listaMostrar = citaService.listarHoy();
        } else {
            // Defecto: Citas de Hoy
            listaMostrar = citaService.listarHoy();
        }

        req.setAttribute("citas", listaMostrar);
        req.setAttribute("tituloTabla", tituloTabla);

        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/citas_gestion.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        CitaServiceImpl citaService = new CitaServiceImpl(conn);

        String accion = req.getParameter("accion");

        try {
            if ("finalizar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                citaService.cambiarEstado(id, "Atendida");
                resp.sendRedirect(req.getContextPath() + "/citas?exito=finalizada");
            }
            else if ("guardar".equals(accion)) {
                // LOGICA DE GUARDAR / EDITAR
                String idStr = req.getParameter("idCita");
                int idCita = (idStr != null && !idStr.isEmpty()) ? Integer.parseInt(idStr) : 0;

                int idPaciente = Integer.parseInt(req.getParameter("idPaciente"));
                int idOdontologo = Integer.parseInt(req.getParameter("idOdontologo"));
                String fecha = req.getParameter("fecha");
                String hora = req.getParameter("hora");
                String motivo = req.getParameter("motivo");
                String estado = req.getParameter("estado"); // Puede venir de edición

                LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora + ":00");

                Paciente p = new Paciente(); p.setIdPaciente(idPaciente);
                Odontologo o = new Odontologo(); o.setIdOdontologo(idOdontologo);

                Cita cita = new Cita(idCita, fechaHora, motivo, estado, p, o);

                citaService.agendarCita(cita);
                resp.sendRedirect(req.getContextPath() + "/citas?exito=guardada");
            }
        } catch (ServiceJdbcException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error interno.");
            doGet(req, resp);
        }
    }
}