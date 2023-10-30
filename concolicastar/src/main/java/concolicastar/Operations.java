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
        int index = (int) bc.get("index");
        String type = (String) bc.get("type");
        //get lv_type and values
        // ProgramStack ps = new ProgramStack();
        Element el = new Element(type,Stack.getLv().getIndexEl(index)); 
        Stack.getLv().push(el);
        return Stack;
    }

    public static ProgramStack _push(ProgramStack Stack){
        JSONObject values = (JSONObject) bc.get("value");
        Object value = (Number) values.get("value");
        String type = (String) values.get("type");
        Element el = new Element(type, value);
        Stack.getLv().push(el);
        return Stack;
    }
    public static ProgramStack _return(ProgramStack Stack){
        // System.out.println("Not implemented yet");
        if (bc.get("type") == null){
            System.out.println("(return) None");
            return Stack;
        }else if(bc.get("type") == "int"){
            System.out.println("(return) int: "+ Stack.getLv().popNum());
            return Stack;
        }else if(bc.get("type") == "float"){
            System.out.println("(return) float: "+ Stack.getLv().popNum());
            return Stack;
        }else{
            System.out.println("return type not implemented "+ bc.get("type").toString());
            return Stack;
        }
       
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

    public static ProgramStack _store(ProgramStack Stack){
        Object el = (Object) Stack.getOp().pop();
        if ((Integer) bc.get("index") >= Stack.getLv().size()){
            Stack.getLv().push(el);
        }
        else{
            Stack.getLv().insert((Integer) bc.get("index"), el);        
        }
        return Stack;
    }

    public static ProgramStack _incr(ProgramStack Stack){
        Element el = (Element) Stack.getLv().getIndexEl((Integer) bc.get("index"));
        int value = (Integer) el.getValue();
        int res = value + (Integer) bc.get("amount");
        el.setValue(res);
        Stack.getLv().replace(el, (Integer) bc.get("index"));
        return Stack;
    }

}
