package clienteescritoriofitnutrition;

import clienteescritoriofitnutrition.dominio.AdministradorImp;
import clienteescritoriofitnutrition.dominio.MedicoImp;
import clienteescritoriofitnutrition.dominio.UsuarioImp;
import clienteescritoriofitnutrition.dto.RSAutenticar;
import clienteescritoriofitnutrition.dto.Respuesta;
import clienteescritoriofitnutrition.pojo.Administrador;
import clienteescritoriofitnutrition.pojo.Medico;
import clienteescritoriofitnutrition.pojo.Usuario;
import clienteescritoriofitnutrition.utilidad.Utilidades;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FXMLPerfilController implements Initializable {

    @FXML
    private ImageView ivFoto;
    @FXML
    private Label lbIniciales;
    @FXML
    private Label lbNombreCompleto;
    @FXML
    private Label lbRol;
    @FXML
    private Label lbEstatus;
    @FXML
    private Label lbValorNombre;
    @FXML
    private Label lbValorCorreo;
    @FXML
    private Label lbValorTelefono;
    @FXML
    private Label lbValorFechaNacimiento;
    @FXML
    private Label lbValorGenero;
    @FXML
    private Label lbValorNumeroPersonal;
    @FXML
    private Label lbValorCedula;
    @FXML
    private PasswordField pfContrasenaActual;
    @FXML
    private PasswordField pfContrasenaNueva;
    @FXML
    private PasswordField pfContrasenaConfirmar;

    private RSAutenticar sesion;
    private Usuario usuario;
    private Integer idUsuario;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ivFoto.setClip(new Circle(48, 48, 48));
    }

    public void inicializarSesion(RSAutenticar sesion) {
        this.sesion = sesion;
        cargarDatos();
        cargarFoto();
    }

    private void cargarDatos() {
        if (sesion == null) {
            return;
        }

        String numeroPersonal = "";
        String cedula = null;
        String rolTexto = sesion.getTipoUsuario() != null ? sesion.getTipoUsuario() : "";

        if (sesion.getAdministrador() != null) {
            Administrador admin = sesion.getAdministrador();
            usuario = admin.getUsuario();
            numeroPersonal = admin.getNumeroPersonal();
        } else if (sesion.getMedico() != null) {
            Medico medico = sesion.getMedico();
            usuario = medico.getUsuario();
            numeroPersonal = medico.getNumeroPersonal();
            cedula = medico.getCedulaProfesional();
        }

        if (usuario != null) {
            idUsuario = usuario.getIdUsuario();

            String nombreCompleto = (texto(usuario.getNombre()) + " " + texto(usuario.getApellidoPaterno())
                    + " " + texto(usuario.getApellidoMaterno())).trim().replaceAll("\\s+", " ");

            lbNombreCompleto.setText(nombreCompleto.isEmpty() ? "Usuario" : nombreCompleto);
            lbValorNombre.setText(nombreCompleto.isEmpty() ? "-" : nombreCompleto);
            lbValorCorreo.setText(valorOGuion(usuario.getCorreo()));
            lbValorTelefono.setText(valorOGuion(usuario.getTelefono()));
            lbValorFechaNacimiento.setText(valorOGuion(usuario.getFechaNacimiento()));
            lbValorGenero.setText(valorOGuion(usuario.getGenero()));
            lbEstatus.setText(usuario.getEstatus() != null ? valorOGuion(usuario.getEstatus().getNombre()) : "-");
            lbIniciales.setText(obtenerIniciales(usuario));
        }

        lbValorNumeroPersonal.setText(valorOGuion(numeroPersonal));
        lbValorCedula.setText(cedula != null && !cedula.trim().isEmpty() ? cedula.trim() : "No aplica");
        lbRol.setText(rolTexto);
    }

    private String obtenerIniciales(Usuario usuario) {
        String nombre = texto(usuario.getNombre());
        String apellido = texto(usuario.getApellidoPaterno());
        String iniciales = (nombre.isEmpty() ? "" : nombre.substring(0, 1))
                + (apellido.isEmpty() ? "" : apellido.substring(0, 1));
        return iniciales.toUpperCase();
    }

    private void cargarFoto() {
        if (idUsuario == null) {
            return;
        }

        Usuario usuarioFoto = UsuarioImp.obtenerFoto(idUsuario);

        if (usuarioFoto != null && usuarioFoto.getFotoBase64() != null && !usuarioFoto.getFotoBase64().trim().isEmpty()) {
            try {
                byte[] datos = Base64.getDecoder().decode(usuarioFoto.getFotoBase64().trim());
                ivFoto.setImage(new Image(new ByteArrayInputStream(datos)));
                lbIniciales.setVisible(false);
                return;
            } catch (Exception e) {
                // Si la imagen no se puede decodificar, se muestran las iniciales.
            }
        }

        lbIniciales.setVisible(true);
    }

    @FXML
    private void clickCambiarFoto(ActionEvent event) {
        if (idUsuario == null) {
            Utilidades.mostrarAlertaSimple("Perfil", "No se pudo identificar al usuario en sesión.", Alert.AlertType.ERROR);
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar fotografía de perfil");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        Stage escenario = (Stage) ivFoto.getScene().getWindow();
        File archivo = selector.showOpenDialog(escenario);

        if (archivo == null) {
            return;
        }

        try {
            byte[] datos = Files.readAllBytes(archivo.toPath());
            Respuesta respuesta = UsuarioImp.subirFoto(idUsuario, datos);

            Utilidades.mostrarAlertaSimple(
                    respuesta.isError() ? "Error" : "Operación exitosa",
                    respuesta.getMensaje(),
                    respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
            );

            if (!respuesta.isError()) {
                ivFoto.setImage(new Image(new ByteArrayInputStream(datos)));
                lbIniciales.setVisible(false);
            }
        } catch (Exception e) {
            Utilidades.mostrarAlertaSimple("Error", "No se pudo leer el archivo seleccionado.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clickCambiarContrasena(ActionEvent event) {
        String actual = pfContrasenaActual.getText();
        String nueva = pfContrasenaNueva.getText();
        String confirmar = pfContrasenaConfirmar.getText();

        if (actual == null || actual.trim().isEmpty()
                || nueva == null || nueva.trim().isEmpty()
                || confirmar == null || confirmar.trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campo requerido", "Todos los campos de contraseña son obligatorios.", Alert.AlertType.WARNING);
            return;
        }

        if (nueva.trim().length() > 20) {
            Utilidades.mostrarAlertaSimple("Campo requerido", "La nueva contraseña no puede exceder 20 caracteres.", Alert.AlertType.WARNING);
            return;
        }

        if (!nueva.trim().equals(confirmar.trim())) {
            Utilidades.mostrarAlertaSimple("Campo requerido", "La confirmación no coincide con la nueva contraseña.", Alert.AlertType.WARNING);
            return;
        }

        Respuesta respuesta;

        if (sesion != null && sesion.getAdministrador() != null) {
            respuesta = AdministradorImp.cambiarContrasena(sesion.getAdministrador().getIdAdministrador(), actual.trim(), nueva.trim());
        } else if (sesion != null && sesion.getMedico() != null) {
            respuesta = MedicoImp.cambiarContrasena(sesion.getMedico().getIdMedico(), actual.trim(), nueva.trim());
        } else {
            Utilidades.mostrarAlertaSimple("Error", "No se pudo identificar el rol de la sesión.", Alert.AlertType.ERROR);
            return;
        }

        Utilidades.mostrarAlertaSimple(
                respuesta.isError() ? "Error" : "Operación exitosa",
                respuesta.getMensaje(),
                respuesta.isError() ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );

        if (!respuesta.isError()) {
            pfContrasenaActual.clear();
            pfContrasenaNueva.clear();
            pfContrasenaConfirmar.clear();
        }
    }

    private String texto(String valor) {
        return valor != null ? valor : "";
    }

    private String valorOGuion(String valor) {
        return valor != null && !valor.trim().isEmpty() ? valor.trim() : "-";
    }
}
