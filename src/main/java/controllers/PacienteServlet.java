package controllers;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.1
 * Descripción:
 * Controlador (Servlet) encargado del Módulo de Gestión de Pacientes.
 * Este servlet actúa como el punto de entrada para todas las peticiones HTTP relacionadas
 * con la administración de expedientes de pacientes.
 *
 * Responsabilidades:
 * 1. Recepción de peticiones (GET/POST) desde la vista.
 * 2. Orquestación de la lógica de negocio a través de PacienteService.
 * 3. Gestión del flujo de navegación (Redirecciones y Despachos).
 * 4. Manejo de errores de validación y excepciones de negocio.
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
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Servlet mapeado a la URL "/pacientes".
 * Implementa el patrón Front Controller para este módulo específico, delegando
 * las acciones a métodos privados según el parámetro 'accion'.
 */
@WebServlet("/pacientes")
public class PacienteServlet extends HttpServlet {

    /**
     * Procesa las solicitudes HTTP GET.
     * Se utiliza para operaciones de lectura, navegación y carga de formularios.
     *
     * @param req  La solicitud HTTP.
     * @param resp La respuesta HTTP.
     * @throws ServletException Error en el ciclo de vida del Servlet.
     * @throws IOException Error de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*
         * 1. INYECCIÓN DE DEPENDENCIAS
         * Recuperamos la conexión a la base de datos gestionada por el filtro (ConexionFilter).
         * Esto asegura que el Servlet participe en la transacción global del request.
         */
        Connection conn = (Connection) req.getAttribute("conn");

        // Instanciamos el servicio pasándole la conexión activa.
        PacienteService service = new PacienteServiceImpl(conn);

        /*
         * 2. ENRUTAMIENTO (ROUTING)
         * Determinamos qué acción realizar basándonos en el parámetro 'accion' de la URL.
         * Si no se especifica ninguna, la acción por defecto es 'listar'.
         */
        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        // Switch para delegar la lógica a métodos especializados
        switch (accion) {
            case "listar":
                // Muestra la tabla principal de pacientes activos.
                listarPacientes(service, req, resp);
                break;

            case "inactivos":
                // Muestra la "Papelera de Reciclaje" (pacientes archivados).
                listarInactivos(service, req, resp);
                break;

            case "eliminar":
                // Procesa la baja lógica (Soft Delete).
                eliminarPaciente(service, req, resp);
                break;

            case "activar":
                // Procesa la restauración de un paciente archivado.
                activarPaciente(service, req, resp);
                break;

            case "buscar":
                // Realiza una búsqueda por número de cédula.
                buscarPaciente(service, req, resp);
                break;

            case "editar":
                /*
                 * LÓGICA DE PRECARGA PARA EDICIÓN
                 * 1. Recuperamos el ID del paciente a editar.
                 * 2. Buscamos el objeto en la base de datos.
                 * 3. Si existe, lo enviamos al JSP como atributo 'pacienteEditar'.
                 * 4. Activamos la bandera 'mostrarModal' para que el formulario se abra automáticamente.
                 */
                try {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Optional<Paciente> pOpt = service.porId(id);

                    if (pOpt.isPresent()) {
                        req.setAttribute("pacienteEditar", pOpt.get());
                        req.setAttribute("mostrarModal", true); // Bandera para el JSP
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Log de error si el ID no es válido
                }
                // Finalmente, mostramos la lista de fondo para mantener el contexto visual.
                listarPacientes(service, req, resp);
                break;

            default:
                // Fallback de seguridad: listar pacientes.
                listarPacientes(service, req, resp);
        }
    }

    /**
     * Procesa las solicitudes HTTP POST.
     * Se utiliza exclusivamente para el envío de datos de formularios (Crear/Editar).
     *
     * @param req  La solicitud HTTP con los datos del formulario.
     * @param resp La respuesta HTTP.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Preparación del Entorno
        Connection conn = (Connection) req.getAttribute("conn");
        PacienteService service = new PacienteServiceImpl(conn);

        // 2. Recolección de Datos (Binding manual)
        String idStr = req.getParameter("idPaciente");
        String cedula = req.getParameter("cedula");
        String nombres = req.getParameter("nombres");
        String apellidos = req.getParameter("apellidos");
        String telefono = req.getParameter("telefono");
        String email = req.getParameter("email");
        String alergias = req.getParameter("alergias");

        int id = (idStr == null || idStr.isEmpty()) ? 0 : Integer.parseInt(idStr);

        // Construcción del objeto de transferencia de datos (DTO/Modelo)
        Paciente p = new Paciente();
        p.setIdPaciente(id);
        p.setCedula(cedula);
        p.setNombres(nombres);
        p.setApellidos(apellidos);
        p.setTelefono(telefono);
        p.setEmail(email);
        p.setAlergias(alergias);

        try {
            /*
             * 3. VALIDACIONES DE BACKEND (SEGURIDAD Y REGLAS DE NEGOCIO)
             * Aunque el frontend ya valida, el backend DEBE validar por seguridad.
             */

