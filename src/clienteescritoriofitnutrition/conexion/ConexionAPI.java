package clienteescritoriofitnutrition.conexion;

import clienteescritoriofitnutrition.pojo.RespuestaHTTP;
import clienteescritoriofitnutrition.utilidad.Constantes;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConexionAPI {
    
    public static RespuestaHTTP peticionBody(String URL, String metodoHTTP, String parametros, String contentType){
        RespuestaHTTP respuesta = new RespuestaHTTP();
        try{
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection)urlWS.openConnection();
            conexionHTTP.setRequestMethod(metodoHTTP);
            conexionHTTP.setRequestProperty("Content-Type", contentType);
            conexionHTTP.setDoOutput(true);
            OutputStream os = conexionHTTP.getOutputStream();
            os.write(parametros.getBytes());
            os.flush();
            os.close();
            int codigo = conexionHTTP.getResponseCode();
            
            if (codigo == HttpURLConnection.HTTP_OK){
                respuesta.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            }
            respuesta.setCodigo(codigo);
        } catch (MalformedURLException e){
            respuesta.setCodigo(Constantes.ERROR_MALFORMED_URL);
            respuesta.setContenido(e.getMessage());
        } catch (IOException e) {
            respuesta.setCodigo(Constantes.ERROR_PETICION);
            respuesta.setContenido(e.getMessage());
        }
        return respuesta; 
    }
    
    public static RespuestaHTTP peticionSinBody(String URL, String metodoHTTP){
        RespuestaHTTP res = new RespuestaHTTP(); 
        try{
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection)urlWS.openConnection(); 
            conexionHTTP.setRequestMethod(metodoHTTP);
            int codigo = conexionHTTP.getResponseCode(); 
            //codigo == 200 
            if(codigo ==HttpURLConnection.HTTP_OK){
                res.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            }
            res.setCodigo(codigo);
        }catch(MalformedURLException e){
            res.setCodigo(Constantes.ERROR_PETICION);
            res.setContenido(e.getMessage());
        }catch(IOException e){
            res.setCodigo(Constantes.ERROR_MALFORMED_URL);
            res.setContenido(e.getMessage());
        }
        return res; 
    }
    
    public static RespuestaHTTP peticionGET(String URL ){
        RespuestaHTTP res = new RespuestaHTTP(); 
        try{
            URL urlWS = new URL(URL);
            HttpURLConnection conexionHTTP = (HttpURLConnection)urlWS.openConnection(); 
            int codigo = conexionHTTP.getResponseCode(); 
            //codigo == 200 
            if(codigo ==HttpURLConnection.HTTP_OK){
                res.setContenido(Utilidades.streamToString(conexionHTTP.getInputStream()));
            }
            res.setCodigo(codigo);
        }catch(MalformedURLException e){
            res.setCodigo(Constantes.ERROR_PETICION);
            res.setContenido(e.getMessage());
        }catch(IOException e){
            res.setCodigo(Constantes.ERROR_MALFORMED_URL);
            res.setContenido(e.getMessage());
        }
        return res; 
    }
    
    public static RespuestaHTTP peticionBytes(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        int codigo = conn.getResponseCode();

        InputStream in = (codigo >= 200 && codigo < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        byte[] bytes = leerBytes(in);

        RespuestaHTTP r = new RespuestaHTTP();
        r.setCodigo(codigo);
        r.setContenidoBytes(bytes); // agrega este campo en tu POJO RespuestaHTTP
        return r;
    }

    private static byte[] leerBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = in.read(buffer)) != -1) baos.write(buffer, 0, n);
        return baos.toByteArray();
    }

}
