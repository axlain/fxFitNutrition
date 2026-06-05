package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.AlimentoImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Alimento;
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

public class FXMLAdministracionAlimentosController implements Initializable, INotificador {

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
    private TableView<Alimento> tvAlimentos;
    @FXML
    private TableColumn tcNombre;
    @FXML
    private TableColumn tcPorcion;
    @FXML
    private TableColumn tcCalorias;

    private ObservableList<Alimento> alimentos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionAlimentos();
    }

    private void configurarTabla() {
        tcNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        tcPorcion.setCellValueFactory(new PropertyValueFactory("porcionUnidad"));
        tcCalorias.setCellValueFactory(new PropertyValueFactory("caloriasPorPorcion"));
    }

    private void cargarInformacionAlimentos() {
        List<Alimento> lista = AlimentoImp.obtenerTodos();
        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple("Sin conexión", "No fue posible cargar los alimentos.", Alert.AlertType.WARNING);
        }
        alimentos = FXCollections.observableArrayList(lista);
        tvAlimentos.setItems(alimentos);
    }

    @FXML
    private void clickBuscar(ActionEvent event) {
        String filtro = tfBuscar.getText();
        if (filtro == null || filtro.trim().isEmpty()) {
            cargarInformacionAlimentos();
            return;
        }

        // Filtrado local por nombre sobre los alimentos ya cargados desde la API.
        List<Alimento> todos = AlimentoImp.obtenerTodos();
        List<Alimento> lista = new ArrayList<>();
        if (todos != null) {
            for (Alimento a : todos) {
                if (a.getNombre() != null && a.getNombre().toLowerCase().contains(filtro.toLowerCase())) {
                    lista.add(a);
                }
            }
        }
        
        tvAlimentos.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void clickLimpiar(ActionEvent event) {
        tfBuscar.clear();
        cargarInformacionAlimentos();
    }

    @FXML
    private void clickRegistrar(ActionEvent event) {
        irFormulario(null);
    }

    @FXML
    private void clickEditar(ActionEvent event) {
        Alimento alimento = obtenerAlimentoSeleccionado("Selecciona un alimento para editar.");
        if (alimento != null) {
            irFormulario(alimento);
        }
    }

    @FXML
    private void clickEliminar(ActionEvent event) {
        Alimento alimento = obtenerAlimentoSeleccionado("Selecciona un alimento para eliminar.");
        if (alimento == null) {
            return;
        }

        boolean confirmado = Utilidades.mostrarAlertaConfirmacion(
                "Eliminar alimento",
                "¿Deseas eliminar el alimento seleccionado? Esto afectará a las dietas donde se utiliza."
        );
        if (!confirmado) {
            return;
        }

        Respuesta respuesta = AlimentoImp.eliminar(alimento.getIdAlimento());
        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operación exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        if (!respuesta.isError()) {
            cargarInformacionAlimentos();
        }
    }

    private Alimento obtenerAlimentoSeleccionado(String mensaje) {
        Alimento alimento = tvAlimentos.getSelectionModel().getSelectedItem();
        if (alimento == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", mensaje, Alert.AlertType.WARNING);
        }
        return alimento;
    }

    private void irFormulario(Alimento alimento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioAlimento.fxml"));
            Parent vista = loader.load();
            FXMLFormularioAlimentoController controlador = loader.getController();
            controlador.inicializarDatos(alimento, this);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(alimento == null ? "Registrar Alimento" : "Editar Alimento");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el formulario de alimento.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionAlimentos();
    }
}
