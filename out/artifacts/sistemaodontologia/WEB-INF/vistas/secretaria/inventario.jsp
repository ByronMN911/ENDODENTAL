<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="models.Producto" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Inventario - EndoDental</title>
    <!-- CSS Links -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<%
    // --- LÓGICA JAVA (Scriptlet) ---
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    if (productos == null) productos = new ArrayList<>();

    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Inventario de Productos";

    Boolean esPapeleraObj = (Boolean) request.getAttribute("esPapelera");
    boolean esPapelera = (esPapeleraObj != null) ? esPapeleraObj : false;

    // Objeto para editar (si existe)
    Producto prodEdit = (Producto) request.getAttribute("productoEditar");

    // Valores por defecto para el modal de Crear/Editar
    int idVal = 0;
    String nombreVal = "";
    String marcaVal = "";
    String descVal = "";
    String precioVal = "";
    String stockVal = "";
    String stockMinVal = "";
    String tituloModal = "Registrar Nuevo Producto";
    String btnModal = "Guardar Producto";

    if (prodEdit != null) {
        idVal = prodEdit.getIdProducto();
        nombreVal = prodEdit.getNombre();
        marcaVal = prodEdit.getMarca();
        descVal = prodEdit.getDescripcion();
        precioVal = prodEdit.getPrecioVenta().toString();
        stockVal = String.valueOf(prodEdit.getStock());
        stockMinVal = String.valueOf(prodEdit.getStockMinimo());
        tituloModal = "Editar Producto";
        btnModal = "Actualizar";
    }

    // Bandera para abrir modal automáticamente (desde Servlet)
    Boolean mostrarModalObj = (Boolean) request.getAttribute("mostrarModal");
    boolean mostrarModal = (mostrarModalObj != null) ? mostrarModalObj : false;
%>

<div class="dashboard-wrapper">
    <!-- SIDEBAR -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inventario"/>
    </jsp:include>

    <main class="main-content">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold"><%= titulo %></h2>

            <div>
                <% if (!esPapelera) { %>
                <a href="inventario?accion=inactivos" class="btn btn-secondary me-2" style="border-radius: 10px;">
                    <i class="fas fa-trash-restore me-2"></i> Papelera
                </a>
                <button class="btn btn-primary-custom" onclick="abrirModalNuevo()">
                    <i class="fas fa-plus me-2"></i> Nuevo Producto
                </button>
                <% } else { %>
                <a href="inventario?accion=listar" class="btn btn-outline-primary" style="border-radius: 10px;">
                    <i class="fas fa-arrow-left me-2"></i> Volver al Inventario
                </a>
                <% } %>
            </div>
        </div>

        <div class="custom-table-container">
            <table class="table table-custom table-hover align-middle">
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

                <% if (productos.isEmpty()) { %>
                <tr>
                    <td colspan="7" class="text-center py-4 text-muted">No se encontraron productos.</td>
                </tr>
                <% } else {
                    for (Producto p : productos) {
                        boolean bajoStock = (p.getStock() <= p.getStockMinimo());
                %>
                <tr>
                    <td><%= p.getNombre() %></td>
                    <td><%= p.getMarca() %></td>
                    <td>$ <%= p.getPrecioVenta() %></td>

                    <td class="<%= bajoStock ? "fw-bold text-danger" : "fw-bold" %>">
                        <%= p.getStock() %>
                    </td>

                    <td><%= p.getStockMinimo() %></td>

                    <td>
                        <% if (p.getEstado() == 1) {
                            if (bajoStock) { %>
                        <span class="badge bg-warning text-dark">Bajo Stock</span>
                        <% } else { %>
                        <span class="badge bg-success">OK</span>
                        <% }
                        } else { %>
                        <span class="badge bg-secondary">Inactivo</span>
                        <% } %>
                    </td>

                    <td>
                        <% if (!esPapelera) { %>
                        <!-- MODO NORMAL: Editar y Eliminar -->
                        <a href="inventario?accion=editar&id=<%= p.getIdProducto() %>" class="btn-action-edit me-2" title="Editar">
                            <i class="fas fa-edit"></i>
                        </a>

                        <!-- BOTÓN QUE ABRE EL MODAL DE ELIMINAR -->
                        <button onclick="abrirModalEliminar(<%= p.getIdProducto() %>, '<%= p.getNombre() %>')"
                                class="btn-action-delete border-0 bg-transparent text-danger"
                                title="Eliminar">
                            <i class="fas fa-trash"></i>
                        </button>

                        <% } else { %>
                        <!-- MODO PAPELERA: Reactivar -->
                        <a href="inventario?accion=activar&id=<%= p.getIdProducto() %>"
                           class="text-success fw-bold text-decoration-none"
                           title="Restaurar">
                            <i class="fas fa-check-circle"></i> Reactivar
                        </a>
                        <% } %>
                    </td>
                </tr>
                <% }} %>

                </tbody>
            </table>
        </div>
    </main>
