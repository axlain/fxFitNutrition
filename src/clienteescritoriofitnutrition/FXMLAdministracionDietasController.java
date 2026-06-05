package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.DietaAlimentoImp;
import clienteescritoriofitnutrition.dominio.DietaImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Dieta;
import clienteescritoriofitnutrition.pojo.DietaAlimento;
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
    private Button btnDetalle;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private TableView<Dieta> tvDietas;
    @FXML
    private TableColumn<Dieta, String> tcNombre;
    @FXML
    private TableColumn<Dieta, String> tcCalorias;
    @FXML
    private TableColumn<Dieta, String> tcDescripcion;
    @FXML
    private TableColumn<Dieta, String> tcAlimentos;

    private ObservableList<Dieta> dietas;
    private RSAutenticar sesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionDietas();
        configurarDobleClick();
    }

    public void inicializarSesion(RSAutenticar sesion) {
        this.sesion = sesion;
    }

    private Integer obtenerIdMedicoSesion() {
        if (sesion != null && sesion.getMedico() != null) {
            return sesion.getMedico().getIdMedico();
        }
        return null;
    }

    private void configurarTabla() {
        tcNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(valorSeguro(cellData.getValue().getNombre()))
        );

        tcCalorias.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getTotalCalorias()))
        );

        tcDescripcion.setCellValueFactory(cellData ->
                new SimpleStringProperty(valorSeguro(cellData.getValue().getObservaciones()))
        );

        tcAlimentos.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerNombresAlimentos(cellData.getValue()))
        );
    }

    private void configurarDobleClick() {
        tvDietas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tvDietas.getSelectionModel().getSelectedItem() != null) {
                mostrarDetalleDieta(tvDietas.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void cargarInformacionDietas() {
        List<Dieta> lista = DietaImp.obtenerTodas();

        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple(
                    "Sin conexión",
                    "No fue posible cargar las dietas.",
                    Alert.AlertType.WARNING
            );
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

        List<Dieta> lista = DietaImp.buscar(filtro.trim());

        if (lista == null) {
            lista = new ArrayList<>();
            Utilidades.mostrarAlertaSimple(
                    "Búsqueda",
                    "No se encontraron dietas o no fue posible realizar la búsqueda.",
                    Alert.AlertType.INFORMATION
            );
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
    private void clickDetalle(ActionEvent event) {
        Dieta dieta = obtenerDietaSeleccionada("Selecciona una dieta para ver el detalle.");
        if (dieta != null) {
            mostrarDetalleDieta(dieta);
        }
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
                "¿Deseas eliminar la dieta seleccionada?"
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
            Utilidades.mostrarAlertaSimple(
                    "Selección requerida",
                    mensaje,
                    Alert.AlertType.WARNING
            );
        }

        return dieta;
    }

    private String obtenerNombresAlimentos(Dieta dieta) {
        if (dieta == null || dieta.getIdDieta() == null || dieta.getIdDieta() <= 0) {
            return "No hay alimentos asignados";
        }

        List<DietaAlimento> alimentos = DietaAlimentoImp.obtenerPorDieta(dieta.getIdDieta());

        if (alimentos == null || alimentos.isEmpty()) {
            return "No hay alimentos asignados";
        }

        StringBuilder nombres = new StringBuilder();

        for (DietaAlimento da : alimentos) {
            String nombre = obtenerNombreAlimento(da);

            if (nombre != null && !nombre.trim().isEmpty()) {
                if (nombres.length() > 0) {
                    nombres.append(", ");
                }

                nombres.append(nombre);
            }
        }

        if (nombres.length() == 0) {
            return "No hay alimentos asignados";
        }

        return nombres.toString();
    }

    private String obtenerNombreAlimento(DietaAlimento dietaAlimento) {
        if (dietaAlimento == null) {
            return null;
        }

        if (dietaAlimento.getAlimento() != null) {
            return dietaAlimento.getAlimento().getNombre();
        }

        return null;
    }

    private void mostrarDetalleDieta(Dieta dieta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDetalleDieta.fxml"));
            Parent vista = loader.load();

            FXMLDetalleDietaController controlador = loader.getController();
            controlador.inicializarDatos(dieta);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Detalle de dieta");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    "No se pudo abrir el detalle de la dieta.",
                    Alert.AlertType.ERROR
            );
        }
    }

    private void irFormulario(Dieta dieta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioDieta.fxml"));
            Parent vista = loader.load();

            FXMLFormularioDietaController controlador = loader.getController();
            controlador.inicializarDatos(dieta, this, obtenerIdMedicoSesion());

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle(dieta == null ? "Registrar Dieta" : "Editar Dieta");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    "No se pudo abrir el formulario de dieta.",
                    Alert.AlertType.ERROR
            );
        }
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "Sin información" : valor;
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
        cargarInformacionDietas();
    }
}