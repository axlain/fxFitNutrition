package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dominio.PacienteImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Medico;
import clienteescritoriofitnutrition.pojo.Paciente;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FXMLReasignarPacientesController implements Initializable {

    @FXML
    private ComboBox<Medico> cbMedicoOrigen;
    @FXML
    private ComboBox<Medico> cbMedicoDestino;
    @FXML
    private TableView<Paciente> tvPacientes;
    @FXML
    private TableColumn tcCodigo;
    @FXML
    private TableColumn tcNombre;
    @FXML
    private TableColumn tcCorreo;
    @FXML
    private TableColumn tcMedico;

    private INotificador notificador;
    private ObservableList<Paciente> pacientesOrigen;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarMedicos();
        cbMedicoOrigen.valueProperty().addListener(new ChangeListener<Medico>() {
            @Override
            public void changed(ObservableValue<? extends Medico> observable, Medico anterior, Medico nuevo) {
                cargarPacientesOrigen();
            }
        });
    }

    public void inicializarDatos(Medico medicoOrigen, INotificador notificador) {
        this.notificador = notificador;
        if (medicoOrigen != null) {
            seleccionarMedico(cbMedicoOrigen, medicoOrigen.getIdMedico());
        }
        cargarPacientesOrigen();
    }

    private void configurarTabla() {
        tcCodigo.setCellValueFactory(new PropertyValueFactory("codigoAcceso"));
        tcNombre.setCellValueFactory(new PropertyValueFactory("nombreCompleto"));
        tcCorreo.setCellValueFactory(new PropertyValueFactory("correoUsuario"));
        tcMedico.setCellValueFactory(new PropertyValueFactory("medicoAsignado"));
    }

    private void cargarMedicos() {
        List<Medico> lista = MedicoImp.obtenerTodos();
        if (lista == null) {
            lista = new ArrayList<>();
        }
        ObservableList<Medico> medicos = FXCollections.observableArrayList(lista);
        cbMedicoOrigen.setItems(medicos);
        cbMedicoDestino.setItems(FXCollections.observableArrayList(lista));
    }

    private void seleccionarMedico(ComboBox<Medico> combo, Integer idMedico) {
        if (idMedico == null) {
            return;
        }
        for (Medico medico : combo.getItems()) {
            if (medico.getIdMedico() != null && medico.getIdMedico().equals(idMedico)) {
                combo.setValue(medico);
                return;
            }
        }
    }

    private void cargarPacientesOrigen() {
        Medico origen = cbMedicoOrigen.getValue();
        List<Paciente> todos = PacienteImp.obtenerTodos();
        pacientesOrigen = FXCollections.observableArrayList();

        if (origen != null && todos != null) {
            for (Paciente paciente : todos) {
                if (paciente.getIdMedico() != null && paciente.getIdMedico().equals(origen.getIdMedico())) {
                    pacientesOrigen.add(paciente);
                }
            }
        }

        tvPacientes.setItems(pacientesOrigen);
    }

    @FXML
    private void clickReasignar(ActionEvent event) {
        reasignar(false);
    }

    @FXML
    private void clickReasignarYDarBaja(ActionEvent event) {
        reasignar(true);
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrar();
    }

    private void reasignar(boolean darBajaOrigen) {
        Medico origen = cbMedicoOrigen.getValue();
        Medico destino = cbMedicoDestino.getValue();

        if (origen == null) {
            Utilidades.mostrarAlertaSimple("Seleccion requerida", "Selecciona el medico origen.", Alert.AlertType.WARNING);
            return;
        }

        if (destino == null) {
            Utilidades.mostrarAlertaSimple("Seleccion requerida", "Selecciona el medico destino.", Alert.AlertType.WARNING);
            return;
        }

        if (origen.getIdMedico().equals(destino.getIdMedico())) {
            Utilidades.mostrarAlertaSimple("Seleccion invalida", "El medico destino debe ser diferente al origen.", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmado = Utilidades.mostrarAlertaConfirmacion(
                "Reasignar pacientes",
                "Deseas reasignar los pacientes del medico origen al medico destino?"
        );
        if (!confirmado) {
            return;
        }

        int actualizados = actualizarPacientes(destino);
        if (actualizados < 0) {
            return;
        }

        if (darBajaOrigen) {
            Respuesta baja = MedicoImp.darBaja(origen.getIdMedico());
            if (baja.isError()) {
                Utilidades.mostrarAlertaSimple("Reasignacion parcial", baja.getMensaje(), Alert.AlertType.WARNING);
                return;
            }
        }

        Utilidades.mostrarAlertaSimple(
                "Operacion exitosa",
                "Pacientes reasignados: " + actualizados + (darBajaOrigen ? ". Medico dado de baja." : "."),
                Alert.AlertType.INFORMATION
        );

        if (notificador != null) {
            notificador.notificarOperacionExitosa("reasignacion", origen.getNombreCompleto());
        }
        cerrar();
    }

    private int actualizarPacientes(Medico destino) {
        int actualizados = 0;
        if (pacientesOrigen == null || pacientesOrigen.isEmpty()) {
            return 0;
        }

        for (Paciente paciente : pacientesOrigen) {
            paciente.setIdMedico(destino.getIdMedico());
            paciente.setMedico(destino);
            Respuesta respuesta = PacienteImp.editar(paciente);
            if (respuesta.isError()) {
                Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
                return -1;
            }
            actualizados++;
        }

        return actualizados;
    }

    private void cerrar() {
        Stage stage = (Stage) tvPacientes.getScene().getWindow();
        stage.close();
    }
}
