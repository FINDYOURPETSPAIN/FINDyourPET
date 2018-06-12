/*Codigo diseñado por Mónica Morán Blanco y Rubén Barrado González*/
package findyourpet.sql;

public class Usuario {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private String pregunta;
    private String respuesta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre () {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setpregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}