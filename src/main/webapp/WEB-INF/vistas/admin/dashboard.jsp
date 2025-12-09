<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, models.*" %>

<!--
=============================================================================
VISTA: DASHBOARD ADMINISTRADOR (dashboard.jsp)
Autor: Byron Melo
Fecha: 05/12/2025
Versión: 3.3
Descripción:
Panel de gestión de usuarios. Permite al administrador crear, editar y desactivar
cualquier tipo de rol (Admin, Secretaria, Odontólogo).

CORRECCIÓN DE ROLES:
- ID 1: Administrador
- ID 2: Odontólogo (Ajustado según BD)
- ID 3: Secretaria (Ajustado según BD)
=============================================================================
-->

<%
    // 1. RECUPERACIÓN DE DATOS
    List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
    if (usuarios == null) usuarios = new ArrayList<>();

    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Gestión de Personal";

    Boolean esPapelera = (Boolean) request.getAttribute("esPapelera");
    if (esPapelera == null) esPapelera = false;

    String error = request.getParameter("error");
    String exito = request.getParameter("exito");

    // 2. VARIABLES PARA MODAL DE EDICIÓN
    Usuario uEdit = (Usuario) request.getAttribute("usuarioEditar");
    Odontologo odoEdit = (Odontologo) request.getAttribute("odontologoExtra");

    int idVal = 0;
    String nomVal = "", userVal = "", mailVal = "";
    int rolVal = 3; // Default Secretaria (Ahora es ID 3)
    String espVal = "", codVal = "";
    String tituloModal = "Registrar Personal";
    String btnModal = "Guardar";

    if (uEdit != null) {
        idVal = uEdit.getIdUsuario();
        nomVal = (uEdit.getNombreCompleto() != null) ? uEdit.getNombreCompleto() : "";
        userVal = (uEdit.getUsername() != null) ? uEdit.getUsername() : "";
        mailVal = (uEdit.getEmail() != null) ? uEdit.getEmail() : "";
        if (uEdit.getRol() != null) rolVal = uEdit.getRol().getIdRol();

        tituloModal = "Editar Usuario";
        btnModal = "Actualizar";
    }

    if (odoEdit != null) {
        espVal = (odoEdit.getEspecialidad() != null) ? odoEdit.getEspecialidad() : "";
        codVal = (odoEdit.getCodigoMedico() != null) ? odoEdit.getCodigoMedico() : "";
    }

    Boolean mostrarModal = (Boolean) request.getAttribute("mostrarModal");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Admin - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<div class="dashboard-wrapper">
    <!-- SIDEBAR -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inicio"/>
    </jsp:include>

    <main class="main-content">

        <!-- ALERTAS -->
        <% if(exito != null) { %>
        <div class="alert alert-success alert-dismissible fade show">
            <i class="fas fa-check-circle me-2"></i> Operación exitosa.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>
        <% if(error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show">
            <i class="fas fa-exclamation-triangle me-2"></i> <%=error%>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold text-dark"><%= titulo %></h2>

            <div class="d-flex gap-2">
                <% if (!esPapelera) { %>
                <!-- BOTÓN CREAR ADMIN (NUEVO) -->
                <button class="btn btn-dark" onclick="abrirModalCrear(1)" title="Nuevo Administrador">
                    <i class="fas fa-user-shield"></i> Admin
                </button>

                <!-- BOTÓN CREAR SECRETARIA (ID 3) -->
                <button class="btn btn-info text-white" onclick="abrirModalCrear(3)" title="Nueva Secretaria">
                    <i class="fas fa-user-plus me-2"></i> Secretaria
                </button>

                <!-- BOTÓN CREAR ODONTÓLOGO (ID 2) -->
                <button class="btn btn-primary-custom" onclick="abrirModalCrear(2)" title="Nuevo Odontólogo">
                    <i class="fas fa-user-md me-2"></i> Odontólogo
                </button>

                <!-- BOTÓN VER INACTIVOS -->
                <a href="admin?accion=inactivos" class="btn btn-secondary" title="Papelera">
                    <i class="fas fa-trash-restore"></i>
                </a>
                <% } else { %>
                <a href="admin" class="btn btn-outline-primary">
                    <i class="fas fa-arrow-left me-2"></i> Volver a Activos
                </a>
                <% } %>
            </div>
        </div>

        <!-- TABLA DE USUARIOS -->
        <div class="custom-table-container">
            <table class="table table-custom table-hover align-middle">
                <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Usuario</th>
                    <th>Rol</th>
                    <th>Email</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <%
                    if (usuarios.isEmpty()) {
                %>
                <tr><td colspan="5" class="text-center py-4 text-muted">No hay usuarios registrados.</td></tr>
                <%
                } else {
                    for (Usuario u : usuarios) {
                        String rol = (u.getRol() != null) ? u.getRol().getNombreRol() : "Sin Rol";

                        String badgeClass = "bg-secondary";
                        if("Administrador".equalsIgnoreCase(rol)) badgeClass = "bg-dark";
                        if("Secretaria".equalsIgnoreCase(rol)) badgeClass = "bg-info text-dark";
                        if("Odontologo".equalsIgnoreCase(rol)) badgeClass = "bg-primary";
                %>
                <tr>
                    <td class="fw-bold"><%= u.getNombreCompleto() %></td>
                    <td><%= u.getUsername() %></td>
                    <td><span class="badge <%= badgeClass %>"><%= rol %></span></td>
                    <td><%= u.getEmail() %></td>
                    <td>
                        <% if (!esPapelera) { %>
                        <a href="admin?accion=editar&id=<%= u.getIdUsuario() %>" class="btn-action-edit me-2" title="Editar">
                            <i class="fas fa-edit"></i>
                        </a>

                        <!-- BOTÓN DESACTIVAR CON MODAL -->
                        <button onclick="abrirModalDesactivar(<%= u.getIdUsuario() %>, '<%= u.getNombreCompleto() %>')"
                                class="btn-action-delete border-0 bg-transparent text-danger" title="Desactivar">
                            <i class="fas fa-user-times"></i>
                        </button>

                        <% } else { %>
                        <a href="admin?accion=activar&id=<%= u.getIdUsuario() %>" class="text-success fw-bold text-decoration-none" title="Reactivar">
                            <i class="fas fa-check-circle me-1"></i> Reactivar
                        </a>
                        <% } %>
                    </td>
                </tr>
                <% }} %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!-- MODAL USUARIO (Crear/Editar) -->
<div class="modal fade" id="modalUsuario" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="tituloModal"><%= tituloModal %></h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="admin" method="POST">
                <div class="modal-body p-4">
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idUsuario" id="idUsuario" value="<%= idVal %>">

                    <div class="mb-3">
                        <label class="fw-bold">Rol</label>
                        <!--
                            CORRECCIÓN DE ROLES APLICADA:
                            - ID 2: Odontólogo
                            - ID 3: Secretaria

                            Controlamos el bloqueo visual (pointer-events) vía JS.
                        -->
                        <select name="idRol" id="idRol" class="form-select bg-light" onchange="toggleCamposOdontologo()">
                            <option value="1">Administrador</option>
                            <option value="3">Secretaria</option>
                            <option value="2">Odontólogo</option>
                        </select>
                    </div>

                    <!-- VALIDACIÓN DE FRONTEND -->
                    <div class="mb-3">
                        <label>Nombre Completo</label>
                        <input type="text" name="nombre" id="nombre" class="form-control"
                               required
                               pattern="[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+"
                               title="El nombre solo puede contener letras y espacios."
                               value="<%= nomVal %>">
                    </div>

                    <div class="mb-3">
                        <label>Usuario (Login)</label>
                        <input type="text" name="username" id="username" class="form-control" required value="<%= userVal %>">
                    </div>
                    <div class="mb-3">
                        <label>Contraseña</label>
                        <input type="password" name="password" class="form-control"
                               placeholder="<%= (idVal > 0) ? "Dejar vacío para no cambiar" : "Requerido" %>"
                            <%= (idVal == 0) ? "required" : "" %>>
                    </div>
                    <div class="mb-3">
                        <label>Email</label>
                        <input type="email" name="email" id="email" class="form-control" value="<%= mailVal %>">
                    </div>

                    <!-- CAMPOS EXTRA ODONTÓLOGO -->
                    <div id="camposOdontologo" style="display: none; background-color: #f8f9fa; padding: 15px; border-radius: 10px;">
                        <h6 class="text-primary fw-bold border-bottom pb-2 mb-3">Datos Profesionales</h6>
                        <div class="mb-3">
                            <label>Especialidad</label>
                            <input type="text" name="especialidad" id="especialidad" class="form-control" value="<%= espVal %>">
                        </div>
                        <div class="mb-3">
                            <label>Código Médico / Licencia</label>
                            <input type="text" name="codigo" id="codigo" class="form-control" value="<%= codVal %>">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- MODAL DESACTIVAR USUARIO -->
<div class="modal fade" id="modalDesactivar" tabindex="-1">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Desactivar Usuario</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <i class="fas fa-user-slash fa-3x text-danger mb-3"></i>
                <p>¿Estás seguro que deseas desactivar el acceso a <strong id="nombreUsuarioDesactivar"></strong>?</p>
                <small class="text-muted">El usuario ya no podrá iniciar sesión.</small>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <a href="#" id="btnConfirmarDesactivar" class="btn btn-danger">Sí, Desactivar</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var modal = new bootstrap.Modal(document.getElementById('modalUsuario'));
    var modalDesactivar = new bootstrap.Modal(document.getElementById('modalDesactivar'));

    function toggleCamposOdontologo() {
        var rol = document.getElementById("idRol").value;
        var div = document.getElementById("camposOdontologo");
        // CORRECCIÓN: Ahora el Odontólogo es el Rol ID 2
        if(rol == "2") {
            div.style.display = "block";
            document.getElementById("especialidad").required = true;
        } else {
            div.style.display = "none";
            document.getElementById("especialidad").required = false;
        }
    }

    function abrirModalCrear(idRolPreseleccionado) {
        document.getElementById("tituloModal").innerText = "Registrar Personal";
        document.getElementById("idUsuario").value = "0"; // Nuevo
        document.getElementById("nombre").value = "";
        document.getElementById("username").value = "";
        document.getElementById("email").value = "";
        document.getElementById("especialidad").value = "";
        document.getElementById("codigo").value = "";

        // Seleccionamos el rol correcto
        var selectRol = document.getElementById("idRol");
        selectRol.value = idRolPreseleccionado;

        // Bloqueamos el select visualmente para creación
        selectRol.style.pointerEvents = "none";
        selectRol.classList.add("bg-light");

        toggleCamposOdontologo();
        modal.show();
    }

    function abrirModalDesactivar(id, nombre) {
        document.getElementById("nombreUsuarioDesactivar").innerText = nombre;
        document.getElementById("btnConfirmarDesactivar").href = "admin?accion=eliminar&id=" + id;
        modalDesactivar.show();
    }

    // Auto-abrir si venimos del servidor (Edición)
    <% if(mostrarModal != null && mostrarModal) { %>
    var selectRol = document.getElementById("idRol");
    selectRol.value = "<%= rolVal %>";

    // En MODO EDICIÓN: Desbloqueamos el select para permitir cambiar el rol
    selectRol.style.pointerEvents = "auto";
    selectRol.classList.remove("bg-light");

    toggleCamposOdontologo();
    modal.show();
    <% } %>
</script>

</body>
</html>