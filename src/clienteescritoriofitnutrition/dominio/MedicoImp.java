package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.pojo.Medico;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MedicoImp {
    
    public static List<Medico> obtenerTodos() {
        String url = Constantes.URL_WS + "medico/obtener-todos";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Medico>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static Medico obtenerPorId(Integer idMedico) {
        String url = Constantes.URL_WS + "medico/obtener-por-id/" + idMedico;
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);
        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(resp.getContenido(), Medico.class);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static List<Medico> buscar(String filtro) {
        if (filtro == null) filtro = "";
        
        try {
            String parametro = URLEncoder.encode(filtro.trim(), StandardCharsets.UTF_8.name());
            String url = Constantes.URL_WS + "medico/buscar?filtro=" + parametro;
            
            RespuestaHTTP resp = ConexionAPI.peticionGET(url);

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Medico>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            }
        } catch (Exception e) {
            return null;
        }
        
        return null;
    }

    public static Respuesta registrar(Medico medico) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "medico/registrar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(medico);

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
                        "No es posible registrar al médico en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al registrar médico: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta editar(Medico medico) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "medico/editar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(medico);

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
                        "No es posible editar al médico en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al editar médico: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta darBaja(Integer idMedico) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "medico/dar-baja/" + idMedico;

        try {
            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_DELETE, // "DELETE"
                    "", // Cuerpo vacío
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            } else {
                respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(),
                        "No es posible dar de baja al médico en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al dar de baja al médico: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta cambiarContrasena(Integer idMedico, String contrasenaActual, String nuevaContrasena) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "medico/cambiar-contrasena";

        try {
            
            class Cambio {
                public Integer idMedico;
                public String contrasenaActual;
                public String nuevaContrasena;
            }

            Cambio cambio = new Cambio();
            cambio.idMedico = idMedico;
            cambio.contrasenaActual = contrasenaActual;
            cambio.nuevaContrasena = nuevaContrasena;

            Gson gson = new Gson();
            String json = gson.toJson(cambio);

            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_PUT, // "PUT"
                    json,
                    Constantes.APPLICATION_JSON
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            } else {
                respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(),
                        "No es posible cambiar la contraseña en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al cambiar la contraseña: " + e.getMessage());
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