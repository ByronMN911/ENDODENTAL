<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- IMPORTANTE: Importar las clases Java necesarias para manejar listas y objetos del modelo -->
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="models.Paciente" %>

<!--
=============================================================================
VISTA: LISTADO DE PACIENTES (lista.jsp)
Autor: Byron Melo
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Esta vista JSP es el componente central del módulo de gestión de pacientes.
Implementa una interfaz de usuario dinámica que permite:
1. Visualizar el listado de pacientes activos e inactivos (Papelera).
2. Buscar pacientes por número de cédula en tiempo real.
3. Gestionar operaciones CRUD (Crear, Editar, Eliminar/Archivar) mediante Modales.
4. Proveer feedback visual mediante alertas de éxito o error.

Arquitectura:
Esta vista actúa como la capa de presentación final en el patrón MVC, recibiendo
datos procesados previamente por el controlador (PacienteServlet).
=============================================================================
-->

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Pacientes - EndoDental</title>
    <!--
        DEPENDENCIAS CSS
        Se utilizan librerías CDN para estilos (Bootstrap 5) e iconos (FontAwesome).
        Style.css contiene las personalizaciones de colores corporativos y ajustes finos.
    -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<%
    /*
     * -------------------------------------------------------------------------
     * BLOQUE DE LÓGICA DE PRESENTACIÓN (SERVER-SIDE SCRIPTLET)
     * -------------------------------------------------------------------------
     * En este bloque recuperamos los atributos enviados por el Controlador (Servlet).
     * Es fundamental manejar posibles valores nulos para evitar excepciones en tiempo de ejecución.
     */

    // 1. Recuperación de la lista principal de pacientes.
    // Se inicializa una lista vacía si el atributo es null para permitir que la tabla se renderice vacía sin romper la página.
    List<Paciente> pacientes = (List<Paciente>) request.getAttribute("pacientes");
    if (pacientes == null) pacientes = new ArrayList<>();

    // 2. Título dinámico de la página.
    // Permite que el mismo JSP sirva para "Listado General" o "Resultados de Búsqueda".
    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Gestión de Pacientes";

    // 3. Bandera de Estado (Papelera vs Activos).
    // Determina qué botones de acción se mostrarán (Eliminar vs Restaurar).
    Boolean esPapeleraObj = (Boolean) request.getAttribute("esPapelera");
    boolean esPapelera = (esPapeleraObj != null) ? esPapeleraObj : false;

    // 4. Mensajes de retroalimentación para el usuario.
    String error = (String) request.getAttribute("error");
    String exito = request.getParameter("exito");

    /*
     * LÓGICA DE PRECARGA PARA EL MODAL (EDICIÓN O ERROR)
     * --------------------------------------------------
     * Si el Servlet devuelve un objeto 'pacienteEditar', significa que el usuario
     * quiere editar un registro o que hubo un error de validación al crear/editar
     * y necesitamos repoblar el formulario para que no pierda sus datos.
     */
    Paciente pEdit = (Paciente) request.getAttribute("pacienteEditar");

    // Variables auxiliares para rellenar los campos del formulario modal
    int idVal = 0;
    String cedulaVal = "", nombresVal = "", apellidosVal = "";
    String telefonoVal = "", emailVal = "", alergiasVal = "";
    String tituloModal = "Registrar Nuevo Paciente";
    String btnModal = "Guardar Paciente";

    // Si existe un objeto de edición, extraemos sus valores
    if (pEdit != null) {
        idVal = pEdit.getIdPaciente();
        // Usamos operadores ternarios para convertir nulls de la BD en cadenas vacías visuales
        cedulaVal = (pEdit.getCedula() != null) ? pEdit.getCedula() : "";
        nombresVal = (pEdit.getNombres() != null) ? pEdit.getNombres() : "";
        apellidosVal = (pEdit.getApellidos() != null) ? pEdit.getApellidos() : "";
        telefonoVal = (pEdit.getTelefono() != null) ? pEdit.getTelefono() : "";
        emailVal = (pEdit.getEmail() != null) ? pEdit.getEmail() : "";
        alergiasVal = (pEdit.getAlergias() != null) ? pEdit.getAlergias() : "";

        // Si el ID es mayor a 0, estamos en modo EDICIÓN
        if (idVal > 0) {
            tituloModal = "Editar Datos del Paciente";
            btnModal = "Actualizar Datos";
        }
    }

    // Bandera que indica al JavaScript (al final del archivo) si debe abrir el modal automáticamente
    Boolean mostrarModalObj = (Boolean) request.getAttribute("mostrarModal");
    boolean mostrarModal = (mostrarModalObj != null) ? mostrarModalObj : false;
%>

