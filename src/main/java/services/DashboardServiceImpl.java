package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para el Dashboard.
 * Esta clase se encarga de coordinar la obtención de las métricas clave (KPIs)
 * que se muestran en la pantalla de inicio.
 *
 * Actúa como puente entre el DashboardServlet (Controlador) y el DashboardRepository (Datos),
 * encapsulando la lógica de fechas y el manejo de fallos para la visualización.
 */

import repository.DashboardRepository;
import repository.DashboardRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class DashboardServiceImpl implements DashboardService {

    // Dependencia del repositorio que ejecuta las consultas SQL de agregación (COUNT, SUM)
    private DashboardRepository repository;

    /**
     * Constructor que inicializa el servicio.
     * Inyecta la conexión a la base de datos en la implementación del repositorio.
     *
     * @param conn La conexión JDBC activa proveniente del filtro o controlador.
     */
    public DashboardServiceImpl(Connection conn) {
        this.repository = new DashboardRepositoryImpl(conn);
    }

    /**
     * Obtiene la cantidad de citas programadas para la fecha actual.
     * Utiliza la fecha del sistema del servidor para filtrar los resultados.
     *
     * @return El número de citas de hoy. Retorna 0 si ocurre un error de base de datos.
     */
    @Override
    public int getCitasHoy() {
        try {
            // Generamos la fecha actual en formato ISO (YYYY-MM-DD) para la consulta SQL
            String fechaHoy = LocalDate.now().toString();
            return repository.contarCitasDia(fechaHoy);
        } catch (SQLException e) {
            // Manejo de Excepción "Silenciosa":
            // En un Dashboard, si falla una métrica, preferimos mostrar '0'
            // en lugar de romper toda la página con un error 500.
            e.printStackTrace(); // Log para depuración en consola
            return 0; // Valor por defecto seguro
        }
    }

    /**
     * Obtiene el total histórico de pacientes activos registrados en el sistema.
     *
     * @return El número total de pacientes activos. Retorna 0 si ocurre un error.
     */
    @Override
    public int getPacientesTotales() {
        try {
            return repository.contarPacientesActivos();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Calcula los ingresos monetarios generados en el día actual.
     * Suma los totales de todas las facturas emitidas hoy.
     *
     * @return La suma total facturada. Retorna 0.0 si ocurre un error.
     */
    @Override
    public double getIngresosHoy() {
        try {
            String fechaHoy = LocalDate.now().toString();
            return repository.sumarFacturadoDia(fechaHoy);
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}