package controllers;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Controlador (Servlet) encargado de la gestión del Módulo de Agenda.
 * Este servlet actúa como el punto central para coordinar las operaciones relacionadas con las citas.
 *
 * Responsabilidades:
 * 1. Gestionar la visualización de la agenda filtrada por fecha y estado (Pendiente, Facturada, Cancelada).
 * 2. Procesar la creación y edición de citas (Agendamiento).
 * 3. Manejar la cancelación de citas.
 * 4. Proveer los datos necesarios (Listas de Pacientes/Odontólogos) para los formularios modales.
 */

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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/citas")
public class CitaServlet extends HttpServlet {

    /**
     * Maneja las peticiones GET para la visualización y navegación de la agenda.
     * Se encarga de preparar todos los datos necesarios para renderizar la vista 'citas_gestion.jsp',
     * incluyendo listas desplegables y la tabla de citas filtrada.
     *
     * @param req  La solicitud HTTP con parámetros de filtro (fecha, acción).
     * @param resp La respuesta HTTP.
     * @throws ServletException Si ocurre un error en el Servlet.
     * @throws IOException Si hay errores de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Recuperación de la conexión inyectada por el filtro (Patrón Connection per Request)
        Connection conn = (Connection) req.getAttribute("conn");

        // 2. Instanciación de servicios necesarios para la lógica de negocio
        CitaServiceImpl citaService = new CitaServiceImpl(conn);
        PacienteService pacienteService = new PacienteServiceImpl(conn);
        OdontologoService odontologoService = new OdontologoServiceImpl(conn);

        /*
         * CARGA DE CATÁLOGOS AUXILIARES
         * Recuperamos las listas completas de Pacientes y Odontólogos.
         * Estos datos son fundamentales para poblar los <select> en el modal de "Nueva Cita".
         */
        req.setAttribute("listaPacientes", pacienteService.listar());
        req.setAttribute("listaOdontologos", odontologoService.listar());

        // 3. Gestión de Parámetros de Control de Vista
        // 'accion' determina qué pestaña o filtro se está solicitando (agenda, facturadas, etc.)
        String accion = req.getParameter("accion");
        if (accion == null) accion = "agenda"; // Vista por defecto

        // 'fecha' permite navegar en el calendario. Si no viene, usamos la fecha actual.
        String fechaFiltro = req.getParameter("fecha");
        if (fechaFiltro == null || fechaFiltro.isEmpty()) {
            fechaFiltro = LocalDate.now().toString();
        }

        List<Cita> listaMostrar;
        String tituloTabla;

        /*
         * ENRUTAMIENTO DE LÓGICA DE VISUALIZACIÓN
         * Dependiendo de la acción, consultamos al servicio diferentes sets de datos.
         */
        try {
            switch (accion) {
                case "agenda":
                    // Muestra citas operativas del día (Pendientes y Atendidas)
                    // Es la vista principal de trabajo para la secretaria.
                    listaMostrar = citaService.listarAgendaPorFecha(fechaFiltro);
                    tituloTabla = "Agenda de Citas";
                    break;

                case "facturadas":
                    // Muestra el historial de citas ya cobradas en la fecha seleccionada
                    listaMostrar = citaService.listarFacturadasPorFecha(fechaFiltro);
                    tituloTabla = "Citas Facturadas";
                    break;

                case "canceladas":
                    // Muestra citas que fueron canceladas, útil para auditoría o reagendamiento
                    listaMostrar = citaService.listarCanceladasPorFecha(fechaFiltro);
                    tituloTabla = "Citas Canceladas";
                    break;

                case "buscar":
                    // Búsqueda global por cédula de paciente (ignora la fecha seleccionada)
                    String cedula = req.getParameter("busqueda");
                    if (cedula != null && Pattern.matches("\\d+", cedula)) {
                        listaMostrar = citaService.buscarPorCedula(cedula);
                        tituloTabla = "Búsqueda por Cédula: " + cedula;
                        // Limpiamos el filtro de fecha visualmente ya que mostramos historial completo
                        fechaFiltro = "";
                    } else {
                        req.setAttribute("error", "Cédula inválida. Ingrese solo números.");
                        // Fallback: mostrar agenda de hoy
                        listaMostrar = citaService.listarAgendaPorFecha(LocalDate.now().toString());
                        tituloTabla = "Agenda";
                    }
                    break;

                case "editar":
                    // Carga los datos de una cita específica para mostrarlos en el Modal de Edición
                    try {
                        int id = Integer.parseInt(req.getParameter("id"));
                        // Inyectamos el objeto 'citaEditar' para que el JSP lo detecte y abra el modal
                        req.setAttribute("citaEditar", citaService.porId(id).orElse(null));
                    } catch (Exception e) {
                        // Si falla el ID, ignoramos silenciosamente y mostramos la lista
                    }
                    // Mantenemos la lista de fondo visible (Agenda Operativa)
                    listaMostrar = citaService.listarAgendaPorFecha(fechaFiltro);
                    tituloTabla = "Agenda de Citas";
                    break;

                default:
                    // Caso por defecto de seguridad
                    listaMostrar = citaService.listarAgendaPorFecha(fechaFiltro);
                    tituloTabla = "Agenda de Citas";
                    accion = "agenda";
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            // En caso de error crítico, inicializamos lista vacía para no romper el JSP
            listaMostrar = List.of();
            tituloTabla = "Error cargando datos";
        }

        // 4. Inyección de Atributos al Alcance de la Petición (Request Scope)
        req.setAttribute("citas", listaMostrar);
        req.setAttribute("titulo", tituloTabla);
        req.setAttribute("fechaFiltro", fechaFiltro);
        req.setAttribute("vistaActual", accion); // Necesario para resaltar la pestaña activa

        // 5. Despacho a la Vista
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/citas_gestion.jsp").forward(req, resp);
    }

