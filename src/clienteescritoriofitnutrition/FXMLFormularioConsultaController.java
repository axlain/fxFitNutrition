package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.ConsultaImp;
import clienteescritoriofitnutrition.dominio.DietaImp;
import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dominio.PacienteImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Consulta;
import clienteescritoriofitnutrition.pojo.Dieta;
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

public class FXMLFormularioConsultaController implements Initializable {

    @FXML
    private ComboBox<Paciente> cbPaciente;
    @FXML
    private ComboBox<Medico> cbMedico;
    @FXML
    private ComboBox<Dieta> cbDieta;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField tfHora;
    @FXML
    private TextField tfPeso;
    @FXML
    private TextField tfEstatura;
    @FXML
    private TextField tfTalla;
    @FXML
    private TextField tfImc;
    @FXML
    private TextField tfObservaciones;

    private Consulta consultaEdicion;
    private INotificador notificador;
    private Integer idMedicoSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarPacientes();
        cargarMedicos();
        cargarDietas();
    }

    public void inicializarDatos(Consulta consulta, INotificador notificador, Integer idMedicoSesion) {
        this.consultaEdicion = consulta;
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

    private void cargarDietas() {
        List<Dieta> lista = DietaImp.obtenerTodas();
        if (lista == null) {
            lista = new ArrayList<>();
        }
        cbDieta.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarDatosEdicion() {
        if (consultaEdicion == null) {
            return;
        }

        if (consultaEdicion.getFechaHora() != null && !consultaEdicion.getFechaHora().isEmpty()) {
            String[] partes = consultaEdicion.getFechaHora().split(" ");
            if (partes.length >= 2) {
                try {
                    dpFecha.setValue(LocalDate.parse(partes[0]));
                    String horaStr = partes[1];
                    if (horaStr.length() > 5) {
                        horaStr = horaStr.substring(0, 5);
                    }
                    tfHora.setText(horaStr);
                } catch (Exception e) {}
            }
        }
        
        tfPeso.setText(consultaEdicion.getPeso() != null ? String.valueOf(consultaEdicion.getPeso()) : "");
        tfEstatura.setText(consultaEdicion.getEstatura() != null ? String.valueOf(consultaEdicion.getEstatura()) : "");
        tfTalla.setText(valorFormulario(consultaEdicion.getTalla()));
        tfImc.setText(consultaEdicion.getImc() != null ? String.valueOf(consultaEdicion.getImc()) : "");
        tfObservaciones.setText(valorFormulario(consultaEdicion.getObservaciones()));

        seleccionarPaciente(consultaEdicion.getIdPaciente());
        seleccionarMedico(consultaEdicion.getIdMedico());
        seleccionarDieta(consultaEdicion.getIdDieta());
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

    private void seleccionarDieta(Integer idDieta) {
        if (idDieta == null) {
            return;
        }
        for (Dieta dieta : cbDieta.getItems()) {
            if (dieta.getIdDieta() != null && dieta.getIdDieta().equals(idDieta)) {
                cbDieta.setValue(dieta);
                return;
            }
        }
    }

    @FXML
    private void clickCalcularIMC(ActionEvent event) {
        try {
            double peso = Double.parseDouble(tfPeso.getText().trim());
            double estatura = Double.parseDouble(tfEstatura.getText().trim());
            if (estatura > 0) {
                double imc = peso / (estatura * estatura);
                tfImc.setText(String.format("%.2f", imc).replace(",", "."));
            } else {
                Utilidades.mostrarAlertaSimple("Error", "La estatura debe ser mayor a 0.", Alert.AlertType.WARNING);
            }
        } catch (NumberFormatException ex) {
            Utilidades.mostrarAlertaSimple("Error", "Ingrese valores numéricos válidos para peso y estatura.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clickGuardar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        Consulta consulta = construirObjeto();
        Respuesta respuesta = consultaEdicion == null
                ? ConsultaImp.registrar(consulta)
                : ConsultaImp.editar(consulta);

        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operación exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );

        if (!respuesta.isError()) {
            if (notificador != null) {
                notificador.notificarOperacionExitosa("consulta", "");
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
        String hora = tfHora.getText().trim();
        if (!hora.matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            mostrarCampoRequerido("La hora debe tener el formato HH:mm.");
            return false;
        }
        if (estaVacio(tfPeso.getText())) {
            mostrarCampoRequerido("El peso es obligatorio.");
            return false;
        }
        if (estaVacio(tfEstatura.getText())) {
            mostrarCampoRequerido("La estatura es obligatoria.");
            return false;
        }
        try {
            Double.parseDouble(tfPeso.getText().trim());
            Double.parseDouble(tfEstatura.getText().trim());
        } catch (NumberFormatException e) {
            mostrarCampoRequerido("Peso y Estatura deben ser números.");
            return false;
        }
        return true;
    }

    private Consulta construirObjeto() {
        Consulta consulta = consultaEdicion != null ? consultaEdicion : new Consulta();
        Paciente paciente = cbPaciente.getValue();
        Medico medico = cbMedico.getValue();
        Dieta dieta = cbDieta.getValue();

        String fecha = dpFecha.getValue().toString();
        String hora = tfHora.getText().trim() + ":00";
        consulta.setFechaHora(fecha + " " + hora);
        
        consulta.setPeso(Double.parseDouble(tfPeso.getText().trim()));
        consulta.setEstatura(Double.parseDouble(tfEstatura.getText().trim()));
        consulta.setTalla(valorFormulario(tfTalla.getText()));
        if (!estaVacio(tfImc.getText())) {
            consulta.setImc(Double.parseDouble(tfImc.getText().trim()));
        }
        consulta.setObservaciones(valorFormulario(tfObservaciones.getText()));
        
        consulta.setIdPaciente(paciente.getIdPaciente());
        consulta.setPaciente(paciente);
        consulta.setIdMedico(medico.getIdMedico());
        consulta.setMedico(medico);
        
        if (dieta != null) {
            consulta.setIdDieta(dieta.getIdDieta());
            consulta.setDieta(dieta);
        } else {
            consulta.setIdDieta(null);
            consulta.setDieta(null);
        }

        return consulta;
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
        Stage escenario = (Stage) tfPeso.getScene().getWindow();
        escenario.close();
    }
}
