package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.CitaImp;
import clienteescritoriofitnutrition.dominio.ConsultaImp;
import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dominio.PacienteImp;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.interfaz.INotificador;
import clienteescritoriofitnutrition.pojo.Cita;
import clienteescritoriofitnutrition.pojo.Consulta;
import clienteescritoriofitnutrition.pojo.Medico;
import clienteescritoriofitnutrition.pojo.Paciente;
import clienteescritoriofitnutrition.pojo.Usuario;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FXMLPrincipalController implements Initializable, INotificador {

    @FXML
    private Label lbTitulo;
    @FXML
    private Label lbSesion;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnMedicos;
    @FXML
    private Button btnPacientes;
    @FXML
    private Button btnCitas;
    @FXML
    private Button btnConsultas;
    @FXML
    private Button btnDietas;
    @FXML
    private Button btnAlimentos;
    @FXML
    private BorderPane bpContenedor;
    @FXML
    private VBox vbDashboard;
    @FXML
    private Label lbTotalPacientes;
    @FXML
    private Label lbActivosPacientes;
    @FXML
    private Label lbTotalMedicos;
    @FXML
    private Label lbMedicosActivos;
    @FXML
    private Label lbCitasHoy;
    @FXML
    private Label lbCitasPendientes;
    @FXML
    private Label lbConsultasTotal;
    @FXML
    private Label lbConsultasHoy;
    @FXML
    private VBox vbPacientesRecientes;
    @FXML
    private VBox vbCitasHoy;
    @FXML
    private Label lbFechaCitas;

    private RSAutenticar sesion;
    private Parent dashboard;
    private double desplazamientoX;
    private double desplazamientoY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboard = vbDashboard;
    }

    public void inicializarSesion(RSAutenticar sesion) {
        this.sesion = sesion;
        cargarDatosSesion();
        aplicarPermisos();
        cargarDashboard();
    }

    private void cargarDatosSesion() {
        String nombre = "Usuario";
        String tipo = "";

        if (sesion != null) {
            tipo = sesion.getTipoUsuario() != null ? sesion.getTipoUsuario() : "";
            if (sesion.getAdministrador() != null) {
                nombre = obtenerNombreUsuario(sesion.getAdministrador().getUsuario());
            } else if (sesion.getMedico() != null) {
                nombre = obtenerNombreUsuario(sesion.getMedico().getUsuario());
            }
        }

        lbTitulo.setText("Dashboard");
        lbSesion.setText(nombre + (tipo.isEmpty() ? "" : " | " + tipo));
    }

    @FXML
    private void clickCerrarAplicacion(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void clickMinimizar(ActionEvent event) {
        Stage stage = (Stage) bpContenedor.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void clickMaximizar(ActionEvent event) {
        Stage stage = (Stage) bpContenedor.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }

    @FXML
    private void iniciarArrastre(MouseEvent event) {
        Stage stage = (Stage) bpContenedor.getScene().getWindow();
        desplazamientoX = stage.getX() - event.getScreenX();
        desplazamientoY = stage.getY() - event.getScreenY();
    }

    @FXML
    private void arrastrarVentana(MouseEvent event) {
        Stage stage = (Stage) bpContenedor.getScene().getWindow();
        if (!stage.isMaximized()) {
            stage.setX(event.getScreenX() + desplazamientoX);
            stage.setY(event.getScreenY() + desplazamientoY);
        }
    }

    private String obtenerNombreUsuario(Usuario usuario) {
        if (usuario == null) {
            return "Usuario";
        }
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellido = usuario.getApellidoPaterno() != null ? usuario.getApellidoPaterno() : "";
        String nombreCompleto = (nombre + " " + apellido).trim();
        return nombreCompleto.isEmpty() ? "Usuario" : nombreCompleto;
    }

    private void aplicarPermisos() {
        boolean esAdministrador = esAdministrador();
        btnMedicos.setDisable(!esAdministrador);
        btnPacientes.setDisable(false);
        btnCitas.setDisable(false);
        btnConsultas.setDisable(false);
        btnDietas.setDisable(false);
        btnAlimentos.setDisable(false);
    }

    private boolean esAdministrador() {
        if (sesion == null || sesion.getAdministrador() != null) {
            return true;
        }
        String tipo = sesion.getTipoUsuario();
        return tipo != null && tipo.toLowerCase().contains("administrador");
    }

    @FXML
    private void clickDashboard(ActionEvent event) {
        lbTitulo.setText("Dashboard");
        bpContenedor.setCenter(dashboard);
        cargarDashboard();
    }

    @FXML
    private void clickMedicos(ActionEvent event) {
        lbTitulo.setText("Medicos");
        cargarModulo("FXMLAdministracionMedicos.fxml", "No se pudo cargar el modulo de medicos.");
    }

    @FXML
    private void clickPacientes(ActionEvent event) {
        lbTitulo.setText("Pacientes");
        cargarModulo("FXMLAdministracionPacientes.fxml", "No se pudo cargar el modulo de pacientes.");
    }

    @FXML
    private void clickCitas(ActionEvent event) {
        lbTitulo.setText("Citas");
        cargarModulo("FXMLAgendaCitas.fxml", "El modulo de citas esta pendiente de integracion.");
    }

    @FXML
    private void clickConsultas(ActionEvent event) {
        lbTitulo.setText("Consultas");
        cargarModulo("FXMLAdministracionConsultas.fxml", "El modulo de consultas esta pendiente de integracion.");
    }

    @FXML
    private void clickDietas(ActionEvent event) {
        lbTitulo.setText("Dietas");
        cargarModulo("FXMLAdministracionDietas.fxml", "El modulo de dietas esta pendiente de integracion.");
    }

    @FXML
    private void clickAlimentos(ActionEvent event) {
        lbTitulo.setText("Alimentos");
        cargarModulo("FXMLAdministracionAlimentos.fxml", "El modulo de alimentos esta pendiente de integracion.");
    }

    private void cargarDashboard() {
        List<Paciente> pacientes = PacienteImp.obtenerTodos();
        if (pacientes == null) {
            pacientes = new ArrayList<Paciente>();
        }

        List<Medico> medicos = obtenerMedicosDashboard();
        List<Cita> citasHoy = obtenerCitasHoy(medicos);
        List<Consulta> consultas = obtenerConsultasDashboard(pacientes);

        lbTotalPacientes.setText(String.valueOf(contarActivosPacientes(pacientes)));
        lbActivosPacientes.setText(pacientes.size() + " registrados");
        lbTotalMedicos.setText(String.valueOf(medicos.size()));
        lbMedicosActivos.setText(contarActivosMedicos(medicos) + " activos");
        lbCitasHoy.setText(String.valueOf(citasHoy.size()));
        lbCitasPendientes.setText(contarCitasPorEstado(citasHoy, "Pendiente") + " pendientes");
        lbConsultasTotal.setText(String.valueOf(consultas.size()));
        lbConsultasHoy.setText(contarConsultasHoy(consultas) + " hoy");

        LocalDate hoy = LocalDate.now();
        lbFechaCitas.setText(hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        cargarPacientesRecientes(pacientes);
        cargarCitasDelDia(citasHoy);
    }

    private List<Medico> obtenerMedicosDashboard() {
        List<Medico> medicos = new ArrayList<Medico>();
        if (sesion != null && sesion.getMedico() != null) {
            medicos.add(sesion.getMedico());
            return medicos;
        }

        List<Medico> respuesta = MedicoImp.obtenerTodos();
        if (respuesta != null) {
            medicos.addAll(respuesta);
        }
        return medicos;
    }

    private List<Cita> obtenerCitasHoy(List<Medico> medicos) {
        List<Cita> citas = new ArrayList<Cita>();
        String fecha = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        for (Medico medico : medicos) {
            if (medico != null && medico.getIdMedico() != null) {
                List<Cita> citasMedico = CitaImp.obtenerPorMedico(medico.getIdMedico(), fecha);
                if (citasMedico != null) {
                    citas.addAll(citasMedico);
                }
            }
        }
        return citas;
    }

    private List<Consulta> obtenerConsultasDashboard(List<Paciente> pacientes) {
        List<Consulta> consultas = new ArrayList<Consulta>();
        for (Paciente paciente : pacientes) {
            if (paciente != null && paciente.getIdPaciente() != null) {
                List<Consulta> historial = ConsultaImp.obtenerHistorialPaciente(paciente.getIdPaciente());
                if (historial != null) {
                    consultas.addAll(historial);
                }
            }
        }
        return consultas;
    }

    private int contarActivosPacientes(List<Paciente> pacientes) {
        int total = 0;
        for (Paciente paciente : pacientes) {
            if ("Activo".equalsIgnoreCase(paciente.getEstatusUsuario())) {
                total++;
            }
        }
        return total;
    }

    private int contarActivosMedicos(List<Medico> medicos) {
        int total = 0;
        for (Medico medico : medicos) {
            if ("Activo".equalsIgnoreCase(medico.getEstatusUsuario())) {
                total++;
            }
        }
        return total;
    }

    private int contarCitasPorEstado(List<Cita> citas, String estado) {
        int total = 0;
        for (Cita cita : citas) {
            if (estado.equalsIgnoreCase(obtenerEstadoCita(cita))) {
                total++;
            }
        }
        return total;
    }

    private int contarConsultasHoy(List<Consulta> consultas) {
        int total = 0;
        String hoy = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        for (Consulta consulta : consultas) {
            if (consulta.getFechaHora() != null && consulta.getFechaHora().startsWith(hoy)) {
                total++;
            }
        }
        return total;
    }

    private void cargarPacientesRecientes(List<Paciente> pacientes) {
        vbPacientesRecientes.getChildren().clear();
        if (pacientes.isEmpty()) {
            vbPacientesRecientes.getChildren().add(crearTextoVacio("Sin pacientes registrados."));
            return;
        }

        int limite = Math.min(3, pacientes.size());
        for (int i = 0; i < limite; i++) {
            Paciente paciente = pacientes.get(i);
            HBox fila = new HBox();
            fila.setSpacing(12);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label nombre = new Label(paciente.getNombreCompleto());
            nombre.setPrefWidth(190);
            Label medico = new Label(paciente.getMedicoAsignado());
            medico.setPrefWidth(140);
            medico.getStyleClass().add("label-subtitulo");
            Label estatus = new Label(paciente.getEstatusUsuario());
            estatus.getStyleClass().add(obtenerClaseEstatusUsuario(paciente.getEstatusUsuario()));

            fila.getChildren().addAll(nombre, medico, estatus);
            vbPacientesRecientes.getChildren().add(fila);
        }
    }

    private void cargarCitasDelDia(List<Cita> citas) {
        vbCitasHoy.getChildren().clear();
        if (citas.isEmpty()) {
            vbCitasHoy.getChildren().add(crearTextoVacio("Sin citas registradas para hoy."));
            return;
        }

        for (Cita cita : citas) {
            HBox fila = new HBox();
            fila.setSpacing(12);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            Label hora = new Label(formatearHora(cita.getHora()));
            hora.setPrefWidth(52);
            hora.getStyleClass().add("label-stat-valor");

            VBox datos = new VBox();
            HBox.setHgrow(datos, Priority.ALWAYS);
            Label paciente = new Label(obtenerNombrePaciente(cita));
            Label detalle = new Label(obtenerNombreMedico(cita) + " - " + obtenerTexto(cita.getObservaciones()));
            detalle.getStyleClass().add("label-subtitulo");
            datos.getChildren().addAll(paciente, detalle);

            Label estado = new Label(obtenerEstadoCita(cita));
            estado.getStyleClass().add(obtenerClaseEstadoCita(estado.getText()));

            fila.getChildren().addAll(hora, datos, estado);
            vbCitasHoy.getChildren().add(fila);
        }
    }

    private Label crearTextoVacio(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("label-subtitulo");
        return label;
    }

    private String obtenerClaseEstatusUsuario(String estatus) {
        if ("Activo".equalsIgnoreCase(estatus)) {
            return "pill-activo";
        }
        return "pill-baja";
    }

    private String obtenerClaseEstadoCita(String estado) {
        if ("Pendiente".equalsIgnoreCase(estado)) {
            return "pill-pendiente";
        }
        if ("Cancelada".equalsIgnoreCase(estado) || "Ausente".equalsIgnoreCase(estado)) {
            return "pill-cancelada";
        }
        return "pill-confirmada";
    }

    private String obtenerEstadoCita(Cita cita) {
        if (cita != null && cita.getEstadoCita() != null && cita.getEstadoCita().getNombre() != null) {
            return cita.getEstadoCita().getNombre();
        }
        return "";
    }

    private String obtenerNombrePaciente(Cita cita) {
        if (cita != null && cita.getPaciente() != null) {
            return cita.getPaciente().getNombreCompleto();
        }
        return "Paciente";
    }

    private String obtenerNombreMedico(Cita cita) {
        if (cita != null && cita.getMedico() != null) {
            return cita.getMedico().getNombreCompleto();
        }
        return "Medico";
    }

    private String obtenerTexto(String texto) {
        return texto != null && !texto.trim().isEmpty() ? texto : "Sin observaciones";
    }

    private String formatearHora(String hora) {
        if (hora == null) {
            return "";
        }
        return hora.length() >= 5 ? hora.substring(0, 5) : hora;
    }

    private void cargarModulo(String nombreFXML, String mensajeError) {
        try {
            URL recurso = getClass().getResource(nombreFXML);
            if (recurso == null) {
                Utilidades.mostrarAlertaSimple("Modulo no disponible", mensajeError, Alert.AlertType.INFORMATION);
                return;
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            Parent vista = loader.load();
            bpContenedor.setCenter(vista);
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", mensajeError, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickCerrarSesion(ActionEvent event) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource("FXMLInicioSesion.fxml"));
            Scene escenaLogin = new Scene(vista);
            Stage stage = (Stage) bpContenedor.getScene().getWindow();
            Stage stageLogin = new Stage();
            stageLogin.setScene(escenaLogin);
            stageLogin.setTitle("FIT NUTRITION - Iniciar sesion");
            stageLogin.show();
            stage.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo cerrar la sesion.", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void notificarOperacionExitosa(String operacion, String nombre) {
    }
}
