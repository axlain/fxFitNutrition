package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.AutenticarImp;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
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
    private ComboBox<String> cbTipoUsuario;
    @FXML
    private Label lbError;

    private ObservableList<String> tiposUsuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBox();
    }    

    private void configurarComboBox() {
        // Agregamos los 3 roles que soporta tu backend
        tiposUsuario = FXCollections.observableArrayList("Administrador", "Médico", "Paciente");
        cbTipoUsuario.setItems(tiposUsuario);
    }

    @FXML
    private void clicIniciarSesion(ActionEvent event) {
        lbError.setText(""); // Limpiar errores previos
        String usuario = tfUsuario.getText();
        String password = pfContrasena.getText();
        String tipoUsuario = cbTipoUsuario.getValue();

        // Validación de campos vacíos
        if (usuario == null || usuario.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            tipoUsuario == null) {
            
            lbError.setText("Por favor, ingresa tus credenciales y selecciona tu rol.");
            return;
        }

        verificarCredenciales(usuario.trim(), password.trim(), tipoUsuario);
    }

    private void verificarCredenciales(String usuario, String password, String tipoUsuario) {
        RSAutenticar respuesta = new RSAutenticar();

        // Dependiendo del ComboBox, invocamos el API correcta
        switch (tipoUsuario) {
            case "Administrador":
                respuesta = AutenticarImp.loginAdministrador(usuario, password);
                break;
            case "Médico":
                respuesta = AutenticarImp.loginMedico(usuario, password);
                break;
        }

        if (!respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Ingreso exitoso", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
            irPantallaInicio(respuesta);
        } else {
            Utilidades.mostrarAlertaSimple("Credenciales incorrectas", respuesta.getMensaje(), Alert.AlertType.ERROR);
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