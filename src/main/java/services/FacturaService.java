package services;

/*
 * Autor: Génesis Escobar
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio del Módulo de Facturación.
 * Establece las operaciones necesarias para gestionar el ciclo de vida financiero de las citas,
 * incluyendo la emisión de comprobantes, cálculos de impuestos (IVA) y consulta de historial.
 * Desacopla la capa web (FacturaServlet) de la lógica de persistencia y cálculo.
 */

import models.Factura;
import java.util.List;
import java.util.Optional;

public interface FacturaService {

    /**
     * Recupera el historial completo de facturas emitidas por la clínica.
     * Útil para reportes, auditoría y visualización en el dashboard financiero.
     *
     * @return Una lista de objetos {@link Factura} ordenados cronológicamente (generalmente descendente).
     */
    List<Factura> listar();

    /**
     * Busca una factura específica por su número único (ID).
     * Este método es esencial para la funcionalidad de "Ver Detalle" o "Imprimir PDF",
     * donde se requiere recuperar toda la información de una factura específica.
     *
     * @param id El identificador primario de la factura.
     * @return Un {@link Optional} que contiene la factura si existe (con sus detalles cargados), o vacío si no.
     */
    Optional<Factura> porId(int id);

    /**
     * Registra una nueva factura en el sistema.
     * Este es un método transaccional complejo que debe encargarse de:
     * 1. Validar los datos de la factura.
     * 2. Persistir la cabecera y los detalles de la factura.
     * 3. Descontar el stock de los productos vendidos.
     * 4. Actualizar el estado de la cita asociada a 'Facturada'.
     *
     * @param factura El objeto Factura con todos los datos (cliente, ítems, totales).
     * @return El ID generado de la nueva factura (útil para redirigir inmediatamente a la impresión).
     */
    int guardar(Factura factura);

    /**
     * Realiza los cálculos matemáticos financieros sobre una factura.
     * Suma los subtotales de los ítems, calcula el impuesto (IVA) y determina el total a pagar.
     * Se expone en la interfaz para permitir recalcular montos antes de guardar o al visualizar
     * una factura en memoria.
     *
     * @param factura El objeto Factura cuyos totales deben ser calculados y actualizados.
     */
    void calcularTotales(Factura factura);
}