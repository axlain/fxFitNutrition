package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.PacienteImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Paciente;
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

public class FXMLAdministracionPacientesController implements Initializable, INotificador {

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
    private Button btnDarBaja;
    @FXML
    private Button btnCodigo;
    @FXML
    private Button btnHistorial;
    @FXML
    private TableView<Paciente> tvPacientes;
    @FXML
    private TableColumn tcCodigo;
    @FXML
    private TableColumn tcNombre;
    @FXML
    private TableColumn tcCorreo;
    @FXML
    private TableColumn tcTelefono;
    @FXML
    private TableColumn tcMedico;
    @FXML
    private TableColumn tcEstatus;

    private ObservableList<Paciente> pacientes;
    private RSAutenticar sesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionPacientes();
    }

    public void inicializarSesion(RSAutenticar sesion) {
        this.sesion = sesion;
        cargarInformacionPacientes();
    }

    private Integer obtenerIdMedicoSesion() {
        if (sesion != null && sesion.getMedico() != null) {
            return sesion.getMedico().getIdMedico();
        }
        return null;
    }

    private void configurarTabla() {
        tcCodigo.setCellValueFactory(new PropertyValueFactory("codigoAcceso"));
        tcNombre.setCellValueFactory(new PropertyValueFactory("nombreCompleto"));
        tcCorreo.setCellValueFactory(new PropertyValueFactory("correoUsuario"));
        tcTelefono.setCellValueFactory(new PropertyValueFactory("telefonoUsuario"));
        tcMedico.setCellValueFactory(new PropertyValueFactory("medicoAsignado"));
        tcEstatus.setCellValueFactory(new PropertyValueFactory("estatusUsuario"));
        tvPacientes.setPlaceholder(new Label("Sin resultados"));
    }

    private void cargarInformacionPacientes() {
        List<Paciente> lista = PacienteImp.obtenerTodos();
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Sin conexion", "No fue posible cargar los pacientes.", Alert.AlertType.WARNING);
        }
        pacientes = FXCollections.observableArrayList(filtrarPorMedicoSesion(lista));
        tvPacientes.setItems(pacientes);
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String filtro = tfBuscar.getText();
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarInformacionPacientes();
            return;
        }

        List<Paciente> lista = PacienteImp.buscar(filtro.trim());
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Busqueda", "No fue posible realizar la busqueda.", Alert.AlertType.WARNING);
        }
        tvPacientes.setItems(FXCollections.observableArrayList(filtrarPorMedicoSesion(lista)));
    }

    // Admin: toda la clinica. Medico: solo sus pacientes asignados (mismo criterio que el dashboard).
    private List<Paciente> filtrarPorMedicoSesion(List<Paciente> lista) {
        Integer idMedico = obtenerIdMedicoSesion();
        if (idMedico == null) {
            return lista;
        }
        List<Paciente> mios = new ArrayList<>();
        for (Paciente paciente : lista) {
            if (paciente != null && idMedico.equals(paciente.getIdMedico())) {
                mios.add(paciente);
            }
        }
        return mios;
    }

    @FXML
    private void clickLimpiar(ActionEvent event) {
        tfBuscar.clear();
        cargarInformacionPacientes();
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
        irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Paciente paciente = obtenerPacienteSeleccionado("Selecciona un paciente para editar.");
        if (paciente != null) {
            irFormulario(paciente);
        }
    }

    @FXML
    private void clickDarBaja(ActionEvent event) {
        Paciente paciente = obtenerPacienteSeleccionado("Selecciona un paciente para dar de baja.");
        if (paciente == null) {
            return;
        }

        boolean confirmado = Utilidades.mostrarAlertaConfirmacion(
                "Dar de baja paciente",
                "Deseas dar de baja al paciente seleccionado?"
        );
        if (!confirmado) {
            return;
        }

        Respuesta respuesta = PacienteImp.darBaja(paciente.getIdPaciente());
        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operacion exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        if (!respuesta.isError()) {
            cargarInformacionPacientes();
        }
    }

    @FXML
    private void clickActualizarCodigo(ActionEvent event) {
        Paciente paciente = obtenerPacienteSeleccionado("Selecciona un paciente para generar codigo.");
        if (paciente == null) {
            return;
        }

        Respuesta respuesta = PacienteImp.actualizarCodigoAcceso(paciente.getIdPaciente());
        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Codigo actualizado",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        if (!respuesta.isError()) {
            cargarInformacionPacientes();
        }
    }

    @FXML
    private void clickHistorial(ActionEvent event) {
        Paciente paciente = obtenerPacienteSeleccionado("Selecciona un paciente para ver historial.");
        if (paciente == null) {
            return;
        }
        irHistorial(paciente);
    }

    private Paciente obtenerPacienteSeleccionado(String mensaje) {
        Paciente paciente = tvPacientes.getSelectionModel().getSelectedItem();
        if (paciente == null) {
            Utilidades.mostrarAlertaSimple("Seleccion requerida", mensaje, Alert.AlertType.WARNING);
        }
        return paciente;
    }

    private void irFormulario(Paciente paciente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioPaciente.fxml"));
            Parent vista = loader.load();
            FXMLFormularioPacienteController controlador = loader.getController();
            controlador.inicializarDatos(paciente, this, obtenerIdMedicoSesion());

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(paciente == null ? "Registrar paciente" : "Editar paciente");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el formulario de paciente.", Alert.AlertType.ERROR);
        }
    }

    private void irHistorial(Paciente paciente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLHistorialClinico.fxml"));
            Parent vista = loader.load();
            FXMLHistorialClinicoController controlador = loader.getController();
            controlador.inicializarDatos(paciente);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Historial clinico");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el historial clinico.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionPacientes();
    }
}
