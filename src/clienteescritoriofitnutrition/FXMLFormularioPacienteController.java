package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dominio.PacienteImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Domicilio;
import clienteescritoriofitnutrition.pojo.Medico;
import clienteescritoriofitnutrition.pojo.Paciente;
import clienteescritoriofitnutrition.pojo.Usuario;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLFormularioPacienteController implements Initializable {

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
    private ComboBox<Medico> cbMedico;
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

    private Paciente pacienteEdicion;
    private INotificador notificador;
    private Integer idMedicoSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "Otro"));
        cargarMedicos();
    }

    public void inicializarDatos(Paciente paciente, INotificador notificador, Integer idMedicoSesion) {
        this.pacienteEdicion = paciente;
        this.notificador = notificador;
        this.idMedicoSesion = idMedicoSesion;
        cargarDatosEdicion();
        aplicarMedicoSesion();
    }

    private void aplicarMedicoSesion() {
        if (idMedicoSesion != null) {
            seleccionarMedico(idMedicoSesion);
            cbMedico.setDisable(true);
        }
    }

    private void cargarMedicos() {
        List<Medico> lista = MedicoImp.obtenerTodos();
        if (lista == null) {
            lista = new ArrayList<>();
        }
        cbMedico.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarDatosEdicion() {
        if (pacienteEdicion == null) {
            return;
        }

        Usuario usuario = pacienteEdicion.getUsuario();
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

        seleccionarMedico(pacienteEdicion.getIdMedico());
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
    private void clickGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        Paciente paciente = construirObjeto();
        Respuesta respuesta = pacienteEdicion == null
                ? PacienteImp.registrar(paciente)
                : PacienteImp.editar(paciente);

        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operacion exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );

        if (!respuesta.isError()) {
            if (notificador != null) {
                notificador.notificarOperacionExitosa("paciente", paciente.getUsuario().getNombre());
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
        if (cbMedico.getValue() == null) {
            mostrarCampoRequerido("El medico asignado es obligatorio.");
            return false;
        }
        return true;
    }

    private Paciente construirObjeto() {
        Paciente paciente = pacienteEdicion != null ? pacienteEdicion : new Paciente();
        Usuario usuario = paciente.getUsuario() != null ? paciente.getUsuario() : new Usuario();
        Domicilio domicilio = usuario.getDomicilio() != null ? usuario.getDomicilio() : new Domicilio();
        Medico medico = cbMedico.getValue();

        usuario.setNombre(tfNombre.getText().trim());
        usuario.setApellidoPaterno(tfApellidoPaterno.getText().trim());
        usuario.setApellidoMaterno(valorFormulario(tfApellidoMaterno.getText()));
        usuario.setFechaNacimiento(dpFechaNacimiento.getValue() != null ? dpFechaNacimiento.getValue().toString() : null);
        usuario.setGenero(cbGenero.getValue());
        usuario.setCorreo(tfCorreo.getText().trim());
        usuario.setTelefono(valorFormulario(tfTelefono.getText()));
        usuario.setIdRol(3);
        usuario.setIdEstatus(1);

        domicilio.setCalle(valorFormulario(tfCalle.getText()));
        domicilio.setNumeroExterior(valorFormulario(tfNumeroExterior.getText()));
        domicilio.setColonia(valorFormulario(tfColonia.getText()));
        domicilio.setMunicipio(valorFormulario(tfMunicipio.getText()));
        domicilio.setEstado(valorFormulario(tfEstado.getText()));
        domicilio.setCodigoPostal(valorFormulario(tfCodigoPostal.getText()));
        usuario.setDomicilio(domicilio);

        paciente.setUsuario(usuario);
        paciente.setIdMedico(medico.getIdMedico());
        paciente.setMedico(medico);

        return paciente;
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
