package domain;

public class User {
    private String username;
    private String password;
    private String token;
    private Playlist[] playlists;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
