package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.DietaAlimentoImp;
import clienteescritoriofitnutrition.pojo.Dieta;
import clienteescritoriofitnutrition.pojo.DietaAlimento;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class FXMLDetalleDietaController implements Initializable {

    @FXML
    private Label lbNombre;
    @FXML
    private Label lbCalorias;
    @FXML
    private TextArea taObservaciones;
    @FXML
    private TableView<DietaAlimento> tvAlimentos;
    @FXML
    private TableColumn<DietaAlimento, String> tcAlimento;
    @FXML
    private TableColumn<DietaAlimento, String> tcSegmento;
    @FXML
    private TableColumn<DietaAlimento, String> tcCantidad;
    @FXML
    private TableColumn<DietaAlimento, String> tcCalorias;
    @FXML
    private Button btnCerrar;

    private Dieta dieta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    public void inicializarDatos(Dieta dieta) {
        this.dieta = dieta;

        if (dieta == null) {
            return;
        }

        lbNombre.setText(valorSeguro(dieta.getNombre()));
        lbCalorias.setText("Total de calorías: " + valorDouble(dieta.getTotalCalorias()));
        taObservaciones.setText(valorSeguro(dieta.getObservaciones()));

        cargarAlimentos();
    }

    private void configurarTabla() {
        tcAlimento.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerNombreAlimento(cellData.getValue()))
        );

        tcSegmento.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerNombreSegmento(cellData.getValue()))
        );

        tcCantidad.setCellValueFactory(cellData ->
                new SimpleStringProperty(valorDouble(cellData.getValue().getCantidad()))
        );

        tcCalorias.setCellValueFactory(cellData ->
                new SimpleStringProperty(obtenerCaloriasAlimento(cellData.getValue()))
        );
    }

    private void cargarAlimentos() {
        if (dieta == null || dieta.getIdDieta() == null || dieta.getIdDieta() <= 0) {
            tvAlimentos.setPlaceholder(new Label("No hay alimentos asignados a esta dieta."));
            tvAlimentos.setItems(FXCollections.observableArrayList(new ArrayList<>()));
            return;
        }

        List<DietaAlimento> lista = DietaAlimentoImp.obtenerPorDieta(dieta.getIdDieta());

        if (lista == null || lista.isEmpty()) {
            tvAlimentos.setPlaceholder(new Label("No hay alimentos asignados a esta dieta."));
            tvAlimentos.setItems(FXCollections.observableArrayList(new ArrayList<>()));
            return;
        }

        tvAlimentos.setItems(FXCollections.observableArrayList(lista));
    }

    private String obtenerNombreAlimento(DietaAlimento dietaAlimento) {
        if (dietaAlimento == null || dietaAlimento.getAlimento() == null) {
            return "Sin alimento";
        }

        return valorSeguro(dietaAlimento.getAlimento().getNombre());
    }

    private String obtenerNombreSegmento(DietaAlimento dietaAlimento) {
        if (dietaAlimento == null || dietaAlimento.getSegmentoDia() == null) {
            return "Sin segmento";
        }

        return valorSeguro(dietaAlimento.getSegmentoDia().getNombre());
    }

    private String obtenerCaloriasAlimento(DietaAlimento dietaAlimento) {
        if (dietaAlimento == null || dietaAlimento.getAlimento() == null) {
            return "0";
        }

        return valorDouble(dietaAlimento.getAlimento().getCaloriasPorPorcion());
    }

    private String valorSeguro(String valor) {
        return valor == null || valor.trim().isEmpty() ? "Sin información" : valor;
    }

    private String valorDouble(Double valor) {
        return valor == null ? "0.00" : String.format("%.2f", valor);
    }

    @FXML
    private void clickCerrar(ActionEvent event) {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}