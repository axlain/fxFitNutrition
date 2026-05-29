package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.CitaImp;
import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dominio.PacienteImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Cita;
import clienteescritoriofitnutrition.pojo.Medico;
import clienteescritoriofitnutrition.pojo.Paciente;
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

public class FXMLFormularioCitaController implements Initializable {

    @FXML
    private ComboBox<Paciente> cbPaciente;
    @FXML
    private ComboBox<Medico> cbMedico;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField tfHora;
    @FXML
    private TextField tfObservaciones;

    private Cita citaEdicion;
    private INotificador notificador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarPacientes();
        cargarMedicos();
    }

    public void inicializarDatos(Cita cita, INotificador notificador) {
        this.citaEdicion = cita;
        this.notificador = notificador;
        cargarDatosEdicion();
    }

    private void cargarPacientes() {
        List<Paciente> lista = PacienteImp.obtenerTodos();
        if (lista == null) {
            lista = new ArrayList<>();
        }
        cbPaciente.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarMedicos() {
        List<Medico> lista = MedicoImp.obtenerTodos();
        if (lista == null) {
            lista = new ArrayList<>();
        }
        cbMedico.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarDatosEdicion() {
        if (citaEdicion == null) {
            return;
        }

        if (citaEdicion.getFecha() != null && !citaEdicion.getFecha().trim().isEmpty()) {
            try {
                dpFecha.setValue(LocalDate.parse(citaEdicion.getFecha().trim()));
            } catch (Exception e) {
                dpFecha.setValue(null);
            }
        }
        String horaCita = valorFormulario(citaEdicion.getHora());
        if (horaCita.length() > 5) {
            horaCita = horaCita.substring(0, 5);
        }
        tfHora.setText(horaCita);
        tfObservaciones.setText(valorFormulario(citaEdicion.getObservaciones()));

        seleccionarPaciente(citaEdicion.getIdPaciente());
        seleccionarMedico(citaEdicion.getIdMedico());
    }

    private void seleccionarPaciente(Integer idPaciente) {
        if (idPaciente == null) {
            return;
        }
        for (Paciente paciente : cbPaciente.getItems()) {
            if (paciente.getIdPaciente() != null && paciente.getIdPaciente().equals(idPaciente)) {
                cbPaciente.setValue(paciente);
                return;
            }
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
    private void clickGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        Cita cita = construirObjeto();
        Respuesta respuesta = citaEdicion == null
                ? CitaImp.registrar(cita)
                : CitaImp.editar(cita);

        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operación exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );

        if (!respuesta.isError()) {
            if (notificador != null) {
                notificador.notificarOperacionExitosa("cita", "");
            }
            cerrar();
        }
    }

    @FXML
    private void clickCancelar(ActionEvent event) {
        cerrar();
    }

    private boolean validarCampos() {
        if (cbPaciente.getValue() == null) {
            mostrarCampoRequerido("El paciente es obligatorio.");
            return false;
        }
        if (cbMedico.getValue() == null) {
            mostrarCampoRequerido("El médico es obligatorio.");
            return false;
        }
        if (dpFecha.getValue() == null) {
            mostrarCampoRequerido("La fecha es obligatoria.");
            return false;
        }
        if (estaVacio(tfHora.getText())) {
            mostrarCampoRequerido("La hora es obligatoria.");
            return false;
        }
        // Validar formato de hora simple (HH:mm)
        String hora = tfHora.getText().trim();
        if (!hora.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            mostrarCampoRequerido("La hora debe tener el formato HH:mm.");
            return false;
        }
        return true;
    }

    private Cita construirObjeto() {
        Cita cita = citaEdicion != null ? citaEdicion : new Cita();
        Paciente paciente = cbPaciente.getValue();
        Medico medico = cbMedico.getValue();

        cita.setFecha(dpFecha.getValue().toString());
        cita.setHora(tfHora.getText().trim());
        cita.setObservaciones(valorFormulario(tfObservaciones.getText()));
        
        // Estado inicial de la cita: 1 (Agendada / Pendiente) - dependerá de la DB.
        if (cita.getIdCita() == null) {
            cita.setIdEstadoCita(1);
        }

        cita.setIdPaciente(paciente.getIdPaciente());
        cita.setPaciente(paciente);
        cita.setIdMedico(medico.getIdMedico());
        cita.setMedico(medico);

        return cita;
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
        Stage escenario = (Stage) tfHora.getScene().getWindow();
        escenario.close();
    }
}
