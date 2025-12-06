<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>EndoDental</title>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/animejs/3.2.1/anime.min.js"></script>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="assets/estilos/Style.css">

    <link rel="icon" href="${pageContext.request.contextPath}/assets/img/dienteUno.png" type="image/png">
</head>
<body>

<!-- NAVBAR RESPONSIVE -->
<nav class="navbar navbar-expand-lg navbar-light bg-white sticky-top shadow-sm py-3"> <!-- Aumenté el padding vertical (py-3) -->
    <div class="container-fluid px-lg-5 px-3">
        <a class="navbar-brand d-flex align-items-center" href="#">
            <!-- AUMENTADO: Height 90 para que se vea más grande y profesional -->
            <img src="assets/img/sinfondo.png" alt="EndoDental" height="90" style="width: auto; object-fit: contain;">
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse justify-content-end" id="navbarNav">
            <ul class="navbar-nav align-items-center text-center">
                <li class="nav-item"><a class="nav-link" href="#">Inicio</a></li>
                <li class="nav-item"><a class="nav-link" href="#historia">Nosotros</a></li>
                <li class="nav-item"><a class="nav-link" href="#servicios">Servicios</a></li>
                <li class="nav-item"><a class="nav-link" href="#equipo">Equipo</a></li>
                <li class="nav-item"><a class="nav-link" href="#pacientes">Pacientes</a></li>
                <li class="nav-item"><a class="nav-link" href="#contacto">Contacto</a></li>
                <li class="nav-item ms-lg-2 mt-3 mt-lg-0">
                    <a class="nav-link shadow-sm mx-auto" href="<%= request.getContextPath() %>/login" title="Iniciar Sesión"
                       style="background-color: #EC647D; color: white !important; width: 45px; height: 45px; border-radius: 50%; display: flex; align-items: center; justify-content: center; transition: all 0.3s ease;">
                        <i class="fas fa-user fs-5"></i>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container-fluid px-0">
    <div class="hero-section">
        <div class="hero-overlay"></div>
        <h1 class="hero-text text-center px-3" id="bannerTitle">CENTRO DE ESPECIALIDADES ODONTOLÓGICAS</h1>
    </div>
</div>

<section id="historia" class="section-padding bg-white">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-6 mb-4">
                <img src="assets/img/local.png" alt="Historia EndoDental" class="img-fluid rounded-4 shadow w-100">
            </div>
            <div class="col-md-6">
                <h2 class="section-title">Nuestra Historia</h2>
                <span class="history-quote d-block mb-3">“Tu sonrisa en manos de profesionales”</span>
                <div class="history-text">
                    <p>EndoDental nació del sueño de la <strong>Dra. Giomara Silva</strong>, una odontóloga apasionada por el bienestar y la salud dental. Con una visión clara de elevar el estándar de atención en la ciudad, fundó la clínica con el propósito de ofrecer un servicio moderno, humano y respaldado siempre por la ciencia.</p>
                    <p>Lo que comenzó como un consultorio pequeño pronto se convirtió en un centro odontológico integral. Hoy, somos un referente local, reconocidos por nuestra calidad y por ofrecer un abanico completo de especialidades bajo un mismo techo.</p>
                </div>
            </div>
        </div>
    </div>
</section>

