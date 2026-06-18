package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.CitaImp;
import clienteescritoriofitnutrition.dominio.ConsultaImp;
import clienteescritoriofitnutrition.dominio.DietaImp;
import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dominio.PacienteImp;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Cita;
import clienteescritoriofitnutrition.pojo.Consulta;
import clienteescritoriofitnutrition.pojo.Dieta;
import clienteescritoriofitnutrition.pojo.Medico;
import clienteescritoriofitnutrition.pojo.Paciente;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FXMLFormularioConsultaController implements Initializable {

    @FXML private ComboBox<Paciente> cbPaciente;
    @FXML private ComboBox<Medico> cbMedico;
    @FXML private ComboBox<Cita> cbCita;
    @FXML private ComboBox<Dieta> cbDieta;
    @FXML private TextField tfPeso;
    @FXML private TextField tfEstatura;
    @FXML private TextField tfTalla;
    @FXML private TextField tfImc;
    @FXML private TextField tfObservaciones;

    private Consulta consultaEdicion;
    private INotificador notificador;
    private Integer idMedicoSesion;
    private boolean cargandoDatos = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombos();
        bloquearComboCitas("Selecciona primero un paciente");
        cargarMedicos();
        cargarDietas();
        configurarCamposNumericos();
    }

    private void configurarCamposNumericos() {
        TextFormatter<String> formatterPeso = new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            return nuevoTexto.matches("\\d*\\.?\\d*") ? change : null;
        });
        TextFormatter<String> formatterEstatura = new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            return nuevoTexto.matches("\\d*\\.?\\d*") ? change : null;
        });
        tfPeso.setTextFormatter(formatterPeso);
        tfEstatura.setTextFormatter(formatterEstatura);
    }

    private void configurarCombos() {
        cbPaciente.setConverter(new StringConverter<Paciente>() {
            @Override
            public String toString(Paciente paciente) {
                if (paciente == null) return "";

                if (paciente.getUsuario() != null) {
                    String nombre = texto(paciente.getUsuario().getNombre());
                    String apellido = texto(paciente.getUsuario().getApellidoPaterno());
                    return (nombre + " " + apellido + " - " + texto(paciente.getCodigoAcceso())).trim();
                }

                return texto(paciente.getCodigoAcceso());
            }

            @Override
            public Paciente fromString(String string) {
                return null;
            }
        });

        cbMedico.setConverter(new StringConverter<Medico>() {
            @Override
            public String toString(Medico medico) {
                if (medico == null) return "";

                if (medico.getUsuario() != null) {
                    String nombre = texto(medico.getUsuario().getNombre());
                    String apellido = texto(medico.getUsuario().getApellidoPaterno());
                    return (nombre + " " + apellido + " - " + texto(medico.getNumeroPersonal())).trim();
                }

                return texto(medico.getNumeroPersonal());
            }

            @Override
            public Medico fromString(String string) {
                return null;
            }
        });

        cbDieta.setConverter(new StringConverter<Dieta>() {
            @Override
            public String toString(Dieta dieta) {
                return dieta == null ? "" : texto(dieta.getNombre());
            }

            @Override
            public Dieta fromString(String string) {
                return null;
            }
        });

        cbCita.setConverter(new StringConverter<Cita>() {
            @Override
            public String toString(Cita cita) {
                if (cita == null) return "";

                String fecha = texto(cita.getFecha());
                String hora = texto(cita.getHora());

                if (hora.length() > 5) {
                    hora = hora.substring(0, 5);
                }

                return fecha + " " + hora;
            }

            @Override
            public Cita fromString(String string) {
                return null;
            }
        });

        cbPaciente.setOnAction(event -> {
            if (cargandoDatos) return;

            Paciente paciente = cbPaciente.getValue();

            if (paciente == null || paciente.getIdPaciente() == null) {
                bloquearComboCitas("Selecciona primero un paciente");
                if (idMedicoSesion == null) {
                    cbMedico.setValue(null);
                    cbMedico.setDisable(false);
                }
                return;
            }

            if (idMedicoSesion == null) {
                seleccionarMedico(paciente.getIdMedico());
                cbMedico.setDisable(true);
            }

            boolean modoEdicion = consultaEdicion != null
                    && paciente.getIdPaciente().equals(consultaEdicion.getIdPaciente());
            cargarCitasPaciente(paciente.getIdPaciente(), modoEdicion);
        });
    }

    public void inicializarDatos(Consulta consulta, INotificador notificador, Integer idMedicoSesion) {
        this.consultaEdicion = consulta;
        this.notificador = notificador;
        this.idMedicoSesion = idMedicoSesion;

        cargarPacientes();
        aplicarMedicoSesion();
        cargarDatosEdicion();
    }

    private void aplicarMedicoSesion() {
        if (idMedicoSesion != null) {
            seleccionarMedico(idMedicoSesion);
            cbMedico.setDisable(true);
        }
    }

    private void cargarPacientes() {
        List<Paciente> lista = PacienteImp.obtenerTodos();
        List<Paciente> resultado = new ArrayList<>();

        if (lista != null) {
            for (Paciente paciente : lista) {
                if (esPacienteDeEdicion(paciente)) {
                    resultado.add(paciente);
                    continue;
                }
                if (!pacienteActivo(paciente)) continue;
                if (idMedicoSesion != null && !idMedicoSesion.equals(paciente.getIdMedico())) continue;
                resultado.add(paciente);
            }
        }

        cbPaciente.setItems(FXCollections.observableArrayList(resultado));
    }

    private boolean esPacienteDeEdicion(Paciente paciente) {
        return consultaEdicion != null
                && paciente.getIdPaciente() != null
                && paciente.getIdPaciente().equals(consultaEdicion.getIdPaciente());
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

    private void cargarCitasPaciente(Integer idPaciente) {
        cargarCitasPaciente(idPaciente, false);
    }

    private void cargarCitasPaciente(Integer idPaciente, boolean modoEdicion) {
        List<Cita> lista;

        if (modoEdicion) {
            lista = CitaImp.obtenerTodosPorPaciente(idPaciente);
            if (lista == null || lista.isEmpty()) {
                bloquearComboCitas("Sin citas para este paciente");
                return;
            }
        } else {
            List<Cita> vigentes = CitaImp.obtenerPorIdPaciente(idPaciente);
            List<Cita> citasHoy = new ArrayList<>();
            String hoy = LocalDate.now().toString();
            if (vigentes != null) {
                for (Cita cita : vigentes) {
                    if (hoy.equals(cita.getFecha())) {
                        citasHoy.add(cita);
                    }
                }
            }
            if (citasHoy.isEmpty()) {
                bloquearComboCitas("Sin citas para hoy");
                return;
            }
            lista = citasHoy;
        }

        cbCita.setItems(FXCollections.observableArrayList(lista));
        cbCita.setDisable(false);
        cbCita.setPromptText("Seleccionar cita");
    }

    private void bloquearComboCitas(String mensaje) {
        cbCita.getItems().clear();
        cbCita.setValue(null);
        cbCita.setDisable(true);
        cbCita.setPromptText(mensaje);
    }

    private void cargarDatosEdicion() {
        if (consultaEdicion == null) {
            return;
        }

        tfPeso.setText(consultaEdicion.getPeso() != null ? String.valueOf(consultaEdicion.getPeso()) : "");
        tfEstatura.setText(consultaEdicion.getEstatura() != null ? String.valueOf(consultaEdicion.getEstatura()) : "");
        tfTalla.setText(valorFormulario(consultaEdicion.getTalla()));
        tfImc.setText(consultaEdicion.getImc() != null ? String.valueOf(consultaEdicion.getImc()) : "");
        tfObservaciones.setText(valorFormulario(consultaEdicion.getObservaciones()));

        cargandoDatos = true;
        seleccionarPaciente(consultaEdicion.getIdPaciente());
        seleccionarMedico(consultaEdicion.getIdMedico());
        seleccionarDieta(consultaEdicion.getIdDieta());
        cargandoDatos = false;

        if (consultaEdicion.getIdPaciente() != null) {
            cargarCitasPaciente(consultaEdicion.getIdPaciente(), true);
            seleccionarCitaPorFechaHora(consultaEdicion.getFecha(), consultaEdicion.getHora());
        }
    }

    private void seleccionarPaciente(Integer idPaciente) {
        if (idPaciente == null) return;

        for (Paciente paciente : cbPaciente.getItems()) {
            if (paciente.getIdPaciente() != null && paciente.getIdPaciente().equals(idPaciente)) {
                cbPaciente.setValue(paciente);
                return;
            }
        }
    }

    private void seleccionarMedico(Integer idMedico) {
        if (idMedico == null) return;

        for (Medico medico : cbMedico.getItems()) {
            if (medico.getIdMedico() != null && medico.getIdMedico().equals(idMedico)) {
                cbMedico.setValue(medico);
                return;
            }
        }
    }

    private void seleccionarDieta(Integer idDieta) {
        if (idDieta == null) return;

        for (Dieta dieta : cbDieta.getItems()) {
            if (dieta.getIdDieta() != null && dieta.getIdDieta().equals(idDieta)) {
                cbDieta.setValue(dieta);
                return;
            }
        }
    }

    private void seleccionarCita(Integer idCita) {
        if (idCita == null) return;

        for (Cita cita : cbCita.getItems()) {
            if (idCita.equals(cita.getIdCita())) {
                cbCita.setValue(cita);
                return;
            }
        }
    }

    private void seleccionarCitaPorFechaHora(String fechaConsulta, String horaConsulta) {
        if (fechaConsulta == null || horaConsulta == null) {
            return;
        }

        for (Cita cita : cbCita.getItems()) {
            if (fechaConsulta.equals(cita.getFecha())
                    && normalizarHora(horaConsulta).equals(normalizarHora(cita.getHora()))) {
                cbCita.setValue(cita);
                return;
            }
        }
    }

    @FXML
    private void clickCrearCita(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLFormularioCita.fxml"));
            Parent vista = loader.load();

            FXMLFormularioCitaController controlador = loader.getController();
            controlador.inicializarDatos(null, null, idMedicoSesion);

            Stage escenario = new Stage();
            escenario.setScene(new Scene(vista));
            escenario.setTitle("Registrar Cita");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

            Paciente paciente = cbPaciente.getValue();

            if (paciente != null && paciente.getIdPaciente() != null) {
                cargarCitasPaciente(paciente.getIdPaciente());
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    "No se pudo abrir el formulario de cita.",
                    Alert.AlertType.ERROR
            );
        }
    }

    @FXML
    private void clickCalcularIMC(ActionEvent event) {
        if (!validarPesoYEstatura()) {
            return;
        }

        double peso = Double.parseDouble(tfPeso.getText().trim());
        double estatura = Double.parseDouble(tfEstatura.getText().trim());

        double imc = peso / (estatura * estatura);
        tfImc.setText(String.format("%.2f", imc).replace(",", "."));
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

        if (cbCita.getValue() == null) {
            mostrarCampoRequerido("Debe seleccionar una cita para tomar la fecha y hora.");
            return false;
        }

        if (cbDieta.getValue() == null) {
            mostrarCampoRequerido("Debe asignar una dieta antes de guardar la consulta.");
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

        if (estaVacio(tfTalla.getText())) {
            mostrarCampoRequerido("La talla es obligatoria.");
            return false;
        }

        if (!pacienteActivo(cbPaciente.getValue())) {
            mostrarCampoRequerido("No se puede registrar una consulta para un paciente dado de baja.");
            return false;
        }

        if (!validarPesoYEstatura()) {
            return false;
        }

        if (estaVacio(tfImc.getText())) {
            clickCalcularIMC(null);
        }

        return true;
    }

    private boolean validarPesoYEstatura() {
        if (estaVacio(tfPeso.getText())) {
            mostrarCampoRequerido("El peso es obligatorio.");
            return false;
        }

        if (estaVacio(tfEstatura.getText())) {
            mostrarCampoRequerido("La estatura es obligatoria.");
            return false;
        }

        double peso;
        double estatura;

        try {
            peso = Double.parseDouble(tfPeso.getText().trim());
            estatura = Double.parseDouble(tfEstatura.getText().trim());
        } catch (NumberFormatException e) {
            mostrarCampoRequerido("Peso y estatura deben ser valores numéricos. Ejemplo: peso 54 y estatura 1.55.");
            return false;
        }

        if (peso <= 0 || peso > 400) {
            mostrarCampoRequerido("El peso debe estar en kilogramos y ser un valor válido. Ejemplo: 54.");
            return false;
        }

        if (estatura > 3) {
            mostrarCampoRequerido("La estatura debe capturarse en metros. Ejemplo correcto: 1.55, no 155.");
            return false;
        }

        if (estatura < 0.50 || estatura > 2.50) {
            mostrarCampoRequerido("La estatura debe estar entre 0.50 m y 2.50 m.");
            return false;
        }

        return true;
    }

    private boolean pacienteActivo(Paciente paciente) {
        if (paciente == null) {
            return false;
        }

        if (paciente.getUsuario() == null || paciente.getUsuario().getEstatus() == null) {
            return true;
        }

        String estatus = paciente.getUsuario().getEstatus().getNombre();

        return estatus != null && estatus.equalsIgnoreCase("Activo");
    }

    private Consulta construirObjeto() {
        Consulta consulta = consultaEdicion != null ? consultaEdicion : new Consulta();

        Paciente paciente = cbPaciente.getValue();
        Medico medico = cbMedico.getValue();
        Cita cita = cbCita.getValue();
        Dieta dieta = cbDieta.getValue();

        consulta.setFecha(cita.getFecha());
        consulta.setHora(normalizarHora(cita.getHora()));

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

        consulta.setIdDieta(dieta.getIdDieta());
        consulta.setDieta(dieta);

        return consulta;
    }

    private String normalizarHora(String hora) {
        if (hora == null) {
            return "";
        }

        hora = hora.trim();

        if (hora.length() == 5) {
            return hora + ":00";
        }

        if (hora.length() > 8) {
            return hora.substring(0, 8);
        }

        return hora;
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

    private String texto(String valor) {
        return valor != null ? valor : "";
    }

    private void cerrar() {
        Stage escenario = (Stage) tfPeso.getScene().getWindow();
        escenario.close();
    }
}