<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, models.*" %>

<%
    List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
    if (usuarios == null) usuarios = new ArrayList<>();

    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Gestión de Personal";

    Boolean esPapelera = (Boolean) request.getAttribute("esPapelera");
    if (esPapelera == null) esPapelera = false;

    String error = request.getParameter("error");
    String exito = request.getParameter("exito");

    // Variables Edición
    Usuario uEdit = (Usuario) request.getAttribute("usuarioEditar");
    Odontologo odoEdit = (Odontologo) request.getAttribute("odontologoExtra");

    int idVal = (uEdit != null) ? uEdit.getIdUsuario() : 0;
    String nomVal = (uEdit != null) ? uEdit.getNombreCompleto() : "";
    String userVal = (uEdit != null) ? uEdit.getUsername() : "";
    String mailVal = (uEdit != null) ? uEdit.getEmail() : "";
    int rolVal = (uEdit != null) ? uEdit.getRol().getIdRol() : 2; // Default Secretaria

    String espVal = (odoEdit != null) ? odoEdit.getEspecialidad() : "";
    String codVal = (odoEdit != null) ? odoEdit.getCodigoMedico() : "";

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
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inicio"/>
    </jsp:include>

    <main class="main-content">

        <% if(exito != null) { %><div class="alert alert-success alert-dismissible fade show">Operación exitosa.<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><% } %>
        <% if(error != null) { %><div class="alert alert-danger"><%=error%></div><% } %>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold text-dark"><%= titulo %></h2>

            <div class="d-flex gap-2">
                <% if (!esPapelera) { %>
                <button class="btn btn-info text-white" onclick="abrirModalCrear(2)">
                    <i class="fas fa-user-plus me-2"></i> Secretaria
                </button>
                <button class="btn btn-primary-custom" onclick="abrirModalCrear(3)">
                    <i class="fas fa-user-md me-2"></i> Odontólogo
                </button>
                <a href="admin?accion=inactivos" class="btn btn-secondary">
                    <i class="fas fa-trash-restore"></i> Inactivos
                </a>
                <% } else { %>
                <a href="admin" class="btn btn-outline-primary">
                    <i class="fas fa-arrow-left"></i> Volver
                </a>
                <% } %>
            </div>
        </div>

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
                <% for (Usuario u : usuarios) {
                    String rol = u.getRol().getNombreRol();
                    String badgeClass = "bg-secondary";
                    if("Administrador".equals(rol)) badgeClass = "bg-dark";
                    if("Secretaria".equals(rol)) badgeClass = "bg-info text-dark";
                    if("Odontologo".equals(rol)) badgeClass = "bg-primary";
                %>
                <tr>
                    <td class="fw-bold"><%= u.getNombreCompleto() %></td>
                    <td><%= u.getUsername() %></td>
                    <td><span class="badge <%= badgeClass %>"><%= rol %></span></td>
                    <td><%= u.getEmail() %></td>
                    <td>
                        <% if (!esPapelera) { %>
                        <a href="admin?accion=editar&id=<%= u.getIdUsuario() %>" class="btn-action-edit me-2"><i class="fas fa-edit"></i></a>

                        <!-- BOTÓN DESACTIVAR (Abre Modal) -->
                        <button onclick="abrirModalDesactivar(<%= u.getIdUsuario() %>, '<%= u.getNombreCompleto() %>')"
                                class="btn-action-delete border-0 bg-transparent text-danger" title="Desactivar">
                            <i class="fas fa-user-times"></i>
                        </button>

                        <% } else { %>
                        <a href="admin?accion=activar&id=<%= u.getIdUsuario() %>" class="text-success fw-bold text-decoration-none"><i class="fas fa-check-circle"></i> Reactivar</a>
                        <% } %>
                    </td>
                </tr>
                <% } %>
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
                <h5 class="modal-title" id="tituloModal">Editar Usuario</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="admin" method="POST">
                <div class="modal-body p-4">
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idUsuario" id="idUsuario" value="<%= idVal %>">

                    <div class="mb-3">
                        <label class="fw-bold">Rol</label>
                        <select name="idRol" id="idRol" class="form-select" onchange="toggleCamposOdontologo()">
                            <option value="2">Secretaria</option>
                            <option value="3">Odontólogo</option>
                            <option value="1">Administrador</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label>Nombre Completo</label>
                        <input type="text" name="nombre" id="nombre" class="form-control" required value="<%= nomVal %>">
                    </div>
                    <div class="mb-3">
                        <label>Usuario (Login)</label>
                        <input type="text" name="username" id="username" class="form-control" required value="<%= userVal %>">
                    </div>
                    <div class="mb-3">
                        <label>Contraseña</label>
                        <input type="password" name="password" class="form-control" placeholder="<%= (idVal > 0) ? "Dejar vacío para no cambiar" : "Requerido" %>" <%= (idVal == 0) ? "required" : "" %>>
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
                    <button type="submit" class="btn btn-primary">Actualizar</button>
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
                <!-- Enlace dinámico -->
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
        if(rol == "3") {
            div.style.display = "block";
            document.getElementById("especialidad").required = true;
        } else {
            div.style.display = "none";
            document.getElementById("especialidad").required = false;
        }
    }

    function abrirModalCrear(idRolPreseleccionado) {
        document.getElementById("tituloModal").innerText = "Registrar Personal";
        document.getElementById("idUsuario").value = "0";
        document.getElementById("nombre").value = "";
        document.getElementById("username").value = "";
        document.getElementById("email").value = "";

        document.getElementById("idRol").value = idRolPreseleccionado;
        toggleCamposOdontologo();

        modal.show();
    }

    // Función para abrir el modal de desactivación
    function abrirModalDesactivar(id, nombre) {
        document.getElementById("nombreUsuarioDesactivar").innerText = nombre;
        // Configuramos el enlace para que apunte al Servlet con la acción 'eliminar'
        document.getElementById("btnConfirmarDesactivar").href = "admin?accion=eliminar&id=" + id;
        modalDesactivar.show();
    }

    <% if(mostrarModal != null && mostrarModal) { %>
    document.getElementById("idRol").value = "<%= rolVal %>";
    toggleCamposOdontologo();
    modal.show();
    <% } %>
</script>

</body>
</html>