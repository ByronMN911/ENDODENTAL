<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="models.Paciente" %>
<%@ page import="models.Odontologo" %>
<%@ page import="models.Cita" %>
<%@ page import="models.Usuario" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Gestión de Citas - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
</head>
<body>

<%
    // --- RECUPERACIÓN DE DATOS ---
    List<Paciente> listaPacientes = (List<Paciente>) request.getAttribute("listaPacientes");
    if (listaPacientes == null) listaPacientes = new ArrayList<>();

    List<Odontologo> listaOdontologos = (List<Odontologo>) request.getAttribute("listaOdontologos");
    if (listaOdontologos == null) listaOdontologos = new ArrayList<>();

    List<Cita> listaCitas = (List<Cita>) request.getAttribute("citas");
    if (listaCitas == null) listaCitas = new ArrayList<>();

    String titulo = (String) request.getAttribute("titulo");
    // Si no hay título, mostramos la fecha actual del servidor para evitar confusión
    if (titulo == null || titulo.equals("Agenda del Día")) {
        titulo = "Agenda del: " + LocalDate.now().toString();
    }

    String error = (String) request.getAttribute("error");
    String exito = request.getParameter("exito");

    // Recuperar fecha de filtro si existe, sino usar hoy
    String fechaFiltro = request.getParameter("fecha");
    if (fechaFiltro == null) fechaFiltro = LocalDate.now().toString();

    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuario");
    String nombreUsuario = (usuarioLogueado != null) ? usuarioLogueado.getNombreCompleto() : "Secretaria";

    DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

    String currentQuery = request.getQueryString();
    if (currentQuery == null) currentQuery = "";
%>

