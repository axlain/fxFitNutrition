package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.CitaImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Cita;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
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

public class FXMLAgendaCitasController implements Initializable, INotificador {

    @FXML
    private Button btnFiltroTodas;
    @FXML
    private Button btnFiltroPendientes;
    @FXML
    private Button btnFiltroAtendidas;
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
    private Button btnCancelar;
    @FXML
    private TableView<Cita> tvCitas;
    @FXML
    private TableColumn tcFecha;
    @FXML
    private TableColumn tcHora;
    @FXML
    private TableColumn tcPaciente;
    @FXML
    private TableColumn tcMedico;
    @FXML
    private TableColumn tcEstado;

    private ObservableList<Cita> citas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionCitas();
    }

    private void configurarTabla() {
        tcFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        tcHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        
        tcPaciente.setCellValueFactory(cellData -> {
            Cita cita = (Cita) ((TableColumn.CellDataFeatures) cellData).getValue();
            return new SimpleStringProperty(getNombrePacienteSeguro(cita));
        });
        
        tcMedico.setCellValueFactory(cellData -> {
            Cita cita = (Cita) ((TableColumn.CellDataFeatures) cellData).getValue();
            return new SimpleStringProperty(getNombreMedicoSeguro(cita));
        });

        tcEstado.setCellValueFactory(cellData -> {
            Cita cita = (Cita) ((TableColumn.CellDataFeatures) cellData).getValue();
            return new SimpleStringProperty(getNombreEstadoSeguro(cita));
        });
    }

    private String getNombrePacienteSeguro(Cita cita) {
        if (cita != null && cita.getPaciente() != null && cita.getPaciente().getUsuario() != null) {
            String nombre = cita.getPaciente().getUsuario().getNombre() != null ? cita.getPaciente().getUsuario().getNombre() : "";
            String apellido = cita.getPaciente().getUsuario().getApellidoPaterno() != null ? cita.getPaciente().getUsuario().getApellidoPaterno() : "";
            return (nombre + " " + apellido).trim();
        }
        return "";
    }

    private String getNombreMedicoSeguro(Cita cita) {
        if (cita != null && cita.getMedico() != null && cita.getMedico().getUsuario() != null) {
            String nombre = cita.getMedico().getUsuario().getNombre() != null ? cita.getMedico().getUsuario().getNombre() : "";
            String apellido = cita.getMedico().getUsuario().getApellidoPaterno() != null ? cita.getMedico().getUsuario().getApellidoPaterno() : "";
            return (nombre + " " + apellido).trim();
        }
        return "";
    }

    private String getNombreEstadoSeguro(Cita cita) {
        if (cita != null && cita.getEstadoCita() != null && cita.getEstadoCita().getNombre() != null) {
            return cita.getEstadoCita().getNombre();
        }
        return "";
    }

    private void cargarInformacionCitas() {
        List<Cita> lista = CitaImp.obtenerTodas();
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Sin conexión", "No fue posible cargar las citas.", Alert.AlertType.WARNING);
        }
        citas = FXCollections.observableArrayList(lista);
        tvCitas.setItems(citas);
    }

    @FXML
    private void clickFiltroTodas(ActionEvent event) {
        btnFiltroTodas.getStyleClass().setAll("button", "segmented-btn-activo");
        btnFiltroPendientes.getStyleClass().setAll("button", "segmented-btn");
        btnFiltroAtendidas.getStyleClass().setAll("button", "segmented-btn");
        cargarInformacionCitas();
    }

    @FXML
    private void clickFiltroPendientes(ActionEvent event) {
        btnFiltroTodas.getStyleClass().setAll("button", "segmented-btn");
        btnFiltroPendientes.getStyleClass().setAll("button", "segmented-btn-activo");
        btnFiltroAtendidas.getStyleClass().setAll("button", "segmented-btn");
        cargarInformacionCitas();
    }

    @FXML
    private void clickFiltroAtendidas(ActionEvent event) {
        btnFiltroTodas.getStyleClass().setAll("button", "segmented-btn");
        btnFiltroPendientes.getStyleClass().setAll("button", "segmented-btn");
        btnFiltroAtendidas.getStyleClass().setAll("button", "segmented-btn-activo");
        cargarInformacionCitas();
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String filtro = tfBuscar.getText();
        if (filtro == null || filtro.trim().isEmpty()) {
            tvCitas.setItems(citas);
            return;
        }

        String filtroLower = filtro.toLowerCase().trim();
        ObservableList<Cita> filtradas = FXCollections.observableArrayList();

        if (citas != null) {
            for (Cita cita : citas) {
                if (getNombrePacienteSeguro(cita).toLowerCase().contains(filtroLower) ||
                    getNombreMedicoSeguro(cita).toLowerCase().contains(filtroLower) ||
                    (cita.getFecha() != null && cita.getFecha().toLowerCase().contains(filtroLower))) {
                    filtradas.add(cita);
                }
            }
        }
        tvCitas.setItems(filtradas);
    }

    @FXML
    private void clickLimpiar(ActionEvent event) {
        tfBuscar.clear();
        cargarInformacionCitas();
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
        irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Cita cita = obtenerCitaSeleccionada("Selecciona una cita para editar.");
        if (cita != null) {
            irFormulario(cita);
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        Cita cita = obtenerCitaSeleccionada("Selecciona una cita para cancelar.");
        if (cita == null) {
            return;
        }

        if (getNombreEstadoSeguro(cita).equalsIgnoreCase("Cancelada")) {
            Utilidades.mostrarAlertaSimple("Acción no permitida", "La cita seleccionada ya se encuentra cancelada.", Alert.AlertType.WARNING);
            return;
        }

        boolean confirmado = Utilidades.mostrarAlertaConfirmacion(
                "Cancelar cita",
                "¿Deseas cancelar la cita seleccionada?"
        );
        if (!confirmado) {
            return;
        }

        cita.setMotivoCancelacion("Cancelada desde administración");
        Respuesta respuesta = CitaImp.cancelar(cita);
        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operación exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        if (!respuesta.isError()) {
            cargarInformacionCitas();
        }
    }

    private Cita obtenerCitaSeleccionada(String mensaje) {
        Cita cita = tvCitas.getSelectionModel().getSelectedItem();
        if (cita == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", mensaje, Alert.AlertType.WARNING);
        }
        return cita;
    }

    private void irFormulario(Cita cita) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioCita.fxml"));
            Parent vista = loader.load();
            FXMLFormularioCitaController controlador = loader.getController();
            controlador.inicializarDatos(cita, this);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(cita == null ? "Registrar cita" : "Editar cita");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el formulario de cita.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionCitas();
    }
}
