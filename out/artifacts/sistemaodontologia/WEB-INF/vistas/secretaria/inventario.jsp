<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- IMPORTANTE: Importar las clases Java necesarias para manejar listas y objetos del modelo -->
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="models.Producto" %>

<!--
=============================================================================
VISTA: INVENTARIO DE PRODUCTOS (inventario.jsp)
Autor: Mathew Lara
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Esta vista JSP gestiona el inventario de productos e insumos médicos de la clínica.
Permite a la secretaria:
1. Visualizar el stock actual de productos.
2. Identificar productos con bajo stock mediante alertas visuales.
3. Agregar nuevos productos o editar los existentes mediante un formulario modal.
4. Realizar borrado lógico (archivar) y reactivación de productos.

Integra lógica de presentación (Scriptlets) para manejar el flujo de datos enviado
por el ProductoServlet.
=============================================================================
-->

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Inventario - EndoDental</title>
    <!--
        SECCIÓN DE DEPENDENCIAS DE ESTILO
        Se utilizan librerías externas para garantizar un diseño moderno y responsivo.
    -->
    <!-- Bootstrap 5 para la maquetación y componentes UI -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome para íconos vectoriales -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Google Fonts (Poppins) para la tipografía corporativa -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Estilos personalizados del proyecto -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <!-- Favicon de la aplicación -->
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<%
    /* * -------------------------------------------------------------------------
     * BLOQUE DE LÓGICA DE PRESENTACIÓN (SERVER-SIDE SCRIPTLET)
     * -------------------------------------------------------------------------
     * Recuperamos los objetos enviados desde el ProductoServlet mediante el request.
     * Se implementa manejo defensivo de nulos para evitar excepciones en tiempo de ejecución.
     */

    // 1. Recuperación de la lista de productos a mostrar.
    // Puede contener productos activos o inactivos según la acción del Servlet.
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    if (productos == null) productos = new ArrayList<>();

    // 2. Título dinámico de la página (Ej: "Inventario" o "Papelera").
    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Inventario de Productos";

    // 3. Bandera para saber si estamos visualizando la papelera de reciclaje.
    // Esto determina qué botones de acción mostrar (Eliminar vs Reactivar).
    Boolean esPapeleraObj = (Boolean) request.getAttribute("esPapelera");
    boolean esPapelera = (esPapeleraObj != null) ? esPapeleraObj : false;

    /*
     * LÓGICA DE PRECARGA PARA EL MODAL DE EDICIÓN
     * -------------------------------------------
     * Si el Servlet envía un objeto 'productoEditar', significa que el usuario solicitó editar.
     * Extraemos los datos de ese objeto para pre-llenar los campos del formulario modal.
     */
    Producto prodEdit = (Producto) request.getAttribute("productoEditar");

    // Variables por defecto para el modo "Crear Nuevo"
    int idVal = 0;
    String nombreVal = "";
    String marcaVal = "";
    String descVal = "";
    String precioVal = "";
    String stockVal = "";
    String stockMinVal = "";
    String tituloModal = "Registrar Nuevo Producto";
    String btnModal = "Guardar Producto";

    // Si hay un objeto de edición, sobrescribimos las variables con los datos reales
    if (prodEdit != null) {
        idVal = prodEdit.getIdProducto();
        nombreVal = prodEdit.getNombre();
        marcaVal = prodEdit.getMarca();
        descVal = prodEdit.getDescripcion();
        // Convertimos BigDecimal e Integer a String para mostrarlos en los inputs
        precioVal = prodEdit.getPrecioVenta().toString();
        stockVal = String.valueOf(prodEdit.getStock());
        stockMinVal = String.valueOf(prodEdit.getStockMinimo());

        // Cambiamos los textos del modal para reflejar que es una edición
        tituloModal = "Editar Producto";
        btnModal = "Actualizar";
    }

    // Bandera booleana para controlar la apertura automática del modal mediante JS.
    Boolean mostrarModalObj = (Boolean) request.getAttribute("mostrarModal");
    boolean mostrarModal = (mostrarModalObj != null) ? mostrarModalObj : false;
%>

