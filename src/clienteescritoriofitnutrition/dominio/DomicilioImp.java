package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.pojo.Domicilio;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.net.HttpURLConnection;

public class DomicilioImp {

    public static Respuesta registrar(Domicilio domicilio) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "domicilio/registrar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(domicilio);

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
                        "No es posible registrar el domicilio en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al registrar el domicilio: " + e.getMessage());
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