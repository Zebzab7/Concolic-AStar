package concolicastar;

public class Operations {
    public static ProgramStack doFunction(ProgramStack stack, String opr){
        java.lang.reflect.Method method;
        try{
            method = Operations.class.getDeclaredMethod("_"+opr, ProgramStack.class);
            stack = (ProgramStack) method.invoke(Operations.class,stack);
        } catch (Exception e) {
            System.out.println("Error: Method might not exist "+ e);
        }
        return stack;
    }

    public static ProgramStack _add(ProgramStack Stack){
        Stack.getLv().push((int)Stack.getLv().pop() + (int)Stack.getLv().pop());
        return Stack;
    }
    public static ProgramStack _push(ProgramStack Stack){
        System.out.println("Not Implemented yet");;

        return Stack;
    }
    public static ProgramStack _return(ProgramStack Stack){
        System.out.println("Not implemented yet");
        return Stack;
    }


}
