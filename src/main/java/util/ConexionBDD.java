package util;
/*
 * Autor: Byron Melo
 * Fecha: 29/11/2025
 * Versión: 1.0
 * Descripción: Clase que permite crear una conexión centralizada y global a la base de datos
 * "clinica-odontologica" en MySQL.
 * */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBDD {
    /*Esta es la cadena de dirección, que es la dirección de en donde se encuentra
     el driver de la base de datos
    */
    private static String url = "jdbc:mysql://localhost:3306/clinica_odontologica?serverTimezone=UTC";
    /*
     * Definimos variables privadas y estáticas (permiten utilizar directamente las variables sin crear instancias
     * pero como son private solo se pueden usar en esta clase.
     * Representan el username y password del usuario necesarias para conectarnos a MySQL.
     * */
    private static String username="root";
    private static String password="misifu";

    /*
     *metodo de la clase que retorna un objeto de tipo connection, lanza excepciones SQL y
     * necesita 3 parámetros para establecer la conexión (url, username, password).
     * */
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url, username, password);
    }
   
}
