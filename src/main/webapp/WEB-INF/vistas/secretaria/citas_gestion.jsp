<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Importamos las clases necesarias para manejar listas, fechas y los modelos del dominio -->
<%@ page import="java.util.*" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="models.*" %>

<!--
=============================================================================
VISTA: GESTIÓN DE AGENDA DE CITAS (citas_gestion.jsp)
Autor: Byron Melo
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Interfaz gráfica principal para la gestión del cronograma de citas odontológicas.
Esta vista actúa como el "Dashboard de Operaciones" para la secretaria.

Funcionalidades Principales:
1. Visualización de citas filtradas por fecha y estado (Pestañas de navegación).
2. Buscador global de citas por número de cédula del paciente.
3. Gestión del ciclo de vida de la cita:
- Crear/Agendar (Estado: Pendiente)
- Editar (Solo si está Pendiente)
- Cancelar (Soft Delete / Cambio de estado)
- Facturar (Solo si está Atendida por el doctor)

Arquitectura:
Implementa el patrón MVC recibiendo datos pre-procesados del CitaServlet.
Utiliza un diseño de "Single View" donde los formularios se manejan mediante Modales
para evitar recargas de página innecesarias y mejorar la UX.
=============================================================================
-->

<%
    /* * -------------------------------------------------------------------------
     * BLOQUE DE LÓGICA DE PRESENTACIÓN (SERVER-SIDE SCRIPTLET)
     * -------------------------------------------------------------------------
     * En este bloque recuperamos los objetos enviados por el Controlador (Servlet).
     * Aplicamos "Defensive Programming" inicializando listas vacías si los atributos son null
     * para evitar excepciones (NullPointerException) durante el renderizado.
     */

    // 1. Recuperación de la lista principal de citas (filtrada por el Servlet según la acción)
    List<Cita> listaCitas = (List<Cita>) request.getAttribute("citas");
    if (listaCitas == null) listaCitas = new ArrayList<>();

    // 2. Recuperación de catálogos para poblar los selectores (dropdowns) del modal
    List<Paciente> listaPacientes = (List<Paciente>) request.getAttribute("listaPacientes");
    if (listaPacientes == null) listaPacientes = new ArrayList<>();

    List<Odontologo> listaOdontologos = (List<Odontologo>) request.getAttribute("listaOdontologos");
    if (listaOdontologos == null) listaOdontologos = new ArrayList<>();

    // 3. Variables de contexto y estado de la vista
    String titulo = (String) request.getAttribute("titulo");
    // Si no hay fecha filtro, usamos la fecha actual del sistema
    String fechaFiltro = (String) request.getAttribute("fechaFiltro");
    if (fechaFiltro == null) fechaFiltro = LocalDate.now().toString();

    // 'vistaActual' controla qué pestaña (botón) se muestra activa visualmente
    String vistaActual = (String) request.getAttribute("vistaActual");

    // Mensajes de feedback para el usuario
    String error = (String) request.getAttribute("error");
    String exito = request.getParameter("exito");

    // Formateador para mostrar la hora amigablemente (HH:mm)
    DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");

    /* * LÓGICA DE PRECARGA PARA EL MODAL (CREAR VS EDITAR)
     * --------------------------------------------------
     * Si el Servlet envía el objeto 'citaEditar', extraemos sus datos para rellenar
     * las variables que se usarán en los 'value' de los inputs del formulario.
     */
    Cita citaEdit = (Cita) request.getAttribute("citaEditar");
    boolean esEdicion = (citaEdit != null);

    // Valores por defecto (Modo Crear)
    int idVal = 0;
    String motivoVal = "";
    String fechaVal = "";
    String horaVal = "";
    int idPacVal = 0;
    int idOdoVal = 0;
    String estadoVal = "Pendiente";
    String tituloModal = "Nueva Cita";
    String btnModal = "Agendar";

    // Sobreescritura de valores (Modo Editar)
    if (esEdicion) {
        idVal = citaEdit.getIdCita();
        motivoVal = citaEdit.getMotivo();
        // Descomponemos LocalDateTime a Date y Time para los inputs HTML5
        fechaVal = citaEdit.getFechaHora().toLocalDate().toString();
        horaVal = citaEdit.getFechaHora().toLocalTime().toString();
        idPacVal = citaEdit.getPaciente().getIdPaciente();
        idOdoVal = citaEdit.getOdontologo().getIdOdontologo();
        estadoVal = citaEdit.getEstado();

        tituloModal = "Editar Cita #" + idVal;
        btnModal = "Actualizar";
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Agenda - EndoDental</title>
    <!--
        DEPENDENCIAS DE ESTILO
        Bootstrap 5 para el layout responsivo y componentes UI.
        FontAwesome para la iconografía.
        Estilos personalizados (Style.css) para la identidad corporativa.
    -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<div class="dashboard-wrapper">
    <!--
        COMPONENTE SIDEBAR
        Inclusión de la barra lateral común, marcando 'agenda' como la página activa.
    -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="agenda"/>
    </jsp:include>

    <!-- CONTENIDO PRINCIPAL -->
    <main class="main-content">

        <!-- SECCIÓN DE ALERTAS (Renderizado Condicional) -->
        <% if(error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show">
            <i class="fas fa-exclamation-circle me-2"></i> <%=error%>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>
        <% if(exito != null) { %>
        <div class="alert alert-success alert-dismissible fade show">
            <i class="fas fa-check-circle me-2"></i> Operación exitosa.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <!--
            BARRA DE HERRAMIENTAS SUPERIOR
            Contiene:
            1. Pestañas de navegación (Agenda / Facturadas / Canceladas).
            2. Filtro de Fecha.
            3. Buscador por Cédula.
            4. Botón de Agendar (solo visible en la pestaña principal).
        -->
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
            <div>
                <!-- GRUPO DE BOTONES (PESTAÑAS) -->
                <div class="btn-group mb-2">
                    <!-- Lógica CSS dinámica: Agrega la clase 'btn-primary' (relleno) si es la vista actual, sino 'btn-outline' -->
                    <a href="citas?accion=agenda&fecha=<%=fechaFiltro%>"
                       class="btn btn-sm <%= "agenda".equals(vistaActual) ? "btn-primary" : "btn-outline-primary" %>">Agenda</a>
                    <a href="citas?accion=facturadas&fecha=<%=fechaFiltro%>"
                       class="btn btn-sm <%= "facturadas".equals(vistaActual) ? "btn-success" : "btn-outline-success" %>">Facturadas</a>
                    <a href="citas?accion=canceladas&fecha=<%=fechaFiltro%>"
                       class="btn btn-sm <%= "canceladas".equals(vistaActual) ? "btn-danger" : "btn-outline-danger" %>">Canceladas</a>
                </div>

                <!-- FILTRO DE FECHA: Al cambiar el input, se envía el formulario automáticamente (onchange) -->
                <form action="citas" method="GET" class="d-flex align-items-center gap-2">
                    <input type="hidden" name="accion" value="<%= vistaActual %>">
                    <h4 class="fw-bold m-0 text-dark">Fecha:</h4>
                    <input type="date" name="fecha" value="<%= fechaFiltro %>" class="form-control form-control-sm" style="width: auto;" onchange="this.form.submit()">
                </form>
            </div>

            <div class="d-flex gap-2">
                <!-- BUSCADOR POR CÉDULA -->
                <form action="citas" method="GET" class="d-flex">
                    <input type="hidden" name="accion" value="buscar">
                    <input type="text" name="busqueda" class="form-control form-control-custom me-2" placeholder="Cédula..." pattern="\d+" required>
                    <button type="submit" class="btn btn-secondary rounded-4"><i class="fas fa-search"></i></button>
                </form>

                <!-- BOTÓN AGENDAR: Solo visible en la vista de Agenda operativa -->
                <% if("agenda".equals(vistaActual)) { %>
                <button type="button" class="btn btn-primary-custom" onclick="abrirModalCrear()">
                    <i class="fas fa-plus me-2"></i> Agendar
                </button>
                <% } %>
            </div>
        </div>

        <!-- TABLA DE DATOS -->
        <div class="custom-table-container">
            <table class="table table-custom table-hover align-middle">
                <thead>
                <tr>
                    <th>Hora</th>
                    <th>Paciente</th>
                    <th>Odontólogo</th>
                    <th>Motivo</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <%
                    /* * BUCLE DE RENDERIZADO DE CITAS
                     * Itera sobre la lista de citas y genera las filas de la tabla.
                     */
                    if (listaCitas.isEmpty()) {
                %>
                <tr><td colspan="6" class="text-center py-5 text-muted">No hay citas en esta vista para la fecha seleccionada.</td></tr>
                <% } else {
                    for (Cita c : listaCitas) {
                        // Formateo de datos para visualización
                        String hora = c.getFechaHora().format(fmtHora);
                        String nomPac = c.getPaciente().getNombres() + " " + c.getPaciente().getApellidos();
                        // Verificación de nulo para el odontólogo (por seguridad en citas antiguas)
                        String nomDoc = (c.getOdontologo().getUsuario() != null) ? c.getOdontologo().getUsuario().getNombreCompleto() : "Dr. Desconocido";
                        String st = c.getEstado();

                        // Lógica de colores para los Badges de estado
                        String badge = "bg-secondary";
                        if ("Pendiente".equals(st)) badge = "bg-warning text-dark";
                        if ("Atendida".equals(st)) badge = "bg-info text-white";
                        if ("Facturada".equals(st)) badge = "bg-success";
                        if ("Cancelada".equals(st)) badge = "bg-danger";

                        // Datos formateados para pasar a las funciones JavaScript de los modales
                        String fIso = c.getFechaHora().toLocalDate().toString();
                        String hIso = c.getFechaHora().toLocalTime().toString();
                %>
                <tr>
                    <td class="fw-bold"><%= hora %></td>
                    <td><%= nomPac %> <br> <small class="text-muted"><%= c.getPaciente().getCedula() %></small></td>
                    <td><%= nomDoc %></td>
                    <td><%= c.getMotivo() %></td>
                    <td><span class="badge <%= badge %>"><%= st %></span></td>
                    <td>
                        <!-- LÓGICA DE ACCIONES SEGÚN EL ESTADO DE LA CITA -->
                        <% if ("Pendiente".equals(st)) { %>
                        <!-- ESTADO PENDIENTE: Permitimos Editar y Cancelar -->
                        <button class="btn btn-warning btn-sm text-white me-1"
                                onclick="abrirModalEditar(<%= c.getIdCita() %>, '<%= fIso %>', '<%= hIso %>', '<%= c.getMotivo() %>', <%= c.getPaciente().getIdPaciente() %>, <%= c.getOdontologo().getIdOdontologo() %>)"
                                title="Editar Cita">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="abrirModalCancelar(<%= c.getIdCita() %>)" title="Cancelar Cita">
                            <i class="fas fa-times"></i>
                        </button>

                        <% } else if ("Atendida".equals(st)) { %>
                        <!-- ESTADO ATENDIDA: Flujo hacia Facturación -->
                        <!-- Enviamos el ID de la cita precargado al módulo de facturación -->
                        <a href="facturacion?id_cita_pre=<%= c.getIdCita() %>" class="btn btn-primary btn-sm" title="Ir a Facturar">
                            <i class="fas fa-file-invoice-dollar me-1"></i> Facturar
                        </a>

                        <% } else if ("Facturada".equals(st)) { %>
                        <span class="text-success small"><i class="fas fa-check-double"></i> Facturada</span>
                        <% } else { %>
                        <span class="text-muted small">--</span>
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
    MODAL 1: CREAR / EDITAR CITA
    Este formulario modal reutilizable maneja tanto la creación como la edición.
    Los campos se rellenan dinámicamente mediante las variables Java definidas al inicio
    o mediante JavaScript al hacer clic en "Editar".
-->
<div class="modal fade" id="modalCita" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="tituloModalCita"><%= tituloModal %></h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <form id="formCita" action="citas" method="POST">
                    <!-- Inputs ocultos para controlar la acción en el Servlet -->
                    <input type="hidden" name="accion" value="guardar">
                    <input type="hidden" name="idCita" id="idCita" value="<%= idVal %>">
                    <input type="hidden" name="estado" id="estado" value="<%= estadoVal %>">

                    <!-- Selector de Paciente -->
                    <div class="mb-3">
                        <label class="form-label fw-bold">Paciente</label>
                        <select name="idPaciente" id="idPaciente" class="form-select" required>
                            <option value="" disabled selected>Seleccione...</option>
                            <% for(Paciente p : listaPacientes) {
                                String sel = (p.getIdPaciente() == idPacVal) ? "selected" : ""; %>
                            <option value="<%= p.getIdPaciente() %>" <%= sel %>><%= p.getNombres() %> <%= p.getApellidos() %></option>
                            <% } %>
                        </select>
                    </div>

                    <!-- Selector de Odontólogo -->
                    <div class="mb-3">
                        <label class="form-label fw-bold">Odontólogo</label>
                        <select name="idOdontologo" id="idOdontologo" class="form-select" required>
                            <option value="" disabled selected>Seleccione...</option>
                            <% for(Odontologo o : listaOdontologos) {
                                String sel = (o.getIdOdontologo() == idOdoVal) ? "selected" : "";
                                String nomDoc = (o.getUsuario() != null) ? o.getUsuario().getNombreCompleto() : "Doc"; %>
                            <option value="<%= o.getIdOdontologo() %>" <%= sel %>><%= nomDoc %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="row mb-3">
                        <div class="col-6">
                            <label class="form-label fw-bold">Fecha</label>
                            <input type="date" name="fecha" id="fecha" class="form-control" required value="<%= fechaVal %>">
                        </div>
                        <div class="col-6">
                            <label class="form-label fw-bold">Hora</label>
                            <input type="time" name="hora" id="hora" class="form-control" required value="<%= horaVal %>">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Motivo</label>
                        <input type="text" name="motivo" id="motivo" class="form-control" required value="<%= motivoVal %>">
                    </div>

                    <div class="text-end">
                        <button type="button" class="btn btn-secondary rounded-pill me-2" data-bs-dismiss="modal">Cerrar</button>
                        <button type="submit" class="btn btn-primary rounded-pill px-4"><%= btnModal %></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!--
    MODAL 2: CONFIRMACIÓN DE CANCELACIÓN
    Modal de seguridad para evitar cancelaciones accidentales.
-->
<div class="modal fade" id="modalCancelar" tabindex="-1">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Cancelar Cita</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <i class="fas fa-calendar-times fa-3x text-danger mb-3"></i>
                <p>¿Seguro que deseas cancelar esta cita?</p>
            </div>
            <div class="modal-footer justify-content-center">
                <form action="citas" method="POST">
                    <input type="hidden" name="accion" value="cancelar">
                    <input type="hidden" name="id" id="idCitaCancelar">
                    <!-- Importante: Enviamos la fecha actual para que al recargar volvamos al mismo día -->
                    <input type="hidden" name="fechaActual" value="<%= fechaFiltro %>">
                    <button type="button" class="btn btn-secondary rounded-pill me-2" data-bs-dismiss="modal">No</button>
                    <button type="submit" class="btn btn-danger rounded-pill px-4">Sí, Cancelar</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Scripts de Bootstrap -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<!-- SCRIPTS DE INTERACCIÓN DEL LADO DEL CLIENTE -->
<script>
    var modalCita = new bootstrap.Modal(document.getElementById('modalCita'));
    var modalCancelar = new bootstrap.Modal(document.getElementById('modalCancelar'));

    /*
     * Prepara el modal para crear una nueva cita.
     * Limpia el formulario y establece la fecha actual por defecto.
     */
    function abrirModalCrear() {
        document.getElementById("tituloModalCita").innerText = "Nueva Cita";
        document.getElementById("formCita").reset();
        document.getElementById("idCita").value = "0"; // ID 0 indica creación
        document.getElementById("estado").value = "Pendiente";

        // Lógica para establecer la fecha de hoy en formato ISO (YYYY-MM-DD)
        const now = new Date();
        // Ajuste de zona horaria simple para el input date
        const offset = now.getTimezoneOffset() * 60000;
        const localISOTime = (new Date(now - offset)).toISOString().slice(0, 10);
        document.getElementById("fecha").value = localISOTime;

        modalCita.show();
    }

    /*
     * Prepara el modal para editar una cita existente.
     * Rellena los campos con los datos recibidos desde la tabla.
     */
    function abrirModalEditar(id, fecha, hora, motivo, idPac, idOdo) {
        document.getElementById("tituloModalCita").innerText = "Editar Cita #" + id;
        document.getElementById("idCita").value = id;
        document.getElementById("fecha").value = fecha;
        document.getElementById("hora").value = hora;
        document.getElementById("motivo").value = motivo;
        document.getElementById("idPaciente").value = idPac;
        document.getElementById("idOdontologo").value = idOdo;
        modalCita.show();
    }

    /*
     * Prepara el modal de cancelación seteando el ID correcto.
     */
    function abrirModalCancelar(id) {
        document.getElementById("idCitaCancelar").value = id;
        modalCancelar.show();
    }

    // Lógica Server-Side: Si el Servlet determinó que se está editando (ej: tras un error), abrir modal automáticamente.
    <% if (esEdicion) { %>
    modalCita.show();
    <% } %>
</script>

</body>
</html>