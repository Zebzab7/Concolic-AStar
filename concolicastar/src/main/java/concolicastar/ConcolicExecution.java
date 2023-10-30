package concolicastar;
import org.omg.CORBA.Context;

import com.microsoft.z3.*;
public class ConcolicExecution{

    public static Number doOperation(String opr, Number a,Number b){
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
    //  // Create a Z3 context
    //  Context ctx = new Context();
    //  // Create integer variables
    //  IntExpr x = ctx.mkIntConst("x");
    //  IntExpr y = ctx.mkIntConst("y");
   
    public static Number _add(Number a,Number b){
        //concrete inputs
        //symbolic inputs
        return a.doubleValue() + b.doubleValue();
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
 
    






}
