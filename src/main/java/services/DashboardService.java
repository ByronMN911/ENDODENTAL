package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Interfaz que define el contrato para la lógica de negocio del Dashboard (Panel de Control).
 * Establece las operaciones necesarias para calcular y recuperar los indicadores clave de rendimiento (KPIs)
 * que se muestran en la pantalla principal de la secretaria o administrador.
 * Su objetivo es proveer una vista rápida del estado actual de la clínica.
 */

public interface DashboardService {

    /**
     * Calcula el número total de citas agendadas para la fecha actual.
     * Generalmente filtra citas que no estén canceladas para dar una visión real de la carga de trabajo.
     *
     * @return Un entero con la cantidad de citas válidas para el día de hoy.
     */
    int getCitasHoy();

    /**
     * Obtiene el conteo total de pacientes registrados y activos en el sistema.
     * Este indicador ayuda a medir el crecimiento de la base de clientes de la clínica.
     *
     * @return Un entero con el número total de pacientes con estado activo (1).
     */
    int getPacientesTotales();

    /**
     * Calcula el monto total de ingresos facturados durante el día actual.
     * Suma el valor 'total_pagar' de todas las facturas emitidas hoy.
     * Útil para el cierre de caja diario y seguimiento de metas financieras.
     *
     * @return Un valor double que representa la suma de dinero facturado hoy.
     */
    double getIngresosHoy();
}