<div class="dashboard-wrapper">
    <!--
        COMPONENTE SIDEBAR
        Se incluye la barra lateral común, pasando el parámetro 'activePage' para resaltar
        la sección actual en el menú de navegación.
    -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="pacientes"/>
    </jsp:include>

    <!-- CONTENIDO PRINCIPAL -->
    <main class="main-content">

        <!--
            SECCIÓN DE ALERTAS
            Renderizado condicional de mensajes de éxito o error utilizando componentes Bootstrap.
        -->
        <% if (error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="fas fa-exclamation-circle me-2"></i> <%= error %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <% } %>
        <% if (exito != null) { %>
        <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle me-2"></i> Operación exitosa. <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        <% } %>

        <!-- ENCABEZADO DE LA PÁGINA -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold"><%= titulo %></h2>
                <!-- Botones de alternancia entre vista de Activos e Inactivos (Papelera) -->
                <div class="btn-group">
                    <a href="pacientes?accion=listar" class="btn btn-sm <%= (!esPapelera) ? "btn-primary" : "btn-outline-primary" %>">Activos</a>
                    <a href="pacientes?accion=inactivos" class="btn btn-sm <%= (esPapelera) ? "btn-danger" : "btn-outline-danger" %>">Papelera</a>
                </div>
            </div>

            <!-- Botón 'Nuevo Paciente': Solo visible si NO estamos en la papelera -->
            <% if (!esPapelera) { %>
            <button class="btn btn-primary-custom" onclick="abrirModalNuevo()">
                <i class="fas fa-plus me-2"></i> Nuevo Paciente
            </button>
            <% } %>
        </div>

        <div class="custom-table-container">
            <!--
                BARRA DE BÚSQUEDA
                Formulario GET que envía el parámetro 'busqueda' al Servlet.
                Incluye validación HTML5 (pattern="\d+") para asegurar que solo se ingresen números.
            -->
            <form action="pacientes" method="GET" class="mb-4 d-flex w-50">
                <input type="hidden" name="accion" value="buscar">
                <input type="text" name="busqueda" class="form-control form-control-custom me-2"
                       placeholder="Buscar por cédula..." pattern="\d+" title="Solo números" required>
                <button type="submit" class="btn btn-secondary rounded-4"><i class="fas fa-search"></i></button>
            </form>

            <!-- TABLA DE DATOS -->
            <table class="table table-custom table-hover align-middle">
                <thead>
                <tr>
                    <th>Cédula</th>
                    <th>Apellidos</th>
                    <th>Nombres</th>
                    <th>Contacto</th>
                    <th>Alergias</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <%
                    /* * BUCLE DE GENERACIÓN DE FILAS
                     * Itera sobre la lista de pacientes. Si está vacía, muestra un mensaje informativo.
                     */
                    if (pacientes.isEmpty()) {
                %>
                <tr><td colspan="6" class="text-center py-4 text-muted">No se encontraron pacientes registrados.</td></tr>
                <% } else {
                    for (Paciente p : pacientes) { %>
                <tr>
                    <td class="fw-bold"><%= p.getCedula() %></td>
                    <td><%= p.getApellidos() %></td>
                    <td><%= p.getNombres() %></td>
                    <td>
                        <!-- Información de contacto formateada en dos líneas -->
                        <div class="small"><i class="fas fa-phone me-1"></i> <%= p.getTelefono() %></div>
                        <div class="small text-muted"><i class="fas fa-envelope me-1"></i> <%= p.getEmail() %></div>
                    </td>
                    <td>
                        <!-- Lógica visual: Si tiene alergias, muestra un badge rojo de alerta -->
                        <% if (p.getAlergias() != null && !p.getAlergias().isEmpty() && !p.getAlergias().equalsIgnoreCase("Ninguna")) { %>
                        <span class="badge bg-danger"><%= p.getAlergias() %></span>
                        <% } else { %>
                        <span class="text-muted small">Ninguna</span>
                        <% } %>
                    </td>
                    <td>
                        <!--
                            BOTONES DE ACCIÓN CONDICIONALES
                            - Si estamos en Papelera -> Mostrar botón Restaurar.
                            - Si estamos en Activos -> Mostrar Editar y Archivar.
                        -->
                        <% if (esPapelera) { %>
                        <a href="pacientes?accion=activar&id=<%= p.getIdPaciente() %>" class="btn btn-success btn-sm"><i class="fas fa-undo"></i> Restaurar</a>
                        <% } else { %>

                        <!-- Botón Editar: Llama a función JS pasando los datos del objeto para rellenar el modal sin recargar -->
                        <button class="btn btn-warning btn-sm text-white me-1"
                                onclick="abrirModalEditar(<%= p.getIdPaciente() %>, '<%= p.getCedula() %>', '<%= p.getNombres() %>', '<%= p.getApellidos() %>', '<%= p.getTelefono() %>', '<%= p.getEmail() %>', '<%= p.getAlergias() %>')">
                            <i class="fas fa-edit"></i>
                        </button>

                        <!-- Botón Eliminar: Abre modal de confirmación -->
                        <button class="btn btn-danger btn-sm" onclick="abrirModalEliminar(<%= p.getIdPaciente() %>, '<%= p.getNombres() %>')">
                            <i class="fas fa-trash-alt"></i>
                        </button>
                        <% } %>
                    </td>
                </tr>
                <% }} %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!--
    MODAL 1: FORMULARIO DE PACIENTE (CREAR / EDITAR)
    Este componente modal reutilizable maneja tanto la creación como la edición.
    El título y la acción del botón cambian dinámicamente vía JavaScript/Java.
-->
<div class="modal fade" id="modalPaciente" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="modalTitle"><%= tituloModal %></h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <!-- El formulario envía un POST al Servlet. El campo oculto idPaciente define si es INSERT o UPDATE -->
                <form id="formPaciente" action="pacientes" method="POST">
                    <input type="hidden" name="idPaciente" id="idPaciente" value="<%= idVal %>">

                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Cédula</label>
                            <input type="text" name="cedula" id="cedula" class="form-control form-control-custom"
                                   required pattern="\d{10}" title="10 dígitos numéricos" value="<%= cedulaVal %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Nombres</label>
                            <input type="text" name="nombres" id="nombres" class="form-control form-control-custom" required value="<%= nombresVal %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Apellidos</label>
                            <input type="text" name="apellidos" id="apellidos" class="form-control form-control-custom" required value="<%= apellidosVal %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Teléfono</label>
                            <input type="text" name="telefono" id="telefono" class="form-control form-control-custom" value="<%= telefonoVal %>">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label fw-bold">Email</label>
                            <input type="email" name="email" id="email" class="form-control form-control-custom" value="<%= emailVal %>">
                        </div>
                        <div class="col-md-12">
                            <label class="form-label fw-bold">Alergias</label>
                            <textarea name="alergias" id="alergias" class="form-control form-control-custom" rows="2"><%= alergiasVal %></textarea>
                        </div>
                    </div>

                    <div class="text-end mt-4">
                        <button type="button" class="btn btn-secondary rounded-pill me-2" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary-custom px-4 rounded-pill" id="btnGuardar"><%= btnModal %></button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!--
    MODAL 2: CONFIRMACIÓN DE ELIMINACIÓN
    Provee una capa de seguridad visual antes de ejecutar la acción de archivado (Soft Delete).
-->
<div class="modal fade" id="modalEliminar" tabindex="-1">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Archivar Paciente</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <p>¿Archivar a <strong id="nombrePacEliminar"></strong>?</p>
                <small class="text-muted">Podrás restaurarlo desde la papelera.</small>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No</button>
                <!-- El href de este botón se actualiza dinámicamente con JavaScript -->
                <a href="#" id="btnConfirmarEliminar" class="btn btn-danger">Sí, Archivar</a>
            </div>
        </div>
    </div>
</div>

<!-- Scripts de Bootstrap para funcionalidad de modales -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Inicialización de instancias de modales
    var modalPaciente = new bootstrap.Modal(document.getElementById('modalPaciente'));
    var modalEliminar = new bootstrap.Modal(document.getElementById('modalEliminar'));

    /**
     * Función para abrir el modal en modo "Nuevo Registro".
     * Limpia el formulario y resetea el ID a 0 para indicar inserción.
     */
    function abrirModalNuevo() {
        document.getElementById("formPaciente").reset();
        document.getElementById("idPaciente").value = "0"; // 0 = Nuevo
        document.getElementById("modalTitle").innerText = "Registrar Nuevo Paciente";
        document.getElementById("btnGuardar").innerText = "Guardar Paciente";

        // Limpieza explícita de valores para evitar caché visual
        document.getElementById("cedula").value = "";
        document.getElementById("nombres").value = "";
        document.getElementById("apellidos").value = "";
        document.getElementById("telefono").value = "";
        document.getElementById("email").value = "";
        document.getElementById("alergias").value = "";

        modalPaciente.show();
    }

    /**
     * Función para abrir el modal en modo "Edición".
     * Recibe los datos de la fila seleccionada y los inyecta en los campos del formulario.
     */
    function abrirModalEditar(id, ced, nom, ape, tel, mail, aler) {
        document.getElementById("modalTitle").innerText = "Editar Paciente";
        document.getElementById("btnGuardar").innerText = "Actualizar Datos";

        document.getElementById("idPaciente").value = id;
        document.getElementById("cedula").value = ced;
        document.getElementById("nombres").value = nom;
        document.getElementById("apellidos").value = ape;
        document.getElementById("telefono").value = tel;
        document.getElementById("email").value = mail;
        document.getElementById("alergias").value = aler;

        modalPaciente.show();
    }

    /**
     * Función para configurar y mostrar el modal de eliminación.
     */
    function abrirModalEliminar(id, nombre) {
        document.getElementById("nombrePacEliminar").innerText = nombre;
        // Configura el enlace de confirmación para apuntar al servlet con la acción correcta
        document.getElementById("btnConfirmarEliminar").href = "pacientes?accion=eliminar&id=" + id;
        modalEliminar.show();
    }

    // LÓGICA SERVER-SIDE PARA REAPERTURA AUTOMÁTICA
    // Si el Servlet establece 'mostrarModal' en true (por error de validación), el modal se abre al cargar.
    <% if (mostrarModal) { %>
    modalPaciente.show();
    <% } %>
</script>

</body>
</html>