package banking_system;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your role (admin/manager/user):");
        String role = scanner.nextLine();
        System.out.println("Enter access level (LOW/MEDIUM/HIGH):");
        String access = scanner.nextLine().toUpperCase(); // Ensure uppercase input

        User user = new User(username, role);
        AccessLevel accessLevel = AccessLevel.valueOf(access); // Parse enum directly
        Permission permission = new Permission(accessLevel);
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.manageResource(user, permission);
    }
}