<div class="dashboard-wrapper">
    <!-- SIDEBAR REUTILIZABLE -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="agenda"/>
    </jsp:include>

    <main class="main-content">

        <!-- ALERTAS -->
        <% if (error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-exclamation-triangle"></i> <%= error %> <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <% } %>
        <% if (exito != null) { %>
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle"></i> Operación realizada con éxito. <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <% } %>

        <!-- ENCABEZADO Y FILTROS -->
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
            <div>
                <div class="btn-group mb-2">
                    <a href="${pageContext.request.contextPath}/citas"
                       class="btn btn-sm <%= (!currentQuery.contains("accion") || currentQuery.equals("")) ? "btn-primary" : "btn-outline-primary" %>">
                        Agenda
                    </a>
                    <a href="${pageContext.request.contextPath}/citas?accion=historial"
                       class="btn btn-sm <%= (currentQuery.contains("historial")) ? "btn-primary" : "btn-outline-secondary" %>">
                        Atendidas
                    </a>
                    <a href="${pageContext.request.contextPath}/citas?accion=canceladas"
                       class="btn btn-sm <%= (currentQuery.contains("canceladas")) ? "btn-danger" : "btn-outline-danger" %>">
                        Canceladas
                    </a>
                </div>

                <!-- FILTRO DE FECHA -->
                <form action="${pageContext.request.contextPath}/citas" method="GET" class="d-flex align-items-center gap-2">
                    <input type="hidden" name="accion" value="filtrarFecha">
                    <h4 class="fw-bold m-0 text-dark">Agenda del:</h4>
                    <input type="date" name="fecha" value="<%= fechaFiltro %>" class="form-control form-control-sm" style="width: auto;" onchange="this.form.submit()">
                </form>
            </div>

            <div class="d-flex gap-2">
                <form action="${pageContext.request.contextPath}/citas" method="GET" class="d-flex">
                    <input type="hidden" name="accion" value="buscar">
                    <input type="text" name="busqueda" class="form-control form-control-custom me-2" placeholder="Cédula Paciente..." pattern="\d+" title="Solo números" required>
                    <button type="submit" class="btn btn-secondary rounded-4"><i class="fas fa-search"></i></button>
                </form>

                <button type="button" class="btn btn-primary-custom" onclick="abrirModalCrear()">
                    <i class="fas fa-plus me-2"></i> Nueva Cita
                </button>
            </div>
        </div>

        <!-- TABLA DE CITAS -->
        <div class="custom-table-container">
            <table class="table table-custom table-hover align-middle">
                <thead>
                <tr>
                    <th>Fecha</th>
                    <th>Hora</th>
                    <th>Paciente</th>
                    <th>Odontólogo</th>
                    <th>Motivo</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <% if (listaCitas.isEmpty()) { %>
                <tr>
                    <td colspan="7" class="text-center py-5 text-muted">
                        <i class="fas fa-calendar-day fa-2x mb-2"></i><br>
                        No hay citas para la fecha seleccionada (<%= fechaFiltro %>).
                    </td>
                </tr>
                <% } else {
                    for (Cita c : listaCitas) {
                        String fecha = c.getFechaHora().format(fmtFecha);
                        String hora = c.getFechaHora().format(fmtHora);
                        String nomPac = c.getPaciente().getNombres() + " " + c.getPaciente().getApellidos();
                        String nomDoc = c.getOdontologo().getUsuario().getNombreCompleto();
                        String st = c.getEstado();

                        String badge = "bg-secondary";
                        if ("Pendiente".equals(st)) badge = "bg-warning text-dark";
                        if ("Atendida".equals(st)) badge = "bg-success";
                        if ("Cancelada".equals(st)) badge = "bg-danger";

                        String fechaIso = c.getFechaHora().toLocalDate().toString();
                        String horaIso = c.getFechaHora().toLocalTime().toString();
                %>
                <tr>
                    <td><%= fecha %></td>
                    <td class="fw-bold"><%= hora %></td>
                    <td><%= nomPac %> <br> <small class="text-muted"><%= c.getPaciente().getCedula() %></small></td>
                    <td><%= nomDoc %></td>
                    <td><%= c.getMotivo() %></td>
                    <td><span class="badge <%= badge %>"><%= st %></span></td>
                    <td>
                        <% if ("Pendiente".equals(st)) { %>
                        <!-- BOTÓN EDITAR -->
                        <button class="btn btn-warning btn-sm text-white me-1"
                                onclick="abrirModalEditar(<%= c.getIdCita() %>, '<%= fechaIso %>', '<%= horaIso %>', '<%= c.getMotivo() %>', <%= c.getPaciente().getIdPaciente() %>, <%= c.getOdontologo().getIdOdontologo() %>)"
                                title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>

                        <!-- BOTÓN FINALIZAR -->
                        <button class="btn btn-success btn-sm me-1" onclick="abrirModalFinalizar(<%= c.getIdCita() %>)" title="Marcar como Atendida">
                            <i class="fas fa-check"></i>
                        </button>

                        <!-- BOTÓN CANCELAR -->
                        <button class="btn btn-danger btn-sm" onclick="abrirModalCancelar(<%= c.getIdCita() %>)" title="Cancelar">
                            <i class="fas fa-times"></i>
                        </button>
                        <% } else { %>
                        <span class="text-muted small"><i class="fas fa-lock"></i> Cerrada</span>
                        <% } %>
                    </td>
                </tr>
                <% }} %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!-- ================= MODALS ================= -->

<!-- 1. MODAL CITA (Crear y Editar) -->
<div class="modal fade" id="modalCita" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="tituloModalCita">Nueva Cita</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="formCita" action="${pageContext.request.contextPath}/citas" method="POST">
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idCita" id="idCita" value="0">
                    <!-- Corregido: name="estado" para que el Servlet lo lea correctamente -->
                    <input type="hidden" name="estado" id="estadoInput" value="Pendiente">

                    <div class="mb-3">
                        <label class="form-label">Paciente</label>
                        <select name="idPaciente" id="idPaciente" class="form-select" required>
                            <option value="" disabled selected>Seleccione...</option>
                            <% for(Paciente p : listaPacientes) { %>
                            <option value="<%= p.getIdPaciente() %>"><%= p.getNombres() %> <%= p.getApellidos() %> (<%= p.getCedula() %>)</option>
                            <% } %>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Odontólogo</label>
                        <select name="idOdontologo" id="idOdontologo" class="form-select" required>
                            <option value="" disabled selected>Seleccione...</option>
                            <% for(Odontologo o : listaOdontologos) { %>
                            <option value="<%= o.getIdOdontologo() %>"><%= o.getUsuario().getNombreCompleto() %> (<%= o.getEspecialidad() %>)</option>
                            <% } %>
                        </select>
                    </div>

                    <div class="row mb-3">
                        <div class="col-6">
                            <label class="form-label">Fecha</label>
                            <input type="date" name="fecha" id="fecha" class="form-control" required>
                        </div>
                        <div class="col-6">
                            <label class="form-label">Hora</label>
                            <input type="time" name="hora" id="hora" class="form-control" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Motivo</label>
                        <textarea name="motivo" id="motivo" class="form-control" rows="2" required minlength="4"></textarea>
                    </div>

                    <div class="text-end">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                        <button type="submit" class="btn btn-primary">Guardar Datos</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- 2. MODAL FINALIZAR -->
<div class="modal fade" id="modalFinalizar" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title">Finalizar Atención</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <i class="fas fa-check-circle fa-3x text-success mb-3"></i>
                <p>¿Confirmas que el paciente ha sido atendido?</p>
                <small class="text-muted">La cita pasará al historial de atendidas.</small>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <form action="${pageContext.request.contextPath}/citas" method="POST">
                    <input type="hidden" name="accion" value="finalizar">
                    <input type="hidden" name="id" id="idCitaFinalizar">
                    <button type="submit" class="btn btn-success">Sí, Finalizar</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- 3. MODAL CANCELAR -->
<div class="modal fade" id="modalCancelar" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Cancelar Cita</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <i class="fas fa-exclamation-circle fa-3x text-danger mb-3"></i>
                <p>¿Estás seguro de cancelar esta cita? El horario quedará libre.</p>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No, regresar</button>
                <form action="${pageContext.request.contextPath}/citas" method="POST">
                    <input type="hidden" name="accion" value="cancelar">
                    <input type="hidden" name="id" id="idCitaCancelar">
                    <button type="submit" class="btn btn-danger">Sí, Cancelar</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function abrirModalCrear() {
        document.getElementById("tituloModalCita").innerText = "Nueva Cita";
        document.getElementById("formCita").reset();
        document.getElementById("idCita").value = "0";

        // Auto-llenar con fecha y hora actual para facilitar el uso
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');

        document.getElementById("fecha").value = year + '-' + month + '-' + day;
        document.getElementById("hora").value = hours + ':' + minutes;

        var modal = new bootstrap.Modal(document.getElementById('modalCita'));
        modal.show();
    }

    function abrirModalEditar(id, fecha, hora, motivo, idPac, idOdo) {
        document.getElementById("tituloModalCita").innerText = "Editar Cita #" + id;
        document.getElementById("idCita").value = id;
        document.getElementById("fecha").value = fecha;
        document.getElementById("hora").value = hora;
        document.getElementById("motivo").value = motivo;
        document.getElementById("idPaciente").value = idPac;
        document.getElementById("idOdontologo").value = idOdo;
        var modal = new bootstrap.Modal(document.getElementById('modalCita'));
        modal.show();
    }

    function abrirModalCancelar(id) {
        document.getElementById("idCitaCancelar").value = id;
        var modal = new bootstrap.Modal(document.getElementById('modalCancelar'));
        modal.show();
    }

    function abrirModalFinalizar(id) {
        document.getElementById("idCitaFinalizar").value = id;
        var modal = new bootstrap.Modal(document.getElementById('modalFinalizar'));
        modal.show();
    }
</script>

</body>
</html>
