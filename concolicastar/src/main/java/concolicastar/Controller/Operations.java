package concolicastar.Controller;

import concolicastar.Model.ProgramStack;

public class Operations {
    
    //In case of boolean or char https://miro.medium.com/v2/resize:fit:720/format:webp/1*AQzGbqmrJfVMJeJ7UVQctw.png
    
    public static ProgramStack doOperation(ProgramStack stack, String opr){
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
        Number a = Stack.getLv().popNum();
        Number b = Stack.getLv().popNum();
        //Casts to the highest order https://www.w3schools.com/java/java_type_casting.asp
        Stack.getLv().push(a.doubleValue() + b.doubleValue());
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
