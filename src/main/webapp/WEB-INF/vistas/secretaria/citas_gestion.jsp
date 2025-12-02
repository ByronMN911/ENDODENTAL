<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="models.Paciente" %>
<%@ page import="models.Odontologo" %>
<%@ page import="models.Cita" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Agenda de Citas - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
</head>
<body>

<%
    // --- LÓGICA DE RECUPERACIÓN DE DATOS ---
    List<Paciente> listaPacientes = (List<Paciente>) request.getAttribute("listaPacientes");
    if (listaPacientes == null) listaPacientes = new ArrayList<>();

    List<Odontologo> listaOdontologos = (List<Odontologo>) request.getAttribute("listaOdontologos");
    if (listaOdontologos == null) listaOdontologos = new ArrayList<>();

    List<Cita> listaCitas = (List<Cita>) request.getAttribute("citas");
    if (listaCitas == null) listaCitas = new ArrayList<>();

    // Objeto para edición (puede ser nulo si es nueva cita)
    Cita citaEdit = (Cita) request.getAttribute("citaEditar");
    boolean esEdicion = (citaEdit != null);

    // Valores por defecto para el formulario
    int idCitaVal = esEdicion ? citaEdit.getIdCita() : 0;
    String motivoVal = esEdicion ? citaEdit.getMotivo() : "";
    int idPacVal = esEdicion ? citaEdit.getPaciente().getIdPaciente() : 0;
    int idOdoVal = esEdicion ? citaEdit.getOdontologo().getIdOdontologo() : 0;
    String estadoVal = esEdicion ? citaEdit.getEstado() : "Pendiente";

    // Fechas para inputs (HTML5 requiere yyyy-MM-dd y HH:mm)
    String fechaVal = "";
    String horaVal = "";
    if (esEdicion && citaEdit.getFechaHora() != null) {
        fechaVal = citaEdit.getFechaHora().toLocalDate().toString();
        horaVal = citaEdit.getFechaHora().toLocalTime().toString();
    }

    String tituloTabla = (String) request.getAttribute("tituloTabla");
    if (tituloTabla == null) tituloTabla = "Agenda del Día";

    String error = (String) request.getAttribute("error");
    String exitoParam = request.getParameter("exito");

    DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>

