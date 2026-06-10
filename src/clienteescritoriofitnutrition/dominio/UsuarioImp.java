package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.pojo.Usuario;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.net.HttpURLConnection;
import java.util.Base64;

public class UsuarioImp {

    private static class FotoRequest {
        String fotoBase64;
    }

    public static Respuesta subirFoto(Integer idUsuario, byte[] foto) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "usuario/guardar-foto/" + idUsuario;

        try {
            FotoRequest datos = new FotoRequest();
            datos.fotoBase64 = Base64.getEncoder().encodeToString(foto);

            Gson gson = new Gson();
            String jsonFoto = gson.toJson(datos);

            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_PUT,
                    jsonFoto,
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            } else {
                respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(),
                        "No es posible subir la fotografía en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al subir la fotografía: " + e.getMessage());
        }

        return respuesta;
    }

    public static Usuario obtenerFoto(Integer idUsuario) {
        String url = Constantes.URL_WS + "usuario/obtener-foto/" + idUsuario;
        
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                // El backend devuelve un objeto Usuario con la propiedad fotoBase64 llena
                return gson.fromJson(resp.getContenido(), Usuario.class);
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