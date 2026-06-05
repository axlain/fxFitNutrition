package clienteescritoriofitnutrition.pojo;

public class DietaAlimento {
    private Integer idDietaAlimento;
    private Double cantidad;
    
    // Relaciones
    private Integer idDieta;
    private Dieta dieta;
    private Integer idAlimento;
    private Alimento alimento;
    private Integer idSegmentoDia;
    private SegmentoDia segmentoDia;

    public DietaAlimento() {
    }

    public DietaAlimento(Integer idDietaAlimento, Double cantidad, Integer idDieta, Dieta dieta, Integer idAlimento, Alimento alimento, Integer idSegmentoDia, SegmentoDia segmentoDia) {
        this.idDietaAlimento = idDietaAlimento;
        this.cantidad = cantidad;
        this.idDieta = idDieta;
        this.dieta = dieta;
        this.idAlimento = idAlimento;
        this.alimento = alimento;
        this.idSegmentoDia = idSegmentoDia;
        this.segmentoDia = segmentoDia;
    }

    public Integer getIdDietaAlimento() {
        return idDietaAlimento;
    }

    public void setIdDietaAlimento(Integer idDietaAlimento) {
        this.idDietaAlimento = idDietaAlimento;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getIdDieta() {
        return idDieta;
    }

    public void setIdDieta(Integer idDieta) {
        this.idDieta = idDieta;
    }

    public Dieta getDieta() {
        return dieta;
    }

    public void setDieta(Dieta dieta) {
        this.dieta = dieta;
    }

    public Integer getIdAlimento() {
        return idAlimento;
    }

    public void setIdAlimento(Integer idAlimento) {
        this.idAlimento = idAlimento;
    }

    public Alimento getAlimento() {
        return alimento;
    }

    public void setAlimento(Alimento alimento) {
        this.alimento = alimento;
    }

    public Integer getIdSegmentoDia() {
        return idSegmentoDia;
    }

    public void setIdSegmentoDia(Integer idSegmentoDia) {
        this.idSegmentoDia = idSegmentoDia;
    }

    public SegmentoDia getSegmentoDia() {
        return segmentoDia;
    }

    public void setSegmentoDia(SegmentoDia segmentoDia) {
        this.segmentoDia = segmentoDia;
    }

    // Getters calculados para las columnas de la tabla (estilo PAQ, sin lambda)
    public String getNombreSegmento() {
        return (segmentoDia != null && segmentoDia.getNombre() != null) ? segmentoDia.getNombre() : "";
    }

    public String getNombreAlimento() {
        return (alimento != null && alimento.getNombre() != null) ? alimento.getNombre() : "";
    }

    public String getPorcionString() {
        String u = (alimento != null && alimento.getUnidadPorcion() != null
                && alimento.getUnidadPorcion().getNombre() != null)
                ? alimento.getUnidadPorcion().getNombre() : "";
        String c = (cantidad != null) ? String.valueOf(cantidad) : "";
        return (c + " " + u).trim();
    }

    public Double getCaloriasCalculadas() {
        if (alimento != null && cantidad != null
                && alimento.getCaloriasPorPorcion() != null
                && alimento.getPorcion() != null && alimento.getPorcion() > 0) {
            double cal = (cantidad / alimento.getPorcion()) * alimento.getCaloriasPorPorcion();
            return Math.round(cal * 100.0) / 100.0;
        }
        return null;
    }
}
