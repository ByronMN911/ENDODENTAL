package controllers;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Controlador (Servlet) encargado del Módulo del Odontólogo.
 * Este servlet gestiona el flujo de trabajo diario del personal médico, actuando como
 * punto de entrada para las operaciones clínicas.
 *
 * Responsabilidades:
 * 1. Seguridad: Implementa control de acceso basado en roles (RBAC) para asegurar que solo
 * usuarios con rol 'Odontologo' puedan acceder.
 * 2. Dashboard: Carga la agenda personalizada del doctor, filtrando citas por fecha y estado.
 * 3. Atención: Procesa el registro clínico (Diagnóstico y Tratamiento) y finaliza la cita.
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

/**
 * Servlet mapeado a la URL "/odontologo".
 * Gestiona las peticiones GET para visualizar la agenda y POST para registrar atenciones.
 */
@WebServlet("/odontologo")
public class OdontologoServlet extends HttpServlet {

    /**
     * Maneja las peticiones GET para visualizar el Dashboard del Odontólogo.
     * Recupera y muestra las citas pendientes asignadas específicamente al doctor logueado.
     *
     * @param req  Objeto de solicitud HTTP.
     * @param resp Objeto de respuesta HTTP.
     * @throws ServletException Error en el ciclo de vida del Servlet.
     * @throws IOException Error de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*
         * 1. INYECCIÓN DE DEPENDENCIAS
         * Recuperamos la conexión a la base de datos que fue abierta e inyectada por el filtro.
         * Esto asegura que el Servlet participe en la transacción global del request.
         */
        Connection conn = (Connection) req.getAttribute("conn");

        // 2. RECUPERACIÓN DE SESIÓN
        // Obtenemos la sesión actual para identificar al usuario que está intentando acceder.
        HttpSession session = req.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        /*
         * 3. SEGURIDAD DE ACCESO (RBAC - Role Based Access Control)
         * Verificación estricta:
         * - El usuario debe existir en la sesión (estar logueado).
         * - Su rol debe ser estrictamente 'Odontologo'.
         * Si no cumple, se redirige al login para proteger la información sensible de los pacientes.
         */
        if (usuario == null || !"Odontologo".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Instanciación de los servicios necesarios para la lógica de negocio
            OdontologoServiceImpl odoService = new OdontologoServiceImpl(conn);
            CitaServiceImpl citaService = new CitaServiceImpl(conn);

            /*
             * 4. VINCULACIÓN DE PERFIL PROFESIONAL
             * El objeto 'Usuario' de la sesión solo tiene datos de acceso (user/pass).
             * Necesitamos buscar en la tabla 'odontologos' usando el ID de usuario para
             * obtener el ID de Odontólogo real, el cual es necesario para filtrar las citas.
             */
            Optional<Odontologo> odoOpt = odoService.porIdUsuario(usuario.getIdUsuario());

            if (odoOpt.isPresent()) {
                Odontologo odontologo = odoOpt.get();

                // 5. FILTRO DE FECHA
                // Permite al doctor consultar agendas de días pasados o futuros.
                // Si no se especifica fecha en la URL (parámetro 'fecha'), se asume la fecha actual (Hoy).
                String fechaFiltro = req.getParameter("fecha");
                if (fechaFiltro == null || fechaFiltro.isEmpty()) {
                    fechaFiltro = LocalDate.now().toString();
                }

                /*
                 * 6. RECUPERACIÓN DE AGENDA (CITAS PENDIENTES)
                 * Obtenemos SOLO las citas pendientes asignadas a ESTE doctor en la FECHA seleccionada.
                 * Esto aísla la información y evita que un doctor vea pacientes de otro colega por error.
                 */
                List<Cita> misCitas = citaService.listarPendientesDeAtencion(odontologo.getIdOdontologo(), fechaFiltro);

                // 7. INYECCIÓN DE DATOS A LA VISTA
                // Enviamos la lista de citas y la fecha seleccionada al JSP para su renderizado.
                req.setAttribute("citas", misCitas);
                req.setAttribute("fechaFiltro", fechaFiltro);
                req.setAttribute("titulo", "Pacientes por Atender");

                // Despacho al JSP del Dashboard Médico
                getServletContext().getRequestDispatcher("/WEB-INF/vistas/odontologo/dashboard.jsp").forward(req, resp);
            } else {
                // Caso de Error de Integridad de Datos:
                // El usuario tiene rol 'Odontólogo' pero no existe un registro correspondiente en la tabla 'odontologos'.
                req.setAttribute("error", "Error: Su usuario no tiene un perfil de médico asociado.");
                getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            // Manejo global de excepciones para evitar pantallas de error por defecto
            e.printStackTrace();
            req.setAttribute("error", "Error al cargar dashboard: " + e.getMessage());
            getServletContext().getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    /**
     * Maneja las peticiones POST para procesar acciones clínicas.
     * Principalmente utilizado para registrar la atención médica (Finalizar Consulta).
     *
     * @param req  La solicitud HTTP con los datos del formulario modal.
     * @param resp La respuesta HTTP.
     * @throws ServletException Error en el Servlet.
     * @throws IOException Error de entrada/salida.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getParameter("accion");

        // Acción: ATENDER (Finalizar Consulta)
        if ("atender".equals(accion)) {
            try {
                // Recuperar conexión del filtro
                Connection conn = (Connection) req.getAttribute("conn");
                // Instanciar servicio de atención
                AtencionServiceImpl atencionService = new AtencionServiceImpl(conn);

                // 1. Recolección de datos clínicos desde el formulario
                // Estos datos provienen del Modal de Atención en el JSP
                int idCita = Integer.parseInt(req.getParameter("idCita"));
                String diagnostico = req.getParameter("diagnostico");
                String tratamiento = req.getParameter("tratamiento");
                // String notas = req.getParameter("notas"); // (Campo opcional, si se implementa en el futuro)

                // 2. Construcción del objeto de dominio (DTO)
                Atencion atencion = new Atencion();
                Cita c = new Cita();
                c.setIdCita(idCita); // Vinculamos la atención solo con el ID de la cita
                atencion.setCita(c);
                atencion.setDiagnostico(diagnostico);
                atencion.setTratamientoRealizado(tratamiento);

                /*
                 * 3. REGISTRO TRANSACCIONAL
                 * Llamamos al servicio para guardar la atención.
                 * Internamente, el servicio realiza dos operaciones en una transacción atómica:
                 * a) Insertar el registro en la tabla 'atenciones'.
                 * b) Actualizar el estado de la cita a 'Atendida' en la tabla 'citas'.
                 * Esto habilita a la secretaria para proceder con la facturación.
                 */
                atencionService.registrarAtencion(atencion);

                // 4. Redirección Exitosa (Patrón PRG: Post-Redirect-Get)
                // Redirigimos al dashboard para refrescar la lista y mostrar mensaje de éxito.
                // Usamos redirect para evitar reenvío de formulario al recargar la página.
                resp.sendRedirect(req.getContextPath() + "/odontologo?exito=true");

            } catch (Exception e) {
                e.printStackTrace();
                // En caso de error, redirigimos mostrando la alerta correspondiente en la URL.
                resp.sendRedirect(req.getContextPath() + "/odontologo?error=No se pudo guardar la atención");
            }
        }
    }
}