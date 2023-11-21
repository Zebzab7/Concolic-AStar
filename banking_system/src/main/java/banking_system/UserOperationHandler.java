package banking_system;

public class UserOperationHandler {
    private User user;
    private Permission permission;
    private UserOperation operation;

    public UserOperationHandler(User user, Permission permission, UserOperation operation) {
        this.user = user;
        this.permission = permission;
        this.operation = operation;
    }

    public void handle(){
        if ("admin".equals(user.getRole())){
            if ("CREATE_CUSTOMER_ACCOUNT".equals(operation.name())){
                System.out.println("Admin created a customer account.");
            }
            else if ("DELETE_CUSTOMER_ACCOUNT".equals(operation.name())){
                System.out.println("Admin deleted a customer account.");
            }
            else if ("MODIFY_CUSTOMER_DETAILS".equals(operation.name())){
                System.out.println("Admin modified a customer account.");
            }
            else if ("VIEW_TRANSACTION_HISTORY".equals(operation.name())){
                System.out.println("Admin viewed a customer account's transaction history.");
            }
        }
        else if ("manager".equals(user.getRole())){
            if ("MODIFY_ACCOUNT_DETAILS".equals(operation.name())){
                System.out.println("Manager modified a customer account.");
            }
            else if ("VIEW_CUSTOMER_DETAILS".equals(operation.name())){
                System.out.println("Manager viewed a customer account.");
            }
            else if ("PERFORM_TRANSACTIONS".equals(operation.name())){
                System.out.println("Manager performed a transaction.");
            }
        }
        else{
            if ("DEPOSIT".equals(operation.name())){
                System.out.println("Customer deposited money.");
            }
            else if ("WITHDRAW".equals(operation.name())){
                System.out.println("Customer withdrew money.");
            }
            else if ("TRANSFER".equals(operation.name())){
                System.out.println("Customer transferred money.");
            }
            else if ("VIEW_ACCOUNT_DETAILS".equals(operation.name())){
                System.out.println("Customer viewed account details.");
            }
            else if ("VIEW_TRANSACTION_HISTORY_CUSTOMER".equals(operation.name())){
                System.out.println("Customer viewed transaction history.");
            }
        }
    }
}