<section id="servicios" class="section-padding bg-soft-pink">
    <div class="container">
        <h2 class="section-title text-center mb-5">ESPECIALIDADES</h2>

        <div id="carouselServicios" class="carousel slide" data-bs-ride="carousel">
            <div class="carousel-indicators">
                <button type="button" data-bs-target="#carouselServicios" data-bs-slide-to="0" class="active"></button>
                <button type="button" data-bs-target="#carouselServicios" data-bs-slide-to="1"></button>
                <button type="button" data-bs-target="#carouselServicios" data-bs-slide-to="2"></button>
                <button type="button" data-bs-target="#carouselServicios" data-bs-slide-to="3"></button>
                <button type="button" data-bs-target="#carouselServicios" data-bs-slide-to="4"></button>
                <button type="button" data-bs-target="#carouselServicios" data-bs-slide-to="5"></button>
            </div>

            <div class="carousel-inner">
                <div class="carousel-item active" data-bs-interval="4000">
                    <div class="flip-card">
                        <div class="flip-card-inner">
                            <div class="flip-card-front">
                                <img src="assets/img/endo.jpg" alt="Endodoncia">
                                <div class="service-overlay-title">Endodoncia</div>
                            </div>
                            <div class="flip-card-back">
                                <h3><i class="fas fa-tooth"></i> Endodoncia</h3>
                                <p>Tratamiento de conductos especializado para salvar dientes dañados o infectados, eliminando el dolor y restaurando la funcionalidad sin necesidad de extracción.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="carousel-item" data-bs-interval="4000">
                    <div class="flip-card">
                        <div class="flip-card-inner">
                            <div class="flip-card-front">
                                <img src="assets/img/ortodoncia.jpg" alt="Ortodoncia">
                                <div class="service-overlay-title">Ortodoncia</div>
                            </div>
                            <div class="flip-card-back">
                                <h3><i class="fas fa-teeth"></i> Ortodoncia</h3>
                                <p>Corregimos la posición de dientes y mandíbula. Utilizamos brackets modernos o alineadores invisibles para diseñar tu sonrisa perfecta.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="carousel-item" data-bs-interval="4000">
                    <div class="flip-card">
                        <div class="flip-card-inner">
                            <div class="flip-card-front">
                                <img src="assets/img/peridoncia.jpg" alt="Profilaxis">
                                <div class="service-overlay-title">Profilaxis</div>
                            </div>
                            <div class="flip-card-back">
                                <h3><i class="fas fa-sparkles"></i> Profilaxis</h3>
                                <p>Limpieza profunda con ultrasonido para eliminar sarro y placa bacteriana, previniendo enfermedades y mal aliento.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="carousel-item" data-bs-interval="4000">
                    <div class="flip-card">
                        <div class="flip-card-inner">
                            <div class="flip-card-front">
                                <img src="assets/img/odontopediatria.jpg" alt="Odontopediatría">
                                <div class="service-overlay-title">Odontopediatría</div>
                            </div>
                            <div class="flip-card-back">
                                <h3><i class="fas fa-child"></i> Odontopediatría</h3>
                                <p>Atención especializada para los más pequeños. Formamos adultos con sonrisas sanas mediante un trato paciente y divertido.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="carousel-item" data-bs-interval="4000">
                    <div class="flip-card">
                        <div class="flip-card-inner">
                            <div class="flip-card-front">
                                <img src="assets/img/implantes-dentales.jpg" alt="Implantes Dentales">
                                <div class="service-overlay-title">Implantes Dentales</div>
                            </div>
                            <div class="flip-card-back">
                                <h3><i class="fas fa-screwdriver"></i> Implantes Dentales</h3>
                                <p>Reemplazo permanente de piezas dentales mediante tornillos de titanio biocompatibles que ofrecen estabilidad, estética y funcionalidad como un diente natural.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="carousel-item" data-bs-interval="4000">
                    <div class="flip-card">
                        <div class="flip-card-inner">
                            <div class="flip-card-front">
                                <img src="assets/img/estetica.jpg" alt="Estética Dental">
                                <div class="service-overlay-title">Estética Dental</div>
                            </div>
                            <div class="flip-card-back">
                                <h3><i class="fas fa-gem"></i> Estética Dental</h3>
                                <p>Diseño de sonrisas, carillas y blanqueamiento para lograr resultados naturales, armoniosos y radiantes.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <button class="carousel-control-prev" type="button" data-bs-target="#carouselServicios" data-bs-slide="prev">
                <span class="carousel-control-prev-icon bg-dark rounded-circle p-3" aria-hidden="true"></span>
                <span class="visually-hidden">Anterior</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carouselServicios" data-bs-slide="next">
                <span class="carousel-control-next-icon bg-dark rounded-circle p-3" aria-hidden="true"></span>
                <span class="visually-hidden">Siguiente</span>
            </button>
        </div>
    </div>
</section>

