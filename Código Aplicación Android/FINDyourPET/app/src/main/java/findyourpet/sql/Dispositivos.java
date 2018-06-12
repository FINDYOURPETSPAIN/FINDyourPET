/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.sql;

public class Dispositivos {

    private int id;
    private int id_user;
    private String nombre;
    private String telefono;
    private int img;

    public Dispositivos (int id, int id_user, String nombre, String telefono, int img){

        setNombre(nombre);
        setTelefono(telefono);
        setId(id);
        setId_user(id_user);
        setImg(img);

    }

    public Dispositivos (){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id) {
        this.id_user = id;
    }

    public String getNombre () {
        return nombre;
    }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getImg() { return img; }

    public void setImg(int img) { this.img = img; }

}