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
            System.out.println("Operation: "+ opr);
            method = Operations.class.getDeclaredMethod("_"+opr, ProgramStack.class);
            stack = (ProgramStack) method.invoke(Operations.class,stack);
        } catch (Exception e) {
            System.out.println("Error: Method might not exist "+ e + " " + opr);
            e.printStackTrace();
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
        Number index =  (Number) bc.get("index");
        String type = (String) bc.get("type");
        if(type.equals("ref")){
            throw new IllegalArgumentException("Not implemented yet");
        }
        //get lv_type and values
        // ProgramStack ps = new ProgramStack();
        Element el = new Element(type,Stack.getLv().getIndexEl(index.intValue())); 
        Stack.getLv().push(el);
        return Stack;
    }

    public static ProgramStack _push(ProgramStack Stack){
        JSONObject values = (JSONObject) bc.get("value");
        Object value = (Number) values.get("value");
        String type = (String) values.get("type");
        Element el = new Element(type, value);
        Stack.getLv().push(el);
        System.out.println(Stack.toString());
        return Stack;
    }
    public static ProgramStack _return(ProgramStack Stack){
        // System.out.println("Not implemented yet");
        if (bc.get("type") == null){
            System.out.println("(return) None");
            return Stack;
        }else if( bc.get("type").equals("int")){
            System.out.println("(return) int: "+ Stack.getLv().pop());
            return Stack;
        }else if(bc.get("type").equals("float")){
            System.out.println("(return) float: "+ Stack.getLv().pop());
            return Stack;
        }else{
            System.out.println("return type not implemented "+ bc.get("type").toString());
            return Stack;
        }
       
    }

    public static ProgramStack _binary(ProgramStack Stack){
        Element ela = (Element) Stack.getLv().pop();
        Number a = (Number) ela.getValue();
        Element elb = (Element) Stack.getLv().pop();
        Number b = (Number) elb.getValue();
        if(bc.get("operant")!= null){
            String oprString = (String) bc.get("operant");
            Number res = ConcolicExecution.doBinary(oprString,a,b);
            
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
        if(bc.get("type").equals("ref")){
            throw new IllegalArgumentException("Not implemented yet");
        }

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

    public static ProgramStack _negate(ProgramStack Stack){
        Element el = (Element) Stack.getOp().pop();
        int value = (Integer) el.getValue();
        int res = -value;
        el.setValue(res);
        Stack.getOp().push(el);
        return Stack;
    }

    public static ProgramStack _if(ProgramStack stack) {
        Element el1 = (Element) stack.getOp().pop();
        Element el2 = (Element) stack.getOp().pop();
        int target = (Integer) bc.get("target");
        
        Number value1 = (Number) el1.getValue();
        Number value2 = (Number) el2.getValue();

        if(bc.get("condition")!= null){
            String oprString = (String) bc.get("condition");
            boolean res = ConcolicExecution.doCompare(oprString, value1, value2);
            if (res) {
                stack.setPc(target-1);
            }                                                                    
        }
        return stack;        
    }
    
    public static ProgramStack _ifz(ProgramStack Stack){
        Element el = (Element) Stack.getOp().pop();
        int target = (Integer) bc.get("target");
        
        Number value = (Number) el.getValue();
        Number zero = (Number) 0;
        
        if(bc.get("condition")!= null){
            String oprString = (String) bc.get("condition");
            boolean res = ConcolicExecution.doCompare(oprString, value, zero);
            if (res) {
                Stack.setPc(target-1);
            }                                                                    
        }
        return Stack;
    }

    public static ProgramStack _goto(ProgramStack Stack){
        int target = (Integer) bc.get("target");
        Stack.setPc(target-1);
        return Stack;
    }
    
    public static ProgramStack _get(ProgramStack Stack){
        int pc = Stack.getPc();
        JSONObject field = (JSONObject)bc.get("field");
        String fieldName = (String)field.get("name");

        return Stack;
    }
    public static ProgramStack _invoke(ProgramStack Stack) {
        return Stack;
    }
    public static ProgramStack _dup(ProgramStack Stack) {
        Element el = (Element) Stack.getLv().pop();
        int num = (int) bc.get("words");
        if(num!=1){
            //If num is 2 or greater look at the bytecode, not sure how to handle
            throw new IllegalArgumentException("Not implemented yet");
        }
        Stack.getLv().push(el);
        Stack.getLv().push(el);
        return Stack;
    }
    public static ProgramStack _newarray(ProgramStack Stack) {
        //Get's pushed value's from stack to get size of array
        int[] arr = new int[10];
        int dimension = (int) bc.get("dim");
        String arrayType = ""+dimension;
        for(int i = 0; i<dimension; i++){
            Element e = (Element) Stack.getOp().pop();
            arr[i] = (int) e.getValue();
            //Stores counts for usage in other functions
            arrayType += " " + arr[i];
        }
        
        
        String type = (String) bc.get("type");
        if(!type.equals("int")){
            throw new IllegalArgumentException("Not implemented yet");
        }
    
        switch(dimension){
            case 1:
                Stack.getLv().push(new Element(arrayType, new int[arr[0]]));
                break;
            case 2:
                Stack.getLv().push(new Element (arrayType,new int[arr[0]][arr[1]]));
                break;
            case 3: 
                Stack.getLv().push(new Element (arrayType,new int[arr[0]][arr[1]][arr[2]]));
                break;
            default:
                throw new IllegalArgumentException("Not implemented yet");
        }
        return Stack;
    }
    public static ProgramStack _array_store(ProgramStack Stack) {
        Element e = (Element) Stack.getOp().pop();
        Element array = (Element) Stack.getLv().pop();
        String type = (String)bc.get("type");
        if(!type.equals("int")){
            throw new IllegalArgumentException("Not implemented yet");
        }

        int dim = Integer.parseInt(array.getType().split(" ")[0]);

        Element index = (Element) Stack.getOp().pop();
        //Array index, if multiple dimensions, figure out what happens?
        int indexValue = (int) index.getValue(); 

        switch(dim){
            case 1:
                int[] arr = (int[]) array.getValue();
                int value = (int) e.getValue();
                arr[indexValue] = value;
                break;
            default:
                throw new IllegalArgumentException("Not implemented yet");
        }

        return Stack;
    }
    public static ProgramStack _array_load(ProgramStack Stack){
        Element arr = (Element) Stack.getLv().pop();
        Element index = (Element) Stack.getOp().pop();
        int indexValue = (int) index.getValue();
        
        String type = arr.getType();
        int dim = Integer.parseInt(type.split(" ")[0]);
        switch(dim){
            case 1:
                int[] arr1 = (int[]) arr.getValue();
                Stack.getLv().push(new Element("int", arr1[indexValue]));
                break;
            default:
                throw new IllegalArgumentException("Not implemented yet");
        }
        return Stack;
    }
}
