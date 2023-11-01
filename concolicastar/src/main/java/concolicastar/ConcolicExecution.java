package concolicastar;
import com.microsoft.z3.*;
public class ConcolicExecution{

    public static Number doBinary(String opr, Number a,Number b){
        java.lang.reflect.Method method;
        Number v = null;
        try{
            method = ConcolicExecution.class.getDeclaredMethod("_"+opr, Number.class, Number.class);
            v = (Number)method.invoke(ConcolicExecution.class,a.doubleValue(),b.doubleValue());
        } catch (Exception e) {
            System.out.println("Error: Method might not exist "+ e);
        }
        
        return v;
    }

     public static boolean doCompare(String opr, Number a,Number b){
        java.lang.reflect.Method method;
        boolean v = false;
        try{
            method = ConcolicExecution.class.getDeclaredMethod("_"+opr, Number.class, Number.class);
            v = (boolean) method.invoke(ConcolicExecution.class,a.doubleValue(),b.doubleValue());
        } catch (Exception e) {
            System.out.println("Error: Method might not exist "+ e);
        }
        
        return v;
    }
   
    public static Number _add(Number a,Number b){ 
        // symAdd(a, b);
        return a.doubleValue() + b.doubleValue();
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

    public static Number _sub(Number a, Number b){
        return a.doubleValue() - b.doubleValue();
    }

    public static Number _mul(Number a, Number b){
        return a.doubleValue() * b.doubleValue();
    }

    public static Number _div(Number a, Number b){
        return a.doubleValue() / b.doubleValue();
    }

    public static Number _mod(Number a, Number b){
        return a.doubleValue() % b.doubleValue();
    }
 
    public static boolean _gt(Number a, Number b){
        return a.doubleValue() > b.doubleValue();
    }

    public static boolean _lt(Number a, Number b){
        return a.doubleValue() < b.doubleValue();
    }

    public static boolean _eq(Number a, Number b){
        return a.doubleValue() == b.doubleValue();
    }

    public static boolean _ge(Number a, Number b){
        return a.doubleValue() >= b.doubleValue();
    }

    public static boolean _lz(Number a, Number b){
        return a.doubleValue() <= b.doubleValue();
    }

    public static boolean _le(Number a, Number b){
        return a.doubleValue() != b.doubleValue();
    }

}
