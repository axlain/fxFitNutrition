package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.Dieta;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

public class DietaImp {
    
    public static List<Dieta> obtenerTodas() {
        String url = Constantes.URL_WS + "dieta/obtener-todas";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Dieta>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    public static Respuesta registrar(Dieta dieta) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        String url = Constantes.URL_WS + "dieta/registrar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(dieta);

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
                        "No es posible registrar la dieta en este momento."
                ));
            }
        } catch (Exception e) {
            respuesta.setMensaje("Error al registrar dieta: " + e.getMessage());
        }

        return respuesta;
    }
    
    public static Respuesta editar(Dieta dieta) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        String url = Constantes.URL_WS + "dieta/editar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(dieta);

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
                        "No es posible editar la dieta en este momento."
                ));
            }
        } catch (Exception e) {
            respuesta.setMensaje("Error al editar dieta: " + e.getMessage());
        }

        return respuesta;
    }
    
    public static Respuesta eliminar(Integer idDieta) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        String url = Constantes.URL_WS + "dieta/eliminar/" + idDieta; // O el endpoint que aplique para eliminar

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
                        "No es posible eliminar la dieta en este momento."
                ));
            }
        } catch (Exception e) {
            respuesta.setMensaje("Error al eliminar dieta: " + e.getMessage());
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
