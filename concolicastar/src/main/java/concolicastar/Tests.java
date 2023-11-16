package concolicastar;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Tests {
    
    public static void testList(Interpreter interpreter){
        System.out.println("Starting tests!!!");
        testSimple(interpreter);
        // testCalls(interpreter);

        System.out.println("Tests done :O");
    }
    private static void testHelloWorld(Interpreter interpreter){
        System.out.println("\nTesting helloWorld");
        ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Calls", "helloWorld"), new Element[] {});
        System.out.println(res);
        assertTrue(res.getOp().size() == 0);
        assertTrue(res.getLv().size() == 0);
    }
    private static void testSimple(Interpreter interpreter) {
        System.out.println("Testing simple");
        testFunction(interpreter, new AbsoluteMethod("Simple", "min"));
    }
    private static void testCalls(Interpreter interpreter) {
        System.out.println("Testing calls");
        //testHelloWorld(interpreter);
        testFibonacci(interpreter);
    }
    private static void testFibonacci(Interpreter interpreter){
        Context ctx = new Context();
        System.out.println("\nTesting fibonacci");

        Expr<?> n = ctx.mkIntConst("n");
        // new Element("null", 20, null)
        // Input symbolic value of n
        ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Calls", "fib"),
             new Element[] {new Element("int", 10, n)});
        System.out.println(res);
        Element el = (Element) res.getOp().peek();
        Number num = (Number) el.getValue();
        assertTrue(num.intValue() == 89);
    }
    private static void testFunction(Interpreter interpreter, AbsoluteMethod am) {
        System.out.println("\nTesting min");
        Context ctx = new Context();
        Interpreter.setContext(ctx);

        //TODO: Add getArguments(AbsoluteMethod am) to Interpreter
        // TODO: Expand to work with any type of arguments
        IntExpr aExpr = ctx.mkIntConst("a");
        IntExpr bExpr = ctx.mkIntConst("b");
        ArrayList<Integer> concreteValues = new ArrayList<Integer>(Arrays.asList(0, 0));
        ArrayList<IntExpr> symbolicValues = new ArrayList<IntExpr>(Arrays.asList(aExpr, bExpr));

        BoolExpr fullExpr = ctx.mkTrue();
        Solver solver = ctx.mkSolver();
        Status s = solver.check();

        boolean solveable = true;
        while (solveable) {
            solver = ctx.mkSolver();
            solver.add(fullExpr);

            System.out.println("Solver: " + solver);
            Status satisfiable = solver.check();
            if (satisfiable == Status.SATISFIABLE) {
                System.out.println("SATISFIABLE");
                Model model = solver.getModel();
                System.out.println("model"+ model);
                for (int i = 0; i < symbolicValues.size(); i++) {
                    IntExpr expr = symbolicValues.get(i);
                    if (model.getConstInterp(expr) != null) {
                        concreteValues.set(i, Integer.parseInt(model.getConstInterp(expr).toString()));
                    }
                }
                System.out.println("result: " + satisfiable);
            } else {
                System.out.println("NOT SATISFIABLE");
                solveable = false;
            }
            
            // Element[] args = new Element[] {new Element("int", concreteA, aExpr), new Element("int", concreteB, bExpr)};
            Element[] args = new Element[concreteValues.size()];
            for (int i = 0; i < concreteValues.size(); i++) {
                args[i] = new Element("int", concreteValues.get(i), symbolicValues.get(i));
            }

            ProgramStack res = Interpreter.interpret(am, args);
            System.out.println("\nRESULT: " + res.getBoolExpr().toString());
            
            BoolExpr resExpr = ctx.mkNot((BoolExpr) res.getBoolExpr());
            fullExpr = ctx.mkAnd(fullExpr, resExpr);
        }
        ctx.close();
    }
}
