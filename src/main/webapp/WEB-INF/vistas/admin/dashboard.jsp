<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Importación de utilidades de Java y los modelos del dominio (Usuario, Odontologo, Rol) -->
<%@ page import="java.util.*, models.*" %>

<!--
=============================================================================
VISTA: DASHBOARD ADMINISTRADOR (dashboard.jsp)
Autor: Byron Melo
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Panel de control principal para el rol de Administrador.
Esta vista permite la gestión integral del personal de la clínica (CRUD).

Funcionalidades:
1. Listado de usuarios (Secretarias, Odontólogos, Administradores).
2. Filtrado entre usuarios Activos y Papelera de Reciclaje (Inactivos).
3. Creación y Edición de usuarios mediante un Modal dinámico que se adapta
según el rol seleccionado (muestra campos extra si es Odontólogo).
4. Desactivación (Soft Delete) y Reactivación de cuentas.
=============================================================================
-->

<%
    /* * -------------------------------------------------------------------------
     * BLOQUE DE LÓGICA DE PRESENTACIÓN (SERVER-SIDE SCRIPTLET)
     * -------------------------------------------------------------------------
     */

    // 1. RECUPERACIÓN DE LA LISTA DE USUARIOS
    // Se obtiene la lista enviada por el AdminServlet. Se inicializa vacía si es null para evitar errores.
    List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
    if (usuarios == null) usuarios = new ArrayList<>();

    // 2. CONFIGURACIÓN DEL TÍTULO
    // El título cambia dependiendo de si estamos viendo "Personal Activo" o "Inactivo".
    String titulo = (String) request.getAttribute("titulo");
    if (titulo == null) titulo = "Gestión de Personal";

    // 3. ESTADO DE LA VISTA (ACTIVOS VS PAPELERA)
    // Esta bandera booleana controla qué botones de acción se muestran en la tabla.
    Boolean esPapelera = (Boolean) request.getAttribute("esPapelera");
    if (esPapelera == null) esPapelera = false;

    // 4. MENSAJES DE FEEDBACK
    // Recuperamos mensajes de éxito o error tras realizar una operación (POST/GET).
    String error = request.getParameter("error");
    String exito = request.getParameter("exito");

    /* * -------------------------------------------------------------------------
     * LÓGICA DE PRECARGA PARA EL MODAL (CREACIÓN / EDICIÓN)
     * -------------------------------------------------------------------------
     * Si el Servlet envía un objeto 'usuarioEditar', significa que estamos en modo EDICIÓN.
     * Extraemos sus valores para pre-llenar los inputs del formulario modal.
     */
    Usuario uEdit = (Usuario) request.getAttribute("usuarioEditar");
    // Si es un odontólogo, recuperamos también sus datos profesionales extra.
    Odontologo odoEdit = (Odontologo) request.getAttribute("odontologoExtra");

    // Variables por defecto (Valores vacíos para modo CREACIÓN)
    int idVal = 0;
    String nomVal = "";
    String userVal = "";
    String mailVal = "";
    int rolVal = 2; // Por defecto preseleccionamos el rol 2 (Secretaria)

    // Variables específicas para Odontólogos
    String espVal = "";
    String codVal = "";

    // Sobreescritura de variables si estamos en modo EDICIÓN
    if (uEdit != null) {
        idVal = uEdit.getIdUsuario();
        nomVal = (uEdit.getNombreCompleto() != null) ? uEdit.getNombreCompleto() : "";
        userVal = (uEdit.getUsername() != null) ? uEdit.getUsername() : "";
        mailVal = (uEdit.getEmail() != null) ? uEdit.getEmail() : "";
        // Recuperamos el ID del rol para seleccionar la opción correcta en el <select>
        if (uEdit.getRol() != null) {
            rolVal = uEdit.getRol().getIdRol();
        }
    }

    // Si hay datos de odontólogo, los cargamos
    if (odoEdit != null) {
        espVal = (odoEdit.getEspecialidad() != null) ? odoEdit.getEspecialidad() : "";
        codVal = (odoEdit.getCodigoMedico() != null) ? odoEdit.getCodigoMedico() : "";
    }

    // Bandera para abrir el modal automáticamente al cargar la página (útil tras un error de validación o al hacer clic en editar)
    Boolean mostrarModal = (Boolean) request.getAttribute("mostrarModal");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>Admin - EndoDental</title>
    <!-- Estilos CSS (Bootstrap + Personalizados) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<div class="dashboard-wrapper">
    <!--
        INCLUSIÓN DEL SIDEBAR
        El parámetro 'activePage' = 'inicio' resalta la opción correspondiente en el menú lateral.
    -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inicio"/>
    </jsp:include>

    <main class="main-content">

        <!-- ALERTAS DE SISTEMA -->
        <% if(exito != null) { %>
        <div class="alert alert-success alert-dismissible fade show">
            <i class="fas fa-check-circle me-2"></i> Operación exitosa.
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>
        <% if(error != null) { %>
        <div class="alert alert-danger alert-dismissible fade show">
            <i class="fas fa-exclamation-triangle me-2"></i> <%=error%>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <% } %>

        <!-- ENCABEZADO Y BOTONES DE ACCIÓN -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold text-dark"><%= titulo %></h2>

            <div class="d-flex gap-2">
                <%
                    // Lógica de visualización de botones según el estado (Activos vs Papelera)
                    if (!esPapelera) {
                %>
                <!-- Botón Crear Secretaria: Abre modal preseleccionando rol 2 -->
                <button class="btn btn-info text-white" onclick="abrirModalCrear(2)">
                    <i class="fas fa-user-plus me-2"></i> Secretaria
                </button>
                <!-- Botón Crear Odontólogo: Abre modal preseleccionando rol 3 -->
                <button class="btn btn-primary-custom" onclick="abrirModalCrear(3)">
                    <i class="fas fa-user-md me-2"></i> Odontólogo
                </button>
                <!-- Botón Ir a Papelera -->
                <a href="admin?accion=inactivos" class="btn btn-secondary">
                    <i class="fas fa-trash-restore me-2"></i> Inactivos
                </a>
                <% } else { %>
                <!-- Botón Volver (Solo visible en Papelera) -->
                <a href="admin" class="btn btn-outline-primary">
                    <i class="fas fa-arrow-left me-2"></i> Volver
                </a>
                <% } %>
            </div>
        </div>

        <!-- TABLA DE USUARIOS -->
        <div class="custom-table-container">
            <table class="table table-custom table-hover align-middle">
                <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Usuario</th>
                    <th>Rol</th>
                    <th>Email</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody>
                <%
                    /* * BUCLE DE RENDERIZADO DE FILAS
                     * Recorre la lista de usuarios y genera el HTML correspondiente.
                     */
                    for (Usuario u : usuarios) {
                        String rol = (u.getRol() != null) ? u.getRol().getNombreRol() : "Sin Rol";

                        // Lógica para asignar color al Badge según el rol
                        String badgeClass = "bg-secondary";
                        if("Administrador".equals(rol)) badgeClass = "bg-dark";
                        if("Secretaria".equals(rol)) badgeClass = "bg-info text-dark";
                        if("Odontologo".equals(rol)) badgeClass = "bg-primary";
                %>
                <tr>
                    <td class="fw-bold"><%= u.getNombreCompleto() %></td>
                    <td><%= u.getUsername() %></td>
                    <td><span class="badge <%= badgeClass %>"><%= rol %></span></td>
                    <td><%= u.getEmail() %></td>
                    <td>
                        <% if (!esPapelera) { %>
                        <!-- ACCIONES PARA USUARIOS ACTIVOS -->
                        <a href="admin?accion=editar&id=<%= u.getIdUsuario() %>" class="btn-action-edit me-2" title="Editar">
                            <i class="fas fa-edit"></i>
                        </a>

                        <!-- Botón Desactivar: Abre un modal de confirmación en lugar de alerta simple -->
                        <button onclick="abrirModalDesactivar(<%= u.getIdUsuario() %>, '<%= u.getNombreCompleto() %>')"
                                class="btn-action-delete border-0 bg-transparent text-danger" title="Desactivar">
                            <i class="fas fa-user-times"></i>
                        </button>

                        <% } else { %>
                        <!-- ACCIONES PARA USUARIOS INACTIVOS -->
                        <a href="admin?accion=activar&id=<%= u.getIdUsuario() %>" class="text-success fw-bold text-decoration-none" title="Reactivar">
                            <i class="fas fa-check-circle me-1"></i> Reactivar
                        </a>
                        <% } %>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!--
    MODAL 1: CREAR / EDITAR USUARIO
    Formulario dinámico que muestra u oculta campos según el rol seleccionado.
-->
<div class="modal fade" id="modalUsuario" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content border-0 rounded-4">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="tituloModal">Gestión de Usuario</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="admin" method="POST">
                <div class="modal-body p-4">
                    <input type="hidden" name="accion" value="guardar">
                    <!-- ID: 0 para nuevo, >0 para editar -->
                    <input type="hidden" name="idUsuario" id="idUsuario" value="<%= idVal %>">

                    <div class="mb-3">
                        <label class="fw-bold">Rol</label>
                        <!-- El evento 'onchange' dispara la función JS para mostrar campos de Odontólogo -->
                        <select name="idRol" id="idRol" class="form-select" onchange="toggleCamposOdontologo()">
                            <option value="2">Secretaria</option>
                            <option value="3">Odontólogo</option>
                            <option value="1">Administrador</option>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label>Nombre Completo</label>
                        <input type="text" name="nombre" id="nombre" class="form-control" required value="<%= nomVal %>">
                    </div>
                    <div class="mb-3">
                        <label>Usuario (Login)</label>
                        <input type="text" name="username" id="username" class="form-control" required value="<%= userVal %>">
                    </div>
                    <div class="mb-3">
                        <label>Contraseña</label>
                        <!-- El placeholder y required cambian si es edición (no obligatoria) o creación (obligatoria) -->
                        <input type="password" name="password" class="form-control"
                               placeholder="<%= (idVal > 0) ? "Dejar vacío para no cambiar" : "Requerido" %>"
                            <%= (idVal == 0) ? "required" : "" %>>
                    </div>
                    <div class="mb-3">
                        <label>Email</label>
                        <input type="email" name="email" id="email" class="form-control" value="<%= mailVal %>">
                    </div>

                    <!--
                        CAMPOS EXTRA ODONTÓLOGO
                        Este div está oculto por defecto (display: none) y se muestra vía JS
                        solo si el rol seleccionado es '3' (Odontólogo).
                    -->
                    <div id="camposOdontologo" style="display: none; background-color: #f8f9fa; padding: 15px; border-radius: 10px;">
                        <h6 class="text-primary fw-bold border-bottom pb-2 mb-3">Datos Profesionales</h6>
                        <div class="mb-3">
                            <label>Especialidad</label>
                            <input type="text" name="especialidad" id="especialidad" class="form-control" value="<%= espVal %>">
                        </div>
                        <div class="mb-3">
                            <label>Código Médico / Licencia</label>
                            <input type="text" name="codigo" id="codigo" class="form-control" value="<%= codVal %>">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<!--
    MODAL 2: CONFIRMACIÓN DE DESACTIVACIÓN
    Asegura que el administrador confirme antes de inhabilitar un usuario.
-->
<div class="modal fade" id="modalDesactivar" tabindex="-1">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title">Desactivar Usuario</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body text-center">
                <i class="fas fa-user-slash fa-3x text-danger mb-3"></i>
                <p>¿Estás seguro que deseas desactivar el acceso a <strong id="nombreUsuarioDesactivar"></strong>?</p>
                <small class="text-muted">El usuario ya no podrá iniciar sesión.</small>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <!-- Enlace dinámico: El href se setea con JS -->
                <a href="#" id="btnConfirmarDesactivar" class="btn btn-danger">Sí, Desactivar</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

<script>
    // Instancias de modales
    var modal = new bootstrap.Modal(document.getElementById('modalUsuario'));
    var modalDesactivar = new bootstrap.Modal(document.getElementById('modalDesactivar'));

    /**
     * Función para mostrar u ocultar los campos de especialidad/código
     * dependiendo del rol seleccionado en el dropdown.
     */
    function toggleCamposOdontologo() {
        var rol = document.getElementById("idRol").value;
        var div = document.getElementById("camposOdontologo");
        // Rol 3 = Odontólogo
        if(rol == "3") {
            div.style.display = "block";
            // Hacemos el campo requerido solo si es visible
            document.getElementById("especialidad").required = true;
        } else {
            div.style.display = "none";
            document.getElementById("especialidad").required = false;
        }
    }

    /**
     * Prepara y abre el modal para crear un nuevo usuario.
     * @param {number} idRolPreseleccionado - El rol que se seleccionará por defecto (2=Sec, 3=Doc).
     */
    function abrirModalCrear(idRolPreseleccionado) {
        document.getElementById("tituloModal").innerText = "Registrar Personal";
        document.getElementById("idUsuario").value = "0"; // ID 0 = Nuevo
        // Limpiar campos
        document.getElementById("nombre").value = "";
        document.getElementById("username").value = "";
        document.getElementById("email").value = "";

        document.getElementById("idRol").value = idRolPreseleccionado;

        // Actualizar visibilidad de campos extra
        toggleCamposOdontologo();

        modal.show();
    }

    /**
     * Abre el modal de confirmación para desactivar usuario.
     * @param {number} id - ID del usuario.
     * @param {string} nombre - Nombre del usuario para mostrar en el mensaje.
     */
    function abrirModalDesactivar(id, nombre) {
        document.getElementById("nombreUsuarioDesactivar").innerText = nombre;
        // Configuramos el enlace para que apunte al Servlet con la acción 'eliminar'
        document.getElementById("btnConfirmarDesactivar").href = "admin?accion=eliminar&id=" + id;
        modalDesactivar.show();
    }

    /*
     * LÓGICA SERVER-SIDE PARA REAPERTURA DE MODAL
     * Si venimos de un 'case editar' en el Servlet, 'mostrarModal' será true.
     * Preseleccionamos el rol correcto y mostramos el modal automáticamente.
     */
    <% if(mostrarModal != null && mostrarModal) { %>
    document.getElementById("idRol").value = "<%= rolVal %>";
    toggleCamposOdontologo();
    modal.show();
    <% } %>
</script>

</body>
</html>