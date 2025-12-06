<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - EndoDental</title>

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <!-- Estilos Globales (Ruta corregida) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/estilos/Style.css">
    <!-- Favicon -->
    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">

</head>
<body>

<div class="container-fluid p-0">
    <div class="row g-0">

        <!-- COLUMNA IZQUIERDA (IMAGEN)
             d-none: Oculto en móviles
             d-lg-block: Visible en pantallas grandes (Laptop/PC) -->
        <div class="col-lg-7 d-none d-lg-block login-image-section">
            <div class="login-overlay"></div>
            <div class="position-absolute bottom-0 start-0 p-5 text-white" style="z-index: 2;">
                <h1 class="fw-bold display-5">Bienvenido a EndoDental</h1>
                <p class="fs-4">Tu sonrisa en manos de profesionales.</p>
            </div>
        </div>

        <!-- COLUMNA DERECHA (FORMULARIO) -->
        <div class="col-lg-5 login-form-section">
            <div class="login-container">

                <div class="text-center mb-4">
                    <img src="${pageContext.request.contextPath}/assets/img/sinfondo.png" alt="EndoDental Logo" class="img-fluid mb-3" style="max-width: 130px;">
                    <h2 class="fw-bold text-dark">Iniciar Sesión</h2>
                    <p class="text-muted">Ingresa tus credenciales para acceder al sistema</p>
                </div>

                <!-- ALERTA DE ERROR -->
                <% if (error != null) { %>
                <div class="alert alert-danger alert-dismissible fade show d-flex align-items-center shadow-sm" role="alert">
                    <i class="fas fa-exclamation-circle me-2 fs-5"></i>
                    <div><%= error %></div>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/login" method="POST">

                    <div class="mb-3">
                        <label for="username" class="form-label fw-bold text-secondary small ps-1">Usuario</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0 text-primary">
                                <i class="fas fa-user"></i>
                            </span>
                            <input type="text" class="form-control bg-light border-start-0 py-2" id="username" name="username" placeholder="Ej. admin" required autofocus>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="password" class="form-label fw-bold text-secondary small ps-1">Contraseña</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light border-end-0 text-primary">
                                <i class="fas fa-lock"></i>
                            </span>
                            <input type="password" class="form-control bg-light border-start-0 py-2" id="password" name="password" placeholder="••••••••" required>
                        </div>
                    </div>

                    <div class="d-grid mb-4">
                        <button type="submit" class="btn btn-login btn-primary py-2 rounded-pill fw-bold shadow-sm">
                            Entrar <i class="fas fa-arrow-right ms-2"></i>
                        </button>
                    </div>

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

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>