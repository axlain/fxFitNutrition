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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class CitaImp {
    
    public static List<Cita> obtenerPorIdPaciente(Integer idPaciente) {
        if (idPaciente == null || idPaciente <= 0) {
            return new ArrayList<>();
        }

        String url = Constantes.URL_WS + "cita/obtener-vigentes-paciente/" + idPaciente;

        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Cita>>() {
                }.getType();

                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }

    public static List<Cita> obtenerTodosPorPaciente(Integer idPaciente) {
        if (idPaciente == null || idPaciente <= 0) {
            return new ArrayList<>();
        }

        String url = Constantes.URL_WS + "cita/obtener-por-paciente/" + idPaciente;

        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Cita>>() {}.getType();
                return gson.fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }

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

    public static List<String> obtenerHorasDisponibles(Integer idMedico, String fecha, Integer idCita) {
        if (idMedico == null || idMedico <= 0 || fecha == null || fecha.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try {
            String fechaCodificada = URLEncoder.encode(fecha.trim(), StandardCharsets.UTF_8.name());
            String url = Constantes.URL_WS + "cita/horas-disponibles?idMedico=" + idMedico + "&fecha=" + fechaCodificada;

            if (idCita != null && idCita > 0) {
                url += "&idCita=" + idCita;
            }

            RespuestaHTTP resp = ConexionAPI.peticionGET(url);

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {}.getType();
                List<String> horas = gson.fromJson(resp.getContenido(), listType);
                return horas != null ? horas : new ArrayList<>();
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }

        return new ArrayList<>();
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

    public static HashMap<String, Object> obtenerTodas() {
        HashMap<String, Object> respuesta = new LinkedHashMap<>();
        String url = Constantes.URL_WS + "cita/obtener-todas";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Cita>>() {}.getType();
            respuesta.put(Constantes.KEY_ERROR, false);
            respuesta.put(Constantes.KEY_LISTA, gson.fromJson(resp.getContenido(), listType));
        } else {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(Constantes.KEY_MENSAJE, Utilidades.obtenerMensajeErrorHTTP(
                    resp.getCodigo(), "No fue posible obtener las citas en este momento."));
        }
        return respuesta;
    }

    public static HashMap<String, Object> obtenerPorFecha(String fecha) {
        HashMap<String, Object> respuesta = new LinkedHashMap<>();
        if (fecha == null) fecha = "";
        try {
            String parametro = URLEncoder.encode(fecha.trim(), StandardCharsets.UTF_8.name());
            String url = Constantes.URL_WS + "cita/obtener-por-fecha/" + parametro;
            RespuestaHTTP resp = ConexionAPI.peticionGET(url);

            if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Cita>>() {}.getType();
                respuesta.put(Constantes.KEY_ERROR, false);
                respuesta.put(Constantes.KEY_LISTA, gson.fromJson(resp.getContenido(), listType));
            } else {
                respuesta.put(Constantes.KEY_ERROR, true);
                respuesta.put(Constantes.KEY_MENSAJE, Utilidades.obtenerMensajeErrorHTTP(
                        resp.getCodigo(), "No fue posible obtener las citas por fecha."));
            }
        } catch (Exception e) {
            respuesta.put(Constantes.KEY_ERROR, true);
            respuesta.put(Constantes.KEY_MENSAJE, "Error al obtener las citas por fecha: " + e.getMessage());
        }
        return respuesta;
    }

    public static Respuesta editar(Cita cita) {
        return reagendar(cita);
    }
}
