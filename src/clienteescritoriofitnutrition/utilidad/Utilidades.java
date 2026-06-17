package clienteescritoriofitnutrition.utilidad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

public class Utilidades {
    
    public static String streamToString(InputStream input) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        String inputLine;
        StringBuffer respuestaEntrada = new StringBuffer();
        
        while( (inputLine = in.readLine()) != null){
            respuestaEntrada.append(inputLine);
        }
        
        in.close();
        return respuestaEntrada.toString();
       }
    
    public static void mostrarAlertaSimple(String titulo, String contenido, Alert.AlertType tipo){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
    
    public static boolean mostrarAlertaConfirmacion(String titulo, String contenido){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.setHeaderText(null);
        Optional<ButtonType> btnSeleccion = alerta.showAndWait();
        return (btnSeleccion.get() == ButtonType.OK);
    }
    
    public static String mostrarDialogoEntrada(String titulo, String mensaje){
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle(titulo);
        dialogo.setHeaderText(null);
        dialogo.setContentText(mensaje);
        Optional<String> respuesta = dialogo.showAndWait();
        if (respuesta.isPresent() && !respuesta.get().trim().isEmpty()) {
            return respuesta.get().trim();
        }
        
        return null; 
    }
    
    public static String obtenerMensajeErrorHTTP(int codigo, String mensajeDefault) {
        switch (codigo) {
            case Constantes.ERROR_MALFORMED_URL:
                return Constantes.MSJ_ERROR_URL;
            case Constantes.ERROR_PETICION:
                return Constantes.MSJ_ERROR_PETICION;
            case HttpURLConnection.HTTP_BAD_REQUEST:
                return "Campos en formato incorrecto, por favor verifica la información enviada";
            default:
                return mensajeDefault;
        }
    }

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");
    private static final Pattern PATRON_TELEFONO = Pattern.compile("\\d{10}");

    public static boolean esCorreoValido(String correo) {
        return correo != null && PATRON_CORREO.matcher(correo.trim()).matches();
    }

    public static boolean esTelefonoValido(String telefono) {
        return telefono != null && PATRON_TELEFONO.matcher(telefono.trim()).matches();
    }

    // Valida la fecha de nacimiento: no nula, no futura y edad entre edadMinima y 120 anios.
    public static boolean esFechaNacimientoValida(LocalDate fechaNacimiento, int edadMinima) {
        if (fechaNacimiento == null) {
            return false;
        }
        LocalDate hoy = LocalDate.now();
        if (fechaNacimiento.isAfter(hoy)) {
            return false;
        }
        int edad = Period.between(fechaNacimiento, hoy).getYears();
        return edad >= edadMinima && edad <= 120;
    }

}
