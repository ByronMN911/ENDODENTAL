package controllers;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Controlador (Servlet) encargado del Módulo del Odontólogo.
 * Gestiona el flujo de trabajo diario del personal médico.
 *
 * Responsabilidades:
 * 1. Seguridad: Asegura que solo usuarios con rol 'Odontologo' accedan a estas vistas.
 * 2. Dashboard: Muestra la agenda personalizada del doctor (Citas pendientes de hoy).
 * 3. Atención: Procesa el formulario de consulta médica (Diagnóstico/Tratamiento) y
 * actualiza el estado de la cita para habilitar su facturación.
 */

import models.*;
import services.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebServlet("/odontologo")
public class OdontologoServlet extends HttpServlet {

    /**
     * Maneja las peticiones GET para visualizar el Dashboard del Odontólogo.
     * Recupera las citas pendientes asignadas específicamente al doctor logueado.
     *
     * @param req  Objeto de solicitud HTTP.
     * @param resp Objeto de respuesta HTTP.
     * @throws ServletException Error en el Servlet.
     * @throws IOException Error de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Recuperación de la conexión inyectada por el filtro
        Connection conn = (Connection) req.getAttribute("conn");

        // 2. Recuperación de la sesión para identificar al usuario
        HttpSession session = req.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        /*
         * SEGURIDAD DE ACCESO (RBAC)
         * Verificamos que el usuario exista en sesión y que su rol sea estrictamente 'Odontologo'.
         * Si no cumple, lo redirigimos al login para proteger la vista.
         */
        if (usuario == null || !"Odontologo".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // 3. Instanciación de Servicios
            OdontologoServiceImpl odoService = new OdontologoServiceImpl(conn);
            CitaServiceImpl citaService = new CitaServiceImpl(conn);

            /*
             * VINCULACIÓN DE PERFIL PROFESIONAL
             * El objeto 'Usuario' de la sesión solo tiene datos de acceso (user/pass).
             * Necesitamos buscar en la tabla 'odontologos' usando el ID de usuario para
             * obtener el ID de Odontólogo real y poder filtrar las citas.
             */
            Optional<Odontologo> odoOpt = odoService.porIdUsuario(usuario.getIdUsuario());

            if (odoOpt.isPresent()) {
                Odontologo odontologo = odoOpt.get();

                // 4. FILTRO DE FECHA
                // Permite al doctor ver agendas de días pasados o futuros.
                // Si no se envía fecha en la URL, se asume la fecha actual (Hoy).
                String fechaFiltro = req.getParameter("fecha");
                if (fechaFiltro == null || fechaFiltro.isEmpty()) {
                    fechaFiltro = LocalDate.now().toString();
                }

                // 5. RECUPERACIÓN DE CITAS PENDIENTES
                // Consultamos solo las citas de ESTE doctor, en ESTA fecha y con estado 'Pendiente'.
                List<Cita> misCitas = citaService.listarPendientesDeAtencion(odontologo.getIdOdontologo(), fechaFiltro);

                // 6. INYECCIÓN DE ATRIBUTOS AL JSP
                req.setAttribute("citas", misCitas);
                req.setAttribute("fechaFiltro", fechaFiltro);
                req.setAttribute("titulo", "Pacientes por Atender");

                getServletContext().getRequestDispatcher("/WEB-INF/vistas/odontologo/dashboard.jsp").forward(req, resp);
            } else {
                // Caso de Error de Integridad: Usuario tiene rol 'Odontólogo' pero no existe en tabla 'odontologos'.
                req.setAttribute("error", "Error: Su usuario no tiene un perfil de médico asociado.");
                getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Error al cargar dashboard: " + e.getMessage());
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    /**
     * Maneja las peticiones POST para procesar acciones clínicas (Registrar Atención).
     *
     * @param req  Objeto de solicitud HTTP.
     * @param resp Objeto de respuesta HTTP.
     * @throws ServletException Error en el Servlet.
     * @throws IOException Error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");

        // Acción: ATENDER (Finalizar Consulta)
        if ("atender".equals(accion)) {
            try {
                Connection conn = (Connection) req.getAttribute("conn");
                AtencionServiceImpl atencionService = new AtencionServiceImpl(conn);

                // 1. Recolección de datos del formulario modal
                int idCita = Integer.parseInt(req.getParameter("idCita"));
                String diagnostico = req.getParameter("diagnostico");
                String tratamiento = req.getParameter("tratamiento");
                // String notas = req.getParameter("notas"); // (Opcional)

                // 2. Construcción del objeto Atencion
                Atencion atencion = new Atencion();
                Cita c = new Cita(); c.setIdCita(idCita); // Solo necesitamos el ID de la cita para vincular
                atencion.setCita(c);
                atencion.setDiagnostico(diagnostico);
                atencion.setTratamientoRealizado(tratamiento);

                /*
                 * 3. REGISTRO TRANSACCIONAL
                 * El servicio se encarga de:
                 * a) Insertar el registro en la tabla 'atenciones'.
                 * b) Actualizar el estado de la cita a 'Atendida'.
                 */
                atencionService.registrarAtencion(atencion);

                // 4. Redirección (PRG Pattern)
                // Redirigimos al dashboard mostrando un mensaje de éxito.
                resp.sendRedirect(req.getContextPath() + "/odontologo?exito=true");

            } catch (Exception e) {
                e.printStackTrace();
                // En caso de error, redirigimos con mensaje de error.
                resp.sendRedirect(req.getContextPath() + "/odontologo?error=No se pudo guardar la atención");
            }
        }
    }
}