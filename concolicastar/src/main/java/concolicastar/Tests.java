package concolicastar;

public class Tests {
    
    public static void testList(Interpreter interpreter){
        System.out.println("Starting tests!!!");
        testSimple(interpreter);

        System.out.println("Tests done :O");
    }
    private static void testSimple(Interpreter interpreter) {
        System.out.println("Testing simple");
        simpleNoop(interpreter);
        simpleZero(interpreter);
        simpleHundredAndTwo(interpreter);
        simpleIdentity(interpreter);
        simpleAdd(interpreter);
        simpleMin(interpreter);
        // simpleFactorial(interpreter);

    }
    private static void simpleNoop(Interpreter interpreter) {
        System.out.println("Testing noop");
        interpreter.interpret(new AbsoluteMethod("Simple", "noop"), new Object[] {});
    }
    private static void simpleZero(Interpreter interpreter) {
        System.out.println("Testing zero");
        interpreter.interpret(new AbsoluteMethod("Simple", "zero"), new Object[] {});
        
    }
    private static void simpleHundredAndTwo(Interpreter interpreter) {
        System.out.println("Testing hundredAndTwo");
        interpreter.interpret(new AbsoluteMethod("Simple", "hundredAndTwo"), new Object[] {});
    }
    private static void simpleIdentity(Interpreter interpreter) {
        System.out.println("Testing identity");
        interpreter.interpret(new AbsoluteMethod("Simple", "identity"), new Object[] {1});
    }
    private static void simpleAdd(Interpreter interpreter) {
        System.out.println("Testing add");
        interpreter.interpret(new AbsoluteMethod("Simple", "add"), new Object[] {1, 2});
    }
    private static void simpleMin(Interpreter interpreter) {
        System.out.println("Testing min");
        interpreter.interpret(new AbsoluteMethod("Simple", "min"), new Object[] {1, 2});
    }
    private static void simpleFactorial(Interpreter interpreter) {
        System.out.println("Testing factorial");
        interpreter.interpret(new AbsoluteMethod("Simple", "factorial"), new Object[] {4});
    }



}
