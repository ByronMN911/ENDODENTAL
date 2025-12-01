<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar Sesión - EndoDental</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="assets/estilos/Style.css">

</head>
<body>

<div class="container-fluid">
    <div class="row">

        <div class="col-lg-7 d-none d-lg-block login-image-section">
            <div class="login-overlay"></div>
            <div class="position-absolute bottom-0 start-0 p-5 text-white">
                <h2 class="fw-bold">Bienvenido a EndoDental</h2>
                <p class="fs-5">Tu sonrisa en manos de profesionales.</p>
            </div>
        </div>

        <div class="col-lg-5 login-form-section">
            <div class="login-container">

                <img src="assets/img/sinfondo.png" alt="EndoDental Logo" class="login-logo">

                <h1>Iniciar Sesión</h1>
                <p class="subtitle">Ingresa tus credenciales para acceder</p>

                <form action="<%= request.getContextPath() %>/login" method="POST">

                    <div class="mb-3 text-start">
                        <label for="username" class="form-label ms-1 fw-500 text-muted" style="font-size: 0.85rem;">Usuario</label>
                        <div class="input-group">
                            <span class="input-group-text position-absolute ms-3" style="z-index: 10; top: 10px;">
                                <i class="fas fa-user"></i>
                            </span>
                            <input type="text" class="form-control ps-5" id="username" name="username" placeholder="Ej. admin" required>
                        </div>
                    </div>

                    <div class="mb-4 text-start">
                        <label for="password" class="form-label ms-1 fw-500 text-muted" style="font-size: 0.85rem;">Contraseña</label>
                        <div class="input-group">
                            <span class="input-group-text position-absolute ms-3" style="z-index: 10; top: 10px;">
                                <i class="fas fa-lock"></i>
                            </span>
                            <input type="password" class="form-control ps-5" id="password" name="password" placeholder="••••••••" required>
                        </div>
                    </div>

                    <div class="d-grid gap-2">
                        <input type="submit" class="btn btn-login" value="Entrar">
                    </div>

                    <a href="index.jsp" class="back-link">
                        <i class="fas fa-arrow-left me-1"></i> Volver al sitio web
                    </a>

                </form>
            </div>
        </div>

    </div>
</div>

</body>
</html>