<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--
=============================================================================
VISTA: DASHBOARD DE SECRETARIA (dashboard.jsp)
Autor: Byron Melo
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Panel de control principal para el rol de Secretaría.
Esta vista ofrece un resumen ejecutivo de las operaciones diarias de la clínica.

Funcionalidades:
1. Visualización de KPIs (Key Performance Indicators) en tiempo real:
- Número de citas agendadas para el día actual.
- Total de pacientes activos en el sistema.
- Monto total facturado en el día.
2. Punto de navegación central hacia los módulos operativos (Pacientes, Citas, Facturación).

Arquitectura:
Recibe datos procesados desde el DashboardServlet y utiliza 'includes' para
reutilizar componentes comunes como la barra lateral.
=============================================================================
-->

<%
    /* * -------------------------------------------------------------------------
     * BLOQUE DE LÓGICA DE PRESENTACIÓN (SERVER-SIDE)
     * -------------------------------------------------------------------------
     * Recuperación de métricas enviadas por el Servlet.
     * Se implementa un patrón de "Manejo Defensivo" para evitar excepciones (Error 500)
     * en caso de que el Servlet falle al enviar algún atributo o envíe nulos.
     */

    // 1. Recuperación de Citas del Día
    // Se recibe como Object, se verifica nulidad y se castea a Integer.
    Object citasObj = request.getAttribute("citasHoy");
    // Operador ternario: Si es null, asignamos 0 para mantener la interfaz funcional.
    int citasHoy = (citasObj != null) ? (Integer) citasObj : 0;

    // 2. Recuperación del Total de Pacientes Activos
    Object pacientesObj = request.getAttribute("pacientesTotal");
    int pacientesTotal = (pacientesObj != null) ? (Integer) pacientesObj : 0;

    // 3. Recuperación de Ingresos Financieros del Día
    // Se espera un valor Double que representa la suma de facturas emitidas hoy.
    Object ingresosObj = request.getAttribute("ingresosHoy");
    double ingresosHoy = (ingresosObj != null) ? (Double) ingresosObj : 0.0;
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <title>EndoDental - Dashboard</title>
    <!--
        SECCIÓN DE DEPENDENCIAS CSS
        Bootstrap para el sistema de rejillas (Grid) y tarjetas (Cards).
        FontAwesome para los íconos ilustrativos.
    -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <!-- Estilos personalizados del proyecto -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<!-- Contenedor principal del layout (Flexbox) -->
<div class="dashboard-wrapper">

    <!--
        INCLUSIÓN DEL SIDEBAR (COMPONENTE REUTILIZABLE)
        Importamos la barra lateral común a todas las vistas de gestión.
        El parámetro 'activePage' con valor 'inicio' le indica al sidebar que debe
        resaltar la opción de "Inicio" visualmente (clase CSS .active).
    -->
    <jsp:include page="/WEB-INF/vistas/templates/sidebar.jsp">
        <jsp:param name="activePage" value="inicio"/>
    </jsp:include>

    <!-- SECCIÓN DE CONTENIDO PRINCIPAL -->
    <main class="main-content">

        <!-- Encabezado del Dashboard -->
        <div class="d-flex justify-content-between align-items-center mb-5">
            <div>
                <h2 class="fw-bold text-dark">Panel de Control</h2>
                <p class="text-muted">Resumen de actividades del día.</p>
            </div>
            <!--
                Avatar de Usuario
                Muestra la foto de perfil. Actualmente es una imagen estática, pero
                está preparada para ser dinámica si se implementa subida de fotos en el futuro.
            -->
            <div class="d-flex align-items-center">
                <img src="${pageContext.request.contextPath}/assets/img/usuario.png" alt="User" class="team-photo shadow-sm"
                     style="width: 100px; height: 100px; border-radius: 50%; object-fit: cover; border: 4px solid white;">
            </div>
        </div>

        <!--
            GRILLA DE TARJETAS ESTADÍSTICAS (KPIs)
            Utiliza el sistema de columnas de Bootstrap (col-md-4) para mostrar 3 tarjetas en fila.
        -->
        <div class="row g-4 mb-5">

            <!-- TARJETA 1: CITAS DE HOY -->
            <div class="col-md-4">
                <div class="stat-card p-4 rounded-4 shadow-sm bg-white border-0 h-100 d-flex justify-content-between align-items-center">
                    <div class="stat-info">
                        <!-- Inyección de la variable Java 'citasHoy' en el HTML -->
                        <h3 class="fw-bold text-primary mb-1"><%= citasHoy %></h3>
                        <p class="text-muted mb-0">Citas para Hoy</p>
                    </div>
                    <div class="stat-icon bg-light text-primary p-3 rounded-circle fs-4">
                        <i class="fas fa-calendar-check"></i>
                    </div>
                </div>
            </div>

            <!-- TARJETA 2: PACIENTES ACTIVOS -->
            <div class="col-md-4">
                <div class="stat-card p-4 rounded-4 shadow-sm bg-white border-0 h-100 d-flex justify-content-between align-items-center">
                    <div class="stat-info">
                        <!-- Inyección de la variable Java 'pacientesTotal' -->
                        <h3 class="fw-bold text-info mb-1"><%= pacientesTotal %></h3>
                        <p class="text-muted mb-0">Pacientes Activos</p>
                    </div>
                    <div class="stat-icon bg-light text-info p-3 rounded-circle fs-4">
                        <i class="fas fa-users"></i>
                    </div>
                </div>
            </div>

            <!-- TARJETA 3: FACTURACIÓN DIARIA -->
            <div class="col-md-4">
                <div class="stat-card p-4 rounded-4 shadow-sm bg-white border-0 h-100 d-flex justify-content-between align-items-center">
                    <div class="stat-info">
                        <!--
                            Formateo de Moneda:
                            Utilizamos String.format("%.2f") para asegurar que el valor 'ingresosHoy'
                            siempre muestre exactamente dos decimales (ej: 150.00 en lugar de 150.0).
                        -->
                        <h3 class="fw-bold text-success mb-1">$<%= String.format("%.2f", ingresosHoy) %></h3>
                        <p class="text-muted mb-0">Facturado Hoy</p>
                    </div>
                    <div class="stat-icon bg-light text-success p-3 rounded-circle fs-4">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Scripts de Bootstrap necesarios para la interactividad -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>