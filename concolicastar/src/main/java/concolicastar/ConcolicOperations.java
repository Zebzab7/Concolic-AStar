package concolicastar;
import com.microsoft.z3.*;
public class ConcolicOperations{
    public static Z3PathState pathState = new Z3PathState();

    public static boolean comparisonFlag = false;
    
    public static Element doBinary(String opr, Element a,Element b){
        java.lang.reflect.Method method;
        Element v = null;
        try{
            method = ConcolicOperations.class.getDeclaredMethod("_"+opr,Element.class, Element.class);
            v = (Element) method.invoke(ConcolicOperations.class,a,b);
        } catch (Exception e) {
            System.out.println("Error: Method might not exist "+ e + " " + opr);
            e.printStackTrace();
        }
        
        return v;
    }

     public static boolean doCompare(String opr, Element a,Element b){
        java.lang.reflect.Method method;
        boolean v = false;
        try{
            System.out.println("Operation2: "+ opr);
            method = ConcolicOperations.class.getDeclaredMethod("_"+opr, Element.class, Element.class);
            v = (boolean) method.invoke(ConcolicOperations.class,a,b);
        } catch (Exception e) {
            System.out.println("Error: Method might not exist "+ e);
            e.printStackTrace();
        }
        
        return v;
    }
   
    public static void SymbolicOP(Double a,Double b,String opr){
        pathState.declareVariable("a", pathState.getContext().mkRealSort());
        pathState.declareVariable("b", pathState.getContext().mkRealSort());
        pathState.addConstraint(pathState.getContext().mkEq((RealExpr) pathState.getVariable("a"),          
        pathState.getContext().mkReal(a.toString())));
        pathState.addConstraint(pathState.getContext().mkEq((RealExpr) pathState.getVariable("b"),   
        pathState.getContext().mkReal(b.toString())));
        // Solve and print the model
        switch (opr) {
            case "add":
                    String sumab = Double.toString(a + b);
                    pathState.addConstraint((pathState.getContext().mkEq((RealExpr)pathState.getContext().mkAdd((RealExpr)pathState.getVariable("a"),(RealExpr)pathState.getVariable("b")),pathState.getContext().mkReal(sumab))));
                break;
            case "sub":
                    String subab = Double.toString(a - b);
                    pathState.addConstraint((pathState.getContext().mkEq((RealExpr)pathState.getContext().mkSub(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkReal(subab))));
                break;
            case "mul":
                    String mulab = Double.toString(a * b);
                    pathState.addConstraint((pathState.getContext().mkEq((IntExpr)pathState.getContext().mkMul(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkReal(mulab))));
                break;
            case "div":
                    String divab = Double.toString(a / b);
                    pathState.addConstraint((pathState.getContext().mkEq((IntExpr)pathState.getContext().mkDiv(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkInt(divab))));
                break;
            case "mod":
                    String modab = Double.toString(a % b);
                    pathState.addConstraint((pathState.getContext().mkEq((IntExpr)pathState.getContext().mkDiv(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkInt(modab))));
                break;
            case "gt":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkGt(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a>b))));
                break;
            case "lt":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkLt(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a<b))));
                break;
            case "eq":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkEq(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a==b))));
                break;
            case "ge":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkGe(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a>b || a==b))));
                break;
            case "le":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkLe(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a<b || a==b))));
                break;
        
            default:
                break;
        }
        Model model = pathState.solve();
        if (model != null) {
            System.out.println("Model: " + model);
            
        } else {
            System.out.println("No solution found");
        }
    }
    public static void SymbolicOP(Long a,Long b, String opr){
        pathState.declareVariable("a", pathState.getContext().mkIntSort());
        pathState.declareVariable("b", pathState.getContext().mkIntSort());
        pathState.addConstraint(pathState.getContext().mkEq((IntExpr) pathState.getVariable("a"), pathState.getContext().mkInt(a)));
        pathState.addConstraint(pathState.getContext().mkEq((IntExpr) pathState.getVariable("b"), pathState.getContext().mkInt(b)));
        // Solve and print the model
        switch (opr) {
            case "add":
                    pathState.addConstraint((pathState.getContext().mkEq((IntExpr)pathState.getContext().mkAdd(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkInt(a+b))));
                break;
            case "sub":
                    pathState.addConstraint((pathState.getContext().mkEq((IntExpr)pathState.getContext().mkSub(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkInt(a-b))));
                break;
            case "mul":
                    pathState.addConstraint((pathState.getContext().mkEq((IntExpr)pathState.getContext().mkMul(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkInt(a*b))));
                break;
            case "div":
                    pathState.addConstraint((pathState.getContext().mkEq((IntExpr)pathState.getContext().mkDiv(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkInt(a/b))));
                break;
            case "gt":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkGt(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a>b))));
                break;
            case "lt":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkLt(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a<b))));
                break;
            case "eq":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkEq(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a==b))));
                break;
            case "ge":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkGe(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a>b || a==b))));
                break;
            case "le":
                    pathState.addConstraint((pathState.getContext().mkEq((BoolExpr)pathState.getContext().mkLe(pathState.getVariable("a"),pathState.getVariable("b")),pathState.getContext().mkBool(a<b || a==b))));
                break;
        
            default:
                break;
        }
        Model model = pathState.solve();
        if (model != null) {
            System.out.println("Model: " + model);

        } else {
            System.out.println("No solution found");
        }
    }

    public static void symAdd(Number a,Number b){
        // Create a Z3 context
        Context ctx = new Context();
        // Create variables
        Expr<?> x = ctx.mkConst("x", ctx.mkUninterpretedSort("UnknownType"));
        Expr<?> y = ctx.mkConst("y", ctx.mkUninterpretedSort("UnknownType"));
        
        // Perform the addition
        ArithExpr<?> resAdd = (ArithExpr<?>) ctx.mkAdd((ArithExpr<?>) x, (ArithExpr<?>) y);

        // Create a solver
        Solver solver = ctx.mkSolver();
        // solver.add(ctx.mkEq(resAdd, ctx.mkInt(a.intValue() + b.intValue())));

        // Check for satisfiability (optional)
        Status status = solver.check();

        if (status == Status.SATISFIABLE) {
            // Get the result of a + b
            System.out.println("Result of a + b: " + solver.getModel().evaluate(resAdd, false));
        } else {
            System.out.println("No satisfying assignment.");
        }
        
        // Dispose of the context to free up resources
        ctx.close();
        
    }
    
    public static Element _add(Element a,Element b){ 
        // symAdd(a, b);
        System.out.println("add: "+ (((Number)a.getValue()).doubleValue() + ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        // Expr<?> symoboliExpra = a.getSymbolicValue();
        // Expr<?> symoboliExprb = b.getSymbolicValue();
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            String doubleab = Double.toString(aDouble+bDouble);
            return new Element("double",(Number) (aDouble + bDouble), Interpreter.getCtx().mkReal(doubleab));
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            ArithExpr<ArithSort> newSymbolicValue
                 = Interpreter.getCtx().mkAdd((ArithExpr<ArithSort>) a.getSymbolicValue(),(ArithExpr<ArithSort>)b.getSymbolicValue());
            return new Element("long",(Number) (aLong + bLong), newSymbolicValue);
        }
    }

    public static Element _sub(Element a, Element b){
        System.out.println("sub: "+ (((Number)a.getValue()).doubleValue() - ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            String doubleab = Double.toString(aDouble-bDouble);
            return new Element("double",(Number) (aDouble - bDouble), Interpreter.getCtx().mkReal(doubleab));
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
             ArithExpr<ArithSort> newSymbolicValue
                 = Interpreter.getCtx().mkSub((ArithExpr<ArithSort>) a.getSymbolicValue(),(ArithExpr<ArithSort>)b.getSymbolicValue());
            return new Element("long",(Number) (aLong - bLong), newSymbolicValue);
        }
    }

    public static Element _mul(Element a, Element b){
        // System.out.println("mul: "+ (((Number)a.getValue()).doubleValue() * ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            String doubleab = Double.toString(aDouble*bDouble);
            // ArithExpr<ArithSort> newSymbolicValue = (ArithExpr<ArithSort>) Interpreter.getCtx().mkMul(a.getSymbolicValue(),b.getSymbolicValue());
            return new Element("double",(Number) (aDouble * bDouble), Interpreter.getCtx().mkReal(doubleab));
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            ArithExpr<ArithSort> newSymbolicValue
                 = Interpreter.getCtx().mkMul((ArithExpr<ArithSort>) a.getSymbolicValue(),(ArithExpr<ArithSort>)b.getSymbolicValue());
            return new Element("long",(Number) (aLong * bLong), newSymbolicValue);
        }
    }

    public static Element _div(Element a, Element b){
        System.out.println("div: "+ (((Number)a.getValue()).doubleValue() / ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            String doubleab = Double.toString(aDouble/bDouble);
            return new Element("double",(Number) (aDouble / bDouble), Interpreter.getCtx().mkReal(doubleab));
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            ArithExpr<ArithSort> newSymbolicValue
                 = Interpreter.getCtx().mkDiv((ArithExpr<ArithSort>) a.getSymbolicValue(),(ArithExpr<ArithSort>)b.getSymbolicValue());
 
            return new Element("long",(Number) (aLong / bLong), newSymbolicValue);
        }
    }

    public static Element _mod(Element a, Element b){
        System.out.println("div: "+ (((Number)a.getValue()).doubleValue() / ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            String doubleab = Double.toString(aDouble%bDouble);
            return new Element("double",(Number) (aDouble % bDouble), Interpreter.getCtx().mkReal(doubleab));
        }
        else{
            Long aLong = Long.parseLong(objecta);
            Long bLong = Long.parseLong(objectb);
            Expr<IntSort> newSymbolicValue
                 = Interpreter.getCtx().mkMod((Expr<IntSort>) a.getSymbolicValue(),(Expr<IntSort>)b.getSymbolicValue());
 
            return new Element("long",(Number) (aLong % bLong), newSymbolicValue);
        }
    }
 
    public static boolean _gt(Element a, Element b){
        System.out.println("gt: "+ (((Number)a.getValue()).doubleValue() > ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            if(aDouble > bDouble){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            if(aLong > bLong){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }
        }
    }

    public static boolean _lt(Element a, Element b){
        System.out.println("lt: "+ (((Number)a.getValue()).doubleValue() < ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            if(aDouble < bDouble){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            if(aLong < bLong){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }
            
        }
    }

    public static boolean _eq(Element a, Element b){
        System.out.println("eq: "+ (((Number)a.getValue()).doubleValue() == ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            
            if(aDouble == bDouble){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }
            
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            if(aLong == bLong){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }
        }
    }

    public static boolean _ge(Element a, Element b){
        System.out.println("ge: "+ (((Number)a.getValue()).doubleValue() >= ((Number)b.getValue()).doubleValue()));
        System.out.println("ge: "+ a.getValue() + " " + a.getType());
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            if(aDouble >= bDouble){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }

        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            if(aLong >= bLong){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }

        }
    }

    public static boolean _le(Element a, Element b){
        //System.out.println("le: "+ (((Number)a.getValue()).doubleValue() <= ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            if(aDouble <= bDouble){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }

        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            if(aLong <= bLong){
                comparisonFlag = true;
                return true;
            }else{
                comparisonFlag = false;
                return false;
            }
        }
    }
    public static boolean getComparisonFlag(){
        return comparisonFlag;
    }
    public static void setComparisonFlag(boolean flag){
        comparisonFlag = flag;
    }
}