<div class="dashboard-wrapper">
    <!-- SIDEBAR -->
    <nav class="sidebar">
        <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="EndoDental" class="sidebar-logo">
        <ul class="sidebar-menu">
            <li><a href="${pageContext.request.contextPath}/dashboard" class="sidebar-link"><i class="fas fa-home"></i> Inicio</a></li>
            <li><a href="${pageContext.request.contextPath}/pacientes?accion=listar" class="sidebar-link"><i class="fas fa-user-injured"></i> Pacientes</a></li>
            <li><a href="${pageContext.request.contextPath}/citas" class="sidebar-link active"><i class="fas fa-calendar-alt"></i> Agenda</a></li>
            <li><a href="${pageContext.request.contextPath}/facturacion" class="sidebar-link"><i class="fas fa-file-invoice-dollar"></i> Facturación</a></li>
            <li style="margin-top: auto;">
                <a href="${pageContext.request.contextPath}/login?action=logout" class="sidebar-link text-danger"><i class="fas fa-sign-out-alt"></i> Salir</a>
            </li>
        </ul>
    </nav>

    <main class="main-content">

        <!-- ALERTAS -->
        <% if (error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-triangle"></i> <%= error %> <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <% } %>
        <% if (exitoParam != null) { %>
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle"></i> Operación exitosa. <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <% } %>

        <div class="row">
            <!-- IZQUIERDA: FORMULARIO CREAR / EDITAR -->
            <div class="col-md-4">
                <h4 class="fw-bold mb-3"><%= esEdicion ? "Editar Cita #" + idCitaVal : "Nueva Cita" %></h4>
                <div class="form-card">
                    <form action="${pageContext.request.contextPath}/citas" method="POST">
                        <input type="hidden" name="accion" value="guardar">
                        <input type="hidden" name="idCita" value="<%= idCitaVal %>">
                        <input type="hidden" name="estado" value="<%= estadoVal %>">

                        <div class="mb-3">
                            <label class="form-label-custom">Paciente</label>
                            <select name="idPaciente" class="form-select form-control-custom" required>
                                <option selected disabled value="">Seleccionar Paciente...</option>
                                <% for (Paciente p : listaPacientes) {
                                    String selected = (p.getIdPaciente() == idPacVal) ? "selected" : ""; %>
                                <option value="<%= p.getIdPaciente() %>" <%= selected %>>
                                    <%= p.getApellidos() %> <%= p.getNombres() %> (CI: <%= p.getCedula() %>)
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label-custom">Odontólogo</label>
                            <select name="idOdontologo" class="form-select form-control-custom" required>
                                <option selected disabled value="">Seleccionar Doctor...</option>
                                <% for (Odontologo o : listaOdontologos) {
                                    String nombreDoc = (o.getUsuario() != null) ? o.getUsuario().getNombreCompleto() : "Dr. X";
                                    String selected = (o.getIdOdontologo() == idOdoVal) ? "selected" : ""; %>
                                <option value="<%= o.getIdOdontologo() %>" <%= selected %>>
                                    <%= nombreDoc %> (<%= o.getEspecialidad() %>)
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="row mb-3">
                            <div class="col-6">
                                <label class="form-label-custom">Fecha</label>
                                <input type="date" name="fecha" class="form-control form-control-custom" value="<%= fechaVal %>" required>
                            </div>
                            <div class="col-6">
                                <label class="form-label-custom">Hora</label>
                                <input type="time" name="hora" class="form-control form-control-custom" value="<%= horaVal %>" required>
                            </div>
                        </div>

                        <!-- 2. FORMULARIO DE CREACIÓN CON VALIDACIÓN -->
                        <div class="mb-3">
                            <label class="form-label-custom">Motivo</label>
                            <!-- Agregamos minlength para evitar motivos vacíos o absurdos como "." -->
                            <input type="text" name="motivo" class="form-control form-control-custom"
                                   placeholder="Ej. Endodoncia" maxlength="255" minlength="4" required
                                   value="<%= motivoVal %>">
                        </div>

                        <div class="d-flex gap-2">
                            <% if (esEdicion) { %>
                            <a href="${pageContext.request.contextPath}/citas" class="btn btn-secondary w-50">Cancelar</a>
                            <% } %>
                            <button type="submit" class="btn btn-primary-custom w-100"><%= esEdicion ? "Actualizar" : "Agendar" %></button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- DERECHA: TABLA Y BÚSQUEDA -->
            <div class="col-md-8">
                <!-- BARRAS DE HERRAMIENTAS -->
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="fw-bold m-0"><%= tituloTabla %></h4>
                    <div class="btn-group">
                        <a href="${pageContext.request.contextPath}/citas" class="btn btn-outline-primary btn-sm <%= tituloTabla.contains("Día") ? "active" : "" %>">Hoy</a>
                        <a href="${pageContext.request.contextPath}/citas?accion=historial" class="btn btn-outline-secondary btn-sm <%= tituloTabla.contains("Historial") ? "active" : "" %>">Historial</a>
                    </div>
                </div>

                <!-- 1. BUSCADOR CON VALIDACIÓN -->
                <form action="${pageContext.request.contextPath}/citas" method="GET" class="mb-3 d-flex">
                    <input type="hidden" name="accion" value="buscar">
                    <!--
                         pattern="\d+": Solo acepta números.
                         minlength="3": Al menos 3 números para buscar.
                         title: Mensaje que sale si escriben letras.
                    -->
                    <input type="text" name="busqueda" class="form-control form-control-custom me-2"
                           placeholder="Buscar por Cédula (Solo números)..."
                           pattern="\d+" title="Por favor ingrese solo números válidos para la cédula" required>
                    <button type="submit" class="btn btn-secondary rounded-4"><i class="fas fa-search"></i></button>
                </form>

                <div class="custom-table-container">
                    <table class="table table-custom table-hover">
                        <thead>
                        <tr>
                            <th>Fecha/Hora</th>
                            <th>Paciente</th>
                            <th>Odontólogo</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% if (listaCitas.isEmpty()) { %>
                        <tr><td colspan="5" class="text-center py-5 text-muted">No se encontraron citas.</td></tr>
                        <% } else {
                            for (Cita c : listaCitas) {
                                String fechaStr = c.getFechaHora().format(fmtFecha) + " " + c.getFechaHora().format(fmtHora);
                                String nomPac = c.getPaciente().getNombres() + " " + c.getPaciente().getApellidos();
                                String nomDoc = c.getOdontologo().getUsuario().getNombreCompleto();
                                String st = c.getEstado();
                                String badge = "bg-secondary";
                                if ("Pendiente".equals(st)) badge = "bg-warning text-dark";
                                if ("Atendida".equals(st)) badge = "bg-success";
                        %>
                        <tr>
                            <td class="fw-bold" style="font-size: 0.9rem"><%= fechaStr %></td>
                            <td><%= nomPac %></td>
                            <td><small><%= nomDoc %></small></td>
                            <td><span class="badge <%= badge %>"><%= st %></span></td>
                            <td>
                                <!-- EDITAR (Carga datos en form) -->
                                <a href="${pageContext.request.contextPath}/citas?accion=editar&id=<%= c.getIdCita() %>" class="btn-action-edit me-1" title="Editar">
                                    <i class="fas fa-edit"></i>
                                </a>

                                <!-- FINALIZAR (Solo si está Pendiente) -->
                                <% if ("Pendiente".equals(st)) { %>
                                <form action="${pageContext.request.contextPath}/citas" method="POST" style="display:inline;">
                                    <input type="hidden" name="accion" value="finalizar">
                                    <input type="hidden" name="id" value="<%= c.getIdCita() %>">
                                    <button type="submit" class="btn btn-success btn-sm p-1 px-2" title="Marcar como Atendida" onclick="return confirm('¿Finalizar esta cita?')">
                                        <i class="fas fa-check"></i>
                                    </button>
                                </form>
                                <% } %>
                            </td>
                        </tr>
                        <% }} %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>