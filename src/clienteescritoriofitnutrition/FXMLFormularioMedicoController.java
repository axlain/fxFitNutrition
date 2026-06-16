package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Domicilio;
import clienteescritoriofitnutrition.pojo.Medico;
import clienteescritoriofitnutrition.pojo.Usuario;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

public class FXMLFormularioMedicoController implements Initializable {

    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidoPaterno;
    @FXML
    private TextField tfApellidoMaterno;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private ComboBox<String> cbGenero;
    @FXML
    private TextField tfCorreo;
    @FXML
    private TextField tfTelefono;
    @FXML
    private TextField tfCedulaProfesional;
    @FXML
    private PasswordField pfContrasena;
    @FXML
    private TextField tfCalle;
    @FXML
    private TextField tfNumeroExterior;
    @FXML
    private TextField tfColonia;
    @FXML
    private TextField tfMunicipio;
    @FXML
    private TextField tfEstado;
    @FXML
    private TextField tfCodigoPostal;

    private Medico medicoEdicion;
    private INotificador notificador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "Otro"));
        tfNumeroExterior.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*") ? change : null));
    }

    public void inicializarDatos(Medico medico, INotificador notificador) {
        this.medicoEdicion = medico;
        this.notificador = notificador;
        cargarDatosEdicion();
    }

    private void cargarDatosEdicion() {
        if (medicoEdicion == null) {
            return;
        }

        Usuario usuario = medicoEdicion.getUsuario();
        if (usuario != null) {
            tfNombre.setText(valor(usuario.getNombre()));
            tfApellidoPaterno.setText(valor(usuario.getApellidoPaterno()));
            tfApellidoMaterno.setText(valor(usuario.getApellidoMaterno()));
            tfCorreo.setText(valor(usuario.getCorreo()));
            tfTelefono.setText(valor(usuario.getTelefono()));
            cbGenero.setValue(valor(usuario.getGenero()));
            if (usuario.getFechaNacimiento() != null && !usuario.getFechaNacimiento().trim().isEmpty()) {
                try {
                    dpFechaNacimiento.setValue(LocalDate.parse(usuario.getFechaNacimiento().trim()));
                } catch (Exception e) {
                    dpFechaNacimiento.setValue(null);
                }
            }

            Domicilio domicilio = usuario.getDomicilio();
            if (domicilio != null) {
                tfCalle.setText(valor(domicilio.getCalle()));
                tfNumeroExterior.setText(valor(domicilio.getNumeroExterior()));
                tfColonia.setText(valor(domicilio.getColonia()));
                tfMunicipio.setText(valor(domicilio.getMunicipio()));
                tfEstado.setText(valor(domicilio.getEstado()));
                tfCodigoPostal.setText(valor(domicilio.getCodigoPostal()));
            }
        }

        tfCedulaProfesional.setText(valor(medicoEdicion.getCedulaProfesional()));
        pfContrasena.setDisable(true);
        pfContrasena.setPromptText("No editable desde este formulario");
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        Medico medico = construirObjeto();
        Respuesta respuesta = medicoEdicion == null
                ? MedicoImp.registrar(medico)
                : MedicoImp.editar(medico);

        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operacion exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );

        if (!respuesta.isError()) {
            if (notificador != null) {
                notificador.notificarOperacionExitosa("medico", medico.getUsuario().getNombre());
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
            mostrarCampoRequerido("El nombre es obligatorio.");
            return false;
        }
        if (estaVacio(tfApellidoPaterno.getText())) {
            mostrarCampoRequerido("El apellido paterno es obligatorio.");
            return false;
        }
        if (estaVacio(tfCorreo.getText())) {
            mostrarCampoRequerido("El correo es obligatorio.");
            return false;
        }
        if (estaVacio(tfCedulaProfesional.getText())) {
            mostrarCampoRequerido("La cedula profesional es obligatoria.");
            return false;
        }
        if (medicoEdicion == null && estaVacio(pfContrasena.getText())) {
            mostrarCampoRequerido("La contrasena es obligatoria.");
            return false;
        }
        return true;
    }

    private Medico construirObjeto() {
        Medico medico = medicoEdicion != null ? medicoEdicion : new Medico();
        Usuario usuario = medico.getUsuario() != null ? medico.getUsuario() : new Usuario();
        Domicilio domicilio = usuario.getDomicilio() != null ? usuario.getDomicilio() : new Domicilio();

        usuario.setNombre(tfNombre.getText().trim());
        usuario.setApellidoPaterno(tfApellidoPaterno.getText().trim());
        usuario.setApellidoMaterno(valorFormulario(tfApellidoMaterno.getText()));
        usuario.setFechaNacimiento(dpFechaNacimiento.getValue() != null ? dpFechaNacimiento.getValue().toString() : null);
        usuario.setGenero(cbGenero.getValue());
        usuario.setCorreo(tfCorreo.getText().trim());
        usuario.setTelefono(valorFormulario(tfTelefono.getText()));
        usuario.setIdRol(2);
        usuario.setIdEstatus(1);

        domicilio.setCalle(valorFormulario(tfCalle.getText()));
        domicilio.setNumeroExterior(valorFormulario(tfNumeroExterior.getText()));
        domicilio.setColonia(valorFormulario(tfColonia.getText()));
        domicilio.setMunicipio(valorFormulario(tfMunicipio.getText()));
        domicilio.setEstado(valorFormulario(tfEstado.getText()));
        domicilio.setCodigoPostal(valorFormulario(tfCodigoPostal.getText()));
        usuario.setDomicilio(domicilio);

        medico.setUsuario(usuario);
        medico.setCedulaProfesional(tfCedulaProfesional.getText().trim());
        if (medicoEdicion == null) {
            medico.setContrasena(pfContrasena.getText().trim());
        }

        return medico;
    }

    private void mostrarCampoRequerido(String mensaje) {
        Utilidades.mostrarAlertaSimple("Campo requerido", mensaje, Alert.AlertType.WARNING);
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String valor(String valor) {
        return valor != null ? valor : "";
    }

    private String valorFormulario(String valor) {
        return valor != null ? valor.trim() : "";
    }

    private void cerrar() {
        Stage escenario = (Stage) tfNombre.getScene().getWindow();
        escenario.close();
    }
}
