package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.pojo.Cita;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXMLDetalleCitaController {

    @FXML
    private Label lbHora;
    @FXML
    private Label lbPaciente;
    @FXML
    private Label lbFecha;
    @FXML
    private Label lbMedico;
    @FXML
    private Label lbEstado;
    @FXML
    private Label lbObservaciones;

    public void inicializarDatos(Cita cita) {
        if (cita == null) {
            return;
        }

        lbHora.setText(formatearHora(cita.getHora()));
        lbPaciente.setText(textoODefault(cita.getNombrePaciente(), "Paciente"));
        lbFecha.setText(textoODefault(cita.getFecha(), ""));
        lbMedico.setText(textoODefault(cita.getNombreMedico(), "Medico"));

        String estado = cita.getNombreEstado();
        lbEstado.setText(textoODefault(estado, "Sin estado"));
        lbEstado.getStyleClass().removeAll("pill-confirmada", "pill-pendiente", "pill-cancelada");
        lbEstado.getStyleClass().add(obtenerClaseEstadoCita(estado));

        String observaciones = cita.getObservaciones();
        lbObservaciones.setText(observaciones != null && !observaciones.trim().isEmpty()
                ? observaciones.trim()
                : "Sin observaciones");
    }

    @FXML
    private void clickCerrar(ActionEvent event) {
        Stage escenario = (Stage) lbHora.getScene().getWindow();
        escenario.close();
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

    private String formatearHora(String hora) {
        if (hora == null) {
            return "";
        }
        return hora.length() >= 5 ? hora.substring(0, 5) : hora;
    }

    private String textoODefault(String valor, String porDefecto) {
        return valor != null && !valor.trim().isEmpty() ? valor : porDefecto;
    }
}
