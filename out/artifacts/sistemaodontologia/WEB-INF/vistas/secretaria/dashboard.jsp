<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>EndoDental - Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
</head>
<body>

<div class="dashboard-wrapper">
    <nav class="sidebar">
        <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="EndoDental" class="sidebar-logo">

        <ul class="sidebar-menu">
            <li>
                <a href="${pageContext.request.contextPath}/SvDashboardSecretaria" class="sidebar-link active">
                    <i class="fas fa-home"></i> Inicio
                </a>
            </li>

            <li>
                <a href="<%= request.getContextPath() %>/pacientes?accion=listar" class="sidebar-link">
                    <i class="fas fa-user-injured"></i> Pacientes
                </a>
            </li>

            <li>
                <a href="${pageContext.request.contextPath}/SvCitas" class="sidebar-link">
                    <i class="fas fa-calendar-alt"></i> Agenda
                </a>
            </li>

            <li>
                <a href="${pageContext.request.contextPath}/SvFacturacion" class="sidebar-link">
                    <i class="fas fa-file-invoice-dollar"></i> Facturación
                </a>
            </li>

            <li>
                <a href="${pageContext.request.contextPath}/SvInventario" class="sidebar-link">
                    <i class="fas fa-boxes"></i> Inventario
                </a>
            </li>

            <li style="margin-top: auto;">
                <a href="${pageContext.request.contextPath}/SvLogout" class="sidebar-link text-danger">
                    <i class="fas fa-sign-out-alt"></i> Salir
                </a>
            </li>
        </ul>
    </nav>

    <main class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-5">
            <div>
                <h2 class="fw-bold text-dark">Sistema Interno Endodental</h2>
                <p class="text-muted">Actividades del Día de hoy.</p>
            </div>
            <div class="d-flex align-items-center">
                <img src="${pageContext.request.contextPath}/assets/img/6.jpg" alt="User" class="team-photo" style="width: 50px; height: 50px; border-radius: 50%; object-fit: cover;">
            </div>
        </div>

        <div class="row g-4 mb-5">
            <div class="col-md-4">
                <div class="stat-card">
                    <div class="stat-info">
                        <h3>12</h3>
                        <p>Citas para Hoy</p>
                    </div>
                    <div class="stat-icon"><i class="fas fa-calendar-check"></i></div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card">
                    <div class="stat-info">
                        <h3>85</h3>
                        <p>Pacientes Totales</p>
                    </div>
                    <div class="stat-icon" style="background-color: #E3F2FD; color: #2da1c2;"><i class="fas fa-users"></i></div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="stat-card">
                    <div class="stat-info">
                        <h3>$450</h3>
                        <p>Facturado Hoy</p>
                    </div>
                    <div class="stat-icon" style="background-color: #E8F5E9; color: #4CAF50;"><i class="fas fa-dollar-sign"></i></div>
                </div>
            </div>
        </div>
    </main>
</div>

</body>
</html>