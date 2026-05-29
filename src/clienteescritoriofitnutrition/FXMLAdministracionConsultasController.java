package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.ConsultaImp;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Consulta;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLAdministracionConsultasController implements Initializable, INotificador {

    @FXML
    private TextField tfBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnEditar;
    @FXML
    private TableView<Consulta> tvConsultas;
    @FXML
    private TableColumn tcFechaHora;
    @FXML
    private TableColumn tcPaciente;
    @FXML
    private TableColumn tcMedico;
    @FXML
    private TableColumn tcPeso;
    @FXML
    private TableColumn tcImc;
    @FXML
    private TableColumn tcDieta;

    private ObservableList<Consulta> consultas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionConsultas();
    }

    private void configurarTabla() {
        tcFechaHora.setCellValueFactory(new PropertyValueFactory("fechaHora"));
        tcPaciente.setCellValueFactory(new PropertyValueFactory("nombrePaciente"));
        tcMedico.setCellValueFactory(new PropertyValueFactory("nombreMedico"));
        tcPeso.setCellValueFactory(new PropertyValueFactory("peso"));
        tcImc.setCellValueFactory(new PropertyValueFactory("imc"));
        tcDieta.setCellValueFactory(new PropertyValueFactory("nombreDieta"));
    }

    private void cargarInformacionConsultas() {
        List<Consulta> lista = ConsultaImp.obtenerTodas();
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Sin conexión", "No fue posible cargar las consultas.", Alert.AlertType.WARNING);
        }
        consultas = FXCollections.observableArrayList(lista);
        tvConsultas.setItems(consultas);
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String filtro = tfBuscar.getText();
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarInformacionConsultas();
            return;
        }

        // Simulación: Filtrado local o llamada a la API si existe método buscar
        List<Consulta> lista = ConsultaImp.obtenerPorIdPaciente(Integer.parseInt(filtro)); // Ejemplo
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Búsqueda", "No fue posible realizar la búsqueda.", Alert.AlertType.WARNING);
        }
        tvConsultas.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void clickLimpiar(ActionEvent event) {
        tfBuscar.clear();
        cargarInformacionConsultas();
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
        irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Consulta consulta = obtenerConsultaSeleccionada("Selecciona una consulta para ver o editar.");
        if (consulta != null) {
            irFormulario(consulta);
        }
    }

    private Consulta obtenerConsultaSeleccionada(String mensaje) {
        Consulta consulta = tvConsultas.getSelectionModel().getSelectedItem();
        if (consulta == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", mensaje, Alert.AlertType.WARNING);
        }
        return consulta;
    }

    private void irFormulario(Consulta consulta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioConsulta.fxml"));
            Parent vista = loader.load();
            FXMLFormularioConsultaController controlador = loader.getController();
            controlador.inicializarDatos(consulta, this);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(consulta == null ? "Registrar Consulta" : "Editar Consulta");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el formulario de consulta.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionConsultas();
    }
}
