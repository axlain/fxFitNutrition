package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.ConsultaImp;
import clienteescritoriofitnutrition.dto.RSAutenticar;
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
    private TableColumn tcFecha;
    @FXML
    private TableColumn tcHora;
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
    private RSAutenticar sesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    public void inicializarSesion(RSAutenticar sesion) {
        this.sesion = sesion;
        cargarInformacionConsultas();
    }

    private void configurarTabla() {
        tcFecha.setCellValueFactory(new PropertyValueFactory("fecha"));
        tcHora.setCellValueFactory(new PropertyValueFactory("hora"));
        tcPaciente.setCellValueFactory(new PropertyValueFactory("nombrePaciente"));
        tcMedico.setCellValueFactory(new PropertyValueFactory("nombreMedico"));
        tcPeso.setCellValueFactory(new PropertyValueFactory("peso"));
        tcImc.setCellValueFactory(new PropertyValueFactory("imc"));
        tcDieta.setCellValueFactory(new PropertyValueFactory("nombreDieta"));
    }

    private void cargarInformacionConsultas() {
        List<Consulta> lista;

        Integer idMedicoSesion = obtenerIdMedicoSesion();

        if (idMedicoSesion != null && idMedicoSesion > 0) {
            lista = ConsultaImp.obtenerHistorialMedico(idMedicoSesion);
        } else {
            lista = ConsultaImp.obtenerTodas();
        }

        if (lista == null) {
            lista = new ArrayList<>();
        }

        consultas = FXCollections.observableArrayList(lista);
        tvConsultas.setItems(consultas);
    }

    private Integer obtenerIdPacienteSesion() {
        if (sesion != null && sesion.getPaciente() != null) {
            return sesion.getPaciente().getIdPaciente();
        }
        return null;
    }

    private Integer obtenerIdMedicoSesion() {
        if (sesion != null && sesion.getMedico() != null) {
            return sesion.getMedico().getIdMedico();
        }
        return null;
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String filtro = tfBuscar.getText();

        if (filtro == null || filtro.trim().isEmpty()) {
            tvConsultas.setItems(consultas);
            return;
        }

        String filtroLower = filtro.toLowerCase().trim();
        ObservableList<Consulta> filtradas = FXCollections.observableArrayList();

        if (consultas != null) {
            for (Consulta consulta : consultas) {
                if (consulta.getNombrePaciente().toLowerCase().contains(filtroLower)
                        || consulta.getNombreMedico().toLowerCase().contains(filtroLower)
                        || texto(consulta.getFecha()).toLowerCase().contains(filtroLower)
                        || texto(consulta.getHora()).toLowerCase().contains(filtroLower)
                        || texto(consulta.getNombreDieta()).toLowerCase().contains(filtroLower)) {
                    filtradas.add(consulta);
                }
            }
        }

        tvConsultas.setItems(filtradas);
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
            controlador.inicializarDatos(consulta, this, obtenerIdMedicoSesion());

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(consulta == null ? "Registrar Consulta" : "Editar Consulta");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    "No se pudo abrir el formulario de consulta.",
                    Alert.AlertType.ERROR
            );
        }
    }

    private String texto(String valor) {
        return valor != null ? valor : "";
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionConsultas();
    }
}