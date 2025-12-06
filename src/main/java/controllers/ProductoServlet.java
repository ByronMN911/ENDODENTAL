package controllers;

/*
 * Autor: Mathew Lara
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Controlador (Servlet) encargado de la gestión del Inventario de Productos.
 * Este servlet actúa como el punto de entrada para todas las peticiones HTTP relacionadas
 * con la administración de insumos y productos de venta.
 *
 * Responsabilidades:
 * 1. Listar el inventario activo y la papelera de reciclaje.
 * 2. Gestionar la creación y edición de productos mediante formularios modales.
 * 3. Procesar la eliminación lógica (soft delete) y reactivación de productos.
 * 4. Coordinar la comunicación entre la vista (JSP) y la capa de servicio.
 */

import models.Producto;
import services.ProductoService;
import services.ProductoServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Servlet mapeado a la URL "/inventario".
 * Maneja tanto la navegación (GET) como el procesamiento de formularios (POST) para productos.
 */
@WebServlet("/inventario")
public class ProductoServlet extends HttpServlet {

    /**
     * Procesa las solicitudes HTTP GET.
     * Se utiliza para mostrar listas, cambiar estados (eliminar/activar) y preparar datos para edición.
     *
     * @param req  La solicitud HTTP.
     * @param resp La respuesta HTTP.
     * @throws ServletException Error en el ciclo de vida del Servlet.
     * @throws IOException Error de entrada/salida.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*
         * 1. INYECCIÓN DE DEPENDENCIAS
         * Recuperamos la conexión a la base de datos gestionada por el filtro (ConexionFilter).
         * Esto asegura que el Servlet participe en la transacción global del request.
         */
        Connection conn = (Connection) req.getAttribute("conn");

        // Instanciamos el servicio pasándole la conexión activa.
        ProductoService service = new ProductoServiceImpl(conn);

        /*
         * 2. ENRUTAMIENTO (ROUTING)
         * Determinamos qué acción realizar basándonos en el parámetro 'accion' de la URL.
         * Si no se especifica ninguna, la acción por defecto es 'listar'.
         */
        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        // Switch para delegar la lógica a bloques específicos según la acción solicitada
        switch (accion) {
            case "listar":
                // Muestra la tabla principal de productos activos (Stock disponible).
                listarProductos(service, req, resp);
                break;

            case "inactivos":
                // Muestra la "Papelera de Reciclaje" (productos archivados/inactivos).
                listarProductosInactivos(service, req, resp);
                break;

            case "editar":
                /*
                 * LÓGICA DE PRECARGA PARA EDICIÓN
                 * 1. Recuperamos el ID del producto a editar.
                 * 2. Buscamos el objeto en la base de datos.
                 * 3. Si existe, lo enviamos al JSP como atributo 'productoEditar'.
                 * 4. Activamos la bandera 'mostrarModal' para que el formulario se abra automáticamente en el cliente.
                 */
                try {
                    int idEditar = Integer.parseInt(req.getParameter("id"));
                    Optional<Producto> pOpt = service.porId(idEditar);

                    if (pOpt.isPresent()) {
                        req.setAttribute("productoEditar", pOpt.get());
                        req.setAttribute("mostrarModal", true); // Bandera para el JSP
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Log de error si el ID no es válido
                }
                // Finalmente, recargamos la lista de fondo para mantener el contexto visual.
                listarProductos(service, req, resp);
                break;

            case "eliminar":
                // Procesa la baja lógica (Soft Delete). Cambia estado a 0.
                try {
                    int idEliminar = Integer.parseInt(req.getParameter("id"));
                    service.eliminar(idEliminar);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Redirección para evitar reenvío y limpiar la URL
                resp.sendRedirect(req.getContextPath() + "/inventario");
                break;

            case "activar":
                // Procesa la restauración de un producto archivado. Cambia estado a 1.
                try {
                    int idActivar = Integer.parseInt(req.getParameter("id"));
                    service.activar(idActivar);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Redirigimos a la vista de inactivos para ver que el registro desapareció de la papelera
                resp.sendRedirect(req.getContextPath() + "/inventario?accion=inactivos");
                break;

            default:
                // Fallback de seguridad
                listarProductos(service, req, resp);
        }
    }

    /**
     * Procesa las solicitudes HTTP POST.
     * Se utiliza exclusivamente para guardar datos del formulario (Crear Nuevo o Actualizar Existente).
     *
     * @param req  La solicitud HTTP con los datos del formulario.
     * @param resp La respuesta HTTP.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. Preparación del Entorno
        Connection conn = (Connection) req.getAttribute("conn");
        ProductoService service = new ProductoServiceImpl(conn);

        try {
            // 2. Recolección de Datos (Binding manual)
            // Recuperamos los valores de los inputs del formulario HTML.
            String nombre = req.getParameter("nombre");
            String marca = req.getParameter("marca");
            String descripcion = req.getParameter("descripcion");

            // Conversión de tipos numéricos (BigDecimal para moneda, int para cantidades)
            BigDecimal precio = new BigDecimal(req.getParameter("precio_venta"));
            int stock = Integer.parseInt(req.getParameter("stock"));
            int stockMinimo = Integer.parseInt(req.getParameter("stock_minimo"));

            // Recuperación del ID (campo oculto). Si es vacío, es 0 (Nuevo).
            String idStr = req.getParameter("id_producto");
            int id = (idStr != null && !idStr.isEmpty()) ? Integer.parseInt(idStr) : 0;

            // 3. Construcción del objeto de dominio (DTO)
            Producto p = new Producto();
            p.setIdProducto(id);
            p.setNombre(nombre);
            p.setMarca(marca);
            p.setDescripcion(descripcion);
            p.setPrecioVenta(precio);
            p.setStock(stock);
            p.setStockMinimo(stockMinimo);
            // Nota: El estado se maneja en el DAO (1 al crear, se mantiene al editar).

            /*
             * 4. EJECUCIÓN DE LA LÓGICA DE NEGOCIO
             * Delegamos al servicio la tarea de guardar.
             */
            service.guardar(p);

            // Patrón PRG (Post-Redirect-Get): Redirigimos al listado principal.
            resp.sendRedirect(req.getContextPath() + "/inventario");

        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, redirigimos con un parámetro de error para mostrar alerta
            resp.sendRedirect(req.getContextPath() + "/inventario?error=true");
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES (HELPERS) PARA DESPACHO DE VISTAS
    // =========================================================================

    /**
     * Carga la lista de productos activos y despacha a la vista principal.
     * Configura el título y la bandera 'esPapelera' en falso.
     */
    private void listarProductos(ProductoService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Producto> lista = service.listar();
        req.setAttribute("productos", lista);
        req.setAttribute("titulo", "Inventario de Productos");
        req.setAttribute("esPapelera", false);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/inventario.jsp").forward(req, resp);
    }

    /**
     * Carga la lista de productos inactivos y despacha a la vista.
     * Configura el título y la bandera 'esPapelera' en verdadero.
     */
    private void listarProductosInactivos(ProductoService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Producto> lista = service.listarInactivos();
        req.setAttribute("productos", lista);
        req.setAttribute("titulo", "Papelera de Reciclaje");
        req.setAttribute("esPapelera", true);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/inventario.jsp").forward(req, resp);
    }
}