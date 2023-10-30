package concolicastar;

import org.json.simple.JSONObject;

public class Operations {
    public static JSONObject bc;

    // import bytecode
    public Operations(JSONObject bc){
        Operations.bc = bc;
    }
    
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
    
    // public static ProgramStack _add(ProgramStack Stack){
    //     Number a = Stack.getLv().popNum();
    //     Number b = Stack.getLv().popNum();
    //     //Casts to the highest order https://www.w3schools.com/java/java_type_casting.asp
    //     Stack.getLv().push(a.doubleValue() + b.doubleValue());
    //     return Stack;
    // }
    public static ProgramStack _load(ProgramStack Stack){
        System.out.println("Not Implemented yet");;
        //  Stack.getLv().push();
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

    public static ProgramStack _binary(ProgramStack Stack){
        Number a = Stack.getLv().popNum();
        Number b = Stack.getLv().popNum();
        if(bc.get("operant")!= null){
            String oprString = (String) bc.get("operant");
            Number res = ConcolicExecution.doOperation(oprString,a,b);
            System.out.println("Concolic Execution with Concrete Input:");
            System.out.println("Concrete Input: " + a +","+b);
            System.out.println("Concrete Result: " + res);
            
            
        }
         // Symbolic execution with a symbolic input
        Stack.getLv().push(a.doubleValue() + b.doubleValue());
        return Stack;
    }

   

}
