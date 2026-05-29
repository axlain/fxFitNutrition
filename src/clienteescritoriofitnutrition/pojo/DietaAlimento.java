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
    
    public String getNombreSegmento() {
        return segmentoDia != null && segmentoDia.getNombre() != null ? segmentoDia.getNombre() : "";
    }

    public String getNombreAlimento() {
        return alimento != null && alimento.getNombre() != null ? alimento.getNombre() : "";
    }

    public String getPorcionString() {
        if (alimento != null && alimento.getUnidadPorcion() != null) {
            return String.format("%.2f %s", cantidad, alimento.getUnidadPorcion().getNombre());
        }
        return String.valueOf(cantidad);
    }

    public String getCaloriasCalculadas() {
        if (alimento != null && alimento.getPorcion() != null && alimento.getCaloriasPorPorcion() != null) {
            double cal = (cantidad / alimento.getPorcion()) * alimento.getCaloriasPorPorcion();
            return String.format("%.2f kcal", cal);
        }
        return "";
    }
    

}
