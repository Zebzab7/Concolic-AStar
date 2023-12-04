package banking_system;
public class ResourceManager {
    public void manageResource(User user, Permission permission, UserOperation operation) {
        UserOperationHandler userOperationHandler = new UserOperationHandler(user, permission, operation);
        userOperationHandler.handle();
    }
}
