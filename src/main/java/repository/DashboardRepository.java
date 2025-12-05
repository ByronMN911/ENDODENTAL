package repository;

import java.sql.SQLException;

public interface DashboardRepository {
    int contarCitasDia(String fecha) throws SQLException;
    int contarPacientesActivos() throws SQLException;
    double sumarFacturadoDia(String fecha) throws SQLException;
}
