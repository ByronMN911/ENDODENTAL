<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- IMPORTANTE: Importar las clases Java necesarias para manejar listas y objetos del modelo -->
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="models.Paciente" %>

<!--
=============================================================================
VISTA: LISTADO DE PACIENTES (lista.jsp)
Autor: Byron Melo
Fecha: 05/12/2025
Versión: 3.1
Descripción:
Vista actualizada para mostrar alertas de error dentro de los modales.
=============================================================================
-->

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Pacientes - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<%
    /* LÓGICA DE PRESENTACIÓN */
    List<Paciente> pacientes = (List<Paciente>) request.getAttribute("pacientes");
    if (pacientes == null) pacientes = new ArrayList<>();

    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Gestión de Pacientes";

    Boolean esPapeleraObj = (Boolean) request.getAttribute("esPapelera");
    boolean esPapelera = (esPapeleraObj != null) ? esPapeleraObj : false;

    String error = (String) request.getAttribute("error");
    String exito = request.getParameter("exito");

    // LÓGICA MODAL
    Paciente pEdit = (Paciente) request.getAttribute("pacienteEditar");
    int idVal = 0;
    String cedulaVal = "", nombresVal = "", apellidosVal = "";
    String telefonoVal = "", emailVal = "", alergiasVal = "";
    String tituloModal = "Registrar Nuevo Paciente";
    String btnModal = "Guardar Paciente";

    if (pEdit != null) {
        idVal = pEdit.getIdPaciente();
        cedulaVal = (pEdit.getCedula() != null) ? pEdit.getCedula() : "";
        nombresVal = (pEdit.getNombres() != null) ? pEdit.getNombres() : "";
        apellidosVal = (pEdit.getApellidos() != null) ? pEdit.getApellidos() : "";
        telefonoVal = (pEdit.getTelefono() != null) ? pEdit.getTelefono() : "";
        emailVal = (pEdit.getEmail() != null) ? pEdit.getEmail() : "";
        alergiasVal = (pEdit.getAlergias() != null) ? pEdit.getAlergias() : "";

        if (idVal > 0) {
            tituloModal = "Editar Datos del Paciente";
            btnModal = "Actualizar Datos";
        }
    }

    Boolean mostrarModalObj = (Boolean) request.getAttribute("mostrarModal");
    boolean mostrarModal = (mostrarModalObj != null) ? mostrarModalObj : false;
