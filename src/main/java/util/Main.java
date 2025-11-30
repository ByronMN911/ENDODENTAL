package util;
/*
 * Autor: Byron Melo
 * Fecha: 29/11/2025
 * Versión: 1.0
 * Descripción: Clase principal que prueba la conexión a la base de datos
 * "sistemaventas" utilizando el metodo getConnection de la clase ConexionBDD.
 * */
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    /*
     * Metodo principal que ejecuta la prueba de conexión a la base de datos.
     * Utiliza el metodo estático getConnection() de la clase ConexionBDD para
     * obtener una conexión y valida si fue exitosa o no.
     * */
    public static void main(String[] args) {

        // Mensaje de inicio del proceso de conexión
        System.out.println("==============================================");
        System.out.println("Iniciando la conexión a la base de datos");
        System.out.println("==============================================\n");

        /*
         * Try-with-resources: La conexión se declara dentro de los paréntesis del try.
         * Esto garantiza que la conexión se cierre automáticamente al finalizar el bloque try,
         * sin necesidad de un bloque finally, esto es más limpio y seguro que el try-catch-finally tradicional.
         * */
        try (Connection conexion = ConexionBDD.getConnection()) {

            // Intento de obtener la conexión usando el metodo de ConexionBDD


            /*
             * Si la conexión fue exitosa se ejecuta el código dentro del try y
             * además validamos que el objeto conexión no sea nulo y esté abierto.
             * */
            if (conexion != null && !conexion.isClosed()) {
                System.out.println("Conexión Exitosa");
                System.out.println("Conectado a la base de datos 'clinica_odontologica'");
                System.out.println("Estado de la conexión: ACTIVA\n");
                System.out.println("Conexión cerrada automáticamente");
            }

        } catch (SQLException e) {
            /*
             * Esta excepción se lanza cuando hay problemas con la conexión SQL.
             * Puede deberse a credenciales incorrectas, servidor no disponible,
             * base de datos inexistente, sintaxis incorrecta de la cadena de conexión,
             * nombre de usuario incorrecto, etc
             * */
            System.err.println("Error de conexión al a base de datos " );
            System.err.println("Código de error SQL: " + e.getErrorCode());
            System.err.println("Detalle: " + e.getMessage());
            System.err.println("Solución: Verifique las credenciales, que el servidor MySQL esté activo");
            System.err.println("  y que la base de datos 'clinica-odontologica' exista\n");

        }
    }
}