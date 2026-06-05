package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.AlimentoImp;
import clienteescritoriofitnutrition.dominio.DietaAlimentoImp;
import clienteescritoriofitnutrition.dominio.DietaImp;
import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dominio.SegmentoDiaImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Alimento;
import clienteescritoriofitnutrition.pojo.Dieta;
import clienteescritoriofitnutrition.pojo.DietaAlimento;
import clienteescritoriofitnutrition.pojo.Medico;
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
    private ComboBox<Medico> cbMedico;
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
    private Integer idMedicoSesion;
    private ObservableList<DietaAlimento> alimentosAsignados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        alimentosAsignados = FXCollections.observableArrayList();
        configurarTabla();
        cargarCatalogos();
    }

    public void inicializarDatos(Dieta dieta, INotificador notificador, Integer idMedicoSesion) {
        this.dietaEdicion = dieta;
        this.notificador = notificador;
        this.idMedicoSesion = idMedicoSesion;
        cargarDatosEdicion();
        aplicarMedicoSesion();
    }

    private void configurarTabla() {
        tcSegmento.setCellValueFactory(new PropertyValueFactory("nombreSegmento"));
        tcAlimento.setCellValueFactory(new PropertyValueFactory("nombreAlimento"));
        tcPorcion.setCellValueFactory(new PropertyValueFactory("porcionString"));
        tcCalorias.setCellValueFactory(new PropertyValueFactory("caloriasCalculadas"));
        tvAlimentosDieta.setItems(alimentosAsignados);
    }

    private void cargarCatalogos() {
        List<Alimento> listaAlimentos = AlimentoImp.obtenerTodos();
        if (listaAlimentos == null) {
            listaAlimentos = new ArrayList<>();
        }
        cbAlimento.setItems(FXCollections.observableArrayList(listaAlimentos));

        // Segmentos del día desde la API (datos reales, ya no hardcodeados).
        List<SegmentoDia> listaSegmentos = SegmentoDiaImp.obtenerTodos();
        if (listaSegmentos == null) {
            listaSegmentos = new ArrayList<>();
        }
        cbSegmento.setItems(FXCollections.observableArrayList(listaSegmentos));

        List<Medico> listaMedicos = MedicoImp.obtenerTodos();
        if (listaMedicos == null) {
            listaMedicos = new ArrayList<>();
        }
        cbMedico.setItems(FXCollections.observableArrayList(listaMedicos));
    }

    private void cargarDatosEdicion() {
        if (dietaEdicion == null) {
            return;
        }

        tfNombre.setText(valorFormulario(dietaEdicion.getNombre()));
        tfDescripcion.setText(valorFormulario(dietaEdicion.getObservaciones()));
        seleccionarMedico(dietaEdicion.getIdMedico());

        // Cargar los alimentos ya asignados desde la API (datos reales).
        if (dietaEdicion.getIdDieta() != null && dietaEdicion.getIdDieta() > 0) {
            List<DietaAlimento> asignados = DietaAlimentoImp.obtenerPorDieta(dietaEdicion.getIdDieta());
            if (asignados != null) {
                alimentosAsignados.addAll(asignados);
            }
        }
    }

    private void aplicarMedicoSesion() {
        if (idMedicoSesion != null) {
            seleccionarMedico(idMedicoSesion);
            cbMedico.setDisable(true);
        }
    }

    private void seleccionarMedico(Integer idMedico) {
        if (idMedico == null) {
            return;
        }
        for (Medico medico : cbMedico.getItems()) {
            if (medico.getIdMedico() != null && medico.getIdMedico().equals(idMedico)) {
                cbMedico.setValue(medico);
                return;
            }
        }
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
        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Requerido", "Selecciona un alimento de la tabla para quitarlo.", Alert.AlertType.WARNING);
            return;
        }

        // Si el renglón ya existe en la BD, se elimina del servidor.
        if (seleccionado.getIdDietaAlimento() != null && seleccionado.getIdDietaAlimento() > 0) {
            Respuesta r = DietaAlimentoImp.quitarAlimentoDeDieta(seleccionado.getIdDietaAlimento());
            if (r.isError()) {
                Utilidades.mostrarAlertaSimple("Error", r.getMensaje(), Alert.AlertType.ERROR);
                return;
            }
        }
        alimentosAsignados.remove(seleccionado);
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        Dieta dieta = construirObjeto();
        Respuesta respuesta = dietaEdicion == null
                ? DietaImp.registrar(dieta)
                : DietaImp.editar(dieta);

        if (respuesta.isError()) {
            Utilidades.mostrarAlertaSimple("Error", respuesta.getMensaje(), Alert.AlertType.ERROR);
            return;
        }

        // Id de la dieta para asociar los alimentos (nueva: viene del registro; edición: ya se conoce).
        Integer idDieta = (dietaEdicion != null) ? dietaEdicion.getIdDieta() : respuesta.getIdGenerado();
        Respuesta respuestaAlimentos = guardarAlimentosDieta(idDieta);

        if (respuestaAlimentos.isError()) {
            Utilidades.mostrarAlertaSimple("Dieta guardada con observaciones", respuestaAlimentos.getMensaje(), Alert.AlertType.WARNING);
        } else {
            Utilidades.mostrarAlertaSimple("Operación exitosa", respuesta.getMensaje(), Alert.AlertType.INFORMATION);
        }

        if (notificador != null) {
            notificador.notificarOperacionExitosa("dieta", dieta.getNombre());
        }
        cerrar();
    }

    private Respuesta guardarAlimentosDieta(Integer idDieta) {
        Respuesta respuesta = new Respuesta();
        respuesta.setError(false);

        if (idDieta == null || idDieta <= 0) {
            respuesta.setError(true);
            respuesta.setMensaje("La dieta se guardó pero no fue posible asociar los alimentos (id no disponible).");
            return respuesta;
        }

        for (DietaAlimento da : alimentosAsignados) {
            // Solo se registran los renglones nuevos (sin id asignado por la BD).
            if (da.getIdDietaAlimento() == null || da.getIdDietaAlimento() <= 0) {
                da.setIdDieta(idDieta);
                Respuesta r = DietaAlimentoImp.registrar(da);
                if (r.isError()) {
                    respuesta.setError(true);
                    respuesta.setMensaje("Algunos alimentos no se pudieron guardar: " + r.getMensaje());
                }
            }
        }
        return respuesta;
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
        if (cbMedico.getValue() == null) {
            mostrarCampoRequerido("El médico responsable es obligatorio.");
            return false;
        }
        return true;
    }

    private Dieta construirObjeto() {
        Dieta dieta = dietaEdicion != null ? dietaEdicion : new Dieta();
        dieta.setNombre(tfNombre.getText().trim());
        dieta.setObservaciones(valorFormulario(tfDescripcion.getText()));

        Medico medico = cbMedico.getValue();
        if (medico != null) {
            dieta.setIdMedico(medico.getIdMedico());
            dieta.setMedico(medico);
        }
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
