<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Inventario - EndoDental</title>
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
                <a href="${pageContext.request.contextPath}/SvFacturacion" class="sidebar-link">
                    <i class="fas fa-file-invoice-dollar"></i> Facturación
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/SvInventario" class="sidebar-link active">
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
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold">Inventario de Productos</h2>
            <button class="btn btn-primary-custom" data-bs-toggle="modal" data-bs-target="#modalNuevoProducto">
                <i class="fas fa-plus me-2"></i> Nuevo Producto
            </button>
        </div>

        <div class="custom-table-container">
            <table class="table table-custom table-hover">
                <thead>
                <tr>
                    <th>Nombre Producto</th>
                    <th>Marca</th>
                    <th>Precio Venta</th>
                    <th>Stock Actual</th>
                    <th>Stock Mínimo</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Anestesia Local</td>
                    <td>3M</td>
                    <td>$15.00</td>
                    <td class="fw-bold text-danger">2</td>
                    <td>10</td>
                    <td><span class="badge bg-danger">Bajo Stock</span></td>
                    <td>
                        <a href="#" class="btn-action-edit"><i class="fas fa-edit"></i></a>
                    </td>
                </tr>
                <tr>
                    <td>Guantes Látex</td>
                    <td>Top Glove</td>
                    <td>$8.50</td>
                    <td class="fw-bold">50</td>
                    <td>20</td>
                    <td><span class="badge bg-success">OK</span></td>
                    <td>
                        <a href="#" class="btn-action-edit"><i class="fas fa-edit"></i></a>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </main>
</div>

<div class="modal fade" id="modalNuevoProducto" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content" style="border-radius: 20px; border: none;">

            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold ms-3 mt-3">Registrar Nuevo Producto</h5>
                <button type="button" class="btn-close me-3 mt-3" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body p-4">
                <form action="${pageContext.request.contextPath}/SvInventario" method="POST">

                    <input type="hidden" name="accion" value="crear">

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label-custom">Nombre del Producto</label>
                            <input type="text" name="nombre" class="form-control form-control-custom" required placeholder="Ej. Resina A2">
                        </div>

                        <div class="col-md-6">
                            <label class="form-label-custom">Marca</label>
                            <input type="text" name="marca" class="form-control form-control-custom" placeholder="Ej. 3M">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label-custom">Precio Venta ($)</label>
                            <input type="number" step="0.01" name="precio_venta" class="form-control form-control-custom" required placeholder="0.00">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label-custom">Stock Inicial</label>
                            <input type="number" name="stock" class="form-control form-control-custom" required placeholder="0">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label-custom">Stock Mínimo (Alerta)</label>
                            <input type="number" name="stock_minimo" class="form-control form-control-custom" required placeholder="Ej. 10">
                        </div>

                        <div class="col-12">
                            <label class="form-label-custom">Descripción</label>
                            <textarea name="descripcion" class="form-control form-control-custom" rows="3" placeholder="Detalles del producto..."></textarea>
                        </div>
                    </div>

                    <div class="text-end mt-4">
                        <button type="button" class="btn btn-secondary rounded-4 me-2" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary-custom px-4">Guardar Producto</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>