package concolicastar;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.microsoft.z3.Context;

public class Interpreter {

    public static int count = 0;

    private static Context ctx;
    static ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
    static BootstrapMethods bootstrapMethods;
    public Interpreter(ArrayList<JsonFile> files) {
        for(JsonFile file : files) {
            JSONArray methods = file.getMethods();
            for (Object obj : methods) {
                JSONObject method = (JSONObject) obj;
                String methodName = (String) method.get("name");
                JSONObject code = (JSONObject) method.get("code");
                JSONArray args = (JSONArray) method.get("params");
                Bytecode bc = new Bytecode(file.getFileName(), methodName , (JSONArray) code.get("bytecode"), args);
                BootstrapMethods bm = new BootstrapMethods(file.getFileName(), methodName, (JSONArray) method.get("bootstrapMethods"));
                bytecodes.add(bc);
                bootstrapMethods = bm;
            }
        }
    }

    /**
     * Interpret the specified function (Top-level)
     * @param am
     * @param args
     * @return
     */
    public static ProgramStack interpretFunction(AbsoluteMethod am, Element[] args) {
        count = 0;
        return interpret(am, args);
    }

    /**
     * Interpretation only via invocation
     * @param am
     * @param args
     * @return
     */
    public static ProgramStack interpret(AbsoluteMethod am, Element[] args) {
        if (args == null) {
            args = new Element[0];
        }
        Bytecode bc = findMethod(am);
        ProgramStack stack = new ProgramStack(new Stack(), new Stack(), am, 0, null);
        stack.initializeBitVector(bc);
        for (Element el : args) {
            // System.out.println(el.toString());
            stack.getLv().push(el);
        }

        while (stack.getPc() < bc.getBytecode().size()) {
            JSONObject bytecode = (JSONObject) bc.getBytecode().get(stack.getPc());
            String oprString = (String) bytecode.get("opr");

            Operations op = new Operations(bytecode,bootstrapMethods.getBootstrapMethods(), ctx);
            stack = Operations.doOperation(stack, oprString);
            if (oprString.equals("return")) {
                return stack;
            }

            stack.setPc(stack.getPc() + 1);
            // System.out.println(stack.toString());
        }
        return stack;
    }

    public static void setContext(Context ctx) {
        Interpreter.ctx = ctx;
    }

    public static Bytecode findMethod(AbsoluteMethod am) {
        for (Bytecode bytecode : bytecodes) {
            if (bytecode.getAm().equals(am)) {
                return bytecode;
            }
        }
        throw new IllegalArgumentException("Method not found: " + am.getMethodName());
    }

    public static Element[] getArguments(AbsoluteMethod am) {
        throw new IllegalArgumentException("Method not found: " + am.getMethodName());
    }

    public static String getNewVariableName() {
        StringBuilder varName = new StringBuilder();
        int currentCount = count++;

        while (currentCount >= 0) {
            char letter = (char) ('a' + currentCount % 26);
            varName.insert(0, letter); // Prepend the character
            currentCount = currentCount / 26 - 1;
        }

        return varName.toString();
    }

    public void bytecodesToString() {
        for (Bytecode bytecode : bytecodes) {
            System.out.println(bytecode.toString());
            System.out.println();
        }
    }

    public static Context getCtx() {
        return ctx;
    }

    public static ArrayList<Bytecode> getBytecodes() {
        return bytecodes;
    }
}