    /**
     * Maneja las peticiones POST para el procesamiento de transacciones (Crear, Editar, Cancelar).
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        CitaServiceImpl citaService = new CitaServiceImpl(conn);
        String accion = req.getParameter("accion");

        try {
            if ("guardar".equals(accion)) {
                /*
                 * LÓGICA DE GUARDADO (INSERT / UPDATE)
                 * Recopila todos los campos del formulario modal.
                 */
                // ID: 0 si es nuevo, >0 si es edición
                int idCita = Integer.parseInt(req.getParameter("idCita"));
                int idPaciente = Integer.parseInt(req.getParameter("idPaciente"));
                int idOdontologo = Integer.parseInt(req.getParameter("idOdontologo"));

                // Datos de fecha y hora (Inputs HTML5 separados)
                String fecha = req.getParameter("fecha");
                String hora = req.getParameter("hora");
                String motivo = req.getParameter("motivo");

                // Estado actual (se mantiene al editar, o es 'Pendiente' al crear)
                String estado = req.getParameter("estado");

                // Fusión de fecha y hora en un objeto LocalDateTime
                LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora + ":00");

                // Construcción del objeto de dominio
                Paciente p = new Paciente();
                p.setIdPaciente(idPaciente);

                Odontologo o = new Odontologo();
                o.setIdOdontologo(idOdontologo);

                Cita cita = new Cita(idCita, fechaHora, motivo, estado, p, o);

                // Delegación al servicio (incluye validaciones de negocio como cruce de horarios)
                citaService.agendarCita(cita);

                // Patrón Post-Redirect-Get: Redirigimos a la vista de agenda en la fecha de la cita
                resp.sendRedirect(req.getContextPath() + "/citas?accion=agenda&fecha=" + fecha + "&exito=guardada");
            }
            else if ("cancelar".equals(accion)) {
                /*
                 * LÓGICA DE CANCELACIÓN
                 * Realiza un cambio de estado a 'Cancelada' (Soft Delete).
                 */
                int id = Integer.parseInt(req.getParameter("id"));
                citaService.cancelarCita(id);

                // Intentamos volver a la fecha que el usuario estaba viendo
                String fechaActual = req.getParameter("fechaActual");
                if (fechaActual == null || fechaActual.isEmpty()) fechaActual = LocalDate.now().toString();

                resp.sendRedirect(req.getContextPath() + "/citas?accion=agenda&fecha=" + fechaActual + "&exito=cancelada");
            }
            // NOTA: La acción "finalizar" (Atender) es responsabilidad exclusiva del OdontólogoServlet.

        } catch (ServiceJdbcException e) {
            // Manejo de Errores de Negocio (ej: Horario ocupado)
            req.setAttribute("error", e.getMessage());
            // Volvemos al doGet para mostrar el error en el contexto de la página
            doGet(req, resp);
        } catch (Exception e) {
            // Manejo de Errores Técnicos no esperados
            e.printStackTrace();
            req.setAttribute("error", "Error inesperado: " + e.getMessage());
            doGet(req, resp);
        }
    }
}