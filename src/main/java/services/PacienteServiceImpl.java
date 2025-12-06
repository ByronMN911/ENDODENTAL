package services;

/*
 * Autor: Byron Melo
 * Fecha: 05/12/2025
 * Versión: 3.0
 * Descripción:
 * Implementación de la lógica de negocio para la gestión de Pacientes.
 * Esta clase actúa como intermediario entre la capa de presentación (Controlador) y la capa de datos (Repositorio).
 * Se encarga de aplicar reglas de negocio, como validaciones de cédula y duplicidad, antes de persistir los datos.
 */

import models.Paciente;
import repository.PacienteRepository;
import repository.PacienteRepositoryImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PacienteServiceImpl implements PacienteService {

    // Dependencia del repositorio para el acceso a datos
    private PacienteRepository repository;

    /**
     * Constructor que inicializa el servicio inyectando la conexión a la base de datos.
     * Utiliza el patrón de inyección manual para instanciar la implementación concreta del repositorio.
     *
     * @param conn La conexión JDBC activa proveniente del filtro o controlador.
     */
    public PacienteServiceImpl(Connection conn) {
        this.repository = new PacienteRepositoryImpl(conn);
    }

    /**
     * Recupera el listado de pacientes activos.
     *
     * @return Lista de pacientes con estado activo.
     * @throws ServiceJdbcException Si ocurre un error en la consulta SQL.
     */
    @Override
    public List<Paciente> listar() {
        try {
            return repository.listar();
        } catch (SQLException e) {
            // Envolvemos la excepción técnica en una de negocio
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Recupera el listado de pacientes inactivos (archivados).
     *
     * @return Lista de pacientes con estado inactivo.
     * @throws ServiceJdbcException Si ocurre un error en la consulta SQL.
     */
    @Override
    public List<Paciente> listarInactivos() {
        try {
            return repository.listarInactivos();
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Busca un paciente por su ID.
     *
     * @param id Identificador único del paciente.
     * @return Optional con el paciente si existe.
     */
    @Override
    public Optional<Paciente> porId(int id) {
        try {
            return Optional.ofNullable(repository.porId(id));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Busca un paciente por su número de cédula.
     *
     * @param cedula Cédula de identidad.
     * @return Optional con el paciente si existe.
     */
    @Override
    public Optional<Paciente> porCedula(String cedula) {
        try {
            return Optional.ofNullable(repository.porCedula(cedula));
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Realiza el borrado lógico de un paciente.
     *
     * @param id ID del paciente a desactivar.
     */
    @Override
    public void eliminar(int id) {
        try {
            repository.eliminar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Reactiva un paciente previamente archivado.
     *
     * @param id ID del paciente a activar.
     */
    @Override
    public void activar(int id) {
        try {
            repository.activar(id);
        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /**
     * Guarda un paciente en la base de datos (Crear o Editar).
     * Aplica validaciones de negocio críticas antes de llamar al repositorio.
     *
     * Reglas de Negocio:
     * 1. La cédula debe ser válida según el algoritmo ecuatoriano.
     * 2. No puede existir otro paciente con la misma cédula.
     *
     * @param paciente El objeto Paciente a persistir.
     * @throws ServiceJdbcException Si falla alguna validación o la persistencia.
     */
    @Override
    public void guardar(Paciente paciente) {
        try {
            // 1. VALIDACIÓN DE INTEGRIDAD DE CÉDULA
            // Verificamos que el número cumpla con el algoritmo de control (Módulo 10)
            if (!esCedulaValida(paciente.getCedula())) {
                throw new ServiceJdbcException("La cédula ingresada (" + paciente.getCedula() + ") no es válida.");
            }

            // 2. VALIDACIÓN DE DUPLICIDAD
            // Buscamos si ya existe alguien con esa cédula en la base de datos
            Paciente pacienteExistente = repository.porCedula(paciente.getCedula());

            if (pacienteExistente != null) {
                // Escenario A: Creación de Nuevo Paciente (ID es 0)
                // Si encontramos un registro con esa cédula, es un duplicado ilegal.
                if (paciente.getIdPaciente() == 0) {
                    throw new ServiceJdbcException("La cédula ya está registrada en el sistema.");
                }
                // Escenario B: Edición de Paciente Existente (ID > 0)
                // Verificamos que la cédula encontrada pertenezca al MISMO paciente que estamos editando.
                // Si el ID es diferente, significa que intentamos ponerle la cédula de otra persona.
                else {
                    if (pacienteExistente.getIdPaciente() != paciente.getIdPaciente()) {
                        throw new ServiceJdbcException("La cédula ya pertenece a otro paciente.");
                    }
                }
            }

            // Si pasan todas las validaciones, procedemos a guardar
            repository.guardar(paciente);

        } catch (SQLException e) {
            throw new ServiceJdbcException(e.getMessage(), e.getCause());
        }
    }

    /*
     * -------------------------------------------------------------------------
     * MÉTODOS PRIVADOS DE UTILIDAD (ALGORITMOS DE VALIDACIÓN)
     * -------------------------------------------------------------------------
     */

    /**
     * Valida si una cédula ecuatoriana es correcta utilizando el algoritmo de "Módulo 10".
     *
     * El proceso consiste en:
     * 1. Verificar longitud y formato numérico.
     * 2. Validar el código de provincia (dos primeros dígitos).
     * 3. Validar el tercer dígito (tipo de persona).
     * 4. Aplicar el algoritmo de coeficientes 2.1.2.1... sobre los primeros 9 dígitos.
     * 5. Calcular el dígito verificador y compararlo con el décimo dígito de la cédula.
     *
     * @param cedula La cadena con el número de identificación.
     * @return true si es válida, false si no cumple el formato o el algoritmo.
     */
    private boolean esCedulaValida(String cedula) {
        // Paso 1: Validación básica de formato (no nulo y exactamente 10 dígitos)
        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }

        // Paso 2: Validación de provincia (01 a 24)
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }

        // Paso 3: Validación del tercer dígito (menor a 6 para personas naturales)
        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito >= 6) {
            return false;
        }

        // Paso 4: Algoritmo Módulo 10
        // Coeficientes estándar para cédulas: 2, 1, 2, 1, 2, 1, 2, 1, 2
        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        // Iteramos sobre los primeros 9 dígitos
        for (int i = 0; i < 9; i++) {
            // Multiplicamos el dígito por su coeficiente correspondiente
            int valor = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];

            // Si el resultado de la multiplicación es >= 10, se restan 9 (o se suman los dígitos del resultado, ej: 16 -> 1+6=7, que es igual a 16-9)
            if (valor >= 10) valor -= 9;

            suma += valor;
        }

        // Obtenemos el décimo dígito original (el verificador)
        int digitoVerificador = Character.getNumericValue(cedula.charAt(9));

        // Calculamos la decena superior inmediata a la suma
        // Ej: Si suma es 43, decenaSuperior es 50.
        int decenaSuperior = ((suma + 9) / 10) * 10;

        // Restamos la suma de la decena superior
        int resultado = decenaSuperior - suma;

        // Si el resultado es 10, el dígito verificador debe ser 0
        if (resultado == 10) resultado = 0;

        // Paso 5: Comparación final
        return resultado == digitoVerificador;
    }
}