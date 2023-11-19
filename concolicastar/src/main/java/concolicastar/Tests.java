package concolicastar;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
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
    private static void testSimple(Interpreter interpreter) {
        System.out.println("Testing simple");
        // testFunction(interpreter, new AbsoluteMethod("Simple", "noop"));
        // testFunction(interpreter, new AbsoluteMethod("Simple", "zero"));
        // testFunction(interpreter, new AbsoluteMethod("Simple", "hundredAndTwo"));
        // testFunction(interpreter, new AbsoluteMethod("Simple", "identity"));
        // testFunction(interpreter, new AbsoluteMethod("Simple", "add"));
        // testFunction(interpreter, new AbsoluteMethod("Simple", "min"));
        // testFunction(interpreter, new AbsoluteMethod("Simple", "factorial")); 
        // testFunction(interpreter, new AbsoluteMethod("Simple", "someFunction"));
        testFunction(interpreter, new AbsoluteMethod("Simple", "ifInLoop"));
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
        System.out.println("\nTesting " + am.getMethodName());
        Context ctx = new Context();
        Interpreter.setContext(ctx);

        // Generate list of alphabet: 
        String[] alphabet = new String[26];
        for (int i = 0; i < alphabet.length; i++) {
            alphabet[i] = "i" + (char)('a' + i);
        }

        // TODO: implement all cases
        // Read the arguments of the method and assign initial values
        int count = 0;
        Bytecode bc = Interpreter.findMethod(am);
        ArrayList<Element> elements = new ArrayList<Element>();
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
        BoolExpr fullExpr = null;
        Solver solver = ctx.mkSolver();
        solver.add(ctx.mkEq(elements.get(0).getSymbolicValue(), ctx.mkInt(3)));
        int pathsExplored = 0;
        while (true) {
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

            Element[] args = new Element[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                args[i] = elements.get(i);
            }

            ProgramStack res = Interpreter.interpretFunction(am, args);
            BoolExpr resExpr = ctx.mkNot((BoolExpr) res.getBoolExpr());
            System.out.println("resZEcpr"+resExpr);
            if (fullExpr == null) {
                fullExpr = resExpr;
                solver.add(fullExpr);
            } else {
                // fullExpr = ctx.mkAnd(fullExpr, resExpr);
                solver.add(ctx.mkAnd(fullExpr, resExpr));
            }
            System.out.println("\nRESULT:");
            System.out.println("Solver: " + solver);
            pathsExplored++;
            break;
        }
        System.out.println(pathsExplored + " paths explored!\n");
        ctx.close();
    }
}
