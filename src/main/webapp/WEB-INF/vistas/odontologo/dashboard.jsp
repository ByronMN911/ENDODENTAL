<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Importamos las clases necesarias para el manejo de listas, fechas y modelos de negocio -->
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="models.*" %>

<!--
=============================================================================
VISTA: DASHBOARD ODONTÓLOGO (dashboard.jsp)
Autor: Byron Melo
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Panel de control principal para el rol de Odontólogo.
Esta vista está diseñada para ser el espacio de trabajo diario del doctor.

Funcionalidades:
1. Visualización de la agenda diaria filtrada EXCLUSIVAMENTE para el doctor logueado.
2. Filtro de fecha para consultar agendas futuras o pasadas.
3. Acción de "Atender Paciente": Abre un modal para registrar el diagnóstico y tratamiento.
4. Al completar la atención, la cita cambia de estado y desaparece de esta lista (pasa a facturación).
=============================================================================
-->

<%
    /* * -------------------------------------------------------------------------
     * BLOQUE DE LÓGICA DE PRESENTACIÓN (SERVER-SIDE)
     * -------------------------------------------------------------------------
     */

    // 1. Recuperación de la lista de citas pendientes.
    // El Servlet 'OdontologoServlet' ya se encargó de filtrar por ID de doctor y Fecha.
    List<Cita> citas = (List<Cita>) request.getAttribute("citas");
    if (citas == null) citas = new ArrayList<>();

    // 2. Título dinámico (ej: "Pacientes por Atender").
    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Mi Agenda";

    // 3. Recuperación de la fecha del filtro para mantener el estado del input date.
    String fechaFiltro = (String) request.getAttribute("fechaFiltro");
    if (fechaFiltro == null) fechaFiltro = LocalDate.now().toString();

    // 4. Mensajes de feedback (Éxito al guardar atención / Error en proceso).
    String error = request.getParameter("error");
    String exito = request.getParameter("exito");

    // Formateador para mostrar la hora de la cita de forma legible (HH:mm).
    DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Panel Médico - EndoDental</title>
    <!-- Estilos CSS (Bootstrap + Personalizados) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<div class="dashboard-wrapper">

    <!--
        COMPONENTE SIDEBAR
        Reutilizamos la barra lateral, marcando 'inicio' como activo.
        El sidebar se adapta automáticamente al rol de Odontólogo (muestra menos opciones).
    -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inicio"/>
    </jsp:include>

    <main class="main-content">

        <!-- SECCIÓN DE ALERTAS -->
        <% if (error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-triangle"></i> <%= error %> <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <% } %>
        <% if (exito != null) { %>
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle"></i> Atención registrada correctamente. <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <% } %>

        <!-- CABECERA CON FOTO Y FILTRO -->
        <div class="d-flex justify-content-between align-items-center mb-5">
            <div>
                <h2 class="fw-bold text-dark"><%= titulo %></h2>
                <p class="text-muted">Pacientes pendientes de atención.</p>

                <!--
                    FILTRO DE FECHA
                    Formulario GET simple que recarga la página con el parámetro 'fecha'.
                    El evento 'onchange' hace que se envíe apenas el usuario selecciona un día.
                -->
                <form action="odontologo" method="GET" class="d-flex align-items-center bg-white p-2 rounded shadow-sm border mt-3" style="max-width: fit-content;">
                    <label class="fw-bold me-2 text-secondary"><i class="fas fa-calendar-alt me-1"></i> Fecha:</label>
                    <input type="date" name="fecha" value="<%= fechaFiltro %>" class="form-control form-control-sm border-0" style="width: auto;" onchange="this.form.submit()">
                </form>
            </div>

            <!--
                FOTO DE PERFIL DEL ODONTÓLOGO
                Elemento visual para personalizar la experiencia del doctor.
                Se usa una imagen de placeholder (2.jpeg) con estilos de borde y sombra.
            -->
            <div class="d-flex align-items-center">
                <img src="${pageContext.request.contextPath}/assets/img/usuario.png" alt="Doctor" class="team-photo shadow-sm"
                     style="width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 4px solid white;">
            </div>
        </div>

        <!-- TABLA DE CITAS PENDIENTES -->
        <div class="custom-table-container">
            <table class="table table-custom table-hover align-middle">
                <thead>
                <tr>
                    <th>Hora</th>
                    <th>Paciente</th>
                    <th>Cédula</th>
                    <th>Motivo Consulta</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
                </thead>
                <tbody>
                <%
                    /* * BUCLE DE RENDERIZADO
                     * Si la lista está vacía, muestra un mensaje amigable.
                     * Si hay datos, genera una fila por cada paciente en espera.
                     */
                    if (citas.isEmpty()) {
                %>
                <tr>
                    <td colspan="6" class="text-center py-5 text-muted">
                        <i class="fas fa-check-circle fa-3x mb-3 text-success"></i><br>
                        ¡Excelente! No tienes pacientes pendientes por ahora.
                    </td>
                </tr>
                <% } else {
                    for (Cita c : citas) {
                        // Formateo de datos para la vista
                        String hora = c.getFechaHora().format(fmtHora);
                        String nomPac = c.getPaciente().getNombres() + " " + c.getPaciente().getApellidos();
                %>
                <tr>
                    <td class="fw-bold fs-5"><%= hora %></td>
                    <td class="fw-bold"><%= nomPac %></td>
                    <td><%= c.getPaciente().getCedula() %></td>
                    <td><%= c.getMotivo() %></td>
                    <td><span class="badge bg-warning text-dark">En Espera</span></td>
                    <td>
                        <!--
                            BOTÓN ATENDER
                            Llama a la función JS 'abrirModalAtencion' pasando el ID de la cita y el nombre del paciente.
                            Esto prepara el modal sin necesidad de recargar la página.
                        -->
                        <button class="btn btn-primary btn-sm px-4 rounded-pill"
                                onclick="abrirModalAtencion(<%= c.getIdCita() %>, '<%= nomPac %>')">
                            <i class="fas fa-stethoscope me-2"></i> Atender
                        </button>
                    </td>
                </tr>
                <% }} %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!--
    MODAL DE ATENCIÓN MÉDICA
    Formulario emergente donde el doctor registra la evolución clínica.
    Al enviar este formulario, el estado de la cita cambiará a 'Atendida'.
-->
<div class="modal fade" id="modalAtencion" tabindex="-1" data-bs-backdrop="static">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="fas fa-user-md me-2"></i> Registrar Atención Médica</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>

            <form action="${pageContext.request.contextPath}/odontologo" method="POST">
                <div class="modal-body p-4">
                    <!-- Acción oculta que indica al Servlet qué hacer -->
                    <input type="hidden" name="accion" value="atender">
                    <!-- ID de la cita que se está atendiendo (rellenado por JS) -->
                    <input type="hidden" name="idCita" id="idCitaAtencion">

                    <!-- Información visual del paciente seleccionado -->
                    <div class="alert alert-info d-flex align-items-center mb-4">
                        <i class="fas fa-user-circle me-3 fs-3"></i>
                        <div>
                            Paciente: <strong id="nombrePacienteModal" class="fs-5">...</strong>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Diagnóstico</label>
                        <textarea name="diagnostico" class="form-control form-control-custom" rows="3" required placeholder="Describa el diagnóstico clínico..."></textarea>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Tratamiento Realizado</label>
                        <textarea name="tratamiento" class="form-control form-control-custom" rows="3" required placeholder="Procedimientos efectuados..."></textarea>
                    </div>
                </div>

                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-secondary rounded-pill px-4" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-success rounded-pill px-4">
                        <i class="fas fa-save me-2"></i> Finalizar Consulta
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    /**
     * Función para abrir el modal de atención médica.
     * Recibe los datos de la fila seleccionada y prepara el formulario.
     * @param {number} id - ID de la cita a atender.
     * @param {string} nombrePaciente - Nombre para mostrar en el encabezado del modal.
     */
    function abrirModalAtencion(id, nombrePaciente) {
        // Seteamos los valores en los campos ocultos y visuales
        document.getElementById("idCitaAtencion").value = id;
        document.getElementById("nombrePacienteModal").innerText = nombrePaciente;

        // Instanciamos y mostramos el modal de Bootstrap
        var modal = new bootstrap.Modal(document.getElementById('modalAtencion'));
        modal.show();
    }
</script>

</body>
</html>