package repository;

/*
 * Autor: Byron Melo
 * Fecha: 05-12-2025
 * Versión: 1.0
 * Descripción:
 * Implementación del Repositorio para la entidad 'Atencion'.
 * Esta clase maneja la persistencia de los datos clínicos generados durante la consulta médica.
 * Se encarga exclusivamente de insertar el registro del diagnóstico y tratamiento en la base de datos.
 *
 * Nota: Aunque en este diseño simplificado no implementa una interfaz 'AtencionRepository',
 * sigue el patrón DAO al encapsular el acceso a datos.
 */

import models.Atencion;
import java.sql.*;

public class AtencionRepositoryImpl {

    // La conexión se inyecta desde el servicio para mantener la integridad transaccional
    private Connection conn;

    /**
     * Constructor que recibe la conexión activa.
     * @param conn Objeto Connection gestionado por el filtro o servicio principal.
     */
    public AtencionRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Guarda un nuevo registro de atención médica en la base de datos.
     * Este método se llama cuando el odontólogo finaliza la consulta.
     *
     * @param atencion El objeto que contiene los datos clínicos (diagnóstico, tratamiento, notas).
     * @throws SQLException Si ocurre un error al ejecutar la sentencia INSERT.
     */
    public void guardar(Atencion atencion) throws SQLException {
        // Sentencia SQL parametrizada para evitar Inyección SQL
        String sql = "INSERT INTO atenciones (id_cita, diagnostico, tratamiento_realizado, notas_adicionales) VALUES (?, ?, ?, ?)";

        // Usamos try-with-resources para asegurar que el PreparedStatement se cierre automáticamente
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. Asignamos el ID de la cita (Clave foránea obligatoria)
            stmt.setInt(1, atencion.getCita().getIdCita());

            // 2. Asignamos el diagnóstico (Obligatorio según reglas de negocio)
            stmt.setString(2, atencion.getDiagnostico());

            // 3. Asignamos el tratamiento realizado (Obligatorio)
            stmt.setString(3, atencion.getTratamientoRealizado());

            // 4. Asignamos notas adicionales (Opcional)
            // Verificamos si es nulo para manejar correctamente el tipo SQL NULL
            if (atencion.getNotasAdicionales() != null) {
                stmt.setString(4, atencion.getNotasAdicionales());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }

            // Ejecutamos la inserción
            stmt.executeUpdate();
        }
    }
}