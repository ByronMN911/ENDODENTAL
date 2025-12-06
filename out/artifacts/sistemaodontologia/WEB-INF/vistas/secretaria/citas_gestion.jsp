<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, java.time.format.DateTimeFormatter, java.time.LocalDate" %>
<%@ page import="models.*" %>

<%
    // RECUPERACIÓN DE DATOS
    List<Cita> listaCitas = (List<Cita>) request.getAttribute("citas");
    if (listaCitas == null) listaCitas = new ArrayList<>();

    List<Paciente> listaPacientes = (List<Paciente>) request.getAttribute("listaPacientes");
    if (listaPacientes == null) listaPacientes = new ArrayList<>();

    List<Odontologo> listaOdontologos = (List<Odontologo>) request.getAttribute("listaOdontologos");
    if (listaOdontologos == null) listaOdontologos = new ArrayList<>();

    String titulo = (String) request.getAttribute("titulo");
    String fechaFiltro = (String) request.getAttribute("fechaFiltro");
    if (fechaFiltro == null) fechaFiltro = LocalDate.now().toString();

    String vistaActual = (String) request.getAttribute("vistaActual");
    String error = (String) request.getAttribute("error");
    String exito = request.getParameter("exito");

    DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

    // LOGICA MODAL EDICIÓN
    Cita citaEdit = (Cita) request.getAttribute("citaEditar");
    boolean esEdicion = (citaEdit != null);

    int idVal = esEdicion ? citaEdit.getIdCita() : 0;
    String motivoVal = esEdicion ? citaEdit.getMotivo() : "";
    String fechaVal = esEdicion ? citaEdit.getFechaHora().toLocalDate().toString() : "";
    String horaVal = esEdicion ? citaEdit.getFechaHora().toLocalTime().toString() : "";
    int idPacVal = esEdicion ? citaEdit.getPaciente().getIdPaciente() : 0;
    int idOdoVal = esEdicion ? citaEdit.getOdontologo().getIdOdontologo() : 0;
    String estadoVal = esEdicion ? citaEdit.getEstado() : "Pendiente";
    String tituloModal = esEdicion ? "Editar Cita #" + idVal : "Nueva Cita";
    String btnModal = esEdicion ? "Actualizar" : "Agendar";
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Agenda - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="agenda"/>
    </jsp:include>

    <main class="main-content">

        <% if(error != null) { %><div class="alert alert-danger"><%=error%></div><% } %>
        <% if(exito != null) { %><div class="alert alert-success">Operación exitosa.</div><% } %>

        <!-- ENCABEZADO -->
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
            <div>
                <div class="btn-group mb-2">
                    <a href="citas?accion=agenda&fecha=<%=fechaFiltro%>"
                       class="btn btn-sm <%= "agenda".equals(vistaActual) ? "btn-primary" : "btn-outline-primary" %>">Agenda</a>
                    <a href="citas?accion=facturadas&fecha=<%=fechaFiltro%>"
                       class="btn btn-sm <%= "facturadas".equals(vistaActual) ? "btn-success" : "btn-outline-success" %>">Facturadas</a>
                    <a href="citas?accion=canceladas&fecha=<%=fechaFiltro%>"
                       class="btn btn-sm <%= "canceladas".equals(vistaActual) ? "btn-danger" : "btn-outline-danger" %>">Canceladas</a>
                </div>

                <form action="citas" method="GET" class="d-flex align-items-center gap-2">
                    <input type="hidden" name="accion" value="<%= vistaActual %>">
                    <h4 class="fw-bold m-0 text-dark">Fecha:</h4>
                    <input type="date" name="fecha" value="<%= fechaFiltro %>" class="form-control form-control-sm" style="width: auto;" onchange="this.form.submit()">
                </form>
            </div>

            <div class="d-flex gap-2">
                <form action="citas" method="GET" class="d-flex">
                    <input type="hidden" name="accion" value="buscar">
                    <input type="text" name="busqueda" class="form-control form-control-custom me-2" placeholder="Cédula..." pattern="\d+" required>
                    <button type="submit" class="btn btn-secondary rounded-4"><i class="fas fa-search"></i></button>
                </form>

                <% if("agenda".equals(vistaActual)) { %>
                <button type="button" class="btn btn-primary-custom" onclick="abrirModalCrear()">
                    <i class="fas fa-plus me-2"></i> Agendar
                </button>
                <% } %>
            </div>
        </div>

        <!-- TABLA -->
        <div class="custom-table-container">
            <table class="table table-custom table-hover align-middle">
                <thead>
                <tr>
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
                <tr><td colspan="6" class="text-center py-5 text-muted">No hay citas en esta vista para la fecha seleccionada.</td></tr>
                <% } else {
                    for (Cita c : listaCitas) {
                        String hora = c.getFechaHora().format(fmtHora);
                        String nomPac = c.getPaciente().getNombres() + " " + c.getPaciente().getApellidos();
                        String nomDoc = (c.getOdontologo().getUsuario() != null) ? c.getOdontologo().getUsuario().getNombreCompleto() : "Dr. Desconocido";
                        String st = c.getEstado();

                        String badge = "bg-secondary";
                        if ("Pendiente".equals(st)) badge = "bg-warning text-dark";
                        if ("Atendida".equals(st)) badge = "bg-info text-white"; // Lista para facturar
                        if ("Facturada".equals(st)) badge = "bg-success";
                        if ("Cancelada".equals(st)) badge = "bg-danger";

                        // Datos JS
                        String fIso = c.getFechaHora().toLocalDate().toString();
                        String hIso = c.getFechaHora().toLocalTime().toString();
                %>
                <tr>
                    <td class="fw-bold"><%= hora %></td>
                    <td><%= nomPac %> <br> <small class="text-muted"><%= c.getPaciente().getCedula() %></small></td>
                    <td><%= nomDoc %></td>
                    <td><%= c.getMotivo() %></td>
                    <td><span class="badge <%= badge %>"><%= st %></span></td>
                    <td>
                        <% if ("Pendiente".equals(st)) { %>
                        <!-- SOLO EDITAR Y CANCELAR -->
                        <button class="btn btn-warning btn-sm text-white me-1"
                                onclick="abrirModalEditar(<%= c.getIdCita() %>, '<%= fIso %>', '<%= hIso %>', '<%= c.getMotivo() %>', <%= c.getPaciente().getIdPaciente() %>, <%= c.getOdontologo().getIdOdontologo() %>)">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="abrirModalCancelar(<%= c.getIdCita() %>)">
                            <i class="fas fa-times"></i>
                        </button>

                        <% } else if ("Atendida".equals(st)) { %>
                        <!-- SOLO FACTURAR -->
                        <a href="facturacion?id_cita_pre=<%= c.getIdCita() %>" class="btn btn-primary btn-sm">
                            <i class="fas fa-file-invoice-dollar me-1"></i> Facturar
                        </a>

                        <% } else if ("Facturada".equals(st)) { %>
                        <span class="text-success small"><i class="fas fa-check-double"></i> Facturada</span>
                        <% } else { %>
                        <span class="text-muted small">--</span>
                        <% } %>
                    </td>
                </tr>
                <% }} %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!-- MODAL CITA (Crear/Editar) -->
