package services;

import repository.DashboardRepository;
import repository.DashboardRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class DashboardServiceImpl implements DashboardService {
    private DashboardRepository repository;

    public DashboardServiceImpl(Connection conn) {
        this.repository = new DashboardRepositoryImpl(conn);
    }

    @Override
    public int getCitasHoy() {
        try {
            return repository.contarCitasDia(LocalDate.now().toString());
        } catch (SQLException e) {
            e.printStackTrace(); // En producción usar logger
            return 0;
        }
    }

    @Override
    public int getPacientesTotales() {
        try {
            return repository.contarPacientesActivos();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public double getIngresosHoy() {
        try {
            return repository.sumarFacturadoDia(LocalDate.now().toString());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}