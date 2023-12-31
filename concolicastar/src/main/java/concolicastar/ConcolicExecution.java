package concolicastar;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.BoolSort;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class ConcolicExecution {

    public static int actualCost = 0;
    public static int totalNodesExplored = 0;
    public static boolean foundTarget = false;
    
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
        // testFunction(interpreter, new AbsoluteMethod("Simple", "ifInLoop2"));
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
    public static void testFunction(Interpreter interpreter, AbsoluteMethod am, BranchNode targetNode) {
        System.out.println("\nTesting " + am.getMethodName());
        Context ctx = new Context();
        Interpreter.setContext(ctx);

        totalNodesExplored = 0;
        actualCost = 0;

        // Generate list of alphabet: 
        String[] alphabet = new String[26];
        for (int i = 0; i < alphabet.length; i++) {
            alphabet[i] = "" +(char)('a' + i);
        }

        // Read the arguments of the method and assign initial values
        int count = 0;
        Bytecode bc = Interpreter.findMethod(am);
        ArrayList<Element> elements = new ArrayList<Element>();
        ArrayList<String> argTypes = bc.getArgsTypes();
        ArrayList<IntExpr> intExprs = new ArrayList<IntExpr>();
        for (String type : argTypes) {
            switch (type) {
                case "int":
                    intExprs.add(ctx.mkIntConst(alphabet[count]));
                    elements.add(new Element("int", 0, ctx.mkIntConst(alphabet[count])));
                    break;
                default:
                    throw new IllegalArgumentException("Type not handled");
            }
            count++;
        }
        BoolExpr fullExpr = null;
        Solver solver = ctx.mkSolver();
        int pathsExplored = 0;
        Interpreter.setAstarInterpretation(false);
        Interpreter.setTargetNode(targetNode);
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
                            // IntExpr expr = (IntExpr) e.getSymbolicValue();
                            // System.out.println("expr:" + expr );
                            IntExpr expr = intExprs.get(i);
                            if (model.getConstInterp(expr) != null) {
                                e.setValue(Integer.parseInt(model.getConstInterp(expr).toString()));
                                e.setSymbolicValue(expr);
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
            
            resetCost();
            setFoundTarget(false);
            ProgramStack res = Interpreter.interpretFunction(am, args);
            totalNodesExplored += Interpreter.getNodesExplored();

            ArrayList<BoolExpr> boolExprList = res.getBoolExprList();
            ArrayList<Integer> startBrackets = res.getStartBrackets();
            ArrayList<Integer> endBrackets = res.getEndBrackets();
            BoolExpr resExpr = res.getBoolExpr();

            if (foundTarget) {
                // System.out.println("resExpr: " + resExpr);
                // System.out.println("Actual cost was: " + actualCost);
                break;
            }

            resExpr = ctx.mkNot((BoolExpr) res.getBoolExpr());
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
        }

        System.out.println(pathsExplored + " paths explored!\n");
        ctx.close();
    }

    public static void incrementNode() {
        totalNodesExplored++;
    }

    public static int getTotalNodesExplored() {
        return totalNodesExplored;
    }

    public static int getActualCost() {
        return actualCost;
    }

    public static void setActualCost(int actualCost) {
        ConcolicExecution.actualCost = actualCost;
    }

    public static void setFoundTarget(boolean foundTarget) {
        ConcolicExecution.foundTarget = foundTarget;
    }

    public static boolean getFoundTarget() {
        return foundTarget;
    }

    public static void incrementCost() {
        actualCost++;
    }

    public static void resetCost() {
        actualCost = 0;
    }

    public static String toInfix(Expr expr) {
        if (expr.isConst()) {
            return expr.toString();
        } else if (expr.isApp()) {
            FuncDecl func = expr.getFuncDecl();
            String op = func.getName().toString();

            // Convert Z3 operator to infix operator
            switch (op) {
                case "add":
                    op = "+";
                    break;
                case "sub":
                    op = "-";
                    break;
                case "mul":
                    op = "*";
                    break;
                case "div":
                    op = "/";
                    break;
                case ">":
                    op = ">";
                    break;
                case "<":
                    op = "<";
                    break;
                // Add more cases as needed
                default:
                    // Unhandled operators
                    op = " " + op + " ";
            }

            Expr[] args = expr.getArgs();
            if (args.length == 2) {
                // Binary operation
                return toInfix(args[0]) + " " + op + " " + toInfix(args[1]);
            } else {
                // For non-binary, just return the original S-expression
                return expr.toString();
            }
        } else {
            // For complex expressions, return the original S-expression
            return expr.toString();
        }
    }
}
