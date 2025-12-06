<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--
Autor: Génesis Escobar
Fecha: 05/12/2025
Versión: 3.0
Descripción:
Vista de autenticación (Login) de la aplicación.
Esta página proporciona el formulario de acceso seguro para los usuarios del sistema
(Administradores, Secretarias y Odontólogos). Implementa un diseño responsivo de
pantalla dividida (Split Screen) y maneja la visualización de mensajes de error
enviados desde el backend.
-->

<%
    /*
     * LÓGICA DE PRESENTACIÓN (Scriptlet)
     * ----------------------------------
     * Recuperamos el atributo "error" del objeto request.
     * Este atributo es seteado por el LoginServlet cuando las credenciales son inválidas
     * o el usuario está inactivo. Si existe, se mostrará una alerta visual más abajo.
     */
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <!-- Viewport esencial para el diseño responsivo en móviles -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - EndoDental</title>

    <!--
        DEPENDENCIAS DE ESTILO
        ----------------------
        1. Bootstrap 5: Sistema de rejillas y componentes UI.
        2. FontAwesome: Íconos para campos de usuario/password.
        3. Google Fonts: Tipografía Poppins para consistencia de marca.
        4. Style.css: Estilos personalizados globales.
    -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <!-- Uso de contextPath para asegurar que la ruta del CSS sea absoluta desde la raíz de la app -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">

    <!--
        ESTILOS INCRUSTADOS CRÍTICOS
        Se incluyen aquí para garantizar la renderización correcta del layout de login
        incluso si falla la carga del CSS externo o para sobreescribir comportamientos específicos.
    -->
    <style>
        /* Estilos específicos para Login (Garantiza diseño responsive) */
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #fff;
        }

        .login-image-section {
            /* Imagen de fondo corporativa */
            background-image: url('${pageContext.request.contextPath}/assets/img/equipoodonto.jpg');
            background-size: cover;
            background-position: center;
            min-height: 100vh;
            position: relative;
        }

        .login-overlay {
            /* Capa de superposición con degradado para mejorar legibilidad del texto sobre la imagen */
            position: absolute;
            top: 0; left: 0; right: 0; bottom: 0;
            background: linear-gradient(135deg, rgba(189, 225, 238, 0.85), rgba(236, 100, 125, 0.85));
        }

        .login-form-section {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #ffffff;
        }

        .login-container {
            width: 100%;
            max-width: 420px;
            padding: 2rem;
        }

        /* Ajuste para dispositivos móviles: Reduce el padding vertical */
        @media (max-width: 992px) {
            .login-form-section {
                min-height: 100vh;
                padding: 1rem;
            }
        }

        /* Feedback visual al enfocar inputs */
        .form-control:focus {
            border-color: #EC647D;
            box-shadow: 0 0 0 0.2rem rgba(236, 100, 125, 0.25);
        }

        .btn-login {
            background-color: #EC647D;
            border: none;
            transition: all 0.3s ease;
            color: white;
        }

        .btn-login:hover {
            background-color: #d64d66;
            transform: translateY(-2px);
            color: white;
        }
    </style>
</head>
<body>

<!-- Contenedor fluido sin padding para ocupar toda la pantalla -->
<div class="container-fluid p-0">
    <div class="row g-0">

        <!--
            COLUMNA IZQUIERDA (IMAGEN CORPORATIVA)
            --------------------------------------
            Clases Bootstrap:
            - d-none: Oculto por defecto (en móviles).
            - d-lg-block: Visible solo en pantallas grandes (Laptop/Desktop).
            - col-lg-7: Ocupa el 58% del ancho en escritorio.
        -->
        <div class="col-lg-7 d-none d-lg-block login-image-section">
            <div class="login-overlay"></div>
            <div class="position-absolute bottom-0 start-0 p-5 text-white" style="z-index: 2;">
                <h1 class="fw-bold display-5">Bienvenido a EndoDental</h1>
                <p class="fs-4">Tu sonrisa en manos de profesionales.</p>
            </div>
        </div>

        <!--
            COLUMNA DERECHA (FORMULARIO)
            ----------------------------
            - col-lg-5: Ocupa el 42% restante en escritorio.
            - En móviles ocupa el 100% automáticamente.
        -->
        <div class="col-lg-5 login-form-section">
            <div class="login-container">

                <!-- Cabecera del Formulario -->
                <div class="text-center mb-4">
                    <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="EndoDental Logo" class="img-fluid mb-3" style="max-width: 130px;">
                    <h2 class="fw-bold text-dark">Iniciar Sesión</h2>
                    <p class="text-muted">Ingresa tus credenciales para acceder al sistema</p>
                </div>

                <!--
                    BLOQUE DE ALERTA DE ERROR
                    -------------------------
                    Se renderiza dinámicamente solo si la variable 'error' (capturada del request) no es nula.
                    Utiliza el componente 'alert' de Bootstrap para mostrar feedback visual al usuario.
                -->
                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                    <i class="fas fa-exclamation-circle me-2 fs-5"></i>
                    <div><%= error %></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% } %>

                <!--
                    FORMULARIO DE LOGIN
                    -------------------
                    - action: Apunta al servlet LoginServlet mapeado en '/login'.
                    - method: POST para enviar credenciales de forma segura en el cuerpo de la petición.
                -->
                <form action="<%= request.getContextPath() %>/login" method="POST">

                    <!-- Campo Usuario -->
                    <div class="mb-3">
                        <label for="username" class="form-label fw-bold text-secondary small ps-1">Usuario</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0 text-primary">
                                <i class="fas fa-user"></i>
                            </span>
                            <input type="text" class="form-control bg-light border-start-0 py-2" id="username" name="username" placeholder="Ej. admin" required autofocus>
                        </div>
                    </div>

                    <!-- Campo Contraseña -->
                    <div class="mb-4">
                        <label for="password" class="form-label fw-bold text-secondary small ps-1">Contraseña</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0 text-primary">
                                <i class="fas fa-lock"></i>
                            </span>
                            <input type="password" class="form-control bg-light border-start-0 py-2" id="password" name="password" placeholder="••••••••" required>
                        </div>
                    </div>

                    <!-- Botón de Envío -->
                    <div class="d-grid mb-4">
                        <button type="submit" class="btn btn-login py-2 rounded-pill fw-bold shadow-sm">
                            Entrar <i class="fas fa-arrow-right ms-2"></i>
                        </button>
                    </div>

                    <!-- Enlace de retorno al Home -->
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/index.jsp" class="text-decoration-none text-muted small">
                            <i class="fas fa-arrow-left me-1"></i> Volver al sitio web
                        </a>
                    </div>

                </form>
            </div>
        </div>

    </div>
</div>

<!-- Scripts de Bootstrap requeridos para interactividad (Alertas, etc.) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>