package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.pojo.Administrador;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

public class AdministradorImp {

    public static List<Administrador> obtenerTodos() {
        String url = Constantes.URL_WS + "administrador/obtener-todos";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Administrador>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static Respuesta registrar(Administrador admin) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "administrador/registrar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(admin);

            RespuestaHTTP resp = ConexionAPI.peticionBody(
                    url,
                    Constantes.PETICION_POST, // Asegúrate de tener "POST" en tu clase Constantes
                    json,
                    Constantes.APPLICATION_JSON // Asegúrate de tener "application/json" en Constantes
            );

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                return parsearRespuesta(resp.getContenido());
            } else {
                respuesta.setMensaje(Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(),
                        "No es posible registrar al administrador en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al registrar administrador: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta editar(Administrador admin) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "administrador/editar";

        try {
            Gson gson = new Gson();
            String json = gson.toJson(admin);

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
                        "No es posible editar al administrador en este momento."
                ));
            }

        } catch (Exception e) {
            respuesta.setMensaje("Error al editar administrador: " + e.getMessage());
        }

        return respuesta;
    }

    public static Respuesta cambiarContrasena(Integer idAdministrador, String contrasenaActual, String nuevaContrasena) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(true);

        String url = Constantes.URL_WS + "administrador/cambiar-contrasena";

        try {
            // Se declara la clase interna y se asignan los valores posteriormente
            // para evitar el error "self-reference in initializer"
            class Cambio {
                public Integer idAdministrador;
                public String contrasenaActual;
                public String nuevaContrasena;
            }

            Cambio cambio = new Cambio();
            cambio.idAdministrador = idAdministrador;
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