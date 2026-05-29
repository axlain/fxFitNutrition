package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.pojo.Consulta;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

public class ConsultaImp {

    public static Respuesta registrarConsulta(Consulta consulta) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "consulta/registrar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(consulta);

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
                        "No es posible registrar la consulta en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al registrar la consulta: " + e.getMessage());
        }

        return respuesta;
    }

    public static List<Consulta> obtenerHistorialPaciente(Integer idPaciente) {
        String url = Constantes.URL_WS + "consulta/historial/" + idPaciente;

        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Consulta>>() {
                }.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    public static Consulta obtenerDetalleConsulta(Integer idConsulta) {
        String url = Constantes.URL_WS + "consulta/obtener/" + idConsulta;

        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(resp.getContenido(), Consulta.class);
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

    public static List<Consulta> obtenerTodas() {
        String url = Constantes.URL_WS + "consulta/obtener-todas";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);
        
        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Consulta>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static List<Consulta> obtenerPorIdPaciente(Integer idPaciente) {
        return obtenerHistorialPaciente(idPaciente);
    }

    public static Respuesta registrar(Consulta consulta) {
        return registrarConsulta(consulta);
    }

    public static Respuesta editar(Consulta consulta) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);
        String url = Constantes.URL_WS + "consulta/editar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(consulta);

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
                        "No es posible editar la consulta en este momento."
                ));
            }
        } catch (Exception e) {
            respuesta.setMensaje("Error al editar la consulta: " + e.getMessage());
        }

        return respuesta;
    }

}
