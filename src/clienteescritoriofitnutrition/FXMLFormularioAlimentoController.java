package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.AlimentoImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Alimento;
import clienteescritoriofitnutrition.pojo.UnidadPorcion;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioAlimentoController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfPorcion;
    @FXML
    private ComboBox<UnidadPorcion> cbUnidad;
    @FXML
    private TextField tfCalorias;

    private Alimento alimentoEdicion;
    private INotificador notificador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarUnidades();
    }

    public void inicializarDatos(Alimento alimento, INotificador notificador) {
        this.alimentoEdicion = alimento;
        this.notificador = notificador;
        cargarDatosEdicion();
    }

    private void cargarUnidades() {
        List<UnidadPorcion> unidades = AlimentoImp.obtenerUnidades();
        if (unidades == null) {
            unidades = new ArrayList<>();
        }
        cbUnidad.setItems(FXCollections.observableArrayList(unidades));
    }

    private void cargarDatosEdicion() {
        if (alimentoEdicion == null) {
            return;
        }

        tfNombre.setText(valorFormulario(alimentoEdicion.getNombre()));
        tfPorcion.setText(alimentoEdicion.getPorcion() != null ? String.valueOf(alimentoEdicion.getPorcion()) : "");
        tfCalorias.setText(alimentoEdicion.getCaloriasPorPorcion() != null ? String.valueOf(alimentoEdicion.getCaloriasPorPorcion()) : "");

        seleccionarUnidad(alimentoEdicion.getIdUnidadPorcion());
    }

    private void seleccionarUnidad(Integer idUnidad) {
        if (idUnidad == null) {
            return;
        }
        for (UnidadPorcion unidad : cbUnidad.getItems()) {
            if (unidad.getIdUnidadPorcion() != null && unidad.getIdUnidadPorcion().equals(idUnidad)) {
                cbUnidad.setValue(unidad);
                return;
            }
        }
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        Alimento alimento = construirObjeto();
        Respuesta respuesta = alimentoEdicion == null
                ? AlimentoImp.registrar(alimento)
                : AlimentoImp.editar(alimento);

        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operación exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );

        if (!respuesta.isError()) {
            if (notificador != null) {
                notificador.notificarOperacionExitosa("alimento", alimento.getNombre());
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
            mostrarCampoRequerido("El nombre del alimento es obligatorio.");
            return false;
        }
        if (estaVacio(tfPorcion.getText())) {
            mostrarCampoRequerido("La porción es obligatoria.");
            return false;
        }
        if (cbUnidad.getValue() == null) {
            mostrarCampoRequerido("La unidad de medida es obligatoria.");
            return false;
        }
        if (estaVacio(tfCalorias.getText())) {
            mostrarCampoRequerido("Las calorías son obligatorias.");
            return false;
        }

        try {
            Double.parseDouble(tfPorcion.getText().trim());
            Double.parseDouble(tfCalorias.getText().trim());
        } catch (NumberFormatException e) {
            mostrarCampoRequerido("La porción y las calorías deben ser valores numéricos.");
            return false;
        }

        return true;
    }

    private Alimento construirObjeto() {
        Alimento alimento = alimentoEdicion != null ? alimentoEdicion : new Alimento();
        UnidadPorcion unidad = cbUnidad.getValue();

        alimento.setNombre(tfNombre.getText().trim());
        alimento.setPorcion(Double.parseDouble(tfPorcion.getText().trim()));
        alimento.setCaloriasPorPorcion(Double.parseDouble(tfCalorias.getText().trim()));
        
        alimento.setIdUnidadPorcion(unidad.getIdUnidadPorcion());
        alimento.setUnidadPorcion(unidad);

        return alimento;
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
