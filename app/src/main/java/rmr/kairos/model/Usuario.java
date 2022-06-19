package rmr.kairos.model;

/**
 * Clase modelo con las catacterísticas de un usuario
 * @author Rafa M.
 * @version 1.0
 * @since 1.0
 */
public class Usuario {
    private int id;
    private String username,password,email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}