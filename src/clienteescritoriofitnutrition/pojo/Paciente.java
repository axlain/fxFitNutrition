package clienteescritoriofitnutrition.pojo;

public class Paciente {
    private Integer idPaciente;
    private String codigoAcceso;
    
    private Integer idUsuario; 
    private Usuario usuario;
    private Integer idMedico; 
    private Medico medico; 

    public Paciente() {
    }

    public Paciente(Integer idPaciente, String codigoAcceso, Integer idUsuario, Usuario usuario, Integer idMedico, Medico medico) {
        this.idPaciente = idPaciente;
        this.codigoAcceso = codigoAcceso;
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.idMedico = idMedico;
        this.medico = medico;
    }

    public Integer getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Integer idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getCodigoAcceso() {
        return codigoAcceso;
    }

    public void setCodigoAcceso(String codigoAcceso) {
        this.codigoAcceso = codigoAcceso;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Integer idMedico) {
        this.idMedico = idMedico;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getNombreCompleto() {
        if (usuario == null) {
            return "";
        }
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellidoPaterno = usuario.getApellidoPaterno() != null ? usuario.getApellidoPaterno() : "";
        String apellidoMaterno = usuario.getApellidoMaterno() != null ? usuario.getApellidoMaterno() : "";
        return (nombre + " " + apellidoPaterno + " " + apellidoMaterno).trim();
    }

    public String getCorreoUsuario() {
        return usuario != null && usuario.getCorreo() != null ? usuario.getCorreo() : "";
    }

    public String getTelefonoUsuario() {
        return usuario != null && usuario.getTelefono() != null ? usuario.getTelefono() : "";
    }

    public String getEstatusUsuario() {
        return usuario != null && usuario.getEstatus() != null && usuario.getEstatus().getNombre() != null
                ? usuario.getEstatus().getNombre()
                : "";
    }

    public String getMedicoAsignado() {
        return medico != null ? medico.getNombreCompleto() : "";
    }

    @Override
    public String toString() {
        String nombre = getNombreCompleto();
        if (codigoAcceso != null && !codigoAcceso.trim().isEmpty()) {
            return nombre + " - " + codigoAcceso;
        }
        return nombre;
    }

}
