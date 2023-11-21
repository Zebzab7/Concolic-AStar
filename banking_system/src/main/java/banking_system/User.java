package banking_system;
public class User {
    private String username;
    private String role;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void checkPermission(User user, Permission permission) {
        if ("user".equals(user.getRole()) && !"LOW".equals(permission.getAccessLevel().toString())) {
            System.out.println("User must not have high access level.");
            System.exit(0);
            return;
        } else if ("manager".equals(user.getRole()) && "LOW".equals(permission.getAccessLevel().toString())) {
            System.out.println("Manager must not have low access level.");
            System.exit(0);
            return;
        } else if ("admin".equals(user.getRole()) && "LOW".equals(permission.getAccessLevel().toString())) {
            System.out.println("Admin must not have low access level.");
            System.exit(0);
            return;
        }
    }
}
