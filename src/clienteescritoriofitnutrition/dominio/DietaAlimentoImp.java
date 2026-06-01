package clienteescritoriofitnutrition.dominio;

import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.DietaAlimento;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

public class DietaAlimentoImp {

    public static List<DietaAlimento> obtenerPorDieta(Integer idDieta) {
        if (idDieta == null || idDieta <= 0) {
            return null;
        }

        String url = Constantes.URL_WS + "dieta-alimento/obtener-por-dieta/" + idDieta;
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            Type listType = new TypeToken<List<DietaAlimento>>() {}.getType();
            return new Gson().fromJson(resp.getContenido(), listType);
        }

        return null;
    }

    public static DietaAlimento obtenerPorId(Integer idDietaAlimento) {
        if (idDietaAlimento == null || idDietaAlimento <= 0) {
            return null;
        }

        String url = Constantes.URL_WS + "dieta-alimento/obtener-por-id/" + idDietaAlimento;
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            return new Gson().fromJson(resp.getContenido(), DietaAlimento.class);
        }

        return null;
    }

    public static Respuesta registrar(DietaAlimento dietaAlimento) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        if (dietaAlimento == null) {
            respuesta.setMensaje("La información del alimento de la dieta es obligatoria.");
            return respuesta;
        }

        String url = Constantes.URL_WS + "dieta-alimento/registrar";

        try {
            String json = new Gson().toJson(dietaAlimento);

            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_POST,
                    json,
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            }

            respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                    resp.getCodigo(),
                    "No es posible agregar el alimento a la dieta en este momento."
            ));

        } catch (Exception e) {
            respuesta.setMensaje("Error de conexión al agregar alimento a dieta: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta editar(DietaAlimento dietaAlimento) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        if (dietaAlimento == null 
                || dietaAlimento.getIdDietaAlimento() == null 
                || dietaAlimento.getIdDietaAlimento() <= 0) {
            respuesta.setMensaje("El identificador de la relación dieta-alimento es obligatorio.");
            return respuesta;
        }

        String url = Constantes.URL_WS + "dieta-alimento/editar";

        try {
            String json = new Gson().toJson(dietaAlimento);

            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_PUT,
                    json,
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            }

            respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                    resp.getCodigo(),
                    "No es posible editar el alimento de la dieta en este momento."
            ));

        } catch (Exception e) {
            respuesta.setMensaje("Error de conexión al editar alimento de dieta: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta quitarAlimentoDeDieta(Integer idDietaAlimento) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        if (idDietaAlimento == null || idDietaAlimento <= 0) {
            respuesta.setMensaje("El identificador de la relación dieta-alimento es obligatorio.");
            return respuesta;
        }

        String url = Constantes.URL_WS 
                + "dieta-alimento/quitar-alimento/" 
                + idDietaAlimento;

        try {
            RespuestaHTTP resp = ConexionAPI.peticionSinBody(
                    url,
                    Constantes.PETICION_DELETE
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            }

            respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                    resp.getCodigo(),
                    "No es posible quitar el alimento de la dieta en este momento."
            ));

        } catch (Exception e) {
            respuesta.setMensaje("Error de conexión al quitar alimento de dieta: " + e.getMessage());
        }

        return respuesta;
    }

    private static Respuesta parsearRespuesta(String json) {
        try {
            return new Gson().fromJson(json, Respuesta.class);
        } catch (Exception e) {
            Respuesta r = new Respuesta();
            r.setError(true);
            r.setMensaje("Error al procesar la respuesta del servidor.");
            return r;
        }
    }
}