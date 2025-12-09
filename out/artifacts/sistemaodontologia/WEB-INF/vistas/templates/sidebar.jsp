<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="models.Usuario" %>

<!--
=============================================================================
COMPONENTE: SIDEBAR (BARRA LATERAL DE NAVEGACIÓN)
Autor: Byron Melo
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Este fragmento JSP se encarga de renderizar el menú lateral de la aplicación.
Es un componente reutilizable que se incluye en todas las vistas del dashboard.

Funcionalidades principales:
1. Identificación de Usuario: Muestra el nombre y rol del usuario logueado.
2. Navegación Dinámica: Genera los enlaces según el rol (Admin, Odontólogo, Secretaria).
3. Estado Activo: Resalta la opción del menú donde se encuentra el usuario actualmente.
4. Responsividad: Incluye lógica CSS/JS para colapsarse en dispositivos móviles.
=============================================================================
-->

<%
    /* * -------------------------------------------------------------------------
     * SECCIÓN 1: LÓGICA DE SESIÓN Y USUARIO (JAVA SCRIPTLET)
     * -------------------------------------------------------------------------
     */

    // Recuperamos el objeto 'usuario' almacenado en la sesión HTTP durante el Login.
    // Si la sesión expiró o no existe, este objeto será null.
    Usuario usuarioLogueadoSidebar = (Usuario) session.getAttribute("usuario");

    // Variables por defecto para evitar errores visuales si no hay usuario (Null Safety)
    String nombreUser = "Usuario";
    String rolUser = "";

    // Si existe un usuario en sesión, extraemos sus datos reales para mostrarlos
    if (usuarioLogueadoSidebar != null) {
        nombreUser = usuarioLogueadoSidebar.getNombreCompleto();
        // Validamos que el usuario tenga un rol asignado antes de acceder a él
        if (usuarioLogueadoSidebar.getRol() != null) {
            rolUser = usuarioLogueadoSidebar.getRol().getNombreRol();
        }
    }

    /* * -------------------------------------------------------------------------
     * SECCIÓN 2: LÓGICA DE NAVEGACIÓN ACTIVA
     * -------------------------------------------------------------------------
     * Recibimos el parámetro 'activePage' desde la página padre (ej: dashboard.jsp).
     * Este parámetro nos dice qué botón del menú debe pintarse como 'active' (azul/rosado).
     */
    String activePage = request.getParameter("activePage");
    if (activePage == null) activePage = ""; // Evitamos NullPointerException en comparaciones

    /* * -------------------------------------------------------------------------
     * SECCIÓN 3: ENRUTAMIENTO DINÁMICO DE INICIO
     * -------------------------------------------------------------------------
     * El botón "Inicio" debe llevar a lugares distintos según el rol.
     * - Admin -> /admin
     * - Odontólogo -> /odontologo
     * - Secretaria -> /dashboard (Default)
     */
    String linkInicio = request.getContextPath() + "/dashboard";
    if ("Administrador".equalsIgnoreCase(rolUser)) {
        linkInicio = request.getContextPath() + "/admin";
    } else if ("Odontologo".equalsIgnoreCase(rolUser)) {
        linkInicio = request.getContextPath() + "/odontologo";
    }
%>

<!--
IMPORTACIÓN DE ESTILOS
Se carga la hoja de estilos global. Es necesario hacerlo aquí también por seguridad,
para garantizar que el componente se vea bien incluso si la página padre olvidó importarlo.
${pageContext.request.contextPath} asegura que la ruta sea absoluta desde la raíz del proyecto.
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">

<!--
ESTILOS LOCALES (SCOPE: SIDEBAR)
Ajustes específicos para la presentación del logotipo y el contenedor de usuario
dentro de la barra lateral.
-->
<style>
    /* Controla el tamaño del logo para que no rompa el diseño en pantallas pequeñas */
    .sidebar-logo {
        max-width: 80%;
        max-height: 80px;
        object-fit: contain; /* Mantiene la proporción de la imagen */
        margin: 0 auto 20px auto;
        display: block;
    }

    /* Habilita scroll vertical si el menú es más alto que la pantalla (útil en móviles) */
    .sidebar {
        overflow-y: auto;
        scrollbar-width: thin; /* Estilo fino para Firefox */
    }

    /* Espaciado para la caja de información del usuario */
    .user-info-box {
        margin-bottom: 30px;
        padding: 0 10px;
    }
</style>

