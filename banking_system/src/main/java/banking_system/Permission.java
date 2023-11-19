package banking_system;

public class Permission {
    private AccessLevel accessLevel;

    public Permission(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }
}
