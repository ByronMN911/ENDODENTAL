<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="models.Usuario" %>
<%
    // RECUPERACIÓN DE SESIÓN
    Usuario usuarioLogueadoSidebar = (Usuario) session.getAttribute("usuario");

    String nombreUser = "Usuario";
    String rolUser = "";

    if (usuarioLogueadoSidebar != null) {
        nombreUser = usuarioLogueadoSidebar.getNombreCompleto();
        if (usuarioLogueadoSidebar.getRol() != null) {
            rolUser = usuarioLogueadoSidebar.getRol().getNombreRol();
        }
    }

    // Lógica para resaltar el menú activo
    String activePage = request.getParameter("activePage");
    if (activePage == null) activePage = "";

    // LÓGICA DE RUTA DE INICIO SEGÚN ROL
    String linkInicio = request.getContextPath() + "/dashboard"; // Default (Secretaria)
    if ("Administrador".equalsIgnoreCase(rolUser)) {
        linkInicio = request.getContextPath() + "/admin";
    } else if ("Odontologo".equalsIgnoreCase(rolUser)) {
        linkInicio = request.getContextPath() + "/odontologo";
    }
%>

<!--
IMPORTANTE: Asegúrate de que 'assets' esté en 'webapp/assets', NO en 'webapp/WEB-INF/assets'.
WEB-INF es privado y el navegador no puede leer CSS desde ahí.
-->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">

<!-- Overlay de fondo (fondo oscuro al abrir menú en móvil) -->
<div class="sidebar-overlay" id="sidebarOverlay" onclick="toggleSidebar()"></div>

<!-- Botón Hamburguesa (Visible solo en móvil gracias al CSS) -->
<button class="mobile-toggle" onclick="toggleSidebar()">
    <i class="fas fa-bars"></i>
</button>

<nav class="sidebar" id="sidebarMenu">
    <!-- Logo -->
    <div class="text-center py-4 position-relative">
        <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="EndoDental" class="sidebar-logo">

        <!-- Botón 'X' para cerrar dentro del menú (Solo móvil) -->
        <button class="btn btn-sm text-secondary position-absolute top-0 end-0 m-2 d-md-none" onclick="toggleSidebar()">
            <i class="fas fa-times"></i>
        </button>
    </div>

    <!-- Info Usuario y Bienvenida -->
    <div class="text-center mb-4 px-2 user-info-box">
        <div class="small text-muted opacity-75">Bienvenido/a,</div>
        <div class="fw-bold text-dark text-truncate" title="<%= nombreUser %>">
            <%= nombreUser %>
        </div>
        <div class="badge bg-primary-subtle text-primary mt-1" style="font-size: 0.75rem;">
            <%= rolUser %>
        </div>
    </div>

    <!-- Menú de Navegación -->
    <ul class="sidebar-menu list-unstyled">
        <li>
            <a href="<%= linkInicio %>" class="sidebar-link <%= "inicio".equals(activePage) ? "active" : "" %>">
                <i class="fas fa-home me-2"></i> Inicio
            </a>
        </li>

        <%-- OPCIONES EXCLUSIVAS DE SECRETARIA --%>
        <% if ("Secretaria".equalsIgnoreCase(rolUser)) { %>
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

        <%-- OPCIONES DE ADMINISTRADOR (Si quisieras agregar más en el futuro) --%>
        <% if ("Administrador".equalsIgnoreCase(rolUser)) { %>
        <!-- Por ahora comparte Inicio, pero aquí podrías poner 'Usuarios', 'Reportes', etc. -->
        <% } %>

        <%-- EL ODONTÓLOGO Y EL ADMIN NO TIENEN MENÚS EXTRA POR AHORA, SOLO INICIO Y SALIR --%>

        <li class="pt-4">
            <a href="${pageContext.request.contextPath}/login?action=logout" class="sidebar-link text-danger">
                <i class="fas fa-sign-out-alt me-2"></i> Salir
            </a>
        </li>
    </ul>
</nav>

<!-- Script para manejar la apertura/cierre en móvil -->
<script>
    function toggleSidebar() {
        const sidebar = document.getElementById('sidebarMenu');
        const overlay = document.getElementById('sidebarOverlay');

        sidebar.classList.toggle('active');
        overlay.classList.toggle('active');
    }
</script>