<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- IMPORTANTE: Importar las clases Java que vamos a usar -->
<%@ page import="java.util.List" %>
<%@ page import="models.Paciente" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Pacientes - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
</head>
<body>

<!-- LÓGICA JAVA PREVIA (SCRIPTLET) -->
<%
    // Recuperamos los atributos enviados por el Servlet
    List<Paciente> pacientes = (List<Paciente>) request.getAttribute("pacientes");
    String titulo = (String) request.getAttribute("titulo");
    // Manejo seguro del booleano (puede venir nulo)
    Boolean esPapeleraObj = (Boolean) request.getAttribute("esPapelera");
    boolean esPapelera = (esPapeleraObj != null) ? esPapeleraObj : false;

    if (titulo == null) titulo = "Gestión de Pacientes";
%>

<div class="dashboard-wrapper">
    <!-- SIDEBAR (Igual que antes) -->
    <nav class="sidebar">
        <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="EndoDental" class="sidebar-logo">
        <ul class="sidebar-menu">
            <li><a href="${pageContext.request.contextPath}/dashboard" class="sidebar-link"><i class="fas fa-home"></i> Inicio</a></li>
            <li><a href="${pageContext.request.contextPath}/pacientes?accion=listar" class="sidebar-link active"><i class="fas fa-user-injured"></i> Pacientes</a></li>
            <!-- ... resto del menú ... -->
            <li style="margin-top: auto;">
                <a href="${pageContext.request.contextPath}/login?action=logout" class="sidebar-link text-danger">
                    <i class="fas fa-sign-out-alt"></i> Salir
                </a>
            </li>
        </ul>
    </nav>

    <main class="main-content">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <!-- EXPRESION  Para imprimir variables -->
                <h2 class="fw-bold"><%= titulo %></h2>

                <% if (esPapelera) { %>
                <a href="${pageContext.request.contextPath}/pacientes?accion=listar" class="btn btn-outline-secondary btn-sm">
                    <i class="fas fa-arrow-left"></i> Volver a Activos
                </a>
                <% } else { %>
                <a href="${pageContext.request.contextPath}/pacientes?accion=inactivos" class="btn btn-outline-secondary btn-sm">
                    <i class="fas fa-trash-restore"></i> Ver Papelera
                </a>
                <% } %>
            </div>

            <% if (!esPapelera) { %>
            <a href="${pageContext.request.contextPath}/pacientes?accion=form" class="btn btn-primary-custom">
                <i class="fas fa-plus me-2"></i> Nuevo Paciente
            </a>
            <% } %>
        </div>

        <div class="custom-table-container">
            <!-- BUSCADOR -->
            <div class="mb-4">
                <form action="${pageContext.request.contextPath}/pacientes" method="GET" class="d-flex w-50">
                    <input type="hidden" name="accion" value="buscar">
                    <input type="text" name="busqueda" class="form-control form-control-custom me-2" placeholder="Buscar por cédula...">
                    <button type="submit" class="btn btn-secondary rounded-4"><i class="fas fa-search"></i></button>
                </form>
            </div>

            <table class="table table-custom table-hover">
                <thead>
                <tr>
                    <th>Cédula</th>
                    <th>Apellidos</th>
                    <th>Nombres</th>
                    <th>Teléfono</th>
                    <th>Email</th>
                    <th>Alergias</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <%
                    // BUCLE FOR TRADICIONAL DE JAVA
                    if (pacientes != null && !pacientes.isEmpty()) {
                        for (Paciente p : pacientes) {
                %>
                <tr>
                    <td><%= p.getCedula() %></td>
                    <td><%= p.getApellidos() %></td>
                    <td><%= p.getNombres() %></td>
                    <td><%= p.getTelefono() %></td>
                    <td><%= p.getEmail() %></td>
                    <td>
                        <% if (p.getAlergias() != null && !p.getAlergias().equals("Ninguna")) { %>
                        <span class="badge bg-danger"><%= p.getAlergias() %></span>
                        <% } else { %>
                        <span class="text-muted small">Ninguna</span>
                        <% } %>
                    </td>
                    <td>
                        <% if (esPapelera) { %>
                        <!-- Botón ACTIVAR -->
                        <a href="${pageContext.request.contextPath}/pacientes?accion=activar&id=<%= p.getIdPaciente() %>"
                           class="btn btn-success btn-sm">
                            <i class="fas fa-undo"></i> Activar
                        </a>
                        <% } else { %>
                        <!-- Botones NORMALES -->
                        <a href="${pageContext.request.contextPath}/pacientes?accion=form&id=<%= p.getIdPaciente() %>"
                           class="btn-action-edit me-2">
                            <i class="fas fa-edit"></i>
                        </a>
                        <!-- OJO: Pasamos parámetros a la función JS usando comillas simples escapadas -->
                        <button onclick="confirmarEliminacion(<%= p.getIdPaciente() %>, '<%= p.getNombres() %> <%= p.getApellidos() %>')"
                                class="btn-action-delete border-0 bg-transparent">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                        <% } %>
                    </td>
                </tr>
                <%
                    } // Fin del for
                } else {
                %>
                <tr>
                    <td colspan="7" class="text-center py-4">No se encontraron pacientes.</td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!-- MODAL (Igual que antes, no cambia porque es HTML/JS puro) -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirmar Eliminación</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                ¿Estás seguro que deseas archivar al paciente <strong id="nombrePacienteModal"></strong>?
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <a href="#" id="btnConfirmarEliminar" class="btn btn-danger">Eliminar</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmarEliminacion(id, nombre) {
        document.getElementById("nombrePacienteModal").innerText = nombre;
        const ruta = "${pageContext.request.contextPath}/pacientes?accion=eliminar&id=" + id;
        document.getElementById("btnConfirmarEliminar").href = ruta;
        const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
        modal.show();
    }
</script>

</body>
</html>