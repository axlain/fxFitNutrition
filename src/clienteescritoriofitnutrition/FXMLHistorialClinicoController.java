package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.ConsultaImp;
import clienteescritoriofitnutrition.pojo.Consulta;
import clienteescritoriofitnutrition.pojo.Paciente;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FXMLHistorialClinicoController implements Initializable {

    @FXML
    private Label lbPaciente;
    @FXML
    private Label lbCodigo;
    @FXML
    private TableView<Consulta> tvConsultas;
    @FXML
    private TableColumn tcFecha;
    @FXML
    private TableColumn tcPeso;
    @FXML
    private TableColumn tcEstatura;
    @FXML
    private TableColumn tcTalla;
    @FXML
    private TableColumn tcImc;
    @FXML
    private TableColumn tcObservaciones;

    private Paciente paciente;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    public void inicializarDatos(Paciente paciente) {
        this.paciente = paciente;
        cargarDatosPaciente();
        cargarHistorial();
    }

    private void configurarTabla() {
        tcFecha.setCellValueFactory(new PropertyValueFactory("fechaHora"));
        tcPeso.setCellValueFactory(new PropertyValueFactory("peso"));
        tcEstatura.setCellValueFactory(new PropertyValueFactory("estatura"));
        tcTalla.setCellValueFactory(new PropertyValueFactory("talla"));
        tcImc.setCellValueFactory(new PropertyValueFactory("imc"));
        tcObservaciones.setCellValueFactory(new PropertyValueFactory("observaciones"));
    }

    private void cargarDatosPaciente() {
        if (paciente == null) {
            lbPaciente.setText("Paciente no seleccionado");
            lbCodigo.setText("");
            return;
        }

        lbPaciente.setText(paciente.getNombreCompleto());
        lbCodigo.setText("Codigo de acceso: " + (paciente.getCodigoAcceso() != null ? paciente.getCodigoAcceso() : ""));
    }

    private void cargarHistorial() {
        if (paciente == null || paciente.getIdPaciente() == null) {
            tvConsultas.setItems(FXCollections.observableArrayList(new ArrayList<Consulta>()));
            return;
        }

        List<Consulta> historial = ConsultaImp.obtenerHistorialPaciente(paciente.getIdPaciente());
        if (historial == null) {
            historial = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Historial clinico", "No fue posible cargar el historial clinico.", Alert.AlertType.WARNING);
        }
        tvConsultas.setItems(FXCollections.observableArrayList(historial));
    }

    @FXML
    private void clickCerrar(ActionEvent event) {
        Stage stage = (Stage) tvConsultas.getScene().getWindow();
        stage.close();
    }
}