            // Validación de Cédula: Solo números (la longitud y algoritmo se validan en el Service)
            if (cedula == null || !Pattern.matches("\\d+", cedula)) {
                throw new ServiceJdbcException("La cédula debe contener solo números.");
            }

            // Validación de Nombres y Apellidos: Solo letras y espacios
            if (nombres == null || !Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", nombres)) {
                throw new ServiceJdbcException("Los nombres solo pueden contener letras y espacios.");
            }
            if (apellidos == null || !Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", apellidos)) {
                throw new ServiceJdbcException("Los apellidos solo pueden contener letras y espacios.");
            }

            // Validación de Teléfono: Solo números
            if (telefono != null && !telefono.isEmpty() && !Pattern.matches("\\d+", telefono)) {
                throw new ServiceJdbcException("El teléfono debe contener solo números.");
            }

            /*
             * 4. EJECUCIÓN DE LA LÓGICA DE NEGOCIO
             * Delegamos al servicio la tarea de guardar.
             */
            service.guardar(p);

            // Patrón PRG (Post-Redirect-Get):
            resp.sendRedirect(req.getContextPath() + "/pacientes?exito=true");

        } catch (ServiceJdbcException e) {
            /*
             * 5. MANEJO DE ERRORES DE NEGOCIO
             * Si ocurre error de validación, volvemos al JSP con los datos para corregir.
             */
            req.setAttribute("error", e.getMessage());
            req.setAttribute("pacienteEditar", p); // Re-inyectamos los datos ingresados
            req.setAttribute("mostrarModal", true); // Reabrir modal

            // Recargamos la lista de fondo
            listarPacientes(service, req, resp);
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES (HELPERS) PARA LIMPIEZA DEL CÓDIGO
    // =========================================================================

    /**
     * Carga la lista de pacientes activos y despacha a la vista principal.
     */
    private void listarPacientes(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Paciente> lista = service.listar();
        req.setAttribute("pacientes", lista);
        req.setAttribute("titulo", "Gestión de Pacientes");
        req.setAttribute("esPapelera", false); // Indica al JSP que muestre botones normales
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/lista.jsp").forward(req, resp);
    }

    /**
     * Carga la lista de pacientes inactivos (Papelera) y despacha a la vista.
     */
    private void listarInactivos(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Paciente> lista = service.listarInactivos();
        req.setAttribute("pacientes", lista);
        req.setAttribute("titulo", "Papelera de Pacientes");
        req.setAttribute("esPapelera", true); // Indica al JSP que muestre botón "Restaurar"
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/lista.jsp").forward(req, resp);
    }

    /**
     * Ejecuta la lógica de eliminación lógica (Archivado).
     */
    private void eliminarPaciente(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                service.eliminar(id); // Cambia estado a 0
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Redirigimos a la lista principal para refrescar los datos
        resp.sendRedirect(req.getContextPath() + "/pacientes");
    }

    /**
     * Ejecuta la lógica de reactivación de un paciente.
     */
    private void activarPaciente(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idStr = req.getParameter("id");
            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                service.activar(id); // Cambia estado a 1
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Redirigimos a la vista de inactivos para ver que el registro desapareció de la papelera
        resp.sendRedirect(req.getContextPath() + "/pacientes?accion=inactivos");
    }

    /**
     * Lógica de búsqueda inteligente.
     * Valida la entrada y decide si buscar en BD o mostrar error.
     */
    private void buscarPaciente(PacienteService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String texto = req.getParameter("busqueda");
        List<Paciente> lista;
        String titulo = "Resultados: " + texto;

        // Validación 1: Texto vacío
        if (texto == null || texto.trim().isEmpty()) {
            lista = service.listar();
            titulo = "Gestión de Pacientes";
        }
        // Validación 2: Formato numérico (Regex)
        else if (Pattern.matches("\\d+", texto)) {
            // Si es numérico, buscamos por cédula
            Optional<Paciente> p = service.porCedula(texto);

            // Convertimos el Optional a una Lista (para que el JSP pueda iterar)
            lista = p.isPresent() ? List.of(p.get()) : new ArrayList<>();

            if(lista.isEmpty()) {
                req.setAttribute("error", "No se encontró ningún paciente con esa cédula.");
            }
        }
        // Validación 3: Formato inválido (letras)
        else {
            lista = service.listar(); // Mostramos la lista completa por defecto
            req.setAttribute("error", "Formato inválido. Por favor ingrese solo números en el buscador.");
        }

        // Envío de datos a la vista
        req.setAttribute("pacientes", lista);
        req.setAttribute("titulo", titulo);
        req.setAttribute("esPapelera", false);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/lista.jsp").forward(req, resp);
    }
}