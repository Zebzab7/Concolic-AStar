package banking_system;
import java.util.Scanner;

public class Main {

    private static AccessLevel accessLevel;
    private static UserOperation operation;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your role (admin/manager/user):");
        String role = scanner.nextLine();

        if (!"admin".equals(role) && !"manager".equals(role) && !"user".equals(role)) {
            System.out.println("Invalid role.");
            return;
        }

        User user = new User(username, role);

        System.out.println("Enter access level (LOW/MEDIUM/HIGH):");
        String access = scanner.nextLine().toUpperCase(); // Ensure uppercase input
        
        try{
            accessLevel = AccessLevel.valueOf(access);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid access level.");
            return;
        }
        Permission permission = new Permission(accessLevel);

        user.checkPermission(user, permission);

        System.out.println("Access granted.");
        System.out.println("What type of operation do you want to perform today?");
        String operationName = scanner.nextLine().toUpperCase();

        try{
            operation = UserOperation.valueOf(operationName);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid operation.");
            return;
        }

        ResourceManager resourceManager = new ResourceManager();
        resourceManager.manageResource(user, permission, operation);

    }
}