<div class="modal fade" id="modalCita" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="tituloModalCita"><%= tituloModal %></h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="formCita" action="citas" method="POST">
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idCita" id="idCita" value="<%= idVal %>">
                    <input type="hidden" name="estado" id="estado" value="<%= estadoVal %>">

                    <div class="mb-3">
                        <label class="form-label">Paciente</label>
                        <select name="idPaciente" id="idPaciente" class="form-select" required>
                            <option value="" disabled selected>Seleccione...</option>
                            <% for(Paciente p : listaPacientes) {
                                String sel = (p.getIdPaciente() == idPacVal) ? "selected" : ""; %>
                            <option value="<%= p.getIdPaciente() %>" <%= sel %>><%= p.getNombres() %> <%= p.getApellidos() %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Odontólogo</label>
                        <select name="idOdontologo" id="idOdontologo" class="form-select" required>
                            <option value="" disabled selected>Seleccione...</option>
                            <% for(Odontologo o : listaOdontologos) {
                                String sel = (o.getIdOdontologo() == idOdoVal) ? "selected" : "";
                                String nomDoc = (o.getUsuario() != null) ? o.getUsuario().getNombreCompleto() : "Doc"; %>
                            <option value="<%= o.getIdOdontologo() %>" <%= sel %>><%= nomDoc %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="row mb-3">
                        <div class="col-6">
                            <label>Fecha</label>
                            <input type="date" name="fecha" id="fecha" class="form-control" required value="<%= fechaVal %>">
                        </div>
                        <div class="col-6">
                            <label>Hora</label>
                            <input type="time" name="hora" id="hora" class="form-control" required value="<%= horaVal %>">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label>Motivo</label>
                        <input type="text" name="motivo" id="motivo" class="form-control" required value="<%= motivoVal %>">
                    </div>

                    <div class="text-end">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                        <button type="submit" class="btn btn-primary"><%= btnModal %></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- MODAL CANCELAR -->
<div class="modal fade" id="modalCancelar" tabindex="-1">
    <div class="modal-dialog modal-sm">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Cancelar Cita</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <p>¿Seguro que deseas cancelar esta cita?</p>
            </div>
            <div class="modal-footer justify-content-center">
                <form action="citas" method="POST">
                    <input type="hidden" name="accion" value="cancelar">
                    <input type="hidden" name="id" id="idCitaCancelar">
                    <input type="hidden" name="fechaActual" value="<%= fechaFiltro %>">
                    <button type="submit" class="btn btn-danger">Sí, Cancelar</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    var modalCita = new bootstrap.Modal(document.getElementById('modalCita'));
    var modalCancelar = new bootstrap.Modal(document.getElementById('modalCancelar'));

    function abrirModalCrear() {
        document.getElementById("tituloModalCita").innerText = "Nueva Cita";
        document.getElementById("formCita").reset();
        document.getElementById("idCita").value = "0";
        document.getElementById("estado").value = "Pendiente";

        // Auto-fecha hoy
        const now = new Date();
        document.getElementById("fecha").value = now.toISOString().split('T')[0];

        modalCita.show();
    }

    function abrirModalEditar(id, fecha, hora, motivo, idPac, idOdo) {
        document.getElementById("tituloModalCita").innerText = "Editar Cita #" + id;
        document.getElementById("idCita").value = id;
        document.getElementById("fecha").value = fecha;
        document.getElementById("hora").value = hora;
        document.getElementById("motivo").value = motivo;
        document.getElementById("idPaciente").value = idPac;
        document.getElementById("idOdontologo").value = idOdo;
        modalCita.show();
    }

    function abrirModalCancelar(id) {
        document.getElementById("idCitaCancelar").value = id;
        modalCancelar.show();
    }

    // Auto-abrir si venimos de editar desde el servidor
    <% if (esEdicion) { %>
    modalCita.show();
    <% } %>
</script>

</body>
</html>