<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Datos enviados por DashboardServlet
    int citasHoy = (int) request.getAttribute("citasHoy");
    int pacientesTotal = (int) request.getAttribute("pacientesTotal");
    double ingresosHoy = (double) request.getAttribute("ingresosHoy");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>EndoDental - Dashboard</title>
    <!-- Tus links CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
</head>
<body>

<div class="dashboard-wrapper">

    <!-- IMPORTAMOS EL SIDEBAR -->
    <!-- Pasamos el parámetro 'inicio' para que se ilumine esa opción -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inicio"/>
    </jsp:include>

    <main class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-5">
            <div>
                <h2 class="fw-bold text-dark">Panel de Control</h2>
                <p class="text-muted">Resumen de actividades del día.</p>
            </div>
            <!-- Foto estática o dinámica si tuvieras la ruta en BD -->
            <div class="d-flex align-items-center">
                <img src="${pageContext.request.contextPath}/assets/img/6.jpg" alt="User" class="team-photo shadow-sm" style="width: 50px; height: 50px; border-radius: 50%; object-fit: cover;">
            </div>
        </div>

        <!-- CARDS CON DATOS REALES -->
        <div class="row g-4 mb-5">
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

            <div class="col-md-4">
                <div class="stat-card p-4 rounded-4 shadow-sm bg-white border-0 h-100 d-flex justify-content-between align-items-center">
                    <div class="stat-info">
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

</body>
</html>