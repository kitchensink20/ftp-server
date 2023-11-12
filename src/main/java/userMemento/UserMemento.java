package userMemento;

public class UserMemento {
    private int userId;
    private String username;
    private String password;
    private boolean isAdmin;
    private String homeDirectory;

    public UserMemento(int userId, String username, String password, boolean isAdmin, String homeDirectory) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.homeDirectory = homeDirectory;
    }

    public int getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public String getHomeDirectory() {
        return homeDirectory;
    }
}
