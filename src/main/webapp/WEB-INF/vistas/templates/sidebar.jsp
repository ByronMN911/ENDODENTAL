<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="models.Usuario" %>
<%
    // Recuperamos el usuario de la sesión para mostrar su nombre
    Usuario usuarioLogueadoSidebar = (Usuario) session.getAttribute("usuario");
    String nombreUser = (usuarioLogueadoSidebar != null) ? usuarioLogueadoSidebar.getNombreCompleto() : "Usuario";

    // Recuperamos el parámetro para saber qué link activar
    String activePage = request.getParameter("activePage");
    if (activePage == null) activePage = "";
%>

<nav class="sidebar">
    <!-- Logo -->
    <div class="text-center py-4">
        <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="EndoDental" class="sidebar-logo" style="max-width: 80%; height: auto;">
    </div>

    <!-- Info Usuario -->
    <div class="text-center text-white mb-4 px-2">
        <div class="small opacity-75">Bienvenido/a,</div>
        <div class="fw-bold text-truncate"><%= nombreUser %></div>
    </div>

    <!-- Menú -->
    <ul class="sidebar-menu list-unstyled">
        <li>
            <a href="${pageContext.request.contextPath}/dashboard" class="sidebar-link <%= "inicio".equals(activePage) ? "active" : "" %>">
                <i class="fas fa-home me-2"></i> Inicio
            </a>
        </li>

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

        <li class="mt-auto">
            <a href="${pageContext.request.contextPath}/login?action=logout" class="sidebar-link text-danger">
                <i class="fas fa-sign-out-alt me-2"></i> Salir
            </a>
        </li>
    </ul>
</nav>