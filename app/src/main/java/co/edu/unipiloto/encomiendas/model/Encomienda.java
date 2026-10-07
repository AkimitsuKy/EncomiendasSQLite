package co.edu.unipiloto.encomiendas.model;

public class Encomienda {
    private long id;
    private String guia;
    private String descripcion;
    private double peso;
    private double valorDeclarado;
    private String tipo;
    private String fecha;

    public Encomienda() { }

    public Encomienda(String guia, String descripcion, double peso,
                      double valorDeclarado, String tipo, String fecha) {
        this.guia = guia;
        this.descripcion = descripcion;
        this.peso = peso;
        this.valorDeclarado = valorDeclarado;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getGuia() { return guia; }
    public void setGuia(String guia) { this.guia = guia; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }
    public double getValorDeclarado() { return valorDeclarado; }
    public void setValorDeclarado(double valorDeclarado) { this.valorDeclarado = valorDeclarado; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
