package clienteescritoriofitnutrition.pojo;

public class RespuestaHTTP {
    
    private int codigo; 
    private String contenido; 
    private byte[] contenidoBytes;
    
    public RespuestaHTTP() {
    }

    public RespuestaHTTP(int codigo, String contenido, byte[] contenidoBytes) {
        this.codigo = codigo;
        this.contenido = contenido;
        this.contenidoBytes = contenidoBytes;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public byte[] getContenidoBytes() {
        return contenidoBytes;
    }

    public void setContenidoBytes(byte[] contenidoBytes) {
        this.contenidoBytes = contenidoBytes;
    }

    
    
    
}
