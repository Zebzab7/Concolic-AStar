package concolicastar;

import static org.junit.Assert.assertTrue;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Tests {
    
    public static void testList(Interpreter interpreter){
        System.out.println("Starting tests!!!");
        testSimple(interpreter);
        // testCalls(interpreter);
        System.out.println("Tests done :O");
    }
    // private static void testCalls(Interpreter interpreter) {
    //     System.out.println("Testing calls");
    //     //testHelloWorld(interpreter);
    //     testFibonacci(interpreter);
    // }
    // private static void testHelloWorld(Interpreter interpreter){
    //     System.out.println("\nTesting helloWorld");
    //     ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Calls", "helloWorld"), new Element[] {});
    //     System.out.println(res);
    //     assertTrue(res.getOp().size() == 0);
    //     assertTrue(res.getLv().size() == 0);
    // }
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

    private static void testSimple(Interpreter interpreter) {
        System.out.println("Testing simple");
        // simpleNoop(interpreter);
        // simpleZero(interpreter);
        // simpleHundredAndTwo(interpreter);
        // simpleIdentity(interpreter);
        // simpleAdd(interpreter);
        simpleMin(interpreter);
        // simpleFactorial(interpreter);
        // simpleMain(interpreter);
    }
    // private static void simpleNoop(Interpreter interpreter) {
    //     System.out.println("\nTesting noop");
    //     ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Simple", "noop"), new Element[] {});
    //     assertTrue(res.getLv().size() == 0);
    //     assertTrue(res.getOp().size() == 0);
    // }
    // private static void simpleZero(Interpreter interpreter) {
    //     System.out.println("\nTesting zero");
    //     ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Simple", "zero"), new Element[] {});
    //     Element el = (Element) res.getOp().peek();
    //     Number num = (Number) el.getValue();
    //     assertTrue(num.intValue() == 0);
    // }
    // private static void simpleHundredAndTwo(Interpreter interpreter) {
    //     System.out.println("\nTesting hundredAndTwo");
    //     ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Simple", "hundredAndTwo"), new Element[] {});
    //     Element el = (Element) res.getOp().peek();
    //     Number num = (Number) el.getValue();
    //     assertTrue(num.intValue() == 102);
    // }
    // private static void simpleIdentity(Interpreter interpreter) {
    //     System.out.println("\nTesting identity");
    //     ProgramStack res =Interpreter.interpret(new AbsoluteMethod("Simple", "identity"),
    //         new Element[] {new Element("int", 27)});
    //     Element el = (Element) res.getOp().peek();
    //     Number num = (Number) el.getValue();
    //     assertTrue(num.intValue() == 27);
    // }
    // private static void simpleAdd(Interpreter interpreter) {
    //     System.out.println("\nTesting add");
    //     ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Simple", "add"), 
    //         new Element[] {new Element("int", 1), new Element("int", 2)});
    //     Element el = (Element) res.getOp().peek();
    //     Number num = (Number) el.getValue();
    //     assertTrue(num.intValue() == 3);
    // }
    private static void simpleMin(Interpreter interpreter) {
        System.out.println("\nTesting min");
        Context ctx = new Context();
        Solver solver = ctx.mkSolver();
        
        boolean solveable = true;

        int a = 5;
        int b = 10;

        IntExpr aExpr = ctx.mkIntConst("a");
        IntExpr bExpr = ctx.mkIntConst("b");

        BoolExpr e1 = ctx.mkEq(aExpr, aExpr);
        BoolExpr e2 = ctx.mkEq(bExpr, bExpr);

        // intrepret different operations
        // BoolExpr opr = interpreter(String op, IntExpr a, IntExpr b,Context ctx);
        solver.add(new BoolExpr[]{e1,e2});
        while (solveable) {
            Element[] args = new Element[] {new Element("int", a, aExpr), new Element("int", b, bExpr)};
            
            ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Simple", "min"), args);
            
            for (Expr<?> expr : res.getExpressions()) {
                if (expr instanceof BoolExpr) {
                    solver.add((BoolExpr) expr);
                }
            }
            Status result = solver.check();

            // At this point print all boolean expressions:
            System.out.println("\nPrinting constraints: " + solver);
            if (result == Status.SATISFIABLE) {
                System.out.println("result: " + result);
                System.out.println(solver.getModel());
            } else {
                System.out.println("Not satisfiable result:" + result);
                solveable = false;
            }
            break;
        }
        // assertTrue(num.intValue() == a);
    }

    // private static void simpleFactorial(Interpreter interpreter) {
    //     System.out.println("\nTesting factorial");
    //     ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Simple", "factorial"), 
    //         new Element[] {new Element("int", 6)});
    //     Element el = (Element) res.getOp().peek();
    //     Number num = (Number) el.getValue();
        
    //     System.out.println(res);
    //     assertTrue(num.intValue() == 720);
    // }

    // private static void simpleMain(Interpreter interpreter){
    //     System.out.println("\nTesting main");
    //     ProgramStack res = Interpreter.interpret(new AbsoluteMethod("Simple", "main"), 
    //         new Element[] {new Element("int", 1), new Element("int", 1)});
        
    //     System.out.println(res);
    //     // assertTrue();
    // }
}
