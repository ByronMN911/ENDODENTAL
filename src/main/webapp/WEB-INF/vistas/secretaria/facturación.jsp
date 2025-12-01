<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Facturación - EndoDental</title>
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
                <a href="${pageContext.request.contextPath}/SvCitas" class="sidebar-link">
                    <i class="fas fa-calendar-alt"></i> Agenda
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/SvFacturacion" class="sidebar-link active">
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
        <h2 class="fw-bold mb-4">Generar Factura</h2>

        <form action="${pageContext.request.contextPath}/SvFacturacion" method="POST">

            <input type="hidden" name="accion" value="generar">

            <div class="row">
                <div class="col-md-8">
                    <div class="form-card mb-4">
                        <h5 class="fw-bold mb-3 border-bottom pb-2">Datos de Facturación</h5>
                        <div class="row g-3">
                            <div class="col-md-12">
                                <label class="form-label-custom">Cita a Facturar</label>
                                <select name="id_cita" class="form-select form-control-custom">
                                    <option value="10">Luis Gomez - Limpieza (Hoy)</option>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label-custom">Identificación (RUC/Cédula)</label>
                                <input type="text" name="identificacion_cliente" class="form-control form-control-custom" placeholder="Ej. 17..." required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label-custom">Nombre Cliente</label>
                                <input type="text" name="nombre_cliente_factura" class="form-control form-control-custom" required>
                            </div>
                            <div class="col-md-12">
                                <label class="form-label-custom">Dirección</label>
                                <input type="text" name="direccion_cliente" class="form-control form-control-custom">
                            </div>
                        </div>
                    </div>

                    <div class="custom-table-container">
                        <div class="d-flex justify-content-between mb-3">
                            <h5 class="fw-bold">Detalle de Servicios/Productos</h5>
                            <button type="button" class="btn btn-sm btn-outline-primary rounded-pill" data-bs-toggle="modal" data-bs-target="#modalAgregarItem">
                                <i class="fas fa-plus me-1"></i> Agregar Item
                            </button>
                        </div>
                        <table class="table table-custom">
                            <thead>
                            <tr>
                                <th>Tipo</th>
                                <th>Descripción</th>
                                <th>Zona/Diente</th>
                                <th>Cant.</th>
                                <th>Precio Unit.</th>
                                <th>Subtotal</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr>
                                <td>Servicio</td>
                                <td>Profilaxis (Limpieza)</td>
                                <td>General</td>
                                <td>1</td>
                                <td>$40.00</td>
                                <td>$40.00</td>
                            </tr>
                            <tr>
                                <td>Producto</td>
                                <td>Kit Higiene</td>
                                <td>-</td>
                                <td>1</td>
                                <td>$10.00</td>
                                <td>$10.00</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="stat-card d-block">
                        <h4 class="fw-bold mb-4">Resumen</h4>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Subtotal:</span>
                            <input type="hidden" name="subtotal" value="50.00">
                            <span class="fw-bold">$50.00</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">IVA (15%):</span>
                            <input type="hidden" name="monto_iva" value="7.50">
                            <span class="fw-bold">$7.50</span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between mb-4">
                            <span class="fw-bold fs-4">Total a Pagar:</span>
                            <input type="hidden" name="total_pagar" value="57.50">
                            <span class="fw-bold fs-4 text-primary">$57.50</span>
                        </div>

                        <div class="mb-3">
                            <label class="form-label-custom">Método de Pago</label>
                            <select name="metodo_pago" class="form-select form-control-custom">
                                <option value="Efectivo">Efectivo</option>
                                <option value="Tarjeta">Tarjeta</option>
                                <option value="Transferencia">Transferencia</option>
                            </select>
                        </div>

                        <button type="submit" class="btn btn-primary-custom w-100 py-3 mt-2">
                            <i class="fas fa-check-circle me-2"></i> Confirmar y Facturar
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </main>
</div>

<div class="modal fade" id="modalAgregarItem" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius: 20px; border: none;">

            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold ms-3 mt-3">Agregar Item a la Factura</h5>
                <button type="button" class="btn-close me-3 mt-3" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body p-4">
                <form id="formAgregarItem">
                    <div class="row g-3">

                        <div class="col-md-12">
                            <label class="form-label-custom">Tipo de Item</label>
                            <div class="d-flex gap-3">
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="tipo_item" id="radioServicio" value="servicio" checked>
                                    <label class="form-check-label" for="radioServicio">Servicio Médico</label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="tipo_item" id="radioProducto" value="producto">
                                    <label class="form-check-label" for="radioProducto">Producto / Insumo</label>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-12">
                            <label class="form-label-custom">Descripción</label>
                            <select name="id_item" class="form-select form-control-custom">
                                <option selected disabled>Seleccione...</option>
                                <option value="1">Profilaxis (Limpieza)</option>
                                <option value="2">Resina Simple</option>
                                <option value="3">Kit de Higiene</option>
                            </select>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label-custom">Diente / Zona</label>
                            <input type="text" name="diente_o_zona" class="form-control form-control-custom" placeholder="Ej. Muela 18 / General">
                        </div>

                        <div class="col-md-6">
                            <label class="form-label-custom">Cantidad</label>
                            <input type="number" name="cantidad" class="form-control form-control-custom" value="1" min="1">
                        </div>

                        <div class="col-md-12">
                            <label class="form-label-custom">Precio Unitario ($)</label>
                            <input type="number" step="0.01" name="precio_unitario" class="form-control form-control-custom" placeholder="0.00">
                        </div>
                    </div>

                    <div class="text-end mt-4">
                        <button type="button" class="btn btn-secondary rounded-4 me-2" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary-custom px-4">Agregar a la lista</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>