<!--
ELEMENTOS PARA VERSIÓN MÓVIL
1. Overlay: Fondo oscuro semitransparente que aparece detrás del menú al abrirlo.
2. Botón Toggle: El ícono de hamburguesa (fas fa-bars) visible solo en móviles.
-->
<div class="sidebar-overlay" id="sidebarOverlay" onclick="toggleSidebar()"></div>

<button class="mobile-toggle" onclick="toggleSidebar()">
    <i class="fas fa-bars"></i>
</button>

<!--
ESTRUCTURA PRINCIPAL DEL MENÚ
El ID 'sidebarMenu' es utilizado por el JavaScript para aplicar la clase 'active'.
-->
<nav class="sidebar" id="sidebarMenu">

    <!-- CABECERA DEL MENÚ: LOGO -->
    <div class="text-center py-4 position-relative">
        <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="EndoDental" class="sidebar-logo">

        <!-- Botón 'X' para cerrar el menú, visible solo en móviles (clase d-md-none de Bootstrap) -->
        <button class="btn btn-sm text-secondary position-absolute top-0 end-0 m-2 d-md-none" onclick="toggleSidebar()">
            <i class="fas fa-times"></i>
        </button>
    </div>

    <!-- CABECERA DEL MENÚ: INFORMACIÓN DE USUARIO -->
    <div class="text-center mb-4 px-2 user-info-box">
        <div class="small text-muted opacity-75">Bienvenido/a,</div>
        <!-- Usamos text-truncate para cortar nombres muy largos y evitar desbordamiento -->
        <div class="fw-bold text-dark text-truncate" title="<%= nombreUser %>">
            <%= nombreUser %>
        </div>
        <!-- Badge que muestra el Rol del usuario (Admin, Doc, Sec) -->
        <div class="badge bg-primary-subtle text-primary mt-1" style="font-size: 0.75rem;">
            <%= rolUser %>
        </div>
    </div>

    <!-- LISTA DE ENLACES DE NAVEGACIÓN -->
    <ul class="sidebar-menu list-unstyled">

        <!-- ENLACE INICIO (Común para todos) -->
        <!-- La expresión <%= "inicio".equals(activePage) ? "active" : "" %> agrega la clase CSS si corresponde -->
        <li>
            <a href="<%= linkInicio %>" class="sidebar-link <%= "inicio".equals(activePage) ? "active" : "" %>">
                <i class="fas fa-home me-2"></i> Inicio
            </a>
        </li>

        <%--
            BLOQUE DE SEGURIDAD: VISTAS DE GESTIÓN
            Solo se renderizan si el usuario es Secretaria o Administrador.
            Esto protege la interfaz visualmente (aunque la seguridad real está en los Servlets).
        --%>
        <% if ("Secretaria".equalsIgnoreCase(rolUser) ) { %>
        <li>
            <a href="${pageContext.request.contextPath}/pacientes?accion=listar" class="sidebar-link <%= "pacientes".equals(activePage) ? "active" : "" %>">
                <i class="fas fa-user-injured me-2"></i> Pacientes
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/citas" class="sidebar-link <%= "agenda".equals(activePage) ? "active" : "" %>">
                <i class="fas fa-calendar-alt me-2"></i> Agenda
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/facturacion" class="sidebar-link <%= "facturacion".equals(activePage) ? "active" : "" %>">
                <i class="fas fa-file-invoice-dollar me-2"></i> Facturación
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/inventario" class="sidebar-link <%= "inventario".equals(activePage) ? "active" : "" %>">
                <i class="fas fa-boxes me-2"></i> Inventario
            </a>
        </li>
        <% } %>

        <%-- EL ODONTÓLOGO NO TIENE MENÚS ADICIONALES POR REQUERIMIENTO --%>

        <!-- BOTÓN DE SALIDA -->
        <li class="mt-auto pt-4">
            <!-- Apunta al LoginServlet con acción 'logout' para invalidar la sesión -->
            <a href="${pageContext.request.contextPath}/login?action=logout" class="sidebar-link text-danger">
                <i class="fas fa-sign-out-alt me-2"></i> Salir
            </a>
        </li>
    </ul>
</nav>

<!--
SCRIPT DE COMPORTAMIENTO
Función simple para alternar la clase 'active' en el menú y el overlay.
Esto permite mostrar/ocultar el menú lateral en dispositivos móviles.
-->
<script>
    function toggleSidebar() {
        const sidebar = document.getElementById('sidebarMenu');
        const overlay = document.getElementById('sidebarOverlay');

        // toggle() añade la clase si no existe, o la quita si ya existe
        sidebar.classList.toggle('active');
        overlay.classList.toggle('active');
    }
</script>