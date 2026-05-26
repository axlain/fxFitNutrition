package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.AutenticarImp;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField pfContrasena;
    @FXML
    private Label lbError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicializaciones adicionales si son necesarias
    }    

    @FXML
    private void clicIniciarSesion(ActionEvent event) {
        lbError.setText(""); // Limpiar errores previos
        String usuario = tfUsuario.getText();
        String password = pfContrasena.getText();

        // Validación de campos vacíos
        if (usuario == null || usuario.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            
            lbError.setText("Por favor, ingresa tu número de personal y contraseña.");
            return;
        }

        verificarCredenciales(usuario.trim(), password.trim());
    }

    private void verificarCredenciales(String usuario, String password) {
        // Se llama a un único método que se encarga de identificar el rol
        RSAutenticar respuesta = AutenticarImp.login(usuario, password);

        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Ingreso exitoso", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            irPantallaInicio(respuesta);
        } else {
            Utilidades.mostrarAlertaSimple("Credenciales incorrectas", "El número de personal o la contraseña son incorrectos.", Alert.AlertType.ERROR);
        }
    }

    private void irPantallaInicio(RSAutenticar sesion) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLPrincipal.fxml"));
            Parent vista = cargador.load();
            
            /* * NOTA: Cuando tengas creado tu FXMLPrincipalController, descomenta estas líneas 
             * para enviarle la sesión y saber quién entró.
             */
            // FXMLPrincipalController controlador = cargador.getController();
            // controlador.inicializarSesion(sesion);
            
            Scene escenaPrincipal = new Scene(vista);
            Stage stPrincipal = (Stage) tfUsuario.getScene().getWindow();
            stPrincipal.setScene(escenaPrincipal);
            stPrincipal.setTitle("FITNUTRITION - Inicio");
            stPrincipal.show();
            
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error de navegación", "No se pudo cargar la pantalla principal.", Alert.AlertType.ERROR);
        }   
    }
}