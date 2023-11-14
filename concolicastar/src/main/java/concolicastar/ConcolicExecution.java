package concolicastar;
import com.fasterxml.jackson.databind.util.Converter;
import com.microsoft.z3.*;
public class ConcolicExecution{

    public static Element doBinary(String opr, Element a,Element b){
        java.lang.reflect.Method method;
        Element v = null;
        try{
            method = ConcolicExecution.class.getDeclaredMethod("_"+opr,Element.class, Element.class);
            v = (Element) method.invoke(ConcolicExecution.class,a,b);
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
            method = ConcolicExecution.class.getDeclaredMethod("_"+opr, Element.class, Element.class);
            v = (boolean) method.invoke(ConcolicExecution.class,a,b);
        } catch (Exception e) {
            System.out.println("Error: Method might not exist "+ e);
            e.printStackTrace();
        }
        
        return v;
    }
   
    
    public static void symAdd(Number a,Number b){
        // Create a Z3 context
        Context ctx = new Context();
        // Create variables
        Expr x = ctx.mkConst("x",ctx.mkUninterpretedSort("UnknownType"));
        Expr y = ctx.mkConst("y",ctx.mkUninterpretedSort("UnknownType"));
        //  Perform the addition
        Expr resAdd = ctx.mkAdd(x,y);

        //  create a solver
        Solver solver = ctx.mkSolver();
        solver.add(ctx.mkEq(resAdd,ctx.mkInt(a.intValue()+b.intValue())));
        
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
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return new Element("double",(Number) (aDouble + bDouble), null);
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return new Element("long",(Number) (aLong + bLong), null);
        }
    }

    public static Element _sub(Element a, Element b){
        System.out.println("sub: "+ (((Number)a.getValue()).doubleValue() - ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return new Element("double",(Number) (aDouble - bDouble), null);
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return new Element("long",(Number) (aLong - bLong), null);
        }
    }

    public static Element _mul(Element a, Element b){
        System.out.println("mul: "+ (((Number)a.getValue()).doubleValue() * ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return new Element("double",(Number) (aDouble * bDouble), null);
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return new Element("long",(Number) (aLong * bLong), null);
        }
    }

    public static Element _div(Element a, Element b){
        System.out.println("div: "+ (((Number)a.getValue()).doubleValue() / ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return new Element("double",(Number) (aDouble / bDouble), null);
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return new Element("long",(Number) (aLong / bLong), null);
        }
    }

    public static Element _mod(Element a, Element b){
        System.out.println("div: "+ (((Number)a.getValue()).doubleValue() / ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return new Element("double",(Number) (aDouble % bDouble), null);
        }
        else{
            Long aLong = Long.parseLong(objecta);
            Long bLong = Long.parseLong(objectb);
            return new Element("long",(Number) (aLong % bLong), null);
        }
    }
 
    public static boolean _gt(Element a, Element b){
        System.out.println("gt: "+ (((Number)a.getValue()).doubleValue() > ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return (aDouble > bDouble);
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return aLong > bLong;
        }
    }

    public static boolean _lt(Element a, Element b){
        System.out.println("lt: "+ (((Number)a.getValue()).doubleValue() < ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return aDouble < bDouble;
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return aLong < bLong;
        }
    }

    public static boolean _eq(Element a, Element b){
        System.out.println("eq: "+ (((Number)a.getValue()).doubleValue() == ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return aDouble == bDouble;
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return aLong == bLong;
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
            return aDouble >= bDouble;
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return aLong >= bLong;
        }
    }

    public static boolean _le(Element a, Element b){
        System.out.println("le: "+ (((Number)a.getValue()).doubleValue() <= ((Number)b.getValue()).doubleValue()));
        String objecta = "" + ObjectConverter.convert(a.getValue(),a.getType().getClass());
        String objectb = "" + ObjectConverter.convert(b.getValue(),b.getType().getClass());
        if(objecta.contains(".") || objectb.contains(".")){
            Double aDouble =Double.parseDouble(objecta);
            Double bDouble =Double.parseDouble(objectb);
            return aDouble <= bDouble;
        }
        else{
            Long aLong =Long.parseLong(objecta);
            Long bLong =Long.parseLong(objectb);
            return aLong <= bLong;
        }
    }
}
