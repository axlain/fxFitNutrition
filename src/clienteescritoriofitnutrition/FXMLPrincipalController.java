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
import clienteescritoriofitnutrition.utilidad.Responsividad;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.Modality;
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
    private Button btnPerfil;
    @FXML
    private BorderPane bpContenedor;
    @FXML
    private VBox vbDashboard;
    @FXML
    private Label lbNombrePacientes;
    @FXML
    private Label lbTotalPacientes;
    @FXML
    private Label lbActivosPacientes;
    @FXML
    private Label lbTotalMedicos;
    @FXML
    private Label lbMedicosActivos;
    @FXML
    private VBox cardMedicos;
    @FXML
    private Label lbCitasHoy;
    @FXML
    private Label lbCitasPendientes;
    @FXML
    private Label lbNombreConsultas;
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
    @FXML
    private Label lbFechaFooter;

    private RSAutenticar sesion;
    private Parent dashboard;
    private double desplazamientoX;
    private double desplazamientoY;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dashboard = vbDashboard;
        cargarFechaFooter();
        // Escalado proporcional para el dashboard y los modulos cargados en el contenedor central.
        Responsividad.aplicar(bpContenedor, 15.0, 1240.0, 13.0, 20.0);
    }

    private void cargarFechaFooter() {
        Locale locale = new Locale("es", "MX");
        lbFechaFooter.setText(LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE dd 'de' MMMM 'de' yyyy", locale)));
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
        // En sesion de medico la tarjeta "Medicos" mostraria solo a el mismo: no aporta, se oculta.
        cardMedicos.setVisible(esAdministrador);
        cardMedicos.setManaged(esAdministrador);
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
        cargarModulo("FXMLAdministracionMedicos.fxml", "Medicos", "No se pudo cargar el modulo de medicos.");
    }

    @FXML
    private void clickPacientes(ActionEvent event) {
        cargarModulo("FXMLAdministracionPacientes.fxml", "Pacientes", "No se pudo cargar el modulo de pacientes.");
    }

    @FXML
    private void clickCitas(ActionEvent event) {
        cargarModulo("FXMLAgendaCitas.fxml", "Citas", "El modulo de citas esta pendiente de integracion.");
    }

    @FXML
    private void clickConsultas(ActionEvent event) {
        cargarModulo("FXMLAdministracionConsultas.fxml", "Consultas", "El modulo de consultas esta pendiente de integracion.");
    }

    @FXML
    private void clickDietas(ActionEvent event) {
        cargarModulo("FXMLAdministracionDietas.fxml", "Dietas", "El modulo de dietas esta pendiente de integracion.");
    }

    @FXML
    private void clickAlimentos(ActionEvent event) {
        cargarModulo("FXMLAdministracionAlimentos.fxml", "Alimentos", "El modulo de alimentos esta pendiente de integracion.");
    }

    @FXML
    private void clickPerfil(ActionEvent event) {
        cargarModulo("FXMLPerfil.fxml", "Perfil", "El modulo de perfil esta pendiente de integracion.");
    }

    // Accesos directos desde el dashboard (heuristica: flexibilidad y eficiencia).
    @FXML
    private void clickVerPacientes(MouseEvent event) {
        cargarModulo("FXMLAdministracionPacientes.fxml", "Pacientes", "No se pudo cargar el modulo de pacientes.");
    }

    @FXML
    private void clickVerCitas(MouseEvent event) {
        cargarModulo("FXMLAgendaCitas.fxml", "Citas", "No se pudo cargar el modulo de citas.");
    }

    private void cargarDashboard() {
        boolean esAdmin = esAdministrador();
        Integer idMedico = obtenerIdMedicoSesion();

        List<Paciente> pacientes = obtenerPacientesDashboard(idMedico, esAdmin);
        List<Medico> medicos = obtenerMedicosDashboard();
        List<Cita> citasHoy = obtenerCitasHoy(medicos);
        List<Consulta> consultas = obtenerConsultasDashboard(pacientes, idMedico, esAdmin);

        int pacientesActivos = contarActivosPacientes(pacientes);

        // Las metricas cambian segun el rol: el admin ve la clinica completa; el medico ve solo lo suyo.
        lbNombrePacientes.setText(esAdmin ? "Pacientes activos" : "Mis pacientes");
        lbTotalPacientes.setText(String.valueOf(pacientesActivos));
        lbActivosPacientes.setText("de " + pacientes.size() + (esAdmin ? " registrados" : " asignados"));
        // Un medico dado de baja desaparece (no queda inactivo): activos == total. El dato util del dia
        // es cuantos tienen agenda hoy (esta tarjeta solo se muestra al admin).
        lbTotalMedicos.setText(String.valueOf(medicos.size()));
        lbMedicosActivos.setText(contarMedicosConCitaHoy(citasHoy) + " con cita hoy");
        lbCitasHoy.setText(String.valueOf(citasHoy.size()));
        lbCitasPendientes.setText(contarCitasPorAtender(citasHoy) + " por atender");
        lbNombreConsultas.setText(esAdmin ? "Consultas" : "Mis consultas");
        lbConsultasTotal.setText(String.valueOf(consultas.size()));
        lbConsultasHoy.setText(contarConsultasEsteMes(consultas) + " este mes");

        LocalDate hoy = LocalDate.now();
        lbFechaCitas.setText(hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        cargarPacientesRecientes(idMedico, esAdmin);
        cargarCitasDelDia(citasHoy);
    }

    private Integer obtenerIdMedicoSesion() {
        if (sesion != null && sesion.getMedico() != null) {
            return sesion.getMedico().getIdMedico();
        }
        return null;
    }

    // Admin: toda la clinica. Medico: solo sus pacientes asignados.
    private List<Paciente> obtenerPacientesDashboard(Integer idMedico, boolean esAdmin) {
        List<Paciente> todos = PacienteImp.obtenerTodos();
        if (todos == null) {
            todos = new ArrayList<Paciente>();
        }
        if (esAdmin || idMedico == null) {
            return todos;
        }
        List<Paciente> mios = new ArrayList<Paciente>();
        for (Paciente paciente : todos) {
            if (paciente != null && idMedico.equals(paciente.getIdMedico())) {
                mios.add(paciente);
            }
        }
        return mios;
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

    // Medico: sus consultas directo por id (1 llamada). Admin: agrega el historial de todos los pacientes.
    private List<Consulta> obtenerConsultasDashboard(List<Paciente> pacientes, Integer idMedico, boolean esAdmin) {
        if (!esAdmin && idMedico != null) {
            List<Consulta> mias = ConsultaImp.obtenerHistorialMedico(idMedico);
            return mias != null ? mias : new ArrayList<Consulta>();
        }
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

    private int contarCitasPorAtender(List<Cita> citas) {
        int total = 0;
        for (Cita cita : citas) {
            String estado = obtenerEstadoCita(cita);
            if ("Confirmada".equalsIgnoreCase(estado)
                    || "Pendiente".equalsIgnoreCase(estado)
                    || "Reagendada".equalsIgnoreCase(estado)) {
                total++;
            }
        }
        return total;
    }

    private int contarMedicosConCitaHoy(List<Cita> citas) {
        Set<Integer> ids = new HashSet<Integer>();
        for (Cita cita : citas) {
            if (cita.getIdMedico() != null) {
                ids.add(cita.getIdMedico());
            }
        }
        return ids.size();
    }

    private int contarConsultasEsteMes(List<Consulta> consultas) {
        String mes = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        int total = 0;
        for (Consulta consulta : consultas) {
            if (consulta.getFecha() != null && consulta.getFecha().startsWith(mes)) {
                total++;
            }
        }
        return total;
    }

    // "Pacientes recientes" reales (ordenados por fecha_registro en el API). Admin: global; medico: solo los suyos.
    private void cargarPacientesRecientes(Integer idMedico, boolean esAdmin) {
        vbPacientesRecientes.getChildren().clear();
        List<Paciente> recientes = (esAdmin || idMedico == null)
                ? PacienteImp.obtenerRecientes(3)
                : PacienteImp.obtenerRecientesMedico(idMedico, 3);
        if (recientes == null || recientes.isEmpty()) {
            vbPacientesRecientes.getChildren().add(crearTextoVacio("Sin pacientes registrados."));
            return;
        }

        EventHandler<MouseEvent> irPacientes = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                cargarModulo("FXMLAdministracionPacientes.fxml", "Pacientes", "No se pudo cargar el modulo de pacientes.");
            }
        };

        for (Paciente paciente : recientes) {
            HBox fila = new HBox();
            fila.setSpacing(12);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            fila.getStyleClass().add("fila-clic");
            fila.setOnMouseClicked(irPacientes);

            Label nombre = new Label(paciente.getNombreCompleto());
            nombre.getStyleClass().add("fila-titulo");
            nombre.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(nombre, Priority.ALWAYS);

            Label medico = new Label(paciente.getMedicoAsignado());
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
            final Cita citaFila = cita;

            HBox fila = new HBox();
            fila.setSpacing(14);
            fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            fila.getStyleClass().add("fila-clic");
            fila.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mostrarDetalleCita(citaFila);
                }
            });

            Label hora = new Label(formatearHora(cita.getHora()));
            hora.getStyleClass().add("cita-hora");

            VBox datos = new VBox();
            datos.setSpacing(1);
            datos.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(datos, Priority.ALWAYS);
            Label paciente = new Label(obtenerNombrePaciente(cita));
            paciente.getStyleClass().add("fila-titulo");
            Label detalle = new Label(obtenerNombreMedico(cita));
            detalle.getStyleClass().add("label-subtitulo");
            datos.getChildren().addAll(paciente, detalle);

            Label estado = new Label(obtenerEstadoCita(cita));
            estado.getStyleClass().add(obtenerClaseEstadoCita(estado.getText()));

            fila.getChildren().addAll(hora, datos, estado);
            vbCitasHoy.getChildren().add(fila);
        }
    }

    // Abre el detalle de la cita en una ventana emergente (observacion bien distribuida)
    // en vez de saltar al modulo completo de Citas.
    private void mostrarDetalleCita(Cita cita) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDetalleCita.fxml"));
            Parent vista = loader.load();
            FXMLDetalleCitaController controlador = loader.getController();
            controlador.inicializarDatos(cita);

            Scene escena = new Scene(vista);
            Stage escenario = new Stage();
            escenario.setScene(escena);
            escenario.setTitle("Detalle de la cita");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            Utilidades.mostrarAlertaSimple("Error", "No se pudo abrir el detalle de la cita.", Alert.AlertType.ERROR);
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

    private String formatearHora(String hora) {
        if (hora == null) {
            return "";
        }
        return hora.length() >= 5 ? hora.substring(0, 5) : hora;
    }

    private void cargarModulo(String nombreFXML, String titulo, String mensajeError) {
        try {
            URL recurso = getClass().getResource(nombreFXML);
            if (recurso == null) {
                Utilidades.mostrarAlertaSimple("Modulo no disponible", mensajeError, Alert.AlertType.INFORMATION);
                return;
            }

            FXMLLoader loader = new FXMLLoader(recurso);
            Parent vista = loader.load();

            // Se pasa la sesion al modulo antes de mostrarlo (estilo PAQ).
            Object controlador = loader.getController();
            if (controlador instanceof FXMLAgendaCitasController) {
                ((FXMLAgendaCitasController) controlador).inicializarSesion(sesion);
            } else if (controlador instanceof FXMLAdministracionConsultasController) {
                ((FXMLAdministracionConsultasController) controlador).inicializarSesion(sesion);
            } else if (controlador instanceof FXMLAdministracionDietasController) {
                ((FXMLAdministracionDietasController) controlador).inicializarSesion(sesion);
            } else if (controlador instanceof FXMLAdministracionPacientesController) {
                ((FXMLAdministracionPacientesController) controlador).inicializarSesion(sesion);
            } else if (controlador instanceof FXMLPerfilController) {
                ((FXMLPerfilController) controlador).inicializarSesion(sesion);
            }

            bpContenedor.setCenter(vista);
            lbTitulo.setText(titulo);
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
