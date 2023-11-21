package concolicastar;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Interpreter {

    public static int count = 0;

    public static boolean astar = false;

    public static boolean interrupt;

    public static int actualCost = 0;

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


    /*
     * Initializes the first branchNodes condition
     */
    public static void initializeInterpreter(BranchNode startNode, AbsoluteMethod am) {
        Bytecode bc = findMethod(am);
        ProgramStack stack = new ProgramStack(new Stack(), new Stack(), am, 0);
        stack.initializeBitVector(bc);

        // Generate list of alphabet: 
        String[] alphabet = new String[26];
        for (int i = 0; i < alphabet.length; i++) {
            alphabet[i] = "" +(char)('a' + i);
        }

        // Read the arguments of the method and assign initial values
        int count = 0;
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

        Element[] args = new Element[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            args[i] = elements.get(i);
        }

        for (Element el : args) {
            // System.out.println(el.toString());
            stack.getLv().push(el);
        }

        while (true) {
            JSONObject bytecode = (JSONObject) bc.getBytecode().get(stack.getPc());
            String oprString = (String) bytecode.get("opr");

            Operations op = new Operations(bytecode,bootstrapMethods.getBootstrapMethods(), ctx);
            stack = Operations.doOperation(stack, oprString);

            if (oprString.equals("return")) {
                throw new IllegalArgumentException("Hit a return");
            }

            if (oprString.equals("if") || oprString.equals("ifz")) {
                ArrayList<BoolExpr> exprList = stack.getBoolExprList();
                startNode.setCondition(exprList.get(0));
                return;
            }

            stack.setPc(stack.getPc() + 1);
        }
    }

    /**
     * Potential improvement
     * @param startNode
     * @param targetNode
     */
    public static boolean interpretBranchToTarget(BranchNode startNode, BranchNode targetNode) {
        System.out.println("Running branch to target");
        System.out.println("initial expression: " + startNode.getCondition().toString());
        AbsoluteMethod am = startNode.getAm();
        
        // Generate list of alphabet: 
        String[] alphabet = new String[26];
        for (int i = 0; i < alphabet.length; i++) {
            alphabet[i] = "" + (char)('a' + i);
        }
 
        Solver solver = ctx.mkSolver();
        BoolExpr condition = startNode.getCondition();
        if (startNode.getFalseChild() != null && startNode.getFalseChild().equals(targetNode)) {
            System.out.println("Negating condition");
            condition = ctx.mkNot(condition);
        }

        ArrayList<Element> elements = new ArrayList<Element>();

        solver.add(condition);
        
        Status satisfiable = solver.check();
        if (satisfiable == Status.SATISFIABLE) {
            // Solve in terms of "condition"         
            Model model = solver.getModel();
            
            for (FuncDecl constant : model.getConstDecls()) {

                // Check if the constant is of type Int
                if (constant.getRange().toString().equals("Int")) {
                    
                    // Get the name of the variable
                    String varName = constant.getName().toString();

                    // Get the value of the variable from the model
                    Expr value = model.getConstInterp(constant);
                    Number intValue = (Number) Integer.parseInt(value.toString());
                    // Convert expr to Number
                    // int intValue = ((IntNum) value).getInt();
                    // String numValue = (String) value;
                    
                    elements.add(new Element("int", intValue, ctx.mkIntConst(varName)));
                }else{
                    throw new IllegalArgumentException("Type not handled");
                }
            }
        } else {
            return false;
        }

        Element[] args = new Element[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            args[i] = elements.get(i);
        }

        Bytecode bc = findMethod(am);
        
        int instructionIndex = startNode.getInstructionIndex();

        JSONObject bytecode = (JSONObject) bc.getBytecode().get(instructionIndex);
        String oprString = (String) bytecode.get("opr");
        if(oprString.equals("ifz")){
            instructionIndex--;
        }
        else if(oprString.equals("if")){
            instructionIndex-=2;
        }

        ProgramStack stack = new ProgramStack(new Stack(), new Stack(), am, instructionIndex);
        for (Element el : args) {
            stack.getLv().push(el);
        }

        stack.initializeBitVector(bc);

        while (true) {
            bytecode = (JSONObject) bc.getBytecode().get(stack.getPc());
            oprString = (String) bytecode.get("opr");

            Operations op = new Operations(bytecode,bootstrapMethods.getBootstrapMethods(), ctx);
            stack = Operations.doOperation(stack, oprString);

            if (oprString.equals("return")) {
                throw new IllegalArgumentException("Hit a return");
            }

            if ((oprString.equals("if") || oprString.equals("ifz")) 
                && stack.getPc() == targetNode.getInstructionIndex()) {
                targetNode.setActualCost(count);
                targetNode.setCondition(stack.getBoolExprList().get(stack.getBoolExprList().size()-1));
                return true;
            }

            stack.setPc(stack.getPc() + 1);
        }
    }

    /**
     * Interpret the specified branchNode
     * @param startNode
     * @return
     */
    public static ProgramStack interpretStartToTarget(AbsoluteMethod am, Element[] args, BranchNode targetNode) {
        actualCost = 0;

        if (args == null) {
            args = new Element[0];
        }
        Bytecode bc = findMethod(am);
        ProgramStack stack = new ProgramStack(new Stack(), new Stack(), am, 0);
        stack.initializeBitVector(bc);
        for (Element el : args) {
            // System.out.println(el.toString());
            stack.getLv().push(el);
        }

        boolean flag = false;
        while (stack.getPc() < bc.getBytecode().size()) {
            JSONObject bytecode = (JSONObject) bc.getBytecode().get(stack.getPc());
            String oprString = (String) bytecode.get("opr");

            if ((oprString.equals("if") || oprString.equals("ifz")) 
                && stack.getPc() == targetNode.getInstructionIndex()) {
                flag = true;
            }

            Operations op = new Operations(bytecode,bootstrapMethods.getBootstrapMethods(), ctx);
            stack = Operations.doOperation(stack, oprString);

            actualCost++;
            if (oprString.equals("return")) {
                return stack;
            }

            if (flag) {
                targetNode.setActualCost(count);
                if (ConcolicOperations.getComparisonFlag()) {
                    BoolExpr expr = ctx.mkNot(stack.getBoolExprList().get(stack.getBoolExprList().size()-1));
                    targetNode.setCondition(expr);
                    System.out.println("False: " + stack.getBoolExpr());
                } else {
                    BoolExpr expr = stack.getBoolExprList().get(stack.getBoolExprList().size()-1);
                    targetNode.setCondition(expr);
                    System.out.println("True: " + stack.getBoolExpr());
                }
                System.out.println("Full expression: " + stack.getBoolExpr());
                return stack;
            }

            stack.setPc(stack.getPc() + 1);
            // System.out.println(stack.toString());
        }
        return stack;
    }

    /**
     * Interpret the specified function (Top-level)
     * @param am
     * @param args
     * @return
     */
    public static ProgramStack interpretFunction(AbsoluteMethod am, Element[] args) {
        count = 0;
        interrupt = false;
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
        ProgramStack stack = new ProgramStack(new Stack(), new Stack(), am, 0);
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

    public static void setAstarInterpretation(boolean astar) {
        Interpreter.astar = astar;
    }

    public void bytecodesToString() {
        for (Bytecode bytecode : bytecodes) {
            System.out.println(bytecode.toString());
            System.out.println();
        }
    }

    public static int getActualCost() {
        return actualCost;
    }

    public static void setInterrupt(boolean interrupt) {
        Interpreter.interrupt = interrupt;
    }

    public static boolean getInterrupt() {
        return interrupt;
    }

    public boolean getAstar () {
        return astar;
    }

    public static Context getCtx() {
        return ctx;
    }

    public static ArrayList<Bytecode> getBytecodes() {
        return bytecodes;
    }
}
