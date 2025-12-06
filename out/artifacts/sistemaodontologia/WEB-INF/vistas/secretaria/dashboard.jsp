<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // RECUPERACIÓN SEGURA DE DATOS (Evita Error 500 si vienen nulos)

    // 1. Citas de Hoy
    Object citasObj = request.getAttribute("citasHoy");
    int citasHoy = (citasObj != null) ? (Integer) citasObj : 0;

    // 2. Pacientes Totales
    Object pacientesObj = request.getAttribute("pacientesTotal");
    int pacientesTotal = (pacientesObj != null) ? (Integer) pacientesObj : 0;

    // 3. Ingresos de Facturación (Dato Real)
    Object ingresosObj = request.getAttribute("ingresosHoy");
    double ingresosHoy = (ingresosObj != null) ? (Double) ingresosObj : 0.0;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>EndoDental - Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<div class="dashboard-wrapper">

    <!-- IMPORTAMOS EL SIDEBAR -->
    <!-- El parámetro 'inicio' marca el botón de Inicio como activo -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inicio"/>
    </jsp:include>

    <main class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-5">
            <div>
                <h2 class="fw-bold text-dark">Panel de Control</h2>
                <p class="text-muted">Resumen de actividades del día.</p>
            </div>
            <!-- FOTO DE PERFIL AUMENTADA -->
            <div class="d-flex align-items-center">
                <img src="${pageContext.request.contextPath}/assets/img/6.jpg" alt="User" class="team-photo shadow-sm"
                     style="width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 4px solid white;">
            </div>
        </div>

        <!-- CARDS CON DATOS REALES DE LA BASE DE DATOS -->
        <div class="row g-4 mb-5">
            <!-- Citas de Hoy -->
            <div class="col-md-4">
                <div class="stat-card p-4 rounded-4 shadow-sm bg-white border-0 h-100 d-flex justify-content-between align-items-center">
                    <div class="stat-info">
                        <h3 class="fw-bold text-primary mb-1"><%= citasHoy %></h3>
                        <p class="text-muted mb-0">Citas para Hoy</p>
                    </div>
                    <div class="stat-icon bg-light text-primary p-3 rounded-circle fs-4">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                </div>
            </div>

            <!-- Pacientes Activos -->
            <div class="col-md-4">
                <div class="stat-card p-4 rounded-4 shadow-sm bg-white border-0 h-100 d-flex justify-content-between align-items-center">
                    <div class="stat-info">
                        <h3 class="fw-bold text-info mb-1"><%= pacientesTotal %></h3>
                        <p class="text-muted mb-0">Pacientes Activos</p>
                    </div>
                    <div class="stat-icon bg-light text-info p-3 rounded-circle fs-4">
                        <i class="fas fa-users"></i>
                    </div>
                </div>
            </div>

            <!-- Facturación Real -->
            <div class="col-md-4">
                <div class="stat-card p-4 rounded-4 shadow-sm bg-white border-0 h-100 d-flex justify-content-between align-items-center">
                    <div class="stat-info">
                        <!-- Formateamos a 2 decimales para mostrar centavos -->
                        <h3 class="fw-bold text-success mb-1">$<%= String.format("%.2f", ingresosHoy) %></h3>
                        <p class="text-muted mb-0">Facturado Hoy</p>
                    </div>
                    <div class="stat-icon bg-light text-success p-3 rounded-circle fs-4">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>