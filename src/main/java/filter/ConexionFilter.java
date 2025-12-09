package filter;
/*
Autor: Byron Melo
Fecha: 29/11/2025
Versión: 1.0
Descripción:
Filtro encargado de administrar la conexión a la base de datos para todas las
solicitudes que lleguen a la aplicación. El filtro obtiene una conexión,
la agrega como atributo en el request para que otros componentes (servlets o DAOs)
puedan utilizarla, y realiza commit o rollback según corresponda.
 */

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.ConexionBDD;
import services.ServiceJdbcException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/*
Implementamos una anotación que nos permite ejecutar este filtro para
todas las rutas de la aplicación ("/*"). Eso significa que cada request
pasará por este filtro antes de llegar al recurso solicitado.
 */
@WebFilter("/*")
public class ConexionFilter implements Filter {

    /*
    Un filtro en Java es un componente que intercepta y procesa las solicitudes
    y respuestas antes de que lleguen al servlet o
    después de que el servlet produzca la respuesta.

    Se usa para tareas en una aplicación como el manejo de conexiones a BDD
    autenticación / autorización, logging, compresión de datos y transformación de
    peticiones o respuestas.

    Los filtros funcionan en servidores compatibles con Jakarta EE.
     */

    // Sobrescribimos el metodo doFilter de la interfaz Filter.
    // Este metodo es obligatorio y se ejecuta en cada solicitud.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        /*
        Parámetros:
        - request: contiene la información que envía el cliente.
        - response: es el objeto que será devuelto al cliente.
        - filterChain: controla la invocación del siguiente filtro o servlet
                       mediante filterChain.doFilter(request, response).


        // Casteamos a HttpServletRequest para poder ver la URL

         */
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        // VALIDACIÓN CRÍTICA:
        // Si la petición es para un recurso estático (css, js, img), PASAMOS DE LARGO.
        // No abrimos conexión, solo dejamos pasar la petición.
        if (path.contains("/assets/") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
            filterChain.doFilter(request, response);
            return; // Salimos del metodo aquí.
        }


        // Obtenemos una conexión desde la clase utilitaria ConexionBDD
        try (Connection conn = ConexionBDD.getConnection()) {

            /*
            Antes de procesar la solicitud verificamos y configuramos el autocommit.
            Si el autocommit está en true, lo desactivamos para poder controlar
            manualmente las transacciones (commit o rollback).
             */
            if (conn.getAutoCommit()) {
                conn.setAutoCommit(false);
            }

            try {
                /*
                Agregamos la conexión como atributo dentro del request.
                Esto permite que servlets, DAOs u otros filtros puedan obtenerla
                mediante el siguiente código: (Connection) request.getAttribute("conn").
                 */
                request.setAttribute("conn", conn);

                // Pasamos la solicitud y respuesta al siguiente filtro o servlet
                filterChain.doFilter(request, response);

                // Si sale bien confirmamos los cambios
                conn.commit();

            } catch (SQLException | ServiceJdbcException e) {
                 /*
                Si ocurre algún error durante la ejecución de la solicitud,
                realizamos un rollback para evitar que los cambios se guarden
                de forma incorrecta.
                 */
                conn.rollback();
                /*

                Se envía el código de error 500 al cliente indicando un problema interno del servidor
                 */
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        e.getMessage());
                e.printStackTrace();
            }
        }catch(SQLException throwables) {
            throwables.printStackTrace();

        }
    }
}