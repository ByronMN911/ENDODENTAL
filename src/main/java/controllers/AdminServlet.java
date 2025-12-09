package controllers;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.2
 * Descripción:
 * Controlador (Servlet) encargado del Módulo de Administración.
 * Gestiona la creación de usuarios asegurando que los roles se asignen correctamente
 * y que los datos profesionales (si es médico) se guarden.
 */

import models.*;
import services.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.regex.Pattern;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    /**
     * Maneja las peticiones GET para mostrar el dashboard y listas.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        UsuarioService userService = new UsuarioServiceImpl(conn);
        OdontologoService odoService = new OdontologoServiceImpl(conn);

        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        try {
            switch (accion) {
                case "listar":
                    req.setAttribute("usuarios", userService.listar());
                    req.setAttribute("titulo", "Personal Activo");
                    req.setAttribute("esPapelera", false);
                    break;
                case "inactivos":
                    req.setAttribute("usuarios", userService.listarInactivos());
                    req.setAttribute("titulo", "Personal Inactivo");
                    req.setAttribute("esPapelera", true);
                    break;
                case "eliminar":
                    int idElim = Integer.parseInt(req.getParameter("id"));
                    userService.eliminar(idElim);
                    resp.sendRedirect(req.getContextPath() + "/admin");
                    return;
                case "activar":
                    int idAct = Integer.parseInt(req.getParameter("id"));
                    userService.activar(idAct);
                    resp.sendRedirect(req.getContextPath() + "/admin?accion=inactivos");
                    return;
                case "editar":
                    int idEdit = Integer.parseInt(req.getParameter("id"));
                    Usuario u = userService.porId(idEdit).orElse(null);
                    if(u != null && "Odontologo".equals(u.getRol().getNombreRol())){
                        req.setAttribute("odontologoExtra", odoService.porIdUsuario(u.getIdUsuario()).orElse(null));
                    }
                    req.setAttribute("usuarioEditar", u);
                    req.setAttribute("mostrarModal", true);
                    req.setAttribute("usuarios", userService.listar());
                    req.setAttribute("esPapelera", false);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
        }
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/admin/dashboard.jsp").forward(req, resp);
    }

    /**
     * Maneja las peticiones POST para GUARDAR usuarios.
     * CORRECCIÓN CRÍTICA: Ajuste de IDs de Roles.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        UsuarioService userService = new UsuarioServiceImpl(conn);

        String accion = req.getParameter("accion");

        if ("guardar".equals(accion)) {
            try {
                // 1. Recolección de Datos
                int idUsuario = Integer.parseInt(req.getParameter("idUsuario"));
                String nombre = req.getParameter("nombre");
                String username = req.getParameter("username");
                String email = req.getParameter("email");
                String pass = req.getParameter("password");
                int idRol = Integer.parseInt(req.getParameter("idRol"));

                // Validación de Nombre
                if (nombre == null || !Pattern.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", nombre)) {
                    throw new ServiceJdbcException("El nombre completo solo puede contener letras y espacios.");
                }

                // 2. Construcción del Rol
                Rol rol = new Rol();
                rol.setIdRol(idRol);

                // --- CORRECCIÓN DE MAPEO DE ROLES ---
                // ID 2 = Odontólogo (Según tu Base de Datos)
                // ID 3 = Secretaria
                if(idRol == 1) rol.setNombreRol("Administrador");
                else if(idRol == 2) rol.setNombreRol("Odontologo"); // <--- CORREGIDO AQUÍ
                else if(idRol == 3) rol.setNombreRol("Secretaria"); // <--- CORREGIDO AQUÍ

                Usuario u = new Usuario();
                u.setIdUsuario(idUsuario);
                u.setNombreCompleto(nombre);
                u.setUsername(username);
                u.setEmail(email);
                u.setPassword(pass);
                u.setRol(rol);

                // 3. Recolección de Datos de Odontólogo
                Odontologo odo = null;
                // Si el ID es 2, entonces ES un Odontólogo y debemos guardar sus datos extra
                if (idRol == 2) {
                    odo = new Odontologo();
                    odo.setEspecialidad(req.getParameter("especialidad"));
                    odo.setCodigoMedico(req.getParameter("codigo"));
                }

                // 4. Guardar
                userService.guardarUsuario(u, odo);

                resp.sendRedirect(req.getContextPath() + "/admin?exito=true");

            } catch (Exception e) {
                e.printStackTrace();
                resp.sendRedirect(req.getContextPath() + "/admin?error=" + e.getMessage());
            }
        }
    }
}