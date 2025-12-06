package util;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Clase utilitaria de seguridad encargada de la gestión criptográfica de contraseñas.
 * Implementa el algoritmo de hashing SHA-256 para asegurar que las credenciales
 * nunca se almacenen ni se comparen en texto plano dentro del sistema.
 * Esta clase sigue el principio de "One-Way Hash" (Hash unidireccional).
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    /**
     * Método estático encargado de transformar una contraseña legible en un Hash seguro.
     * Utiliza el algoritmo SHA-256 (Secure Hash Algorithm de 256 bits).
     * * @param password La contraseña en texto plano (ej: "admin123").
     * @return Una cadena hexadecimal de 64 caracteres que representa el hash de la contraseña.
     * Retorna una RuntimeException si el algoritmo no está disponible en el entorno.
     */
    public static String encriptar(String password) {
        try {
            // 1. Instanciamos el generador de HASH usando el algoritmo estándar SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // 2. Convertimos el String a un arreglo de bytes y aplicamos el algoritmo de digestión
            // El método digest() procesa la entrada y devuelve el hash en formato binario (byte[])
            byte[] hash = md.digest(password.getBytes());

            // 3. Conversión de Bytes a Hexadecimal
            // Utilizamos StringBuilder por eficiencia (es mutable), evitando crear múltiples objetos String en memoria.
            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {
                /*
                 * Convertimos cada byte a su representación hexadecimal.
                 * "%02x": Formato que asegura 2 dígitos hexadecimales por byte.
                 * Si el valor es menor a 16 (ej: A), agrega un 0 a la izquierda (0A).
                 */
                sb.append(String.format("%02x", b));
            }

            // 4. Retornamos la cadena final encriptada
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            // Capturamos el error técnico si la JVM no soporta SHA-256 (caso extremadamente raro)
            // y lo relanzamos como error de tiempo de ejecución para detener el proceso.
            throw new RuntimeException("Error crítico de seguridad: Algoritmo de encriptación no encontrado", e);
        }
    }

    /**
     * Método para verificar credenciales durante el proceso de Login.
     * Compara una contraseña ingresada por el usuario contra un hash almacenado en la Base de Datos.
     * * IMPORTANTE: No se "desencripta" la contraseña guardada (SHA-256 es irreversible).
     * En su lugar, se encripta la entrada y se comparan los dos hashes resultantes.
     * * @param passwordPlana La contraseña que el usuario escribió en el formulario de login.
     * @param passwordHash El hash recuperado de la base de datos asociado al usuario.
     * @return true si los hashes son idénticos (contraseña correcta), false en caso contrario.
     */
    public static boolean verificar(String passwordPlana, String passwordHash) {
        // 1. Encriptamos la contraseña que acaba de escribir el usuario
        String nuevoHash = encriptar(passwordPlana);

        // 2. Comparamos el hash resultante con el hash original
        // Usamos .equals() porque son objetos String, no primitivos.
        return nuevoHash.equals(passwordHash);
    }
}