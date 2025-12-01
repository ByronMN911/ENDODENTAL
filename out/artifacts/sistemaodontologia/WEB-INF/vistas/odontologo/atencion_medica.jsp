<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Atención Médica - EndoDental</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
</head>
<body>

<div class="dashboard-wrapper">
    <nav class="sidebar" style="width: 100px; padding: 20px 10px; text-align: center;">
        <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="Logo" style="width: 50px; margin-bottom: 30px;">

        <ul class="sidebar-menu">
            <li>
                <a href="${pageContext.request.contextPath}/SvDashboardOdontologo" class="sidebar-link justify-content-center" title="Volver al Dashboard">
                    <i class="fas fa-arrow-left me-0"></i>
                </a>
            </li>
        </ul>
    </nav>

    <main class="main-content" style="margin-left: 100px;">

        <div class="patient-header-card">
            <div class="row align-items-center">
                <div class="col-md-1">
                    <div class="bg-soft-blue rounded-circle d-flex align-items-center justify-content-center" style="width: 60px; height: 60px;">
                        <i class="fas fa-user fs-3 text-primary"></i>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="patient-data-label">Paciente</div>
                    <div class="patient-data-value">Carlos Ruiz</div>
                </div>
                <div class="col-md-2">
                    <div class="patient-data-label">Edad / Sexo</div>
                    <div class="patient-data-value">28 Años / M</div>
                </div>
                <div class="col-md-4">
                    <div class="patient-data-label">Alertas Médicas</div>
                    <div class="allergy-alert">
                        <i class="fas fa-exclamation-triangle"></i> Alérgico a Penicilina
                    </div>
                </div>
                <div class="col-md-2 text-end">
                    <button class="btn btn-outline-secondary btn-sm rounded-pill">Ver Historia Completa</button>
                </div>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/SvAtencion" method="POST">

            <input type="hidden" name="accion" value="guardar_atencion">
            <input type="hidden" name="id_cita" value="1">

            <div class="row">
                <div class="col-lg-7">
                    <div class="form-card h-100">
                        <h5 class="fw-bold mb-4 text-primary"><i class="fas fa-notes-medical me-2"></i> Registro Clínico</h5>

                        <div class="mb-4">
                            <label class="form-label-custom">Diagnóstico (CIE-10 o Descriptivo)</label>
                            <textarea name="diagnostico" class="clinical-textarea w-100" rows="4" placeholder="Describa el diagnóstico del paciente..." required></textarea>
                        </div>

                        <div class="mb-4">
                            <label class="form-label-custom">Tratamiento Realizado (Narrativa)</label>
                            <textarea name="tratamiento_realizado" class="clinical-textarea w-100" rows="4" placeholder="Describa el procedimiento realizado paso a paso..." required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label-custom">Notas Adicionales / Receta</label>
                            <textarea name="notas_adicionales" class="clinical-textarea w-100" rows="3" placeholder="Indicaciones para el paciente..."></textarea>
                        </div>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="form-card h-100 bg-soft-blue border-0">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="fw-bold text-dark m-0">Procedimientos & Insumos</h5>
                            <button type="button" class="btn btn-sm btn-light text-primary fw-bold rounded-pill shadow-sm" data-bs-toggle="modal" data-bs-target="#modalProcedimiento">
                                <i class="fas fa-plus"></i> Agregar
                            </button>
                        </div>
                        <p class="text-muted small mb-3">Agregue los servicios realizados o insumos gastados para el registro.</p>

                        <div class="bg-white rounded-4 p-3 shadow-sm mb-4" style="min-height: 200px;">
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item border-0 d-flex justify-content-between align-items-center px-0">
                                    <div>
                                        <div class="fw-bold">Endodoncia Unirradicular</div>
                                        <div class="text-muted small">Pieza 14</div>
                                    </div>
                                    <button type="button" class="btn btn-sm text-danger"><i class="fas fa-times"></i></button>
                                </li>
                                <li class="list-group-item border-0 d-flex justify-content-between align-items-center px-0">
                                    <div>
                                        <div class="fw-bold">Anestesia Local (Ampolla)</div>
                                        <div class="text-muted small">Insumo</div>
                                    </div>
                                    <button type="button" class="btn btn-sm text-danger"><i class="fas fa-times"></i></button>
                                </li>
                            </ul>
                        </div>

                        <div class="mt-auto">
                            <button type="submit" class="btn-finish-consultation w-100 py-3 shadow">
                                <i class="fas fa-check-circle me-2"></i> Terminar Consulta
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </main>
</div>

<div class="modal fade" id="modalProcedimiento" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content rounded-4 border-0">
            <div class="modal-header border-0">
                <h5 class="modal-title fw-bold">Agregar Procedimiento</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form>
                    <div class="mb-3">
                        <label class="form-label-custom">Tipo</label>
                        <select class="form-select form-control-custom">
                            <option value="servicio">Servicio (Procedimiento)</option>
                            <option value="producto">Producto (Insumo)</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label-custom">Nombre</label>
                        <select class="form-select form-control-custom">
                            <option>Endodoncia</option>
                            <option>Resina</option>
                            <option>Exodoncia</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label-custom