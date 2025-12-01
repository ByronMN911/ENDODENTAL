<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Doctor Dashboard - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/estilos/Style.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
<!--    <link rel="stylesheet" href="../../../assets/estilos/Style.css">
 -->
</head>
<body>

<div class="dashboard-wrapper">
    <nav class="sidebar">
        <img src="<%= request.getContextPath() %>/assets/img/sinfondo.png" alt="EndoDental" class="sidebar-logo">
        <ul class="sidebar-menu">
            <li><a href="<%= request.getContextPath() %>/dashboard_odontologo.jsp" class="sidebar-link active"><i class="fas fa-user-md"></i> Mis Citas</a></li>

            <li style="margin-top: auto;"><a href="<%= request.getContextPath() %>/index.jsp" class="sidebar-link text-danger"><i class="fas fa-sign-out-alt"></i> Salir</a></li>
        </ul>
    </nav>

    <main class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-5">
            <div>
                <h2 class="fw-bold text-dark">Sistema Odontológico Endodental</h2>
                <p class="text-muted">Tienes <strong class="text-primary">Pacientes</strong> programados para hoy.</p>
            </div>
        </div>

        <div class="custom-table-container">
            <h4 class="fw-bold mb-4">Mis Citas de Hoy</h4>

            <table class="table table-custom table-hover">
                <thead>
                <tr>
                    <th>Hora</th>
                    <th>Paciente</th>
                    <th>Motivo Consulta</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td class="fw-bold fs-5">09:00</td>
                    <td>
                        <div class="d-flex flex-column">
                            <span class="fw-bold">Carlos Ruiz</span>
                            <span class="small text-muted">C.I. 1712345678</span>
                        </div>
                    </td>
                    <td>Dolor agudo en premolar</td>
                    <td><span class="badge bg-warning text-dark">En Sala de Espera</span></td>
                    <td>
                        <a href="atencion_medica.jsp?id_cita=1" class="btn btn-primary-custom btn-sm px-4">
                            <i class="fas fa-stethoscope me-2"></i> Atender
                        </a>
                    </td>
                </tr>
                <tr>
                    <td class="fw-bold fs-5">10:30</td>
                    <td>
                        <div class="d-flex flex-column">
                            <span class="fw-bold">Ana Torres</span>
                            <span class="small text-muted">C.I. 0912345678</span>
                        </div>
                    </td>
                    <td>Control Ortodoncia (Mensual)</td>
                    <td><span class="badge bg-info text-dark">Confirmada</span></td>
                    <td>
                        <button class="btn btn-secondary btn-sm px-4" disabled>Esperando</button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </main>
</div>
</body>
</html>