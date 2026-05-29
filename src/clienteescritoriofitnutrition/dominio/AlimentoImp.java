package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.Alimento;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

public class AlimentoImp {
    
    public static List<Alimento> obtenerTodos() {
        String url = Constantes.URL_WS + "alimento/obtener-activos";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Alimento>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static List<clienteescritoriofitnutrition.pojo.UnidadPorcion> obtenerUnidades() {
        String url = Constantes.URL_WS + "alimento/unidades";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<clienteescritoriofitnutrition.pojo.UnidadPorcion>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    
    public static Respuesta registrar(Alimento alimento) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        String url = Constantes.URL_WS + "alimento/registrar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(alimento);

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
                        "No es posible registrar el alimento en este momento."
                ));
            }
        } catch (Exception e) {
            respuesta.setMensaje("Error al registrar alimento: " + e.getMessage());
        }

        return respuesta;
    }
    
    public static Respuesta editar(Alimento alimento) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        String url = Constantes.URL_WS + "alimento/editar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(alimento);

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
                        "No es posible editar el alimento en este momento."
                ));
            }
        } catch (Exception e) {
            respuesta.setMensaje("Error al editar alimento: " + e.getMessage());
        }

        return respuesta;
    }
    
    public static Respuesta eliminar(Integer idAlimento) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        String url = Constantes.URL_WS + "alimento/dar-baja/" + idAlimento;

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
                        "No es posible eliminar el alimento en este momento."
                ));
            }
        } catch (Exception e) {
            respuesta.setMensaje("Error al eliminar alimento: " + e.getMessage());
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
