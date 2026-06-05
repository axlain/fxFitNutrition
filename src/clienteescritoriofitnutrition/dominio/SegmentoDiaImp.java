package clienteescritoriofitnutrition.dominio;

import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.pojo.SegmentoDia;
import clienteescritoriofitnutrition.utilidad.Constantes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class SegmentoDiaImp {

    public static List<SegmentoDia> obtenerTodos() {
        String url = Constantes.URL_WS + "segmento-dia/obtener-todos";
        RespuestaHTTP resp = ConexionAPI.peticionGET(url);

        if (resp.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Type listType = new TypeToken<List<SegmentoDia>>() {}.getType();
                return new Gson().fromJson(resp.getContenido(), listType);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        return new ArrayList<>();
    }
}
