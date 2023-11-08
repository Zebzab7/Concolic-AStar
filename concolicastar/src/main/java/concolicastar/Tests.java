package concolicastar;

import static org.junit.Assert.assertTrue;

public class Tests {
    
    public static void testList(Interpreter interpreter){
        System.out.println("Starting tests!!!");
        //testSimple(interpreter);
        testCalls(interpreter);
        System.out.println("Tests done :O");
    }
    private static void testCalls(Interpreter interpreter) {
        System.out.println("Testing calls");
        //testHelloWorld(interpreter);
        testFibonacci(interpreter);
    }
    private static void testHelloWorld(Interpreter interpreter){
        System.out.println("\nTesting helloWorld");
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Calls", "helloWorld"), new Element[] {});
        System.out.println(res);
        assertTrue(res.getOp().size() == 0);
        assertTrue(res.getLv().size() == 0);
    }
    private static void testFibonacci(Interpreter interpreter){
        System.out.println("\nTesting fibonacci");
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Calls", "fib"), new Element[] {new Element("int", 10)});
        System.out.println(res);
        Element el = (Element) res.getOp().peek();
        Number num = (Number) el.getValue();
        assertTrue(num.intValue() == 89);
    }

    private static void testSimple(Interpreter interpreter) {
        System.out.println("Testing simple");
        simpleNoop(interpreter);
        simpleZero(interpreter);
        simpleHundredAndTwo(interpreter);
        simpleIdentity(interpreter);
        simpleAdd(interpreter);
        simpleMin(interpreter);
        simpleFactorial(interpreter);
        simpleMain(interpreter);
    }
    private static void simpleNoop(Interpreter interpreter) {
        System.out.println("\nTesting noop");
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Simple", "noop"), new Element[] {});
        assertTrue(res.getLv().size() == 0);
        assertTrue(res.getOp().size() == 0);
    }
    private static void simpleZero(Interpreter interpreter) {
        System.out.println("\nTesting zero");
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Simple", "zero"), new Element[] {});
        Element el = (Element) res.getOp().peek();
        Number num = (Number) el.getValue();
        assertTrue(num.intValue() == 0);
    }
    private static void simpleHundredAndTwo(Interpreter interpreter) {
        System.out.println("\nTesting hundredAndTwo");
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Simple", "hundredAndTwo"), new Element[] {});
        Element el = (Element) res.getOp().peek();
        Number num = (Number) el.getValue();
        assertTrue(num.intValue() == 102);
    }
    private static void simpleIdentity(Interpreter interpreter) {
        System.out.println("\nTesting identity");
        ProgramStack res =interpreter.interpret(new AbsoluteMethod("Simple", "identity"),
            new Element[] {new Element("int", 27)});
        Element el = (Element) res.getOp().peek();
        Number num = (Number) el.getValue();
        assertTrue(num.intValue() == 27);
    }
    private static void simpleAdd(Interpreter interpreter) {
        System.out.println("\nTesting add");
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Simple", "add"), 
            new Element[] {new Element("int", 1), new Element("int", 2)});
        Element el = (Element) res.getOp().peek();
        Number num = (Number) el.getValue();
        assertTrue(num.intValue() == 3);
    }
    private static void simpleMin(Interpreter interpreter) {
        System.out.println("\nTesting min");
        int a = 5;
        int b = 10;
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Simple", "min"), 
            new Element[] {new Element("int", a), new Element("int", b)});
        Element el = (Element) res.getOp().peek();
        Number num = (Number) el.getValue();
        if (a <= b) {
            assertTrue(num.intValue() == a);
        } else {
            assertTrue(num.intValue() == b);
        }
    }
    private static void simpleFactorial(Interpreter interpreter) {
        System.out.println("\nTesting factorial");
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Simple", "factorial"), 
            new Element[] {new Element("int", 6)});
        Element el = (Element) res.getOp().peek();
        Number num = (Number) el.getValue();
        
        System.out.println(res);
        assertTrue(num.intValue() == 720);
    }

    private static void simpleMain(Interpreter interpreter){
        System.out.println("\nTesting main");
        ProgramStack res = interpreter.interpret(new AbsoluteMethod("Simple", "main"), 
            new Element[] {new Element("int", 1), new Element("int", 1)});
        

        System.out.println(res);
        // assertTrue();

        

    }
}
