package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.DietaImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Dieta;
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

public class FXMLAdministracionDietasController implements Initializable, INotificador {

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
    private Button btnEliminar;
    @FXML
    private TableView<Dieta> tvDietas;
    @FXML
    private TableColumn tcNombre;
    @FXML
    private TableColumn tcDescripcion;

    private ObservableList<Dieta> dietas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionDietas();
    }

    private void configurarTabla() {
        tcNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        tcDescripcion.setCellValueFactory(new PropertyValueFactory("observaciones"));
    }

    private void cargarInformacionDietas() {
        List<Dieta> lista = DietaImp.obtenerTodas();
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Sin conexión", "No fue posible cargar las dietas.", Alert.AlertType.WARNING);
        }
        dietas = FXCollections.observableArrayList(lista);
        tvDietas.setItems(dietas);
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String filtro = tfBuscar.getText();
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarInformacionDietas();
            return;
        }

        // Simulación: Filtrado por nombre.
        List<Dieta> todas = DietaImp.obtenerTodas();
        List<Dieta> lista = new ArrayList<>();
        if (todas != null) {
            for (Dieta d : todas) {
                if (d.getNombre() != null && d.getNombre().toLowerCase().contains(filtro.toLowerCase())) {
                    lista.add(d);
                }
            }
        }
        
        tvDietas.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void clickLimpiar(ActionEvent event) {
        tfBuscar.clear();
        cargarInformacionDietas();
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
        irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Dieta dieta = obtenerDietaSeleccionada("Selecciona una dieta para editar.");
        if (dieta != null) {
            irFormulario(dieta);
        }
    }

    @FXML
    private void clickEliminar(ActionEvent event) {
        Dieta dieta = obtenerDietaSeleccionada("Selecciona una dieta para eliminar.");
        if (dieta == null) {
            return;
        }

        boolean confirmado = Utilidades.mostrarAlertaConfirmacion(
                "Eliminar dieta",
                "¿Deseas eliminar la dieta seleccionada? Esto afectará a las consultas asignadas."
        );
        if (!confirmado) {
            return;
        }

        Respuesta respuesta = DietaImp.eliminar(dieta.getIdDieta());
        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operación exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        if (!respuesta.isError()) {
            cargarInformacionDietas();
        }
    }

    private Dieta obtenerDietaSeleccionada(String mensaje) {
        Dieta dieta = tvDietas.getSelectionModel().getSelectedItem();
        if (dieta == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", mensaje, Alert.AlertType.WARNING);
        }
        return dieta;
    }

    private void irFormulario(Dieta dieta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioDieta.fxml"));
            Parent vista = loader.load();
            FXMLFormularioDietaController controlador = loader.getController();
            controlador.inicializarDatos(dieta, this);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(dieta == null ? "Registrar Dieta" : "Editar Dieta");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el formulario de dieta.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionDietas();
    }
}
