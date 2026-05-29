package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.AlimentoImp;
import clienteescritoriofitnutrition.dominio.DietaImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Alimento;
import clienteescritoriofitnutrition.pojo.Dieta;
import clienteescritoriofitnutrition.pojo.DietaAlimento;
import clienteescritoriofitnutrition.pojo.SegmentoDia;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FXMLFormularioDietaController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfDescripcion;
    @FXML
    private ComboBox<SegmentoDia> cbSegmento;
    @FXML
    private ComboBox<Alimento> cbAlimento;
    @FXML
    private TextField tfCantidad;
    @FXML
    private TableView<DietaAlimento> tvAlimentosDieta;
    @FXML
    private TableColumn tcSegmento;
    @FXML
    private TableColumn tcAlimento;
    @FXML
    private TableColumn tcPorcion;
    @FXML
    private TableColumn tcCalorias;

    private Dieta dietaEdicion;
    private INotificador notificador;
    private ObservableList<DietaAlimento> alimentosAsignados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        alimentosAsignados = FXCollections.observableArrayList();
        configurarTabla();
        cargarCatalogos();
    }

    public void inicializarDatos(Dieta dieta, INotificador notificador) {
        this.dietaEdicion = dieta;
        this.notificador = notificador;
        cargarDatosEdicion();
    }

    private void configurarTabla() {
        tcSegmento.setCellValueFactory(new PropertyValueFactory("nombreSegmento"));
        tcAlimento.setCellValueFactory(new PropertyValueFactory("nombreAlimento"));
        tcPorcion.setCellValueFactory(new PropertyValueFactory("porcionString"));
        tcCalorias.setCellValueFactory(new PropertyValueFactory("caloriasCalculadas"));
        tvAlimentosDieta.setItems(alimentosAsignados);
    }

    private void cargarCatalogos() {
        // Cargar alimentos
        List<Alimento> listaAlimentos = AlimentoImp.obtenerTodos();
        if (listaAlimentos == null) {
            listaAlimentos = new ArrayList<>();
        }
        cbAlimento.setItems(FXCollections.observableArrayList(listaAlimentos));

        // Cargar segmentos (Mockeado localmente si no hay Imp, normalmente vendría de BD)
        List<SegmentoDia> listaSegmentos = new ArrayList<>();
        listaSegmentos.add(new SegmentoDia(1, "Desayuno"));
        listaSegmentos.add(new SegmentoDia(2, "Comida"));
        listaSegmentos.add(new SegmentoDia(3, "Cena"));
        listaSegmentos.add(new SegmentoDia(4, "Colación"));
        cbSegmento.setItems(FXCollections.observableArrayList(listaSegmentos));
    }

    private void cargarDatosEdicion() {
        if (dietaEdicion == null) {
            return;
        }

        tfNombre.setText(valorFormulario(dietaEdicion.getNombre()));
        tfDescripcion.setText(valorFormulario(dietaEdicion.getObservaciones()));

        // Cargar alimentos asignados si existen en la edición (Mock)
        // En un caso real se llamaría a DietaAlimentoImp.obtenerPorIdDieta(dietaEdicion.getIdDieta())
    }

    @FXML
    private void clickAgregarAlimento(ActionEvent event) {
        SegmentoDia segmento = cbSegmento.getValue();
        Alimento alimento = cbAlimento.getValue();
        String cantidadStr = tfCantidad.getText();

        if (segmento == null) {
            Utilidades.mostrarAlertaSimple("Requerido", "Selecciona un segmento del día.", Alert.AlertType.WARNING);
            return;
        }
        if (alimento == null) {
            Utilidades.mostrarAlertaSimple("Requerido", "Selecciona un alimento.", Alert.AlertType.WARNING);
            return;
        }
        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Requerido", "Ingresa la cantidad.", Alert.AlertType.WARNING);
            return;
        }

        try {
            Double cantidad = Double.parseDouble(cantidadStr.trim());
            if (cantidad <= 0) {
                Utilidades.mostrarAlertaSimple("Error", "La cantidad debe ser mayor a cero.", Alert.AlertType.WARNING);
                return;
            }
            
            DietaAlimento dietaAlimento = new DietaAlimento();
            dietaAlimento.setIdSegmentoDia(segmento.getIdSegmento());
            dietaAlimento.setSegmentoDia(segmento);
            dietaAlimento.setIdAlimento(alimento.getIdAlimento());
            dietaAlimento.setAlimento(alimento);
            dietaAlimento.setCantidad(cantidad);
            
            alimentosAsignados.add(dietaAlimento);
            
            // Limpiar campos de inserción
            cbSegmento.setValue(null);
            cbAlimento.setValue(null);
            tfCantidad.clear();

        } catch (NumberFormatException e) {
            Utilidades.mostrarAlertaSimple("Error", "La cantidad debe ser numérica.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clickQuitarAlimento(ActionEvent event) {
        DietaAlimento seleccionado = tvAlimentosDieta.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            alimentosAsignados.remove(seleccionado);
        } else {
            Utilidades.mostrarAlertaSimple("Requerido", "Selecciona un alimento de la tabla para quitarlo.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        Dieta dieta = construirObjeto();
        // En una implementación real, aquí se enviarían también los alimentosAsignados al API.
        
        Respuesta respuesta = dietaEdicion == null
                ? DietaImp.registrar(dieta)
                : DietaImp.editar(dieta);

        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operación exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );

        if (!respuesta.isError()) {
            if (notificador != null) {
                notificador.notificarOperacionExitosa("dieta", dieta.getNombre());
            }
            cerrar();
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrar();
    }

    private boolean validarCampos() {
        if (estaVacio(tfNombre.getText())) {
            mostrarCampoRequerido("El nombre de la dieta es obligatorio.");
            return false;
        }
        return true;
    }

    private Dieta construirObjeto() {
        Dieta dieta = dietaEdicion != null ? dietaEdicion : new Dieta();
        dieta.setNombre(tfNombre.getText().trim());
        dieta.setObservaciones(valorFormulario(tfDescripcion.getText()));
        return dieta;
    }

    private void mostrarCampoRequerido(String mensaje) {
        Utilidades.mostrarAlertaSimple("Campo requerido", mensaje, Alert.AlertType.WARNING);
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String valorFormulario(String valor) {
        return valor != null ? valor.trim() : "";
    }

    private void cerrar() {
        Stage escenario = (Stage) tfNombre.getScene().getWindow();
        escenario.close();
    }
}
