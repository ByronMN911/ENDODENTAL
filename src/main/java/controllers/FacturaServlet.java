package controllers;

/*
 * Autor: Génesis Escobar
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Controlador (Servlet) encargado del Módulo de Facturación.
 * Gestiona el proceso de emisión de facturas electrónicas/físicas.
 *
 * Responsabilidades:
 * 1. Cargar la vista de facturación con todos los catálogos necesarios (Citas, Servicios, Productos).
 * 2. Manejar la lógica de "Pre-llenado" cuando se viene desde la Agenda (UX).
 * 3. Procesar el formulario de venta, incluyendo cabecera y múltiples detalles (ítems).
 * 4. Orquestar el guardado y redireccionar a la impresión del PDF.
 */

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.*;
import services.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebServlet("/facturacion")
public class FacturaServlet extends HttpServlet {

    /**
     * Maneja la petición GET para mostrar la pantalla de facturación.
     * Se encarga de recuperar y enviar a la vista toda la información necesaria para
     * que la secretaria pueda realizar el cobro (Listas de precios, Citas pendientes, etc.).
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Obtener la conexión compartida del filtro (Transacción por Request)
        Connection conn = (Connection) req.getAttribute("conn");

        // 2. Instanciar los servicios necesarios para consultar datos
        FacturaService facturaService = new FacturaServiceImpl(conn);
        CitaService citaService = new CitaServiceImpl(conn);
        ServicioService servicioService = new ServicioServiceImpl(conn);
        ProductoService productoService = new ProductoServiceImpl(conn);

        /*
         * LÓGICA DE PRE-LLENADO (UX - Experiencia de Usuario)
         * Si la secretaria hace clic en "Facturar" desde la Agenda, recibimos el ID de esa cita.
         * Buscamos los datos de esa cita específica para que el formulario aparezca ya lleno
         * con el paciente seleccionado, ahorrando tiempo y errores.
         */
        String idPre = req.getParameter("id_cita_pre");
        if(idPre != null && !idPre.isEmpty()) {
            req.setAttribute("idCitaPreseleccionada", idPre);

            // Buscamos la cita para pre-llenar datos del cliente (Cédula, Nombre)
            try {
                int id = Integer.parseInt(idPre);
                Optional<Cita> citaOpt = citaService.porId(id);
                if (citaOpt.isPresent()) {
                    req.setAttribute("citaPre", citaOpt.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            // 3. Cargar datos para la vista
            req.setAttribute("facturas", facturaService.listar());

            // CRÍTICO: Usamos listarAtendidas() para mostrar SOLO las citas que el doctor ya finalizó.
            // Esto evita facturar citas que aún no ocurren o están pendientes.
            req.setAttribute("citasPendientes", citaService.listarAtendidas());

            // Catálogos para agregar ítems a la factura
            req.setAttribute("servicios", servicioService.listar());
            req.setAttribute("productos", productoService.listar());
        } catch (Exception e) {
            req.setAttribute("error", "Error cargando datos: " + e.getMessage());
        }

        // 4. Despachar al JSP
        getServletContext().getRequestDispatcher("/WEB-INF/vistas/secretaria/facturacion.jsp").forward(req, resp);
    }

    /**
     * Maneja la petición POST para procesar el formulario de facturación.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");

        if ("generar".equalsIgnoreCase(accion)) {
            procesarFactura(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/facturacion");
        }
    }

    /**
     * Método auxiliar para procesar la lógica compleja de guardar una factura.
     * Recupera arrays de parámetros del formulario dinámico (múltiples ítems).
     */
    private void procesarFactura(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Connection conn = (Connection) request.getAttribute("conn");
            FacturaService facturaService = new FacturaServiceImpl(conn);

            // 1. Recolección de Datos de Cabecera
            int idCita = Integer.parseInt(request.getParameter("id_cita"));
            String identificacion = request.getParameter("identificacion_cliente");
            String nombreCliente = request.getParameter("nombre_cliente_factura");
            String direccion = request.getParameter("direccion_cliente");
            String metodoPago = request.getParameter("metodo_pago");

            // 2. Recolección de Datos de Detalles (Arrays)
            // El formulario envía múltiples valores con el mismo nombre (un array por cada columna de la tabla)
            String[] tipos = request.getParameterValues("detalle_tipo");
            String[] ids = request.getParameterValues("detalle_id");
            String[] cantidades = request.getParameterValues("detalle_cantidad");
            String[] precios = request.getParameterValues("detalle_precio");
            String[] zonas = request.getParameterValues("detalle_zona");

            // Validación básica de integridad
            if (tipos == null || ids == null || ids.length == 0) {
                throw new ServletException("La factura debe tener al menos un ítem.");
            }

            // 3. Construcción del Objeto Factura (Cabecera)
            Factura factura = new Factura();
            Cita c = new Cita(); c.setIdCita(idCita);
            factura.setCita(c);
            factura.setIdentificacionCliente(identificacion);
            factura.setNombreClienteFactura(nombreCliente);
            factura.setDireccionCliente(direccion);
            factura.setMetodoPago(metodoPago);
            factura.setFechaEmision(LocalDateTime.now());

            // 4. Construcción de la Lista de Detalles
            List<DetalleFactura> detalles = new ArrayList<>();

            for (int i = 0; i < tipos.length; i++) {
                if (ids[i] == null || ids[i].trim().isEmpty()) continue;

                DetalleFactura d = new DetalleFactura();
                d.setTipoItem(tipos[i]); // "Servicio" o "Producto"
                d.setCantidad(Integer.parseInt(cantidades[i]));
                d.setPrecioUnitario(new BigDecimal(precios[i]));
                d.setDienteOZona((zonas != null && i < zonas.length) ? zonas[i] : "-");

                // Vinculación polimórfica (Servicio o Producto)
                int idItem = Integer.parseInt(ids[i]);
                if (d.getTipoItem().equalsIgnoreCase("Servicio")) {
                    Servicio s = new Servicio();
                    s.setIdServicio(idItem);
                    d.setServicio(s);
                } else {
                    Producto p = new Producto();
                    p.setIdProducto(idItem);
                    d.setProducto(p);
                }
                detalles.add(d);
            }
            factura.setDetalles(detalles);

            // 5. Guardado Transaccional
            // El servicio se encargará de calcular totales, guardar en BD, bajar stock y actualizar estado de cita.
            int idFactura = facturaService.guardar(factura);

            // 6. Redirección Exitosa
            // Enviamos el ID de la factura para habilitar el botón de imprimir PDF en la vista.
            response.sendRedirect(request.getContextPath() + "/facturacion?exito=true&idFactura=" + idFactura);

        } catch (Exception e) {
            e.printStackTrace();
            // Manejo de errores: Volvemos al GET mostrando el mensaje
            request.setAttribute("error", e.getMessage());
            doGet(request, response);
        }
    }
}