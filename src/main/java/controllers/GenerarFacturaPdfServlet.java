package controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.DetalleFactura;
import models.Factura;
import services.FacturaService;
import services.FacturaServiceImpl;

import java.io.IOException;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/*
 * Autor: Génesis Escobar
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Servlet encargado de la generación de reportes en formato PDF.
 * Específicamente, convierte los datos de una factura registrada en la base de datos
 * en un documento visual imprimible utilizando la librería iText.
 *
 * Funcionalidades:
 * 1. Recuperación de datos de factura mediante ID.
 * 2. Configuración de la respuesta HTTP para servir archivos binarios (PDF).
 * 3. Diseño y maquetación del documento (Encabezado, Cliente, Detalles, Totales).
 */

@WebServlet("/facturacion/pdf")
public class GenerarFacturaPdfServlet extends HttpServlet {

    /**
     * Maneja la petición GET para generar y descargar el PDF.
     *
     * @param req  La solicitud HTTP que contiene el parámetro 'id' de la factura.
     * @param resp La respuesta HTTP donde se escribirá el flujo de bytes del PDF.
     * @throws ServletException Si ocurre un error en el Servlet.
     * @throws IOException Si hay errores de entrada/salida durante la generación.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. INYECCIÓN DE DEPENDENCIAS
        // Obtenemos la conexión y el servicio necesarios para consultar la factura.
        Connection conn = (Connection) req.getAttribute("conn");
        FacturaService service = new FacturaServiceImpl(conn);

        // 2. VALIDACIÓN DE PARÁMETROS
        // Obtenemos el ID de la factura desde la URL (ej: /facturacion/pdf?id=5)
        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/facturacion?error=ID de factura no proporcionado");
            return;
        }

        try {
            int idFactura = Integer.parseInt(idStr);

            // 3. BÚSQUEDA DE DATOS
            // Recuperamos la factura completa (incluyendo sus detalles/ítems)
            Optional<Factura> oFactura = service.porId(idFactura);

            if (oFactura.isPresent()) {
                Factura factura = oFactura.get();

                /*
                 * 4. CONFIGURACIÓN DE LA RESPUESTA HTTP
                 * - setContentType("application/pdf"): Indica al navegador que el contenido es un PDF.
                 * - setHeader("Content-Disposition", ...):
                 * "inline": Abre el PDF en el visor del navegador.
                 * "attachment": Fuerza la descarga del archivo.
                 */
                resp.setContentType("application/pdf");
                resp.setHeader("Content-Disposition", "inline; filename=Factura_" + idFactura + ".pdf");

