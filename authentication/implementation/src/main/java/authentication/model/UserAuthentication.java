package authentication.model;

import io.jsonwebtoken.Jwts;

import java.io.Serializable;

public class UserAuthentication implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;
    private String username;
    private String password;
    private Jwts jwts;

    public UserAuthentication() {
    }

    public UserAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
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
}
