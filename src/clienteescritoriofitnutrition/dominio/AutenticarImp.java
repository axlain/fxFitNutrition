package clienteescritoriofitnutrition.dominio;

import com.google.gson.Gson;
import clienteescritoriofitnutrition.conexion.ConexionAPI;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.net.HttpURLConnection;

public class AutenticarImp {
    
    public static RSAutenticar loginAdministrador(String numeroPersonal, String contrasena) {
        String parametros = "numero_personal=" + numeroPersonal + "&contrasena=" + contrasena;
        String url = Constantes.URL_WS + "autenticar/administrador";
        return realizarPeticionLogin(url, parametros);
    }
    
    public static RSAutenticar loginMedico(String numeroPersonal, String contrasena) {
        String parametros = "numero_personal=" + numeroPersonal + "&contrasena=" + contrasena;
        String url = Constantes.URL_WS + "autenticar/medico";
        return realizarPeticionLogin(url, parametros);
    }
    
    private static RSAutenticar realizarPeticionLogin(String url, String parametros) {
        RSAutenticar respuesta = new RSAutenticar();
        
        // Se hace la petición usando la clase que me enviaste
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