                // 5. GENERACIÓN DEL DOCUMENTO
                // Llamamos al método auxiliar que construye el contenido visual.
                generarDocumentoPdf(resp, factura);

            } else {
                resp.sendRedirect(req.getContextPath() + "/facturacion?error=Factura no encontrada");
            }

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/facturacion?error=ID invalido");
        } catch (DocumentException e) {
            // Error específico de iText al construir el PDF
            throw new IOException("Error al crear el PDF: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar que contiene la lógica de diseño del PDF usando iText.
     * Construye el documento paso a paso: Títulos, Datos del Cliente, Tabla de Ítems y Totales.
     *
     * @param resp Objeto Response para obtener el Output Stream.
     * @param factura Objeto Factura con los datos a imprimir.
     * @throws DocumentException Si hay error en la estructura del PDF.
     * @throws IOException Si falla la escritura en el stream.
     */
    private void generarDocumentoPdf(HttpServletResponse resp, Factura factura) throws DocumentException, IOException {
        // Inicialización del documento y el escritor
        Document document = new Document();
        PdfWriter.getInstance(document, resp.getOutputStream());
        document.open();

        // --- DEFINICIÓN DE FUENTES ---
        // Preparamos fuentes estándar para mantener consistencia visual (Títulos, Texto normal, Negritas)
        Font tituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Font subTituloFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.GRAY);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font negritaFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);

        // --- SECCIÓN 1: ENCABEZADO ---
        Paragraph titulo = new Paragraph("CLÍNICA ENDODENTAL", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Comprobante de Factura #" + factura.getIdFactura(), subTituloFont);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitulo);
        document.add(new Paragraph(" ")); // Espaciado vertical

        // --- SECCIÓN 2: DATOS DEL CLIENTE ---
        // Usamos una tabla invisible (sin bordes) para alinear etiquetas y valores ordenadamente
        PdfPTable datosTable = new PdfPTable(2);
        datosTable.setWidthPercentage(100);
        datosTable.setWidths(new float[]{1, 2}); // Columna 1 (Etiqueta) ocupa 1/3, Columna 2 (Valor) ocupa 2/3

        agregarCeldaSinBorde(datosTable, "Cliente:", negritaFont);
        agregarCeldaSinBorde(datosTable, factura.getNombreClienteFactura(), normalFont);

        agregarCeldaSinBorde(datosTable, "RUC/Cédula:", negritaFont);
        agregarCeldaSinBorde(datosTable, factura.getIdentificacionCliente(), normalFont);

        agregarCeldaSinBorde(datosTable, "Dirección:", negritaFont);
        agregarCeldaSinBorde(datosTable, factura.getDireccionCliente() != null ? factura.getDireccionCliente() : "S/N", normalFont);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        agregarCeldaSinBorde(datosTable, "Fecha Emisión:", negritaFont);
        agregarCeldaSinBorde(datosTable, factura.getFechaEmision().format(formatter), normalFont);

        document.add(datosTable);
        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));

        // --- SECCIÓN 3: TABLA DE DETALLES (ÍTEMS) ---
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 1, 1}); // Anchos relativos de columnas

        // Cabeceras de la tabla
        agregarCeldaEncabezado(table, "Descripción / Servicio", negritaFont);
        agregarCeldaEncabezado(table, "Cant.", negritaFont);
        agregarCeldaEncabezado(table, "Precio", negritaFont);
        agregarCeldaEncabezado(table, "Total", negritaFont);

        // Iteración sobre los ítems de la factura
        if (factura.getDetalles() != null) {
            for (DetalleFactura d : factura.getDetalles()) {
                // Lógica para determinar qué nombre mostrar (Servicio vs Producto vs Texto libre)
                String nombreItem = "Item";
                if (d.getServicio() != null && d.getServicio().getNombre() != null) {
                    nombreItem = d.getServicio().getNombre();
                } else if (d.getProducto() != null && d.getProducto().getNombre() != null) {
                    nombreItem = d.getProducto().getNombre();
                } else {
                    nombreItem = d.getTipoItem(); // Fallback
                }

                // Agregamos las celdas de la fila
                table.addCell(new Phrase(nombreItem, normalFont));
                table.addCell(new Phrase(String.valueOf(d.getCantidad()), normalFont));
                table.addCell(new Phrase("$" + d.getPrecioUnitario(), normalFont));
                table.addCell(new Phrase("$" + d.getSubtotalItem(), normalFont));
            }
        } else {
            // Fila vacía por si acaso no hay detalles (Edge case)
            table.addCell(new Phrase("Sin detalles", normalFont));
            table.addCell(new Phrase("-", normalFont));
            table.addCell(new Phrase("-", normalFont));
            table.addCell(new Phrase("-", normalFont));
        }

        document.add(table);
        document.add(new Paragraph(" "));

        // --- SECCIÓN 4: TOTALES ---
        // Tabla alineada a la derecha para mostrar los montos finales
        PdfPTable totalesTable = new PdfPTable(2);
        totalesTable.setWidthPercentage(40); // Que ocupe solo el 40% del ancho
        totalesTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        agregarCeldaTotales(totalesTable, "Subtotal:", negritaFont, "$" + factura.getSubtotal());
        agregarCeldaTotales(totalesTable, "IVA (15%):", negritaFont, "$" + factura.getMontoIva());
        agregarCeldaTotales(totalesTable, "TOTAL:", tituloFont, "$" + factura.getTotalPagar());

        document.add(totalesTable);

        document.close();
    }

    // --- MÉTODOS HELPERS (Para reducir duplicidad de código de estilo) ---

    private void agregarCeldaSinBorde(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(5);
        table.addCell(cell);
    }

    private void agregarCeldaEncabezado(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void agregarCeldaTotales(PdfPTable table, String label, Font font, String valor) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, font));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellLabel);

        PdfPCell cellValor = new PdfPCell(new Phrase(valor, font));
        cellValor.setBorder(Rectangle.NO_BORDER);
        cellValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellValor);
    }
}