<section id="equipo" class="section-padding bg-soft-blue">
    <div class="container">
        <h2 class="section-title text-center mb-2">NUESTRO EQUIPO PROFESIONAL</h2>
        <p class="text-center mb-5 text-muted">Odontólogos altamente capacitados listos para atenderte.</p>

        <div class="row g-4">
            <div class="col-md-6 col-lg-4">
                <div class="team-card">
                    <img src="assets/img/1.jpg" onerror="this.src='https://via.placeholder.com/150'" alt="Dra. Giomara Silva" class="team-photo">
                    <h4 class="doctor-name">Dra. Giomara Silva</h4>
                    <p class="doctor-specialty">Endodoncia y Estética</p>
                    <p class="doctor-desc">Fundadora y corazón de la clínica. Más de una década de experiencia salvando piezas dentales con un enfoque conservador y moderno.</p>
                </div>
            </div>

            <div class="col-md-6 col-lg-4">
                <div class="team-card">
                    <img src="assets/img/2.jpeg" onerror="this.src='https://via.placeholder.com/150'" alt="Dr. Andrés Molina" class="team-photo">
                    <h4 class="doctor-name">Dr. Andrés Molina</h4>
                    <p class="doctor-specialty">Ortodoncia</p>
                    <p class="doctor-desc">Experto en transformar sonrisas con brackets y alineadores. Su paciencia lo convierte en el favorito de adolescentes y adultos.</p>
                </div>
            </div>

            <div class="col-md-6 col-lg-4">
                <div class="team-card">
                    <img src="assets/img/3.jpg" onerror="this.src='https://via.placeholder.com/150'" alt="Dra. Valeria Torres" class="team-photo">
                    <h4 class="doctor-name">Dra. Valeria Torres</h4>
                    <p class="doctor-specialty">Periodoncia</p>
                    <p class="doctor-desc">Meticulosa experta en encías y estructuras de soporte. Su enfoque preventivo garantiza salud a largo plazo.</p>
                </div>
            </div>

            <div class="col-md-6 col-lg-4">
                <div class="team-card">
                    <img src="assets/img/4.jpg" onerror="this.src='https://via.placeholder.com/150'" alt="Dr. Sebastián Rojas" class="team-photo">
                    <h4 class="doctor-name">Dr. Sebastián Rojas</h4>
                    <p class="doctor-specialty">Odontopediatría</p>
                    <p class="doctor-desc">Energía y paciencia infinita. Logra que los más pequeños pierdan el miedo al dentista con explicaciones simples y divertidas.</p>
                </div>
            </div>

            <div class="col-md-6 col-lg-4">
                <div class="team-card">
                    <img src="assets/img/5.jpg" onerror="this.src='https://via.placeholder.com/150'" alt="Dra. Camila Herrera" class="team-photo">
                    <h4 class="doctor-name">Dra. Camila Herrera</h4>
                    <p class="doctor-specialty">Estética Dental</p>
                    <p class="doctor-desc">La artista de las sonrisas. Perfeccionista en el uso de resinas y carillas para lograr resultados naturales.</p>
                </div>
            </div>

            <div class="col-md-6 col-lg-4">
                <div class="team-card">
                    <img src="assets/img/7.jpeg" onerror="this.src='https://via.placeholder.com/150'" alt="Dr. Mateo Vega" class="team-photo">
                    <h4 class="doctor-name">Dr. Mateo Vega</h4>
                    <p class="doctor-specialty">Prótesis y Rehabilitación</p>
                    <p class="doctor-desc">Especialista en devolver funcionalidad y belleza. Sus trabajos protésicos combinan tecnología y diseño personalizado.</p>
                </div>
            </div>
        </div>
    </div>
</section>