<div class="dashboard-wrapper">
    <!--
        COMPONENTE SIDEBAR
        Importamos la barra lateral común, indicando que la página activa es 'inventario'.
    -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inventario"/>
    </jsp:include>

    <!-- CONTENIDO PRINCIPAL -->
    <main class="main-content">
        <!-- Encabezado con Título y Botones de Control -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold"><%= titulo %></h2>

            <div>
                <!-- Renderizado condicional de botones según si estamos en papelera o no -->
                <% if (!esPapelera) { %>
                <!-- MODO NORMAL: Botones para ver papelera y crear nuevo -->
                <a href="inventario?accion=inactivos" class="btn btn-secondary me-2" style="border-radius: 10px;">
                    <i class="fas fa-trash-restore me-2"></i> Papelera
                </a>
                <!-- El botón "Nuevo Producto" llama a una función JS para limpiar y abrir el modal -->
                <button class="btn btn-primary-custom" onclick="abrirModalNuevo()">
                    <i class="fas fa-plus me-2"></i> Nuevo Producto
                </button>
                <% } else { %>
                <!-- MODO PAPELERA: Botón para volver al listado principal -->
                <a href="inventario?accion=listar" class="btn btn-outline-primary" style="border-radius: 10px;">
                    <i class="fas fa-arrow-left me-2"></i> Volver al Inventario
                </a>
                <% } %>
            </div>
        </div>

        <!-- TABLA DE DATOS -->
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

                <%
                    /* * BUCLE DE RENDERIZADO DE PRODUCTOS
                     * Si la lista está vacía, mostramos un mensaje informativo.
                     * Si tiene datos, iteramos para crear las filas de la tabla.
                     */
                    if (productos.isEmpty()) {
                %>
                <tr>
                    <td colspan="7" class="text-center py-4 text-muted">No se encontraron productos.</td>
                </tr>
                <% } else {
                    for (Producto p : productos) {
                        // Lógica de negocio visual: Determinar si el stock está bajo
                        boolean bajoStock = (p.getStock() <= p.getStockMinimo());
                %>
                <tr>
                    <td><%= p.getNombre() %></td>
                    <td><%= p.getMarca() %></td>
                    <td>$ <%= p.getPrecioVenta() %></td>

                    <!-- Resaltamos en rojo si el stock es crítico -->
                    <td class="<%= bajoStock ? "fw-bold text-danger" : "fw-bold" %>">
                        <%= p.getStock() %>
                    </td>

                    <td><%= p.getStockMinimo() %></td>

                    <!-- Columna de Estado con Badges (Etiquetas) -->
                    <td>
                        <% if (p.getEstado() == 1) {
                            if (bajoStock) { %>
                        <!-- Alerta visual: Producto activo pero con poco inventario -->
                        <span class="badge bg-warning text-dark">Bajo Stock</span>
                        <% } else { %>
                        <!-- Estado ideal -->
                        <span class="badge bg-success">OK</span>
                        <% }
                        } else { %>
                        <!-- Producto desactivado/eliminado -->
                        <span class="badge bg-secondary">Inactivo</span>
                        <% } %>
                    </td>

                    <!-- Columna de Acciones -->
                    <td>
                        <% if (!esPapelera) { %>
                        <!-- ACCIONES EN MODO NORMAL -->

                        <!-- Botón Editar: Redirige al Servlet para cargar datos y volver a abrir el modal -->
                        <a href="inventario?accion=editar&id=<%= p.getIdProducto() %>" class="btn-action-edit me-2" title="Editar">
                            <i class="fas fa-edit"></i>
                        </a>

                        <!-- Botón Eliminar: Abre el modal de confirmación JS pasando ID y Nombre -->
                        <button onclick="abrirModalEliminar(<%= p.getIdProducto() %>, '<%= p.getNombre() %>')"
                                class="btn-action-delete border-0 bg-transparent text-danger"
                                title="Eliminar">
                            <i class="fas fa-trash-alt"></i>
                        </button>

                        <% } else { %>
                        <!-- ACCIONES EN MODO PAPELERA -->

                        <!-- Botón Reactivar: Restaura el producto cambiando su estado a 1 -->
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

<!-- ================= SECCIÓN DE MODALES ================= -->

<!--
    MODAL 1: FORMULARIO DE PRODUCTO (CREAR / EDITAR)
    Este modal reutilizable sirve tanto para insertar nuevos registros como para modificar existentes.
    Los valores de los inputs se llenan dinámicamente mediante las variables Java definidas al inicio.
-->
<div class="modal fade" id="modalNuevoProducto" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content" style="border-radius: 20px; border: none;">

            <!-- Encabezado del Modal: Color Primario (Azul) -->
            <div class="modal-header bg-primary text-white border-0 pb-0" style="border-top-left-radius: 20px; border-top-right-radius: 20px; padding-bottom: 1rem !important;">
                <h5 class="modal-title fw-bold ms-3" id="modalTitle"><%= tituloModal %></h5>
                <button type="button" class="btn-close btn-close-white me-3" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>

            <div class="modal-body p-4">
                <!-- Formulario que envía los datos al ProductoServlet mediante POST -->
                <form id="formProducto" action="inventario" method="POST">

                    <!-- ID Oculto: Determina si es INSERT (0) o UPDATE (>0) -->
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
                            <!-- step="0.01" permite decimales -->
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

<!--
    MODAL 2: CONFIRMACIÓN DE ELIMINACIÓN
    Modal pequeño de advertencia antes de realizar el borrado lógico.
    El botón de confirmación se configura dinámicamente vía JavaScript.
-->
<div class="modal fade" id="modalEliminar" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <!-- Encabezado Rojo para indicar peligro/atención -->
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
                <!-- Enlace dinámico que ejecutará la acción GET al Servlet -->
                <a href="#" id="btnConfirmarEliminar" class="btn btn-danger">Sí, Eliminar</a>
            </div>
        </div>
    </div>
</div>

<!-- Importación de Bootstrap JS Bundle (necesario para los modales) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<!-- SCRIPTS DE INTERACCIÓN -->
<script>
    // Instancias de los modales de Bootstrap
    var myModal = new bootstrap.Modal(document.getElementById('modalNuevoProducto'));
    var modalEliminar = new bootstrap.Modal(document.getElementById('modalEliminar'));

    /**
     * Función para abrir el modal en modo "Crear".
     * Limpia todos los campos del formulario y resetea el ID a 0.
     */
    function abrirModalNuevo() {
        // Resetea el formulario HTML
        document.getElementById("formProducto").reset();
        // Configura ID en 0 para que el DAO sepa que es un INSERT
        document.getElementById("idProducto").value = "0";
        // Actualiza textos de la interfaz
        document.getElementById("modalTitle").innerText = "Registrar Nuevo Producto";
        document.getElementById("modalBtn").innerText = "Guardar Producto";

        // Limpieza manual explícita de valores (por seguridad de caché visual)
        document.getElementById("nombre").value = "";
        document.getElementById("marca").value = "";
        document.getElementById("precio").value = "";
        document.getElementById("stock").value = "";
        document.getElementById("stockMinimo").value = "";
        document.getElementById("descripcion").value = "";

        myModal.show();
    }

    /**
     * Función para configurar y mostrar el modal de confirmación de eliminación.
     * @param {number} id - ID del producto a eliminar.
     * @param {string} nombre - Nombre del producto para mostrar en el mensaje.
     */
    function abrirModalEliminar(id, nombre) {
        document.getElementById("nombreProductoEliminar").innerText = nombre;
        // Configura el enlace del botón "Sí, Eliminar" apuntando al Servlet
        document.getElementById("btnConfirmarEliminar").href = "inventario?accion=eliminar&id=" + id;
        modalEliminar.show();
    }

    /*
     * LÓGICA SERVER-SIDE PARA APERTURA AUTOMÁTICA
     * Si el Servlet determina que se debe mostrar el modal (por ejemplo, al cargar datos para edición),
     * esta variable 'mostrarModal' será true y ejecutará el show() al cargar la página.
     */
    <% if (mostrarModal) { %>
    myModal.show();
    <% } %>
</script>

</body>
</html>