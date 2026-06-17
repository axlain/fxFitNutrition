package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Medico;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLAdministracionMedicosController implements Initializable, INotificador {

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
    private Button btnReasignar;
    @FXML
    private Button btnDarBaja;
    @FXML
    private TableView<Medico> tvMedicos;
    @FXML
    private TableColumn tcNumeroPersonal;
    @FXML
    private TableColumn tcNombre;
    @FXML
    private TableColumn tcCedula;
    @FXML
    private TableColumn tcCorreo;
    @FXML
    private TableColumn tcTelefono;
    @FXML
    private TableColumn tcEstatus;

    private ObservableList<Medico> medicos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionMedicos();
    }

    private void configurarTabla() {
        tcNumeroPersonal.setCellValueFactory(new PropertyValueFactory("numeroPersonal"));
        tcNombre.setCellValueFactory(new PropertyValueFactory("nombreCompleto"));
        tcCedula.setCellValueFactory(new PropertyValueFactory("cedulaProfesional"));
        tcCorreo.setCellValueFactory(new PropertyValueFactory("correoUsuario"));
        tcTelefono.setCellValueFactory(new PropertyValueFactory("telefonoUsuario"));
        tcEstatus.setCellValueFactory(new PropertyValueFactory("estatusUsuario"));
        tvMedicos.setPlaceholder(new Label("Sin resultados"));
    }

    private void cargarInformacionMedicos() {
        List<Medico> lista = MedicoImp.obtenerTodos();
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Sin conexion", "No fue posible cargar los medicos.", Alert.AlertType.WARNING);
        }
        medicos = FXCollections.observableArrayList(lista);
        tvMedicos.setItems(medicos);
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String filtro = tfBuscar.getText();
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarInformacionMedicos();
            return;
        }

        List<Medico> lista = MedicoImp.buscar(filtro.trim());
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Busqueda", "No fue posible realizar la busqueda.", Alert.AlertType.WARNING);
        }
        tvMedicos.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void clickLimpiar(ActionEvent event) {
        tfBuscar.clear();
        cargarInformacionMedicos();
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
        irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Medico medico = tvMedicos.getSelectionModel().getSelectedItem();
        if (medico == null) {
            Utilidades.mostrarAlertaSimple("Seleccion requerida", "Selecciona un medico para editar.", Alert.AlertType.WARNING);
            return;
        }
        Medico medicoCompleto = MedicoImp.obtenerPorId(medico.getIdMedico());
        if (medicoCompleto == null) {
            Utilidades.mostrarAlertaSimple("Error", "No se pudo obtener la informacion del medico.", Alert.AlertType.ERROR);
            return;
        }
        irFormulario(medicoCompleto);
    }

    @FXML
    private void clickReasignar(ActionEvent event) {
        Medico medico = tvMedicos.getSelectionModel().getSelectedItem();
        irReasignarPacientes(medico);
    }

    @FXML
    private void clickDarBaja(ActionEvent event) {
        Medico medico = tvMedicos.getSelectionModel().getSelectedItem();
        if (medico == null) {
            Utilidades.mostrarAlertaSimple("Seleccion requerida", "Selecciona un medico para dar de baja.", Alert.AlertType.WARNING);
            return;
        }
        irReasignarPacientes(medico);
    }

    private void irFormulario(Medico medico) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioMedico.fxml"));
            Parent vista = loader.load();
            FXMLFormularioMedicoController controlador = loader.getController();
            controlador.inicializarDatos(medico, this);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(medico == null ? "Registrar medico" : "Editar medico");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el formulario de medico.", Alert.AlertType.ERROR);
        }
    }

    private void irReasignarPacientes(Medico medico) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLReasignarPacientes.fxml"));
            Parent vista = loader.load();
            FXMLReasignarPacientesController controlador = loader.getController();
            controlador.inicializarDatos(medico, this);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Reasignar pacientes");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir la reasignacion de pacientes.", Alert.AlertType.ERROR);
        }
    }

    public void darBajaDirecta(Medico medico) {
        if (medico == null || medico.getIdMedico() == null) {
            return;
        }

        boolean confirmado = Utilidades.mostrarAlertaConfirmacion(
                "Dar de baja medico",
                "Deseas dar de baja al medico seleccionado?"
        );
        if (!confirmado) {
            return;
        }

        Respuesta respuesta = MedicoImp.darBaja(medico.getIdMedico());
        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operacion exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        if (!respuesta.isError()) {
            cargarInformacionMedicos();
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionMedicos();
    }
}