<section id="pacientes" class="section-padding bg-white">
    <div class="container">
        <h2 class="section-title text-center mb-5">LO QUE DICEN NUESTROS PACIENTES</h2>

        <div class="row g-4">
            <div class="col-md-4">
                <div class="testimonial-card">
                    <div class="quote-icon"><i class="fas fa-quote-left"></i></div>
                    <div class="stars">
                        <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i>
                    </div>
                    <p class="patient-text">
                        "Llegué con mucho dolor en una muela y miedo al tratamiento. La Dra. Giomara fue increíblemente suave, no sentí nada durante la endodoncia. ¡Totalmente recomendados!"
                    </p>
                    <div class="patient-info">
                        <h5>María Augusta L.</h5>
                        <span class="patient-treatment">Endodoncia</span>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="testimonial-card">
                    <div class="quote-icon"><i class="fas fa-quote-left"></i></div>
                    <div class="stars">
                        <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i>
                    </div>
                    <p class="patient-text">
                        "El Dr. Andrés hizo magia con la sonrisa de mi hija. Usó alineadores invisibles y el cambio fue rapidísimo. Lo mejor es el trato, siempre amables y puntuales."
                    </p>
                    <div class="patient-info">
                        <h5>Carlos Zambrano</h5>
                        <span class="patient-treatment">Ortodoncia</span>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="testimonial-card">
                    <div class="quote-icon"><i class="fas fa-quote-left"></i></div>
                    <div class="stars">
                        <i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i><i class="fas fa-star"></i>
                    </div>
                    <p class="patient-text">
                        "Me hice un diseño de sonrisa y blanqueamiento. Me encantó que el resultado se ve súper natural, nada exagerado. Me siento mucho más segura al sonreír."
                    </p>
                    <div class="patient-info">
                        <h5>Fernanda Torres</h5>
                        <span class="patient-treatment">Estética Dental</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<div class="container">
    <div class="philosophy-section">
        <h2 class="philosophy-title">Nuestra Filosofía</h2>
        <div class="row justify-content-center">
            <div class="col-md-8">
                <p style="font-size: 1.2rem; line-height: 1.6;">
                    "Cada sonrisa cuenta una historia, y en EndoDental trabajamos para que esa historia sea de bienestar y confianza. Aquí unimos experiencia, tecnología y un trato cercano para que cada paciente reciba lo mejor de nosotros."
                </p>
                <p class="mt-3 fw-bold">Porque al final del día, tu sonrisa está en manos de profesionales.</p>
            </div>
        </div>
    </div>
</div>

<footer id="contacto" class="bg-dark text-white py-5">
    <div class="container">
        <div class="row text-start">
            <div class="col-md-4 mb-4">
                <h6 class="footer-title text-uppercase fw-bold mb-3">Legal</h6>
                <div class="footer-links d-flex flex-column gap-2">
                    <a href="#" class="text-white-50 text-decoration-none">Privacidad</a>
                    <a href="#" class="text-white-50 text-decoration-none">Términos y Condiciones</a>
                    <a href="#" class="text-white-50 text-decoration-none">Bases Legales</a>
                </div>
                <div class="social-icons mt-3">
                    <a href="https://www.instagram.com/endodental_ec" class="text-white me-3"><i class="fab fa-instagram fa-lg"></i></a>
                    <a href="https://www.facebook.com/profile.php?id=100065437151949" class="text-white me-3"><i class="fab fa-facebook fa-lg"></i></a>
                    <a href="https://ec.linkedin.com/in/giomara-silva-b409849a" class="text-white"><i class="fab fa-linkedin fa-lg"></i></a>
                </div>
            </div>

            <div class="col-md-4 mb-4">
                <h6 class="footer-title text-uppercase fw-bold mb-3">Productos</h6>
                <div class="footer-links d-flex flex-column gap-2">
                    <a href="#" class="text-white-50 text-decoration-none">Planes</a>
                    <a href="#" class="text-white-50 text-decoration-none">Redes Clínicas</a>
                    <a href="#" class="text-white-50 text-decoration-none">Socios Estratégicos</a>
                </div>
            </div>

            <div class="col-md-4 mb-4">
                <h6 class="footer-title text-uppercase fw-bold mb-3">Contactos</h6>
                <div class="footer-links d-flex flex-column gap-2">
                    <a href="https://api.whatsapp.com/send/?phone=593998574564" target="_blank" class="text-white-50 text-decoration-none">
                        <i class="bi bi-whatsapp me-2"></i> +(593) 99 857 4564
                    </a>
                    <a href="mailto:info@endodental.org" class="text-white-50 text-decoration-none">
                        <i class="bi bi-envelope me-2"></i> endodental@outlook.com
                    </a>
                    <a href="https://maps.app.goo.gl/WQaz8zREMxrhtDBR9" target="_blank" class="text-white-50 text-decoration-none">
                        <i class="bi bi-geo-alt me-2"></i> Bartolomé Alves S6-484 y Juan Bautista Aguirre
                    </a>
                </div>
            </div>
        </div>
    </div>
</footer>

<script>
    // Animación para el título del banner
    anime({
        targets: '#bannerTitle',
        translateY: [50, 0],
        opacity: [0, 1],
        duration: 1500,
        easing: 'easeOutExpo',
        delay: 500
    });
</script>

</body>
</html>