<%@ page contentType="text/html;charset=UTF-8" language="java"
         import="java.util.*, models.*, java.math.BigDecimal" %>

<!--
=============================================================================
VISTA: GESTIÓN DE FACTURACIÓN (facturacion.jsp)
Autor: Génesis Escobar
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Esta vista es el núcleo del módulo financiero. Permite a la secretaria:
1. Generar facturas electrónicas a partir de citas atendidas.
2. Agregar ítems dinámicos (Servicios o Productos) al detalle de la factura.
3. Calcular subtotales, IVA y totales en tiempo real (Cliente) y validarlos (Servidor).
4. Consultar el historial de facturas emitidas y descargar sus PDFs.
5. Autocompletar datos del cliente seleccionando una cita previa.
=============================================================================
-->

<%
    /* * -------------------------------------------------------------------------
     * BLOQUE DE LÓGICA DE PRESENTACIÓN (SERVER-SIDE)
     * -------------------------------------------------------------------------
     * Recuperamos todos los catálogos necesarios para poblar los formularios.
     * Se utiliza un manejo defensivo (inicialización de ArrayList vacíos) para
     * prevenir NullPointerExceptions si el Servlet no envía algún atributo.
     */

    // 1. Lista de Citas pendientes de cobro (Estado: 'Atendida').
    List<Cita> citasPendientes = (List<Cita>) request.getAttribute("citasPendientes");
    if (citasPendientes == null) citasPendientes = new ArrayList<>();

    // 2. Catálogo de Servicios Médicos (Intangibles).
    List<Servicio> servicios = (List<Servicio>) request.getAttribute("servicios");
    if (servicios == null) servicios = new ArrayList<>();

    // 3. Catálogo de Productos/Insumos (Tangibles con control de Stock).
    List<Producto> productos = (List<Producto>) request.getAttribute("productos");
    if (productos == null) productos = new ArrayList<>();

    // 4. Historial de facturas para la tabla de consulta.
    List<Factura> historialFacturas = (List<Factura>) request.getAttribute("facturas");
    if (historialFacturas == null) historialFacturas = new ArrayList<>();

    // Variables de control de flujo (Mensajes y IDs generados).
    String error = (String) request.getAttribute("error");
    String exito = request.getParameter("exito");
    String idFacturaGenerada = request.getParameter("idFactura");

    /*
     * LÓGICA DE PRE-LLENADO (UX AVANZADA)
     * Si la secretaria llega aquí haciendo clic en "Facturar" desde la Agenda,
     * el Servlet envía el ID de la cita ('idCitaPreseleccionada') y el objeto 'citaPre'.
     * Usamos estos datos para autocompletar los campos de Cédula y Nombre del cliente.
     */
    String idPreseleccionado = (String) request.getAttribute("idCitaPreseleccionada");
    Cita citaPre = (Cita) request.getAttribute("citaPre");

    // Variables para el binding en los inputs HTML
    String cedulaVal = "";
    String nombreVal = "";

    if (citaPre != null && citaPre.getPaciente() != null) {
        cedulaVal = citaPre.getPaciente().getCedula();
        // Concatenamos nombres y apellidos para mostrar el nombre completo
        nombreVal = citaPre.getPaciente().getNombres() + " " + citaPre.getPaciente().getApellidos();
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Facturación - EndoDental</title>
    <!-- Estilos CSS (Bootstrap + Personalizados) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<div class="dashboard-wrapper">
    <!-- Inyección del Sidebar común con la opción 'facturacion' activa -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="facturacion"/>
    </jsp:include>

    <main class="main-content">

        <!-- Encabezado y Botón de Historial -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold m-0">Generar Factura</h2>
            <!-- Abre el modal con el listado histórico de facturas emitidas -->
            <button type="button" class="btn btn-secondary rounded-4" data-bs-toggle="modal" data-bs-target="#modalHistorial">
                <i class="fas fa-history me-2"></i> Historial
            </button>
        </div>

        <!--
            SECCIÓN DE NOTIFICACIONES
            Muestra alertas de error (rojo) o éxito (verde) según la respuesta del Servlet.
            Si hay éxito, muestra un botón directo para imprimir el PDF de la factura generada.
        -->
        <% if(error != null) { %>
        <div class="alert alert-danger"><i class="fas fa-exclamation-triangle"></i> <%= error %></div>
        <% } %>
        <% if(exito != null && idFacturaGenerada != null) { %>
        <div class="alert alert-success alert-dismissible fade show">
            <i class="fas fa-check-circle"></i> <strong>¡Factura Generada!</strong>
            <!-- Enlace al Servlet de generación de reportes PDF -->
            <a href="${pageContext.request.contextPath}/facturacion/pdf?id=<%= idFacturaGenerada %>" target="_blank" class="btn btn-sm btn-outline-success ms-3">
                <i class="fas fa-print"></i> Imprimir PDF
            </a>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <!--
            FORMULARIO PRINCIPAL DE FACTURACIÓN
            Este formulario envía los datos de cabecera y una lista dinámica de ítems al Servlet.
        -->
        <form action="${pageContext.request.contextPath}/facturacion" method="POST" id="formFacturaGeneral">
            <!-- Acción oculta para que el Servlet sepa qué método ejecutar -->
            <input type="hidden" name="accion" value="generar">

            <div class="row">
                <!-- COLUMNA IZQUIERDA: DATOS DEL CLIENTE Y TABLA DE ÍTEMS -->
                <div class="col-md-8">

                    <!-- TARJETA DE CABECERA -->
                    <div class="form-card mb-4">
                        <h5 class="fw-bold mb-3 border-bottom pb-2">Datos de Facturación</h5>
                        <div class="row g-3">
                            <div class="col-md-12">
                                <label class="form-label fw-bold">Cita a Facturar</label>
                                <!--
                                    Select inteligente con data-attributes.
                                    Al cambiar la opción, JS lee 'data-cedula' y 'data-cliente'
                                    para autocompletar los inputs inferiores.
                                -->
                                <select name="id_cita" class="form-select form-control-custom" required onchange="actualizarDatosCliente(this)">
                                    <option disabled value="" <%= (idPreseleccionado == null) ? "selected" : "" %>>Seleccione una cita atendida...</option>
                                    <% for (Cita c : citasPendientes) {
                                        String idActual = String.valueOf(c.getIdCita());
                                        // Preselección si venimos redirigidos desde la agenda
                                        String isSelected = (idActual.equals(idPreseleccionado)) ? "selected" : "";
                                        String paciente = c.getPaciente().getNombres() + " " + c.getPaciente().getApellidos();
                                        String cedula = c.getPaciente().getCedula();
                                    %>
                                    <option value="<%=c.getIdCita()%>"
                                            data-cedula="<%= cedula %>"
                                            data-cliente="<%= paciente %>"
                                            <%= isSelected %>>
                                        [<%= c.getFechaHora().toLocalDate() %>] <%= paciente %> - <%= c.getMotivo() %>
                                    </option>
                                    <% } %>
                                </select>
                                <small class="text-muted">Solo aparecen citas con estado "Atendida" que no han sido facturadas.</small>
                            </div>

                            <!-- Campos de texto autocompletables pero editables si se requiere facturar a otra persona -->
                            <div class="col-md-6">
                                <label class="form-label-custom">RUC / Cédula</label>
                                <input type="text" name="identificacion_cliente" id="inputCedula" class="form-control form-control-custom"
                                       value="<%= cedulaVal %>" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label-custom">Nombre Cliente</label>
                                <input type="text" name="nombre_cliente_factura" id="inputNombre" class="form-control form-control-custom"
                                       value="<%= nombreVal %>" required>
                            </div>
                            <div class="col-md-12">
                                <label class="form-label-custom">Dirección</label>
                                <input type="text" name="direccion_cliente" class="form-control form-control-custom">
                            </div>
                        </div>
                    </div>

                    <!-- TABLA DINÁMICA DE ÍTEMS -->
                    <div class="custom-table-container">
                        <div class="d-flex justify-content-between mb-3 align-items-center">
                            <h5 class="fw-bold m-0">Detalle</h5>
                            <!-- Botón que abre el modal para agregar productos/servicios -->
                            <button type="button" class="btn btn-sm btn-outline-primary rounded-pill" data-bs-toggle="modal" data-bs-target="#modalAgregarItem">
                                <i class="fas fa-plus me-1"></i> Agregar Item
                            </button>
                        </div>
                        <table class="table table-custom table-hover align-middle">
                            <thead class="table-light">
                            <tr><th>Tipo</th><th>Descripción</th><th>Cant.</th><th>Precio</th><th>Subtotal</th><th></th></tr>
                            </thead>
                            <!-- El cuerpo de la tabla se llena vía JavaScript -->
                            <tbody id="listaItems"></tbody>
                        </table>
                        <!-- Mensaje visual cuando la tabla está vacía -->
                        <div id="mensajeTablaVacia" class="text-center text-muted py-3">No hay ítems agregados.</div>
                    </div>
                </div>

                <!-- COLUMNA DERECHA: TOTALES Y PAGO -->
                <div class="col-md-4">
                    <div class="stat-card d-block p-4">
                        <h4 class="fw-bold mb-4">Resumen</h4>

                        <!-- Visualización de Totales (Actualizados por JS) -->
                        <div class="d-flex justify-content-between mb-2"><span>Subtotal:</span><span id="subtotalText" class="fw-bold">$0.00</span></div>
                        <div class="d-flex justify-content-between mb-2"><span>IVA (15%):</span><span id="ivaText" class="fw-bold">$0.00</span></div>
                        <hr>
                        <div class="d-flex justify-content-between mb-4"><span class="fw-bold fs-4">Total:</span><span class="fw-bold fs-4 text-primary" id="totalText">$0.00</span></div>

                        <!-- Inputs Ocultos para enviar los totales calculados al Servlet -->
                        <input type="hidden" name="subtotal" id="inputSubtotal" value="0.00">
                        <input type="hidden" name="monto_iva" id="inputIva" value="0.00">
                        <input type="hidden" name="total_pagar" id="inputTotal" value="0.00">

                        <label class="form-label fw-bold">Método de Pago</label>
                        <select name="metodo_pago" class="form-select form-control-custom mb-4">
                            <option value="Efectivo">Efectivo</option>
                            <option value="Tarjeta">Tarjeta</option>
                            <option value="Transferencia">Transferencia</option>
                        </select>

                        <!-- Botón de envío con validación JS previa (validarFactura) -->
                        <button type="submit" class="btn btn-primary-custom w-100 py-3" onclick="return validarFactura()">
                            <i class="fas fa-check-circle me-2"></i> Emitir Factura
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </main>
</div>

<!-- ================= MODALES ================= -->

<!-- MODAL 1: AGREGAR ITEM -->
<div class="modal fade" id="modalAgregarItem" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header border-0">
                <h5 class="fw-bold">Agregar Item</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <form id="formAgregarItem">
                    <label class="form-label-custom">Tipo de Item</label>
                    <!-- Radio buttons para alternar entre Servicios y Productos -->
                    <div class="d-flex gap-3 mb-3">
                        <label class="btn btn-outline-secondary btn-sm flex-fill">
                            <input type="radio" name="tipo_item" value="Servicio" checked onclick="toggleTipo()" class="me-2"> Servicio
                        </label>
                        <label class="btn btn-outline-secondary btn-sm flex-fill">
                            <input type="radio" name="tipo_item" value="Producto" onclick="toggleTipo()" class="me-2"> Producto
                        </label>
                    </div>

                    <label class="form-label-custom">Descripción</label>
                    <!-- Select agrupado: Se muestra/oculta según el radio button seleccionado -->
                    <select name="id_item" id="selectItem" class="form-select form-control-custom mb-3" onchange="actualizarPrecioModal()">
                        <option disabled selected value="">Seleccione...</option>

                        <optgroup label="Servicios Médicos" id="groupServicios">
                            <% for (Servicio s : servicios) { %>
                            <option value="<%=s.getIdServicio()%>" data-precio="<%=s.getPrecioBase()%>">
                                <%=s.getNombre()%>
                            </option>
                            <% } %>
                        </optgroup>

                        <optgroup label="Productos / Insumos" id="groupProductos" style="display:none;">
                            <% for (Producto p : productos) {
                                // Lógica de negocio: Solo mostrar productos con stock disponible y activos
                                if(p.getStock() > 0 && p.getEstado() == 1) {
                            %>
                            <option value="<%=p.getIdProducto()%>" data-precio="<%=p.getPrecioVenta()%>">
                                <%=p.getNombre()%> (Stock: <%=p.getStock()%>)
                            </option>
                            <% }} %>
                        </optgroup>
                    </select>

                    <label class="form-label-custom">Zona / Diente (Opcional)</label>
                    <input type="text" name="diente_o_zona" id="inputZona" class="form-control form-control-custom mb-3" placeholder="Ej. Muela 18">

                    <div class="row">
                        <div class="col-6">
                            <label class="form-label-custom">Cantidad</label>
                            <input type="number" min="1" class="form-control form-control-custom" name="cantidad" id="inputCantidad" value="1">
                        </div>
                        <div class="col-6">
                            <label class="form-label-custom">Precio Unit.</label>
                            <!-- Campo editable pero prellenado automáticamente -->
                            <input type="number" step="0.01" class="form-control form-control-custom" name="precio_unitario" id="inputPrecio" placeholder="0.00">
                        </div>
                    </div>
                </form>
                <button class="btn btn-primary-custom w-100 mt-4" id="btnAgregarItem">Agregar a la lista</button>
            </div>
        </div>
    </div>
</div>

<!-- MODAL 2: HISTORIAL DE FACTURAS -->
<div class="modal fade" id="modalHistorial" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header bg-secondary text-white rounded-top-4">
                <h5 class="modal-title fw-bold"><i class="fas fa-history"></i> Historial de Facturas</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover table-striped mb-0">
                        <thead class="table-light">
                        <tr><th>#</th><th>Fecha</th><th>Cliente</th><th>Total</th><th>PDF</th></tr>
                        </thead>
                        <tbody>
                        <% if (historialFacturas.isEmpty()) { %>
                        <tr><td colspan="5" class="text-center py-3">No hay facturas registradas.</td></tr>
                        <% } else {
                            for(Factura f : historialFacturas) { %>
                        <tr>
                            <td><%= f.getIdFactura() %></td>
                            <td><%= f.getFechaEmision().toLocalDate() %></td>
                            <td><%= f.getNombreClienteFactura() %></td>
                            <td class="fw-bold text-success">$<%= f.getTotalPagar() %></td>
                            <td class="text-center">
                                <a href="${pageContext.request.contextPath}/facturacion/pdf?id=<%=f.getIdFactura()%>"
                                   target="_blank" class="btn btn-sm btn-danger rounded-pill">
                                    <i class="fas fa-file-pdf"></i> PDF
                                </a>
                            </td>
                        </tr>
                        <% } } %>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary rounded-4" data-bs-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>

<!-- SCRIPTS DE COMPORTAMIENTO -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    const tabla = document.getElementById("listaItems");
    const btnAgregar = document.getElementById("btnAgregarItem");
    const mensajeVacio = document.getElementById("mensajeTablaVacia");
    let subtotalGlobal = 0.0;

    /**
     * Actualiza los inputs de Cédula y Nombre cuando se selecciona una cita.
     * Utiliza los atributos data-* definidos en el HTML del option.
     */
    function actualizarDatosCliente(selectElement) {
        // Si se llama desde el onchange, selectElement es 'this'.
        // Si no, lo buscamos por ID.
        const select = selectElement || document.getElementById("selectCita");
        const option = select.options[select.selectedIndex];

        const cedula = option.getAttribute("data-cedula");
        const nombre = option.getAttribute("data-cliente");

        if (cedula && nombre) {
            document.getElementById("inputCedula").value = cedula;
            document.getElementById("inputNombre").value = nombre;
        }
    }

    /**
     * Validación previa al envío del formulario.
     * Asegura que la factura tenga al menos un ítem y un valor positivo.
     */
    function validarFactura() {
        if (subtotalGlobal <= 0) {
            alert("La factura debe tener al menos un ítem y un total mayor a 0.");
            return false;
        }
        return true;
    }

    /**
     * Controla la visibilidad de los grupos de opciones en el select del modal
     * dependiendo de si se elige "Servicio" o "Producto".
     */
    function toggleTipo() {
        const tipo = document.querySelector('input[name="tipo_item"]:checked').value;
        const groupServ = document.getElementById("groupServicios");
        const groupProd = document.getElementById("groupProductos");
        const select = document.getElementById("selectItem");
        const inputZona = document.getElementById("inputZona");

        select.value = "";
        document.getElementById("inputPrecio").value = "";

        if(tipo === "Servicio") {
            groupServ.style.display = "";
            groupProd.style.display = "none";
            inputZona.disabled = false; // Servicios pueden tener zona
        } else {
            groupServ.style.display = "none";
            groupProd.style.display = "";
            inputZona.value = "-";
            inputZona.disabled = true; // Productos no tienen zona
        }
    }

    /**
     * Obtiene el precio base del ítem seleccionado y lo coloca en el input de precio.
     */
    function actualizarPrecioModal() {
        const select = document.getElementById("selectItem");
        const option = select.options[select.selectedIndex];
        if (option) {
            const precio = option.getAttribute("data-precio");
            if(precio) {
                document.getElementById("inputPrecio").value = parseFloat(precio).toFixed(2);
            }
        }
    }

    // Evento Click: Agregar Ítem a la Tabla
    btnAgregar.addEventListener("click", () => {
        const f = document.getElementById("formAgregarItem");

        // Validaciones de entrada
        const idItem = f.id_item.value;
        const cantidad = parseFloat(f.cantidad.value);
        const precio = parseFloat(f.precio_unitario.value);

        if (!idItem || isNaN(cantidad) || cantidad <= 0 || isNaN(precio)) {
            alert("Por favor complete los datos correctamente.");
            return;
        }

        // Recopilación de datos
        const tipo = f.tipo_item.value;
        const desc = f.id_item.options[f.id_item.selectedIndex].text.trim();
        const zona = f.diente_o_zona.value || "-";
        const subtotal = (cantidad * precio);

        // Ocultar mensaje de "sin datos"
        mensajeVacio.style.display = "none";

        // Construcción dinámica de la fila HTML con Inputs Ocultos
        // Los inputs ocultos (name="detalle_...") son los que el Servlet leerá.
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>` + tipo + `</td>
            <td>` + desc + `</td>
            <td>` + zona + `</td>
            <td>` + cantidad + `</td>
            <td>$` + precio.toFixed(2) + `</td>
            <td class="fw-bold">$` + subtotal.toFixed(2) + `</td>
            <td>
                <button type="button" class="btn btn-sm btn-danger rounded-circle" onclick="eliminarFila(this, ` + subtotal + `)">
                    <i class="fas fa-times"></i>
                </button>
                <input type="hidden" name="detalle_tipo" value="` + tipo + `">
                <input type="hidden" name="detalle_id" value="` + idItem + `">
                <input type="hidden" name="detalle_cantidad" value="` + cantidad + `">
                <input type="hidden" name="detalle_precio" value="` + precio.toFixed(2) + `">
                <input type="hidden" name="detalle_zona" value="` + zona + `">
            </td>
        `;

        tabla.appendChild(tr);
        actualizarTotales(subtotal);
        f.reset();
        bootstrap.Modal.getInstance(document.getElementById("modalAgregarItem")).hide();
        toggleTipo();
    });

    /**
     * Recalcula Subtotal, IVA y Total y actualiza tanto la vista como los inputs hidden.
     */
    function actualizarTotales(monto) {
        monto = parseFloat(monto);
        if (isNaN(monto)) monto = 0;
        subtotalGlobal += monto;
        if (subtotalGlobal < 0) subtotalGlobal = 0; // Evitar negativos por errores de redondeo

        const iva = subtotalGlobal * 0.15;
        const total = subtotalGlobal + iva;

        // Vista
        document.getElementById("subtotalText").innerText = "$" + subtotalGlobal.toFixed(2);
        document.getElementById("ivaText").innerText = "$" + iva.toFixed(2);
        document.getElementById("totalText").innerText = "$" + total.toFixed(2);

        // Datos para el Servlet
        document.getElementById("inputSubtotal").value = subtotalGlobal.toFixed(2);
        document.getElementById("inputIva").value = iva.toFixed(2);
        document.getElementById("inputTotal").value = total.toFixed(2);
    }

    /**
     * Elimina una fila de la tabla y resta su valor del total.
     */
    window.eliminarFila = function(btn, montoSubtotal) {
        btn.closest("tr").remove();
        actualizarTotales(-parseFloat(montoSubtotal));
        if (tabla.rows.length === 0) mensajeVacio.style.display = "block";
    }
</script>

</body>
</html>