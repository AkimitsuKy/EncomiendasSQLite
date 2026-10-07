package co.edu.unipiloto.encomiendas.model;

public class Destinatario {
    private long id;
    private String nombre;
    private String documento;
    private String telefono;
    private String direccion;
    private String ciudad;

    public Destinatario() { }

    public Destinatario(String nombre, String documento, String telefono,
                        String direccion, String ciudad) {
        this.nombre = nombre;
        this.documento = documento;
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}
