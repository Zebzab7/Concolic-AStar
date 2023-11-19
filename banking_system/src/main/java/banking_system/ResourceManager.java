package banking_system;
public class ResourceManager {
    public void manageResource(User user, Permission permission) {
        if ("admin".equals(user.getRole())) {
            adminOperation(permission);
        } else if ("manager".equals(user.getRole())) {
            managerOperation(permission);
        } else {
            regularOperation(permission);
        }
    }

    private void adminOperation(Permission permission) {
        if (permission.getAccessLevel() == AccessLevel.HIGH) {
            System.out.println("Admin performed a high-level operation.");
        } else if (permission.getAccessLevel() == AccessLevel.MEDIUM) {
            System.out.println("Admin performed a medium-level operation.");
        } else {
            System.out.println("Admin performed a low-level operation.");
        }
        // ... other admin-specific operations
    }

    private void managerOperation(Permission permission) {
        if (permission.getAccessLevel() == AccessLevel.HIGH) {
            System.out.println("Manager performed a high-level operation.");
        } else if (permission.getAccessLevel() == AccessLevel.MEDIUM) {
            System.out.println("Manager performed a medium-level operation.");
        } else {
            System.out.println("Manager performed a low-level operation.");
        }
        // ... other manager-specific operations
    }

    private void regularOperation(Permission permission) {
        if (permission.getAccessLevel() == AccessLevel.HIGH) {
            System.out.println("User performed a high-level operation.");
        } else if (permission.getAccessLevel() == AccessLevel.MEDIUM) {
            System.out.println("User performed a medium-level operation.");
        } else {
            System.out.println("User performed a low-level operation.");
        }
        // ... other regular user operations
    }
}