</div>

<!-- ================= MODALS ================= -->

<!-- 1. MODAL NUEVO/EDITAR PRODUCTO -->
<div class="modal fade" id="modalNuevoProducto" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content" style="border-radius: 20px; border: none;">

            <!-- CAMBIO: Header con color primario (Azul) y texto blanco -->
            <div class="modal-header bg-primary text-white border-0 pb-0" style="border-top-left-radius: 20px; border-top-right-radius: 20px; padding-bottom: 1rem !important;">
                <h5 class="modal-title fw-bold ms-3" id="modalTitle"><%= tituloModal %></h5>
                <button type="button" class="btn-close btn-close-white me-3" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body p-4">
                <form id="formProducto" action="inventario" method="POST">

                    <!-- ID Oculto -->
                    <input type="hidden" name="id_producto" id="idProducto" value="<%= idVal %>">

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label-custom">Nombre del Producto</label>
                            <input type="text" name="nombre" id="nombre" class="form-control form-control-custom"
                                   required placeholder="Ej. Resina A2" value="<%= nombreVal %>">
                        </div>

                        <div class="col-md-6">
                            <label class="form-label-custom">Marca</label>
                            <input type="text" name="marca" id="marca" class="form-control form-control-custom"
                                   placeholder="Ej. 3M" value="<%= marcaVal %>">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label-custom">Precio Venta ($)</label>
                            <input type="number" step="0.01" name="precio_venta" id="precio" class="form-control form-control-custom"
                                   required placeholder="0.00" value="<%= precioVal %>">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label-custom">Stock Inicial</label>
                            <input type="number" name="stock" id="stock" class="form-control form-control-custom"
                                   required placeholder="0" value="<%= stockVal %>">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label-custom">Stock Mínimo (Alerta)</label>
                            <input type="number" name="stock_minimo" id="stockMinimo" class="form-control form-control-custom"
                                   required placeholder="Ej. 10" value="<%= stockMinVal %>">
                        </div>

                        <div class="col-12">
                            <label class="form-label-custom">Descripción</label>
                            <textarea name="descripcion" id="descripcion" class="form-control form-control-custom" rows="3"
                                      placeholder="Detalles del producto..."><%= descVal %></textarea>
                        </div>
                    </div>

                    <div class="text-end mt-4">
                        <button type="button" class="btn btn-secondary rounded-4 me-2" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary-custom px-4" id="modalBtn"><%= btnModal %></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- 2. MODAL ELIMINAR PRODUCTO -->
<div class="modal fade" id="modalEliminar" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <!-- Header Rojo para indicar acción de eliminación -->
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Eliminar Producto</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center">
                <i class="fas fa-exclamation-circle fa-3x text-danger mb-3"></i>
                <p>¿Estás seguro que deseas eliminar el producto <strong id="nombreProductoEliminar"></strong>?</p>
                <small class="text-muted">Se moverá a la papelera.</small>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <!-- Enlace dinámico para eliminar -->
                <a href="#" id="btnConfirmarEliminar" class="btn btn-danger">Sí, Eliminar</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    var myModal = new bootstrap.Modal(document.getElementById('modalNuevoProducto'));
    var modalEliminar = new bootstrap.Modal(document.getElementById('modalEliminar'));

    function abrirModalNuevo() {
        // Limpiar formulario para nuevo registro
        document.getElementById("formProducto").reset();
        document.getElementById("idProducto").value = "0";
        document.getElementById("modalTitle").innerText = "Registrar Nuevo Producto";
        document.getElementById("modalBtn").innerText = "Guardar Producto";

        // Limpiar valores previos
        document.getElementById("nombre").value = "";
        document.getElementById("marca").value = "";
        document.getElementById("precio").value = "";
        document.getElementById("stock").value = "";
        document.getElementById("stockMinimo").value = "";
        document.getElementById("descripcion").value = "";

        myModal.show();
    }

    // Función para abrir el modal de eliminación
    function abrirModalEliminar(id, nombre) {
        document.getElementById("nombreProductoEliminar").innerText = nombre;
        // Configuramos el enlace del botón "Sí, Eliminar"
        document.getElementById("btnConfirmarEliminar").href = "inventario?accion=eliminar&id=" + id;
        modalEliminar.show();
    }

    // Lógica para abrir el modal de edición automáticamente si venimos del Servlet
    <% if (mostrarModal) { %>
    myModal.show();
    <% } %>
</script>

</body>
</html>