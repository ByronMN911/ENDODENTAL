<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="models.*" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Panel Médico - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<%
    // Recuperar datos enviados por el Servlet
    List<Cita> citas = (List<Cita>) request.getAttribute("citas");
    if (citas == null) citas = new ArrayList<>();

    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Mi Agenda";

    // Fecha del filtro para el input
    String fechaFiltro = (String) request.getAttribute("fechaFiltro");
    if (fechaFiltro == null) fechaFiltro = LocalDate.now().toString();

    String error = request.getParameter("error");
    String exito = request.getParameter("exito");

    DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
%>

<div class="dashboard-wrapper">

    <!-- SIDEBAR -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inicio"/>
    </jsp:include>

    <main class="main-content">

        <!-- ALERTAS -->
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

                <!-- FILTRO DE FECHA -->
                <form action="odontologo" method="GET" class="d-flex align-items-center bg-white p-2 rounded shadow-sm border mt-3" style="max-width: fit-content;">
                    <label class="fw-bold me-2 text-secondary"><i class="fas fa-calendar-alt me-1"></i> Fecha:</label>
                    <input type="date" name="fecha" value="<%= fechaFiltro %>" class="form-control form-control-sm border-0" style="width: auto;" onchange="this.form.submit()">
                </form>
            </div>

            <!-- FOTO DE PERFIL DEL ODONTÓLOGO -->
            <div class="d-flex align-items-center">
                <img src="${pageContext.request.contextPath}/assets/img/2.jpeg" alt="Doctor" class="team-photo shadow-sm"
                     style="width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 4px solid white;">
            </div>
        </div>

        <!-- TABLA DE CITAS -->
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
                <% if (citas.isEmpty()) { %>
                <tr>
                    <td colspan="6" class="text-center py-5 text-muted">
                        <i class="fas fa-coffee fa-3x mb-3 text-secondary"></i><br>
                        No tienes pacientes pendientes para la fecha seleccionada.
                    </td>
                </tr>
                <% } else {
                    for (Cita c : citas) {
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
                        <!-- BOTÓN ATENDER -->
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

<!-- MODAL DE ATENCIÓN -->
<div class="modal fade" id="modalAtencion" tabindex="-1" data-bs-backdrop="static">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="fas fa-user-md me-2"></i> Registrar Atención Médica</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>

            <form action="${pageContext.request.contextPath}/odontologo" method="POST">
                <div class="modal-body p-4">
                    <input type="hidden" name="accion" value="atender">
                    <input type="hidden" name="idCita" id="idCitaAtencion">

                    <div class="alert alert-info d-flex align-items-center mb-4">
                        <i class="fas fa-user me-3 fs-4"></i>
                        <div>
                            Paciente: <strong id="nombrePacienteModal">...</strong>
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
    function abrirModalAtencion(id, nombrePaciente) {
        document.getElementById("idCitaAtencion").value = id;
        document.getElementById("nombrePacienteModal").innerText = nombrePaciente;
        var modal = new bootstrap.Modal(document.getElementById('modalAtencion'));
        modal.show();
    }
</script>

</body>
</html>