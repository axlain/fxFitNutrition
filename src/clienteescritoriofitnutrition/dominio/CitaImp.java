package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.pojo.Cita;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CitaImp {

    public static Respuesta registrar(Cita cita) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "cita/registrar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(cita);

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
                        "No es posible agendar la cita en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al registrar la cita: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta reagendar(Cita cita) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "cita/reagendar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(cita);

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
                        "No es posible reagendar la cita en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al reagendar la cita: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta cancelar(Cita cita) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "cita/cancelar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(cita);

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
                        "No es posible cancelar la cita en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al cancelar la cita: " + e.getMessage());
        }

        return respuesta;
    }

    public static List<Cita> obtenerPorMedico(Integer idMedico, String fecha) {
        if (fecha == null) fecha = "";
        
        try {
            String fechaCodificada = URLEncoder.encode(fecha.trim(), StandardCharsets.UTF_8.name());
            String url = Constantes.URL_WS + "cita/obtener-por-medico?idMedico=" + idMedico + "&fecha=" + fechaCodificada;
            
            RespuestaHTTP resp = ConexionAPI.peticionGET(url);

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Cita>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            }
        } catch (Exception e) {
            return null;
        }
        
        return null;
    }

    public static List<Cita> obtenerVigentesPaciente(Integer idPaciente) {
        String url = Constantes.URL_WS + "cita/obtener-vigentes-paciente/" + idPaciente;
        
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Cita>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }
        
        return null;
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