%>

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="pacientes"/>
    </jsp:include>

    <main class="main-content">

        <!--
            ALERTA GLOBAL (Solo para errores fuera del modal, ej: búsqueda)
            Se muestra si hay error Y NO estamos mostrando el modal.
        -->
        <% if (error != null && !mostrarModal) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> <%= error %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% } %>

        <% if (exito != null) { %>
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-2"></i> Operación exitosa. <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <% } %>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><%= titulo %></h2>
                <div class="btn-group">
                    <a href="pacientes?accion=listar" class="btn btn-sm <%= (!esPapelera) ? "btn-primary" : "btn-outline-primary" %>">Activos</a>
                    <a href="pacientes?accion=inactivos" class="btn btn-sm <%= (esPapelera) ? "btn-danger" : "btn-outline-danger" %>">Papelera</a>
                </div>
            </div>

            <% if (!esPapelera) { %>
            <button class="btn btn-primary-custom" onclick="abrirModalNuevo()">
                <i class="fas fa-plus me-2"></i> Nuevo Paciente
            </button>
            <% } %>
        </div>

        <div class="custom-table-container">
            <form action="pacientes" method="GET" class="mb-4 d-flex w-50">
                <input type="hidden" name="accion" value="buscar">
                <input type="text" name="busqueda" class="form-control form-control-custom me-2"
                       placeholder="Buscar por cédula..." pattern="\d+" title="Solo números" required>
                <button type="submit" class="btn btn-secondary rounded-4"><i class="fas fa-search"></i></button>
            </form>

            <table class="table table-custom table-hover align-middle">
                <thead>
                <tr>
                    <th>Cédula</th>
                    <th>Apellidos</th>
                    <th>Nombres</th>
                    <th>Contacto</th>
                    <th>Alergias</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <% if (pacientes.isEmpty()) { %>
                <tr><td colspan="6" class="text-center py-4 text-muted">No se encontraron pacientes registrados.</td></tr>
                <% } else {
                    for (Paciente p : pacientes) { %>
                <tr>
                    <td class="fw-bold"><%= p.getCedula() %></td>
                    <td><%= p.getApellidos() %></td>
                    <td><%= p.getNombres() %></td>
                    <td>
                        <div class="small"><i class="fas fa-phone me-1"></i> <%= p.getTelefono() %></div>
                        <div class="small text-muted"><i class="fas fa-envelope me-1"></i> <%= p.getEmail() %></div>
                    </td>
                    <td>
                        <% if (p.getAlergias() != null && !p.getAlergias().isEmpty() && !p.getAlergias().equalsIgnoreCase("Ninguna")) { %>
                        <span class="badge bg-danger"><%= p.getAlergias() %></span>
                        <% } else { %>
                        <span class="text-muted small">Ninguna</span>
                        <% } %>
                    </td>
                    <td>
                        <% if (esPapelera) { %>
                        <a href="pacientes?accion=activar&id=<%= p.getIdPaciente() %>" class="btn btn-success btn-sm"><i class="fas fa-undo"></i> Restaurar</a>
                        <% } else { %>
                        <button class="btn btn-warning btn-sm text-white me-1"
                                onclick="abrirModalEditar(<%= p.getIdPaciente() %>, '<%= p.getCedula() %>', '<%= p.getNombres() %>', '<%= p.getApellidos() %>', '<%= p.getTelefono() %>', '<%= p.getEmail() %>', '<%= p.getAlergias() %>')">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="abrirModalEliminar(<%= p.getIdPaciente() %>, '<%= p.getNombres() %>')">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                        <% } %>
                    </td>
                </tr>
                <% }} %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!-- MODAL 1: FORMULARIO DE PACIENTE -->
<div class="modal fade" id="modalPaciente" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="modalTitle"><%= tituloModal %></h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">

                <!-- ALERTA DE ERROR INTERNA DEL MODAL -->
                <% if (error != null && mostrarModal) { %>
                <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i> <strong>Error:</strong> <%= error %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% } %>

                <form id="formPaciente" action="pacientes" method="POST">
                    <input type="hidden" name="idPaciente" id="idPaciente" value="<%= idVal %>">

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Cédula</label>
                            <input type="text" name="cedula" id="cedula" class="form-control form-control-custom"
                                   required pattern="\d{10}" maxlength="10" title="10 dígitos numéricos" value="<%= cedulaVal %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Nombres</label>
                            <input type="text" name="nombres" id="nombres" class="form-control form-control-custom"
                                   required pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+" title="Solo letras y espacios" value="<%= nombresVal %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Apellidos</label>
                            <input type="text" name="apellidos" id="apellidos" class="form-control form-control-custom"
                                   required pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+" title="Solo letras y espacios" value="<%= apellidosVal %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Teléfono</label>
                            <input type="text" name="telefono" id="telefono" class="form-control form-control-custom"
                                   pattern="\d+" title="Solo números" value="<%= telefonoVal %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Email</label>
                            <input type="email" name="email" id="email" class="form-control form-control-custom" value="<%= emailVal %>">
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Alergias</label>
                            <textarea name="alergias" id="alergias" class="form-control form-control-custom" rows="2"><%= alergiasVal %></textarea>
                        </div>
                    </div>

                    <div class="text-end mt-4">
                        <button type="button" class="btn btn-secondary rounded-pill me-2" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary-custom px-4 rounded-pill" id="btnGuardar"><%= btnModal %></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- MODAL 2: CONFIRMACIÓN DE ELIMINACIÓN -->
<div class="modal fade" id="modalEliminar" tabindex="-1">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Archivar Paciente</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <p>¿Archivar a <strong id="nombrePacEliminar"></strong>?</p>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                <a href="#" id="btnConfirmarEliminar" class="btn btn-danger">Sí, Archivar</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    var modalPaciente = new bootstrap.Modal(document.getElementById('modalPaciente'));
    var modalEliminar = new bootstrap.Modal(document.getElementById('modalEliminar'));

    function abrirModalNuevo() {
        document.getElementById("formPaciente").reset();
        document.getElementById("idPaciente").value = "0";
        document.getElementById("modalTitle").innerText = "Registrar Nuevo Paciente";
        document.getElementById("btnGuardar").innerText = "Guardar Paciente";

        // Limpiar valores previos (importante para que no quede basura del scriptlet)
        document.getElementById("cedula").value = "";
        document.getElementById("nombres").value = "";
        document.getElementById("apellidos").value = "";
        document.getElementById("telefono").value = "";
        document.getElementById("email").value = "";
        document.getElementById("alergias").value = "";

        modalPaciente.show();
    }

    function abrirModalEditar(id, ced, nom, ape, tel, mail, aler) {
        document.getElementById("modalTitle").innerText = "Editar Paciente";
        document.getElementById("btnGuardar").innerText = "Actualizar Datos";

        document.getElementById("idPaciente").value = id;
        document.getElementById("cedula").value = ced;
        document.getElementById("nombres").value = nom;
        document.getElementById("apellidos").value = ape;
        document.getElementById("telefono").value = tel;
        document.getElementById("email").value = mail;
        document.getElementById("alergias").value = aler;

        modalPaciente.show();
    }

    function abrirModalEliminar(id, nombre) {
        document.getElementById("nombrePacEliminar").innerText = nombre;
        document.getElementById("btnConfirmarEliminar").href = "pacientes?accion=eliminar&id=" + id;
        modalEliminar.show();
    }

    // Auto-abrir modal si hay error o venimos de editar (server-side)
    <% if (mostrarModal) { %>
    modalPaciente.show();
    <% } %>
</script>

</body>
</html>