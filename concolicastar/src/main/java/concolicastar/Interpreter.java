package concolicastar;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.kitfox.svg.Path;
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

    public static long lastLoopTarget = 0;

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
                // startNode.setCondition(exprList.get(0));
                return;
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
        // stack.initializeBitVector(bc);
        for (Element el : args) {
            // System.out.println(el.toString());
            stack.getLv().push(el);
        }

        boolean targetEncountered = false;

        BranchNode currentBranchNode = null;
        BranchNode lastBranchNode = null;
        BoolExpr lastCondition = null;

        boolean lastEvaluation = false;
        while (stack.getPc() < bc.getBytecode().size()) {
            JSONObject bytecode = (JSONObject) bc.getBytecode().get(stack.getPc());
            String oprString = (String) bytecode.get("opr");

            if ((oprString.equals("if") || oprString.equals("ifz"))) {
                currentBranchNode = Pathcreator.findBranchNodeByAMAndIndex(am, stack.getPc());

                // Update true/false child/parents references:
                if (lastBranchNode != null) {
                    if (lastEvaluation) {
                        System.out.println("Adding True child: " + stack.getBoolExpr());
                        lastBranchNode.setTrueChild(currentBranchNode);
                        currentBranchNode.addParent(lastBranchNode);
                    } else {
                        System.out.println("Adding False child: " + stack.getBoolExpr());
                        lastBranchNode.setFalseChild(currentBranchNode);
                        currentBranchNode.addParent(lastBranchNode);
                    }
                }
                if (currentBranchNode.equals(targetNode)) {
                    targetEncountered = true;
                }
            }

            Operations op = new Operations(bytecode,bootstrapMethods.getBootstrapMethods(), ctx);
            stack = Operations.doOperation(stack, oprString);

            actualCost++;
            if (oprString.equals("return")) {
                return stack;
            }

            if (targetEncountered) {
                targetNode.setCost(actualCost);
                if (ConcolicOperations.getComparisonFlag()) {
                    lastEvaluation = false;
                    lastCondition = ctx.mkNot(stack.getBoolExprList().get(stack.getBoolExprList().size()-1));
                    targetNode.addCondition(lastCondition);
                    System.out.println("False: " + stack.getBoolExpr());
                } else {
                    lastEvaluation = true;
                    lastCondition = stack.getBoolExprList().get(stack.getBoolExprList().size()-1);
                    targetNode.addCondition(lastCondition);
                    System.out.println("True: " + stack.getBoolExpr());
                }
                System.out.println("Full expression: " + stack.getBoolExpr());
                return stack;
            } 



            lastBranchNode = currentBranchNode;
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
        lastLoopTarget = 0;
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
        // stack.initializeBitVector(bc);''
        for (Element el : args) {
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
