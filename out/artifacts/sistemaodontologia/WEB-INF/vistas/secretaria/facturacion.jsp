<%@ page contentType="text/html;charset=UTF-8" language="java"
         import="java.util.*, models.*, java.math.BigDecimal" %>

<!--
=============================================================================
VISTA: GESTIÓN DE FACTURACIÓN (facturacion.jsp)
Autor: Génesis Escobar
Fecha: 05/12/2025
Versión: 3.1 (Corrección: Validación visual de Stock)
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

        <!-- SECCIÓN DE NOTIFICACIONES -->
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

        <!-- FORMULARIO PRINCIPAL DE FACTURACIÓN -->
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
                                <!-- Select inteligente con data-attributes para autocompletar -->
                                <select name="id_cita" id="selectCita" class="form-select form-control-custom" required onchange="actualizarDatosCliente(this)">
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

                            <!-- Campos de texto autocompletables -->
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
                    <div class="d-flex gap-3 mb-3">
                        <label class="btn btn-outline-secondary btn-sm flex-fill">
                            <input type="radio" name="tipo_item" value="Servicio" checked onclick="toggleTipo()" class="me-2"> Servicio
                        </label>
                        <label class="btn btn-outline-secondary btn-sm flex-fill">
                            <input type="radio" name="tipo_item" value="Producto" onclick="toggleTipo()" class="me-2"> Producto
                        </label>
                    </div>

                    <label class="form-label-custom">Descripción</label>
                    <select name="id_item" id="selectItem" class="form-select form-control-custom mb-3" onchange="actualizarPrecioModal()">
                        <option disabled selected value="">Seleccione...</option>

                        <optgroup label="Servicios Médicos" id="groupServicios">
                            <% for (Servicio s : servicios) { %>
                            <!-- Servicios tienen stock infinito (99999) para la lógica JS -->
                            <option value="<%=s.getIdServicio()%>" data-precio="<%=s.getPrecioBase()%>" data-stock="99999">
                                <%=s.getNombre()%>
                            </option>
                            <% } %>
                        </optgroup>

                        <optgroup label="Productos / Insumos" id="groupProductos" style="display:none;">
                            <% for (Producto p : productos) {
                                if(p.getStock() > 0 && p.getEstado() == 1) {
                            %>
                            <!-- AQUÍ AGREGAMOS data-stock CON EL VALOR REAL PARA LA VALIDACIÓN -->
                            <option value="<%=p.getIdProducto()%>"
                                    data-precio="<%=p.getPrecioVenta()%>"
                                    data-stock="<%=p.getStock()%>">
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

    // Función: Actualiza los inputs de Cédula y Nombre cuando se selecciona una cita
    function actualizarDatosCliente(selectElement) {
        const select = selectElement || document.getElementById("selectCita");
        const option = select.options[select.selectedIndex];

        const cedula = option.getAttribute("data-cedula");
        const nombre = option.getAttribute("data-cliente");

        if (cedula && nombre) {
            document.getElementById("inputCedula").value = cedula;
            document.getElementById("inputNombre").value = nombre;
        }
    }

    // Validación general antes de enviar la factura
    function validarFactura() {
        if (subtotalGlobal <= 0) {
            alert("La factura debe tener al menos un ítem y un total mayor a 0.");
            return false;
        }
        return true;
    }

    // Alternar campos según tipo de ítem
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
            inputZona.disabled = false;
        } else {
            groupServ.style.display = "none";
            groupProd.style.display = "";
            inputZona.value = "-";
            inputZona.disabled = true;
        }
    }

    // Actualizar precio en modal
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

    // Evento Click: Agregar Ítem a la Tabla (Con Validación de Stock)
    btnAgregar.addEventListener("click", () => {
        const f = document.getElementById("formAgregarItem");
        const select = document.getElementById("selectItem");

        // Validaciones básicas
        const idItem = f.id_item.value;
        const cantidad = parseFloat(f.cantidad.value);
        const precio = parseFloat(f.precio_unitario.value);

        if (!idItem || isNaN(cantidad) || cantidad <= 0 || isNaN(precio)) {
            alert("Por favor complete los datos correctamente.");
            return;
        }

        // --- LÓGICA DE VALIDACIÓN DE STOCK (FRONTEND) ---
        const option = select.options[select.selectedIndex];
        const stockDisponible = parseFloat(option.getAttribute("data-stock"));
        const tipo = f.tipo_item.value;

        if (tipo === "Producto" && cantidad > stockDisponible) {
            // Utilizamos el nuevo modal de alerta para mejor UX
            document.getElementById("stockErrorProductoNombre").innerText = option.text;
            document.getElementById("stockErrorDisponible").innerText = stockDisponible;
            document.getElementById("stockErrorSolicitado").innerText = cantidad;

            var modalError = new bootstrap.Modal(document.getElementById('modalStockError'));
            modalError.show();
            return; // Detiene la ejecución para no agregar el ítem
        }
        // -------------------------------------------

        const desc = option.text.trim();
        const zona = f.diente_o_zona.value || "-";
        const subtotal = (cantidad * precio);

        mensajeVacio.style.display = "none";

        // Construir fila de tabla
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

    function actualizarTotales(monto) {
        monto = parseFloat(monto);
        if (isNaN(monto)) monto = 0;
        subtotalGlobal += monto;
        if (subtotalGlobal < 0) subtotalGlobal = 0;

        const iva = subtotalGlobal * 0.15;
        const total = subtotalGlobal + iva;

        document.getElementById("subtotalText").innerText = "$" + subtotalGlobal.toFixed(2);
        document.getElementById("ivaText").innerText = "$" + iva.toFixed(2);
        document.getElementById("totalText").innerText = "$" + total.toFixed(2);

        document.getElementById("inputSubtotal").value = subtotalGlobal.toFixed(2);
        document.getElementById("inputIva").value = iva.toFixed(2);
        document.getElementById("inputTotal").value = total.toFixed(2);
    }

    window.eliminarFila = function(btn, montoSubtotal) {
        btn.closest("tr").remove();
        actualizarTotales(-parseFloat(montoSubtotal));
        if (tabla.rows.length === 0) mensajeVacio.style.display = "block";
    }
</script>

<!-- NUEVO MODAL DE ALERTA DE STOCK (UX) -->
<div class="modal fade" id="modalStockError" tabindex="-1">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-warning text-dark">
                <h5 class="modal-title fw-bold">¡Stock Insuficiente!</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <i class="fas fa-box-open fa-3x text-warning mb-3"></i>
                <p>No se puede agregar más cantidad de <strong id="stockErrorProductoNombre"></strong>.</p>
                <small class="text-muted">
                    Disponible: <span id="stockErrorDisponible"></span><br>
                    Solicitado: <span id="stockErrorSolicitado" class="fw-bold"></span>
                </small>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Entendido</button>
            </div>
        </div>
    </div>
</div>

</body>
</html>