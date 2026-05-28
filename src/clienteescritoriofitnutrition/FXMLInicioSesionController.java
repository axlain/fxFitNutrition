package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.AutenticarImp;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField pfContrasena;
    @FXML
    private Label lbError;

    private double desplazamientoX;
    private double desplazamientoY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void clicIniciarSesion(ActionEvent event) {
        lbError.setText("");
        String usuario = tfUsuario.getText();
        String password = pfContrasena.getText();

        if (usuario == null || usuario.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            lbError.setText("Por favor, ingresa tu numero de personal y contrasena.");
            return;
        }

        verificarCredenciales(usuario.trim(), password.trim());
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        tfUsuario.clear();
        pfContrasena.clear();
        lbError.setText("");
    }

    @FXML
    private void clickCerrarAplicacion(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void clickMinimizar(ActionEvent event) {
        Stage stage = (Stage) tfUsuario.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void clickMaximizar(ActionEvent event) {
        Stage stage = (Stage) tfUsuario.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void iniciarArrastre(MouseEvent event) {
        Stage stage = (Stage) tfUsuario.getScene().getWindow();
        desplazamientoX = stage.getX() - event.getScreenX();
        desplazamientoY = stage.getY() - event.getScreenY();
    }

    @FXML
    private void arrastrarVentana(MouseEvent event) {
        Stage stage = (Stage) tfUsuario.getScene().getWindow();
        if (!stage.isMaximized()) {
            stage.setX(event.getScreenX() + desplazamientoX);
            stage.setY(event.getScreenY() + desplazamientoY);
        }
    }

    private void verificarCredenciales(String usuario, String password) {
        RSAutenticar respuesta = AutenticarImp.login(usuario, password);

        if (respuesta != null && !respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Ingreso exitoso", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            irPantallaInicio(respuesta);
        } else {
            Utilidades.mostrarAlertaSimple("Credenciales incorrectas", "El numero de personal o la contrasena son incorrectos.", Alert.AlertType.ERROR);
        }
    }

    private void irPantallaInicio(RSAutenticar sesion) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("FXMLPrincipal.fxml"));
            Parent vista = cargador.load();
            FXMLPrincipalController controlador = cargador.getController();
            controlador.inicializarSesion(sesion);

            Scene escenaPrincipal = new Scene(vista);
            Stage stPrincipal = (Stage) tfUsuario.getScene().getWindow();
            stPrincipal.setScene(escenaPrincipal);
            stPrincipal.setTitle("FIT NUTRITION - Inicio");
            stPrincipal.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error de navegacion", "No se pudo cargar la pantalla principal.", Alert.AlertType.ERROR);
        }
    }
}
