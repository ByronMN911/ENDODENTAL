package controllers;

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

@WebServlet("/inventario")
public class ProductoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        ProductoService service = new ProductoServiceImpl(conn);

        String accion = req.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar":
                listarProductos(service, req, resp);
                break;

            case "inactivos":
                listarProductosInactivos(service, req, resp);
                break;

            case "editar":
                try {
                    int idEditar = Integer.parseInt(req.getParameter("id"));
                    Optional<Producto> pOpt = service.porId(idEditar);
                    if (pOpt.isPresent()) {
                        req.setAttribute("productoEditar", pOpt.get());
                        req.setAttribute("mostrarModal", true); // Bandera para abrir modal
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                listarProductos(service, req, resp);
                break;

            case "eliminar":
                try {
                    int idEliminar = Integer.parseInt(req.getParameter("id"));
                    service.eliminar(idEliminar);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resp.sendRedirect(req.getContextPath() + "/inventario");
                break;

            case "activar":
                try {
                    int idActivar = Integer.parseInt(req.getParameter("id"));
                    service.activar(idActivar);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                resp.sendRedirect(req.getContextPath() + "/inventario?accion=inactivos");
                break;

            default:
                listarProductos(service, req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection conn = (Connection) req.getAttribute("conn");
        ProductoService service = new ProductoServiceImpl(conn);

        try {
            String nombre = req.getParameter("nombre");
            String marca = req.getParameter("marca");
            String descripcion = req.getParameter("descripcion");
            BigDecimal precio = new BigDecimal(req.getParameter("precio_venta"));
            int stock = Integer.parseInt(req.getParameter("stock"));
            int stockMinimo = Integer.parseInt(req.getParameter("stock_minimo"));

            String idStr = req.getParameter("id_producto");
            int id = (idStr != null && !idStr.isEmpty()) ? Integer.parseInt(idStr) : 0;

            Producto p = new Producto();
            p.setIdProducto(id);
            p.setNombre(nombre);
            p.setMarca(marca);
            p.setDescripcion(descripcion);
            p.setPrecioVenta(precio);
            p.setStock(stock);
            p.setStockMinimo(stockMinimo);
            // El estado se maneja en el DAO (1 al crear, se mantiene al editar)

            service.guardar(p);

            resp.sendRedirect(req.getContextPath() + "/inventario");

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/inventario?error=true");
        }
    }

    private void listarProductos(ProductoService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Producto> lista = service.listar();
        req.setAttribute("productos", lista);
        req.setAttribute("titulo", "Inventario de Productos");
        req.setAttribute("esPapelera", false);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/inventario.jsp").forward(req, resp);
    }

    private void listarProductosInactivos(ProductoService service, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Producto> lista = service.listarInactivos();
        req.setAttribute("productos", lista);
        req.setAttribute("titulo", "Papelera de Reciclaje");
        req.setAttribute("esPapelera", true);
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/inventario.jsp").forward(req, resp);
    }
}