package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.pojo.Paciente;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PacienteImp {

    public static List<Paciente> obtenerTodos() {
        String url = Constantes.URL_WS + "paciente/obtener-todos";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Paciente>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static List<Paciente> buscar(String filtro) {
        if (filtro == null) filtro = "";
        
        try {
            String parametro = URLEncoder.encode(filtro.trim(), StandardCharsets.UTF_8.name());
            String url = Constantes.URL_WS + "paciente/buscar?filtro=" + parametro;
            
            RespuestaHTTP resp = ConexionAPI.peticionGET(url);

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Paciente>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            }
        } catch (Exception e) {
            return null;
        }
        
        return null;
    }

    public static Respuesta registrar(Paciente paciente) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "paciente/registrar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(paciente);

            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_POST,
                    json,
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            } else {
                respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(),
                        "No es posible registrar al paciente en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al registrar paciente: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta editar(Paciente paciente) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "paciente/editar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(paciente);

            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_PUT,
                    json,
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            } else {
                respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(),
                        "No es posible editar al paciente en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al editar paciente: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta darBaja(Integer idPaciente) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "paciente/dar-baja/" + idPaciente;

        try {
            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_DELETE,
                    "",
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            } else {
                respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(),
                        "No es posible dar de baja al paciente en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al dar de baja al paciente: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta actualizarCodigoAcceso(Integer idPaciente) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "paciente/generar-codigo/" + idPaciente;

        try {
            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_PUT,
                    "",
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            } else {
                respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(),
                        "No es posible actualizar el código de acceso en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al actualizar código de acceso: " + e.getMessage());
        }

        return respuesta;
    }

    private static Respuesta parsearRespuesta(String json) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(json, Respuesta.class);
        } catch (Exception e) {
            Respuesta r = new Respuesta();
            r.setError(true);
            r.setMensaje("Error al procesar la respuesta del servidor.");
            return r;
        }
    }
}