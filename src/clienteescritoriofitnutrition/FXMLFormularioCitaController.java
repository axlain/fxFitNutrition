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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class FXMLFormularioCitaController implements Initializable {

    @FXML
    private ComboBox<Paciente> cbPaciente;
    @FXML
    private ComboBox<Medico> cbMedico;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<String> cbHora;
    @FXML
    private TextField tfObservaciones;

    private Cita citaEdicion;
    private INotificador notificador;
    private Integer idMedicoSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarCombos();
        cargarMedicos();
        configurarListenersHora();
    }

    private void configurarListenersHora() {
        dpFecha.valueProperty().addListener((obs, valorAnterior, valorNuevo) -> cargarHorasDisponibles());
        cbMedico.valueProperty().addListener((obs, valorAnterior, valorNuevo) -> cargarHorasDisponibles());
    }

    private void cargarHorasDisponibles() {
        LocalDate fecha = dpFecha.getValue();
        Medico medico = cbMedico.getValue();
        String horaActual = cbHora.getValue();

        if (fecha == null || medico == null || medico.getIdMedico() == null) {
            cbHora.setItems(FXCollections.observableArrayList());
            cbHora.setValue(null);
            return;
        }

        Integer idCitaExcluir = citaEdicion != null ? citaEdicion.getIdCita() : null;
        List<String> horas = CitaImp.obtenerHorasDisponibles(medico.getIdMedico(), fecha.toString(), idCitaExcluir);

        if (fecha.isEqual(LocalDate.now())) {
            LocalTime limite = LocalTime.now().plusHours(1);
            horas.removeIf(hora -> {
                try {
                    return LocalTime.parse(hora.length() > 5 ? hora.substring(0, 5) : hora).isBefore(limite);
                } catch (Exception e) {
                    return false;
                }
            });
        }

        if (horaActual != null && !horas.contains(horaActual)) {
            horas.add(horaActual);
            Collections.sort(horas);
        }

        cbHora.setItems(FXCollections.observableArrayList(horas));
        cbHora.setValue(horas.contains(horaActual) ? horaActual : null);
    }

    private void configurarCombos() {
        cbPaciente.setConverter(new StringConverter<Paciente>() {
            @Override
            public String toString(Paciente paciente) {
                if (paciente == null) {
                    return "";
                }

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
                if (medico == null) {
                    return "";
                }

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

        cbPaciente.valueProperty().addListener((obs, anterior, paciente) -> {
            if (idMedicoSesion == null) {
                if (paciente != null) {
                    seleccionarMedico(paciente.getIdMedico());
                    cbMedico.setDisable(true);
                } else {
                    cbMedico.setValue(null);
                    cbMedico.setDisable(false);
                }
            }
        });
    }

    public void inicializarDatos(Cita cita, INotificador notificador, Integer idMedicoSesion) {
        this.citaEdicion = cita;
        this.notificador = notificador;
        this.idMedicoSesion = idMedicoSesion;

        configurarFechaPicker();
        cargarPacientes();
        cargarDatosEdicion();
        aplicarMedicoSesion();
    }

    private void configurarFechaPicker() {
        LocalDate minima = LocalDate.now();
        dpFecha.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setDisable(vacia || fecha.isBefore(minima));
            }
        });
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
                if (!pacienteActivo(paciente)) {
                    continue;
                }
                if (idMedicoSesion == null || idMedicoSesion.equals(paciente.getIdMedico())) {
                    resultado.add(paciente);
                }
            }
        }

        cbPaciente.setItems(FXCollections.observableArrayList(resultado));
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

        tfObservaciones.setText(valorFormulario(citaEdicion.getObservaciones()));

        seleccionarPaciente(citaEdicion.getIdPaciente());
        seleccionarMedico(citaEdicion.getIdMedico());

        if (!horaCita.isEmpty()) {
            boolean esCancelada = citaEdicion.getIdEstadoCita() != null
                    && citaEdicion.getIdEstadoCita() == 3;
            if (!cbHora.getItems().contains(horaCita) && !esCancelada) {
                // Cita activa: re-agregar su hora original (el API la excluye del check).
                // Cita cancelada: no forzar la hora si ya está ocupada por otra cita.
                cbHora.getItems().add(horaCita);
                FXCollections.sort(cbHora.getItems());
            }
            cbHora.setValue(cbHora.getItems().contains(horaCita) ? horaCita : null);
        }
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

        LocalDate hoy = LocalDate.now();
        LocalDate fechaSeleccionada = dpFecha.getValue();
        if (fechaSeleccionada.isBefore(hoy)) {
            mostrarCampoRequerido("No se pueden programar citas en fechas pasadas.");
            return false;
        }

        if (cbHora.getValue() == null) {
            mostrarCampoRequerido("La hora es obligatoria.");
            return false;
        }

        if (!pacienteActivo(cbPaciente.getValue())) {
            mostrarCampoRequerido("No se puede registrar una cita para un paciente dado de baja.");
            return false;
        }

        return true;
    }

    private Cita construirObjeto() {
        Cita cita = citaEdicion != null ? citaEdicion : new Cita();

        Paciente paciente = cbPaciente.getValue();
        Medico medico = cbMedico.getValue();

        cita.setFecha(dpFecha.getValue().toString());
        cita.setHora(normalizarHora(cbHora.getValue()));
        cita.setObservaciones(valorFormulario(tfObservaciones.getText()));

        if (cita.getIdCita() == null) {
            cita.setIdEstadoCita(1);
        }

        cita.setIdPaciente(paciente.getIdPaciente());
        cita.setPaciente(paciente);

        cita.setIdMedico(medico.getIdMedico());
        cita.setMedico(medico);

        return cita;
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

    private String normalizarHora(String hora) {
        if (hora == null) {
            return "";
        }

        hora = hora.trim();

        if (hora.length() == 5) {
            return hora + ":00";
        }

        return hora;
    }

    private void mostrarCampoRequerido(String mensaje) {
        Utilidades.mostrarAlertaSimple("Campo requerido", mensaje, Alert.AlertType.WARNING);
    }

    private String valorFormulario(String valor) {
        return valor != null ? valor.trim() : "";
    }

    private String texto(String valor) {
        return valor != null ? valor : "";
    }

    private void cerrar() {
        Stage escenario = (Stage) cbHora.getScene().getWindow();
        escenario.close();
    }
}