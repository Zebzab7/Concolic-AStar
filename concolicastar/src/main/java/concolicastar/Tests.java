package concolicastar;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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

        // List of elements from alphabet: 
        String[] alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i"};
        int count = 0;

        ArrayList<Element> elements = new ArrayList<Element>();

        // Read the arguments of the method and assign initial values
        Bytecode bc = Interpreter.findMethod(am);
        ArrayList<String> argTypes = bc.getArgsTypes();
        for (String type : argTypes) {
            switch (type) {
                case "int":
                    IntExpr intExpr = ctx.mkIntConst(alphabet[count]);
                    elements.add(new Element("int", 0, intExpr));
                    break;
                default:
                    throw new IllegalArgumentException("Type not handled");
            }
            count++;
        }

        BoolExpr fullExpr = ctx.mkTrue();
        Solver solver = ctx.mkSolver();
        Status s = solver.check();

        while (true) {
            solver = ctx.mkSolver();
            solver.add(fullExpr);

            // Solve the model
            System.out.println("Solver: " + solver);
            Status satisfiable = solver.check();
            if (satisfiable == Status.SATISFIABLE) {
                System.out.println("SATISFIABLE");

                Model model = solver.getModel();
                System.out.println("model"+ model);

                // Update the values of the elements if they changed
                for (int i = 0; i < elements.size(); i++) {
                    Element e = elements.get(i);
                    switch (e.getType()) {
                        case "int":
                            IntExpr expr = (IntExpr) e.getSymbolicValue();
                            if (model.getConstInterp(expr) != null) {
                                e.setValue(Integer.parseInt(model.getConstInterp(expr).toString()));
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Type not handled");
                    }
                }
            } else {
                System.out.println("NOT SATISFIABLE");
                break;
            }
            
            // Element[] args = new Element[] {new Element("int", concreteA, aExpr), new Element("int", concreteB, bExpr)};
            // Element[] args = new Element[concreteValues.size()];
            // for (int i = 0; i < concreteValues.size(); i++) {
            //     args[i] = new Element("int", concreteValues.get(i), symbolicValues.get(i));
            // }

            Element[] args = new Element[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                args[i] = elements.get(i);
            }

            ProgramStack res = Interpreter.interpret(am, args);
            System.out.println("\nRESULT: " + res.getBoolExpr().toString());
            
            BoolExpr resExpr = ctx.mkNot((BoolExpr) res.getBoolExpr());
            fullExpr = ctx.mkAnd(fullExpr, resExpr);
        }
        ctx.close();
    }
}
