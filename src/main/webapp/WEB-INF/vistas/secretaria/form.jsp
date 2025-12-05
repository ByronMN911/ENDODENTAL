<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Importamos el modelo para poder usar sus getters -->
<%@ page import="models.Paciente" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Formulario Paciente - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
</head>
<body>

<%
    // 1. RECUPERAR DATOS DEL SERVLET
    // El servlet siempre manda un atributo "paciente". Si es nuevo, viene vacío (id=0).
    // Si es edición, viene con datos.
    Paciente p = (Paciente) request.getAttribute("paciente");
    String error = (String) request.getAttribute("error");

    // Validamos que p no sea nulo para evitar errores (NullPointerException)
    if (p == null) {
        p = new Paciente(); // Creamos uno vacío por seguridad
    }

    // Determinamos si es Edición o Creación para cambiar el título
    boolean esEdicion = (p.getIdPaciente() > 0);
    String titulo = esEdicion ? "Editar Datos del Paciente" : "Registrar Nuevo Paciente";
    String textoBoton = esEdicion ? "Guardar Cambios" : "Registrar Paciente";
%>

<div class="dashboard-wrapper">
    <!-- SIDEBAR -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="pacientes"/>
    </jsp:include>

    <main class="main-content">
        <div class="mb-4">
            <a href="${pageContext.request.contextPath}/pacientes?accion=listar" class="text-decoration-none text-muted">
                <i class="fas fa-arrow-left"></i> Volver a la lista
            </a>
            <!-- Título Dinámico -->
            <h2 class="fw-bold mt-2"><%= titulo %></h2>
        </div>

        <!-- ALERTA DE ERROR (Si existe) -->
        <% if (error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> <%= error %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% } %>

        <div class="form-card">
            <!-- El action apunta al Servlet '/pacientes' con método POST -->
            <form action="${pageContext.request.contextPath}/pacientes" method="POST">

                <!-- CAMPO OCULTO VITAL: El ID del Paciente -->
                <!-- Si es 0, el DAO hará INSERT. Si es > 0, hará UPDATE -->
                <input type="hidden" name="idPaciente" value="<%= p.getIdPaciente() %>">

                <div class="row g-4">
                    <div class="col-md-6">
                        <label class="form-label-custom">Nombres</label>
                        <!-- Usamos operador ternario para evitar imprimir "null" -->
                        <input type="text" name="nombres" class="form-control form-control-custom" required
                               value="<%= (p.getNombres() != null) ? p.getNombres() : "" %>">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label-custom">Apellidos</label>
                        <input type="text" name="apellidos" class="form-control form-control-custom" required
                               value="<%= (p.getApellidos() != null) ? p.getApellidos() : "" %>">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label-custom">Cédula</label>
                        <input type="text" name="cedula" class="form-control form-control-custom" required
                               value="<%= (p.getCedula() != null) ? p.getCedula() : "" %>">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label-custom">Teléfono</label>
                        <input type="text" name="telefono" class="form-control form-control-custom"
                               value="<%= (p.getTelefono() != null) ? p.getTelefono() : "" %>">
                    </div>

                    <div class="col-md-6">
                        <label class="form-label-custom">Correo Electrónico</label>
                        <input type="email" name="email" class="form-control form-control-custom"
                               value="<%= (p.getEmail() != null) ? p.getEmail() : "" %>">
                    </div>

                    <div class="col-md-12">
                        <label class="form-label-custom">Alergias / Notas Médicas</label>
                        <!-- En Textarea el valor va ENTRE las etiquetas, no en atributo value -->
                        <textarea name="alergias" class="form-control form-control-custom" rows="3"
                                  placeholder="Ej. Penicilina, Ibuprofeno..."><%= (p.getAlergias() != null) ? p.getAlergias() : "" %></textarea>
                    </div>

                    <div class="col-12 mt-4 text-end">
                        <a href="${pageContext.request.contextPath}/pacientes?accion=listar" class="btn btn-secondary rounded-4 me-2 px-4">Cancelar</a>
                        <button type="submit" class="btn btn-primary-custom px-5"><%= textoBoton %></button>
                    </div>
                </div>
            </form>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>