package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Interfaz que define el contrato de acceso a datos para el Dashboard (Panel de Control).
 * Se encarga de las consultas de agregación (conteos, sumas) necesarias para
 * mostrar los indicadores clave de rendimiento (KPIs) de la clínica en la pantalla de inicio.
 *
 * A diferencia de otros repositorios, este se enfoca en datos estadísticos y no en CRUD completo.
 */

import java.sql.SQLException;

public interface DashboardRepository {

    /**
     * Cuenta el número de citas válidas (generalmente excluyendo las canceladas)
     * programadas para una fecha específica.
     *
     * @param fecha La fecha a consultar en formato 'YYYY-MM-DD'.
     * @return El número entero de citas encontradas.
     * @throws SQLException Si ocurre un error en la consulta de conteo (COUNT).
     */
    int contarCitasDia(String fecha) throws SQLException;

    /**
     * Cuenta el total de pacientes registrados que tienen estado activo (1) en el sistema.
     * Útil para conocer el tamaño actual de la base de clientes de la clínica.
     *
     * @return El número total de pacientes activos.
     * @throws SQLException Si ocurre un error en la consulta.
     */
    int contarPacientesActivos() throws SQLException;

    /**
     * Calcula la suma total de los montos facturados ('total_pagar') en una fecha específica.
     * Provee una visión rápida del flujo de caja diario.
     *
     * @param fecha La fecha de emisión de las facturas.
     * @return La suma total como un valor de punto flotante (double).
     * @throws SQLException Si ocurre un error en la función de agregación (SUM).
     */
    double sumarFacturadoDia(String fecha) throws SQLException;
}