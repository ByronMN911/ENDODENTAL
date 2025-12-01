<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Agenda de Citas - EndoDental</title>
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
                <a href="${pageContext.request.contextPath}/SvDashboardSecretaria" class="sidebar-link">
                    <i class="fas fa-home"></i> Inicio
                </a>
            </li>

            <li>
                <a href="${pageContext.request.contextPath}/SvPacientes?accion=listar" class="sidebar-link">
                    <i class="fas fa-user-injured"></i> Pacientes
                </a>
            </li>

            <li>
                <a href="${pageContext.request.contextPath}/SvCitas" class="sidebar-link active">
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
        <div class="row">
            <div class="col-md-4">
                <h4 class="fw-bold mb-3">Nueva Cita</h4>
                <div class="form-card">
                    <form action="${pageContext.request.contextPath}/SvCitas" method="POST">

                        <input type="hidden" name="accion" value="agendar">

                        <div class="mb-3">
                            <label class="form-label-custom">Paciente</label>
                            <select name="id_paciente" class="form-select form-control-custom" required>
                                <option selected disabled>Seleccionar Paciente...</option>
                                <option value="1">María López</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label-custom">Odontólogo</label>
                            <select name="id_odontologo" class="form-select form-control-custom" required>
                                <option selected disabled>Seleccionar Doctor...</option>
                                <option value="1">Dra. Giomara Silva</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label-custom">Fecha y Hora</label>
                            <input type="datetime-local" name="fecha_hora" class="form-control form-control-custom" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label-custom">Motivo</label>
                            <input type="text" name="motivo" class="form-control form-control-custom" placeholder="Ej. Dolor de muela" maxlength="255">
                        </div>
                        <div class="mb-3">
                            <label class="form-label-custom">Estado</label>
                            <select name="estado" class="form-select form-control-custom">
                                <option value="Pendiente" selected>Pendiente</option>
                                <option value="Confirmada">Confirmada</option>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary-custom w-100">Agendar Cita</button>
                    </form>
                </div>
            </div>

            <div class="col-md-8">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="fw-bold">Agenda del Día</h4>
                    <span class="badge bg-white text-dark border px-3 py-2 rounded-pill">Hoy</span>
                </div>

                <div class="custom-table-container">
                    <table class="table table-custom">
                        <thead>
                        <tr>
                            <th>Fecha/Hora</th>
                            <th>Paciente</th>
                            <th>Odontólogo</th>
                            <th>Motivo</th>
                            <th>Estado</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                            <td class="fw-bold">2025-11-30 09:00</td>
                            <td>Carlos Ruiz</td>
                            <td>Dra. Giomara Silva</td>
                            <td>Endodoncia</td>
                            <td><span class="badge bg-warning text-dark">Pendiente</span></td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>