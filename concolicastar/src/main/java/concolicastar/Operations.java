package concolicastar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.lang.reflect.Array;
import java.util.ArrayList;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntSort;

public class Operations {
    public static JSONObject bc;
    public static JSONArray bm;
    public static Context ctx;
    public static Z3PathState pathState = new Z3PathState();
    
    // import bytecode
    public Operations(JSONObject bc,JSONArray bootstrapMethods, Context ctx){
        Operations.bc = bc;
        Operations.bm = bootstrapMethods;
        Operations.ctx = ctx;
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
    
    public static ProgramStack _load(ProgramStack stack){
        Number index = (Number) bc.get("index");
        String type = (String) bc.get("type");
        if(type.equals("ref")){
            throw new IllegalArgumentException("Not implemented yet");
        } else {
            Element el = stack.getLv().get(index.intValue());
            System.out.println("Load: "+ el);
            stack.getOp().push(el);
        }
        return stack;
    }

    public static ProgramStack _push(ProgramStack Stack){
        JSONObject values = (JSONObject) bc.get("value");
        Long value = (Long) values.get("value");
        String type = (String) values.get("type");
        Element el = new Element(type, value, generateArithExpression(type, value));
        System.out.println("Push: "+ el);
        Stack.getOp().push(el);
        return Stack;
    }
    public static ProgramStack _return(ProgramStack stack){
        if (bc.get("type") == null){
            System.out.println("(return) None");
        }else if( bc.get("type").equals("int")){
            System.out.println("(return) int: "+ stack.getOp().peek());
        }else if(bc.get("type").equals("float")){
            System.out.println("(return) float: "+ stack.getOp().peek());
        }else{
            throw new IllegalArgumentException("Not implemented yet" + "return type: " + bc.get("type"));
        }
       return stack;
    }

    // Assumes that stack contains elements of type Number
    public static ProgramStack _binary(ProgramStack Stack){
        Element elb = (Element) Stack.getOp().pop();
        Element ela = (Element) Stack.getOp().pop();

        if(bc.get("operant")!= null){
            String oprString = (String) bc.get("operant");
            Element res = ConcolicOperations.doBinary(oprString,ela,elb); 
            Stack.getOp().push(res);
            System.out.println("Value: "+ res);
        }

         // Symbolic execution with a symbolic input
        return Stack;
    }

    public static ProgramStack _store(ProgramStack stack){
        Element el = stack.getOp().pop();
        if(bc.get("type").equals("ref")){
            //TODO: Implement this?
            throw new IllegalArgumentException("Not implemented yet");
        }
        Number index = (Number) bc.get("index");
        System.out.println("Store: "+ el);
        if (index.intValue() >= stack.getLv().size()){
            stack.getLv().push(el);
        }
        else{
            stack.getLv().replace(index.intValue(), el);        
        }
        return stack;
    }

    public static ProgramStack _incr(ProgramStack stack){
        Number numIndex = (Number) bc.get("index");
        int index = numIndex.intValue();
        
        Element el = (Element) stack.getLv().get(index);

        Number value = (Number) el.getValue();
        Number incrAmount = (Number) bc.get("amount");
        int res = value.intValue() + incrAmount.intValue();
        // Element elCopy = new Element(el.getType(), el.getValue(), el.getSymbolicValue());

        // stack.getOp().pop();
        // stack.getOp().push(elCopy);
        el.setValue(res);
        el.setSymbolicValue(ctx.mkAdd((Expr<IntSort>)el.getSymbolicValue(),(Expr<IntSort>) ctx.mkInt(incrAmount.intValue())));
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
        Number target = (Number) bc.get("target");

        if(bc.get("condition")!= null){
            String oprString = (String) bc.get("condition");
            boolean res = ConcolicOperations.doCompare(oprString, el1, el2);
            // If instruction is condition for a loop, then we only create a new expression the first time
            // if (stack.getBranches().get(stack.getPc()).equals("loop")) {



            // if (stack.getExpressionCreatedVector().get(stack.getPc()) == false) {
                // stack.getExpressionCreatedVector().set(stack.getPc(), true);
                BoolExpr expr = generateIfElseBoolExpression(el1,el2,oprString,res);
                System.out.println("Adding expression: " + expr);
                stack.addBoolExpr(expr);
            // }

            if (res) {
                stack.setPc(target.intValue()-1);
            }                                                                
        }
        return stack;        
    }

    public static ProgramStack _ifz(ProgramStack stack){
        Element el = (Element) stack.getOp().pop();

        Number num = (Number) bc.get("target");
        int target = num.intValue();

        Element zero = new Element("int", 0, generateArithExpression("integer", 0));

        // Finds the boolean condition
        if(bc.get("condition")!= null){
            String oprString = (String) bc.get("condition");
            boolean res = ConcolicOperations.doCompare(oprString, el, zero);
            
            // If instruction is condition for a loop, then we only create a new expression the first time
            // if (stack.getBranches().get(stack.getPc()).equals("loop")) {


            // if (stack.getExpressionCreatedVector().get(stack.getPc()) == false) {
                // stack.getExpressionCreatedVector().set(stack.getPc(), true);
                BoolExpr expr = generateIfElseBoolExpression(el,zero,oprString,res);
                System.out.println("Adding expression: " + expr);
                stack.addBoolExpr(expr);
            // }
            if (res) {
                stack.setPc(target-1);
            }                                                                    
        }
        return stack;
    }

    public static BoolExpr generateIfElseBoolExpression(Element e1, Element e2, String condition, boolean result)  {
        ArithExpr<?> expr1 = (ArithExpr<?>) e1.getSymbolicValue();
        ArithExpr<?> expr2 = (ArithExpr<?>) e2.getSymbolicValue();

        // Expr<IntSort> expr = ctx.mkIntConst("a");
        // if(e1.getValue().toString().contains(".")||e2.getValue().toString().contains(".")){
        //     //double
        // }else{
        //     //long
        // }
        // BoolExpr resBool = ctx.mkBool(result);
        switch(condition) {
            case "gt":
                if (result) {
                    return ctx.mkGt(expr1, expr2);
                } else {
                    return ctx.mkLe(expr1, expr2);
                }
            case "ge":
                if (result) {
                    return ctx.mkGe(expr1, expr2);
                } else {
                    return ctx.mkLt(expr1, expr2);
                }
            case "lt":
                if (result) {
                    return ctx.mkLt(expr1, expr2);
                } else {
                    return ctx.mkGe(expr1, expr2);
                }
            case "le":
                if (result) {
                    return ctx.mkLe(expr1, expr2);
                } else {
                    return ctx.mkGt(expr1, expr2);
                }
            case "eq":
                if (result) {
                    return ctx.mkEq(expr1, expr2);
                } else {
                    return ctx.mkNot(ctx.mkEq(expr1, expr2));
                }
            case "ne":
                if (result) {
                    return ctx.mkNot(ctx.mkEq(expr1, expr2));
                } else {
                    return ctx.mkEq(expr1, expr2);
                }
            default:
                throw new IllegalArgumentException("Not implemented yet" + condition);
        }
    }
    
    public static ArithExpr<?> generateArithExpression(String type, Object value) {
        // TODO: Implement all cases
        Number num = (Number) value;
        switch (type) {
            case "integer":
                return ctx.mkInt(num.intValue());	
            default:
                break;
        }

        return null;
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
        // String fieldName = (String)field.get("name");
        JSONObject fieldType = (JSONObject) field.get("type");
        String typeName = (String) fieldType.get("name");
        String typeKind = (String) fieldType.get("kind");
        Element el  = new Element(typeKind, typeName, generateArithExpression(typeKind, typeName));
        Stack.getOp().push(el);
        return Stack;
    }

    public static ProgramStack _invoke(ProgramStack Stack) {
        String access = (String) bc.get("access");
        Element[] argElments = new Element[0];
        JSONObject method = (JSONObject) bc.get("method");
        switch (access) {
            case "static":
                JSONObject ref = (JSONObject) method.get("ref");
                String className = (String) ref.get("name"); // Null pointer exception
                String[] className_array = className.split("/");
                String finalClass = className_array[className_array.length-1];
                String methodName = (String) method.get("name");
                AbsoluteMethod am = new AbsoluteMethod(finalClass,methodName);
                System.out.println("Invoke: "+ am.toString() + "with args " + method.get("args"));
                if (method.get("args")!=null) {
                    JSONArray args = (JSONArray) method.get("args");
                    argElments = new Element[args.size()];
                    Element e = null;
                    for (int j = 0; j < args.size(); j++) { // 4 
                        e = (Element) Stack.getOp().pop();
                        argElments[j] = new Element(e.getType(), e.getValue(), e.getSymbolicValue());
                        System.out.println("argElments: "+ argElments[j].toString());
                    }
                }
                ProgramStack newStack1 =Interpreter.interpret(am,argElments);
                if (newStack1 != null) {
                    //pops top of invoked function OpStack and push to current stack
                    Stack.getOp().push(newStack1.getOp().pop());
                }
                System.out.println(Stack.toString());
                break;
            case "virtual":
                JSONObject ref1= (JSONObject) method.get("ref");
                String className1 = (String) ref1.get("name"); // Null pointer exception
                String[] className_array1 = className1.split("/");
                String finalClass1 = className_array1[className_array1.length-1];
                String methodName1 = (String) method.get("name");
                AbsoluteMethod am1 = new AbsoluteMethod(finalClass1,methodName1);
                System.out.println("Invoke: "+ am1.toString());
                
                if(finalClass1.equals("PrintStream")){
                    System.out.println(Stack.getOp());
                    Stack.getOp().pop();//Pops the PrintStream
                    Stack.getOp().pop();//Pops the PrintStream 
                    break;
                }
                
                break;
            case "special":
                String methodName2 = (String) method.get("name");
                if (methodName2.equals("<init>")) {
                    Element initEl = new Element("<init>", null, null);
                    Stack.getOp().push(initEl);
                }else{
                    throw new IllegalArgumentException("Invoke type not supported"+ access);
                }
                break;
            case "dynamic":
                //dynamic
                if(bc.get("index")!=null){
                    int index = (int) (Number) bc.get("index");
                    JSONObject object =(JSONObject) bm.get(index);
                    System.out.println(object.toJSONString()); 
                }
                
                String name = (String) method.get("name");
                if(name.equals("makeConcatWithConstants")){
                        System.out.println("Concatinating but not really ;)");
                        break;
                }
                throw new IllegalArgumentException("Invoke type not supported" + access + name);
            default:
                throw new IllegalArgumentException("Not implemented yet" + access);
                // break;
        }
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
                Stack.getLv().push(new Element(arrayType, Array.newInstance(type.getClass(), 0), null));
                break;
            case 2:
                Stack.getLv().push(new Element (arrayType,Array.newInstance(type.getClass(), 0,0), null));
                break;
            case 3: 
                Stack.getLv().push(new Element (arrayType,Array.newInstance(type.getClass(), 0,0,0), null));
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
                Stack.getOp().push(new Element("int", arr1[indexValue], null));
                break;
            default:
                throw new IllegalArgumentException("Not implemented yet");
        }
        return Stack;
    }
}
