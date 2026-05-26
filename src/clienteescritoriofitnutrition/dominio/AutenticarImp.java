package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.net.HttpURLConnection;

public class AutenticarImp {
    
    public static RSAutenticar login(String numeroPersonal, String contrasena) {
        String parametros = "numero_personal=" + numeroPersonal + "&contrasena=" + contrasena;
        
        // 1. Intentar iniciar sesión como Administrador
        String urlAdmin = Constantes.URL_WS + "autenticar/administrador";
        RSAutenticar respuesta = realizarPeticionLogin(urlAdmin, parametros);
        
        // 2. Si falla como Administrador, intentar como Médico
        if (respuesta.isError()) {
            String urlMedico = Constantes.URL_WS + "autenticar/medico";
            respuesta = realizarPeticionLogin(urlMedico, parametros);
        }
        
        return respuesta;
    }
    
    private static RSAutenticar realizarPeticionLogin(String url, String parametros) {
        RSAutenticar respuesta = new RSAutenticar();
        
        RespuestaHTTP respuestaAPI = ConexionAPI.peticionBody(
                url,
                Constantes.PETICION_POST,
                parametros,
                Constantes.APPLICATION_FORM
        );

        if (respuestaAPI.getCodigo() == HttpURLConnection.HTTP_OK) {
            try {
                Gson gson = new Gson();
                respuesta = gson.fromJson(respuestaAPI.getContenido(), RSAutenticar.class);
            } catch (Exception e) {
                respuesta.setError(true);
                respuesta.setMensaje("Error al procesar la respuesta del servidor.");
            }
        } else {
            respuesta.setError(true);
            respuesta.setMensaje(
                Utilidades.obtenerMensajeErrorHTTP(
                    respuestaAPI.getCodigo(), 
                    "No es posible verificar las credenciales en este momento."
                )
            );
        }
        
        return respuesta;
    }
}