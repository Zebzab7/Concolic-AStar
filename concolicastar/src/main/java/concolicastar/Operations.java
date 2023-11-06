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
    public static ProgramStack _load(ProgramStack stack){
        Number index = (Number) bc.get("index");
        String type = (String) bc.get("type");
        if(type.equals("ref")){
            throw new IllegalArgumentException("Not implemented yet");
        } else {
            Element el = stack.getLv().get(index.intValue());
            stack.getOp().push(el);
        }
        return stack;
    }

    public static ProgramStack _push(ProgramStack Stack){
        JSONObject values = (JSONObject) bc.get("value");
        Object value = values.get("value");
        String type = (String) values.get("type");
        Element el = new Element(type, value);
        Stack.getOp().push(el);
        return Stack;
    }
    public static ProgramStack _return(ProgramStack stack){
        // System.out.println("Not implemented yet");
        if (bc.get("type") == null){
            System.out.println("(return) None");
            return stack;
        }else if( bc.get("type").equals("int")){
            System.out.println("(return) int: "+ stack.getOp().peek());
            return stack;
        }else if(bc.get("type").equals("float")){
            System.out.println("(return) float: "+ stack.getOp().peek());
            return stack;
        }else{
            System.out.println("return type not implemented "+ bc.get("type").toString());
            return stack;
        }
       
    }

    // Assumes that stack contains elements of type Number
    public static ProgramStack _binary(ProgramStack Stack){
        Element ela = (Element) Stack.getOp().pop();
        Number a = (Number) ela.getValue();
        Element elb = (Element) Stack.getOp().pop();
        Number b = (Number) elb.getValue();
        if(bc.get("operant")!= null){
            String oprString = (String) bc.get("operant");
            Number res = ConcolicExecution.doBinary(oprString,a,b);
            Element el = new Element("Double", res);
            Stack.getOp().push(el);
        }

         // Symbolic execution with a symbolic input
        return Stack;
    }

    public static ProgramStack _store(ProgramStack Stack){
        Element el = Stack.getOp().pop();
        if(bc.get("type").equals("ref")){
            throw new IllegalArgumentException("Not implemented yet");
        }
        Number index = (Number) bc.get("index");
        if (index.intValue() >= Stack.getLv().size()){
            Stack.getLv().push(el);
        }
        else{
            Stack.getLv().replace(index.intValue(), el);        
        }

        return Stack;
    }

    public static ProgramStack _incr(ProgramStack stack){
        Number numIndex = (Number) bc.get("index");
        int index = numIndex.intValue();
        
        Element el = (Element) stack.getLv().get(index);

        Number value = (Number) el.getValue();
        Number incrAmount = (Number) bc.get("amount");
        int res = value.intValue() + incrAmount.intValue();
        Element elCopy = new Element(el.getType(), el.getValue());
        stack.getOp().pop();
        stack.getOp().push(elCopy);
        el.setValue(res);

        // Stack.getLv().replace(index,)
        // Stack.setLv();
        return stack;
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
        Element el2 = (Element) stack.getOp().pop();
        Element el1 = (Element) stack.getOp().pop();

        Number num = (Number) bc.get("target");
        int target = num.intValue();
        
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

        Number num = (Number) bc.get("target");
        int target = num.intValue();

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
        Number numIndex = (Number) bc.get("target");
        int target = numIndex.intValue();
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
                Stack.getOp().push(new Element("int", arr1[indexValue]));
                break;
            default:
                throw new IllegalArgumentException("Not implemented yet");
        }
        return Stack;
    }
}
