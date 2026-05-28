package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Usuario;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class FXMLPrincipalController implements Initializable, INotificador {

    @FXML
    private Label lbTitulo;
    @FXML
    private Label lbSesion;
    @FXML
    private Button btnMedicos;
    @FXML
    private Button btnPacientes;
    @FXML
    private Button btnCitas;
    @FXML
    private Button btnConsultas;
    @FXML
    private Button btnDietas;
    @FXML
    private Button btnAlimentos;
    @FXML
    private BorderPane bpContenedor;

    private RSAutenticar sesion;
    private double desplazamientoX;
    private double desplazamientoY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void inicializarSesion(RSAutenticar sesion) {
        this.sesion = sesion;
        cargarDatosSesion();
        aplicarPermisos();
    }

    private void cargarDatosSesion() {
        String nombre = "Usuario";
        String tipo = "";

        if (sesion != null) {
            tipo = sesion.getTipoUsuario() != null ? sesion.getTipoUsuario() : "";
            if (sesion.getAdministrador() != null) {
                nombre = obtenerNombreUsuario(sesion.getAdministrador().getUsuario());
            } else if (sesion.getMedico() != null) {
                nombre = obtenerNombreUsuario(sesion.getMedico().getUsuario());
            }
        }

        lbTitulo.setText("Dashboard");
        lbSesion.setText(nombre + (tipo.isEmpty() ? "" : " | " + tipo));
    }

    @FXML
    private void clickCerrarAplicacion(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void clickMinimizar(ActionEvent event) {
        Stage stage = (Stage) bpContenedor.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void clickMaximizar(ActionEvent event) {
        Stage stage = (Stage) bpContenedor.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void iniciarArrastre(MouseEvent event) {
        Stage stage = (Stage) bpContenedor.getScene().getWindow();
        desplazamientoX = stage.getX() - event.getScreenX();
        desplazamientoY = stage.getY() - event.getScreenY();
    }

    @FXML
    private void arrastrarVentana(MouseEvent event) {
        Stage stage = (Stage) bpContenedor.getScene().getWindow();
        if (!stage.isMaximized()) {
            stage.setX(event.getScreenX() + desplazamientoX);
            stage.setY(event.getScreenY() + desplazamientoY);
        }
    }

    private String obtenerNombreUsuario(Usuario usuario) {
        if (usuario == null) {
            return "Usuario";
        }
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellido = usuario.getApellidoPaterno() != null ? usuario.getApellidoPaterno() : "";
        String nombreCompleto = (nombre + " " + apellido).trim();
        return nombreCompleto.isEmpty() ? "Usuario" : nombreCompleto;
    }

    private void aplicarPermisos() {
        boolean esAdministrador = esAdministrador();
        btnMedicos.setDisable(!esAdministrador);
        btnPacientes.setDisable(false);
        btnCitas.setDisable(false);
        btnConsultas.setDisable(false);
        btnDietas.setDisable(false);
        btnAlimentos.setDisable(false);
    }

    private boolean esAdministrador() {
        if (sesion == null || sesion.getAdministrador() != null) {
            return true;
        }
        String tipo = sesion.getTipoUsuario();
        return tipo != null && tipo.toLowerCase().contains("administrador");
    }

    @FXML
    private void clickMedicos(ActionEvent event) {
        cargarModulo("FXMLAdministracionMedicos.fxml", "No se pudo cargar el modulo de medicos.");
    }

    @FXML
    private void clickPacientes(ActionEvent event) {
        cargarModulo("FXMLAdministracionPacientes.fxml", "No se pudo cargar el modulo de pacientes.");
    }

    @FXML
    private void clickCitas(ActionEvent event) {
        cargarModulo("FXMLAgendaCitas.fxml", "El modulo de citas esta pendiente de integracion.");
    }

    @FXML
    private void clickConsultas(ActionEvent event) {
        cargarModulo("FXMLAdministracionConsultas.fxml", "El modulo de consultas esta pendiente de integracion.");
    }

    @FXML
    private void clickDietas(ActionEvent event) {
        cargarModulo("FXMLAdministracionDietas.fxml", "El modulo de dietas esta pendiente de integracion.");
    }

    @FXML
    private void clickAlimentos(ActionEvent event) {
        cargarModulo("FXMLAdministracionAlimentos.fxml", "El modulo de alimentos esta pendiente de integracion.");
    }

    private void cargarModulo(String nombreFXML, String mensajeError) {
        try {
            URL recurso = getClass().getResource(nombreFXML);
            if (recurso == null) {
                Utilidades.mostrarAlertaSimple("Modulo no disponible", mensajeError, Alert.AlertType.INFORMATION);
                return;
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            Parent vista = loader.load();
            bpContenedor.setCenter(vista);
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", mensajeError, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickCerrarSesion(ActionEvent event) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource("FXMLInicioSesion.fxml"));
            Scene escenaLogin = new Scene(vista);
            Stage stage = (Stage) bpContenedor.getScene().getWindow();
            stage.setScene(escenaLogin);
            stage.setTitle("Login");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo cerrar la sesion.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
    }
}
