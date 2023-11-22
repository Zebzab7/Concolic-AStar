package concolicastar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import com.kitfox.svg.A;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import java.util.PriorityQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Pathcreator {

    static ArrayList<Bytecode> bytecodes = Interpreter.getBytecodes();
    
    // Store branches
    static HashMap<AbsoluteMethod,ArrayList<BranchNode>> branches;
    static HashMap<AbsoluteMethod,HashMap<Integer,Integer>> jumps;

    
    // Stores the method invoked, as well as a reference to the location from which it was invoked
    static HashMap<AbsoluteMethod,ArrayList<Reference>> methodInvocations;

    public Pathcreator() {
        // bytecodes = Interpreter.getBytecodes();
        jumps = new HashMap<AbsoluteMethod, HashMap<Integer,Integer>>();
        branches = new HashMap<AbsoluteMethod, ArrayList<BranchNode>>();
        methodInvocations = new HashMap<AbsoluteMethod, ArrayList<Reference>>();
        findMethodsContainingMethods();
        System.out.println(methodstacktoString());
    }

    public void findMethodsContainingMethods(){
        int instruction = 0;
        for(Bytecode bc : bytecodes){
            for(Object obj : bc.getBytecode()){
                JSONObject bytecode = (JSONObject) obj;
                //Asuming that all invokestatic are methods and none are other things.
                
                // Initializes invocation pointers
                if(bytecode.get("opr").equals("invoke") && bytecode.get("access").equals("static")){
                    JSONObject method = (JSONObject) bytecode.get("method");
                    String className = (String) ((JSONObject) method.get("ref")).get("name");  
                    String methodName = (String) (method.get("name"));
                    AbsoluteMethod am = new AbsoluteMethod(className,methodName);

                    //Checks if it exists, if not create new Arraylist object
                    if(!methodInvocations.containsKey(am)){
                        methodInvocations.put(am, new ArrayList<>());
                    }
                    //adds to methodstack
                    methodInvocations.get(am).add(new Reference(bc.getAm(),instruction));
                }

                // initializes jumps and branches 
                if (bytecode.get("opr").equals("if") || bytecode.get("opr").equals("ifz")) {
                    AbsoluteMethod am = bc.getAm();
                    Number numIndex = (Number) bytecode.get("target");
                    int targetIndex = numIndex.intValue();
                    
                    // Add a jump from target to start of if
                    if (!jumps.containsKey(am)) {
                        jumps.put(am, new HashMap<Integer,Integer>());
                    } 
                    jumps.get(am).put(targetIndex, instruction);

                    JSONObject target = (JSONObject) bc.getBytecode().get(targetIndex-1);
                    String targetOpr = (String) target.get("opr");

                    // IF there is a goto at target-1 AND it jumps backwards THEN it must be a loop
                    // IF there is one and it jumpsrwards, it must an if-else
                    // ELSE it must be an if
                    if (!branches.containsKey(am)) {
                        branches.put(am, new ArrayList<BranchNode>());
                    }

                    if (target.get("opr").equals("goto")){
                        System.out.println("Target: " + target.toString());
                        Number numTarget = (Number) target.get("target");
                        int targetTarget = numTarget.intValue();
                        if (targetTarget < targetIndex) {
                            branches.get(bc.getAm()).add(new BranchNode("loop", bc.getAm(), instruction, Integer.MAX_VALUE));
                        } else if ( targetTarget > targetIndex) {
                            branches.get(bc.getAm()).add(new BranchNode("if-else", bc.getAm(), instruction, Integer.MAX_VALUE));
                        }
                    } else {
                        branches.get(bc.getAm()).add(new BranchNode("if", bc.getAm(), instruction, Integer.MAX_VALUE));
                    } 
                    
                }
                instruction++;
            }
            instruction=0;
        }
    }

    // Traverse backwards, passing in which branch we are searching for
    public BranchNode buildHeuristicMap(BranchNode targetNode) {
        AbsoluteMethod am = targetNode.getAm();
        
        System.out.println("Branches for this method: ");
        for (BranchNode branch : branches.get(am)) {
            System.out.println(branch);
        }

        // Find the branch we are looking for
        ArrayList<BranchNode> branchStack = new ArrayList<>();
        for (BranchNode branch : branches.get(am)) {
            if (branch.equals(targetNode)) {
                targetNode = branch;
                targetNode.setH(0);
                branchStack.add(branch);
                System.out.println("Found target node: " + targetNode);
            }
        }

        BranchNode startingBranch = branchStack.get(0);
        
        int cost = 0;
        while (branchStack.size() > 0) {
            BranchNode currentBranch = branchStack.remove(0);
            int instructionIndex = currentBranch.getInstructionIndex();

            AbsoluteMethod currentAM = currentBranch.getAm();
            Bytecode bc = Interpreter.findMethod(am);
            JSONArray currentBytecode = (JSONArray) bc.getBytecode();

            // ArrayList<BranchNode> foundBranches = null;
            ArrayList<BranchNode> foundBranches 
                = searchMethodInvocations(cost, currentAM, currentBytecode, instructionIndex-1, currentBranch);
                
            if (foundBranches == null) {
                MakeGraph.generateGraph(currentBranch);
                System.out.println(currentBranch);
                return currentBranch;
            }

            for (BranchNode branchNode : foundBranches) {
                branchStack.add(branchNode);
            }
        }
        return null;
    }

    public Element[] solverCheck(BoolExpr expr){
        Context ctx = Interpreter.getCtx();
        Solver solver = ctx.mkSolver();
        solver.add(expr);
        ArrayList<Element> elements = new ArrayList<Element>();
        if(solver.check() == Status.SATISFIABLE){
            Model model = solver.getModel();
            //Update the values of the elements if they changed
            for (int i = 0; i < elements.size(); i++) {
                Element e = elements.get(i);
                switch (e.getType()) {
                    case "int":
                        IntExpr intExpr = (IntExpr) e.getSymbolicValue();
                        if (model.getConstInterp(intExpr) != null) {
                            e.setValue(Integer.parseInt(model.getConstInterp(intExpr).toString()));
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Type not handled");
                }
            }
            Element[] args = new Element[model.getNumConsts()];
            for(int i = 0; i < model.getNumConsts(); i++){
                args[i] = elements.get(i);
            }
            return args;
        }
        return null;
    }
    
    public ArrayList<BranchNode> searchMethodInvocations(int cost, AbsoluteMethod am, 
        JSONArray currentBytecode, int initialIndex, BranchNode currentBranch) {
        for (int currentInstructionIndex = initialIndex; currentInstructionIndex >= 0; currentInstructionIndex--) {
            cost++;
            //Has the possible jump or is the top of an If statement

            if (((JSONObject)currentBytecode.get(currentInstructionIndex)).get("opr").equals("goto")) {
                Number numIndex = (Number) ((JSONObject)currentBytecode.get(currentInstructionIndex)).get("target");
                int targetIndex = numIndex.intValue();

                if (targetIndex > currentInstructionIndex) {
                    // If we are jumping forwards, we must be in the else bracket of an if-else
                    // Find the branch, update parent and child reference and cost, then push onto stack
                    for (BranchNode foundBranch : branches.get(am)) {
                        if (foundBranch.getInstructionIndex() == currentInstructionIndex) {
                            currentBranch.addParent(foundBranch);
                            if (foundBranch.getH() > cost) {
                                foundBranch.setH(cost);
                                foundBranch.setFalseChild(currentBranch);
                            }
                            return new ArrayList<BranchNode>(Arrays.asList(foundBranch));
                        }
                    }
                } else {
                    // If we are jumping backwards, we must be in a loop
                    // In this case we just want the condition to enter that loop to be false, because it is faster to skip over it.
                }
            }

            // If and loops
            // jumps.containsKey(am) && jumps.get(am).containsKey(i) 
                // || ((JSONObject)currentBytecode.get(i)).get("opr").equals("if")
                // || ((JSONObject) currentBytecode.get(i)).get("opr").equals("ifz") --
                // jumps.containsKey(am) && jumps.get(am).containsKey(i)  || currentBranch.getType().equals("if/ifz/if_else")
            if (((JSONObject)currentBytecode.get(currentInstructionIndex)).get("opr").equals("if")
                || ((JSONObject) currentBytecode.get(currentInstructionIndex)).get("opr").equals("ifz")) {

                // Find the branch, update parent and child reference and cost, then push onto stack
                for (BranchNode foundBranch : branches.get(am)) {
                    if (foundBranch.getInstructionIndex() == currentInstructionIndex) {
                        String type = (String)((JSONObject)currentBytecode.get(currentInstructionIndex)).get("opr");

                        currentBranch.addParent(foundBranch);
                        if (foundBranch.getH() > cost) {
                            foundBranch.setH(cost);

                            // IF loop or just "if" then:
                            foundBranch.setTrueChild(currentBranch);
                        }
                        return new ArrayList<BranchNode>(Arrays.asList(foundBranch));
                    }
                }
            }
        }

        // If we reach here, we have not found a branch, so we must search for method invocations¨
        if (!methodInvocations.containsKey(am)) {
            return null;
        }
        ArrayList<BranchNode> methodBranches = new ArrayList<BranchNode>();
        for (Reference ref : methodInvocations.get(am)) {
            ArrayList<BranchNode> branches 
                = searchMethodInvocations(cost, ref.getAM(), currentBytecode, ref.getInstruction(), currentBranch);
            methodBranches.addAll(branches);
        }
        return methodBranches;
    }

    public BoolExpr aStar(BranchNode startNode, BranchNode targetNode, HashSet<BranchNode> explored) {
        Interpreter.setAstarInterpretation(true);
        Context ctx = Interpreter.getCtx();

        AbsoluteMethod am = startNode.getAm();
        Bytecode bc = Interpreter.findMethod(am);

        // Generate list of alphabet: 
        String[] alphabet = new String[26];
        for (int i = 0; i < alphabet.length; i++) {
            alphabet[i] = "" + (char)('a' + i);
        }

        // Read initial arguments
        ArrayList<Element> elements = new ArrayList<Element>();
        ArrayList<String> argTypes = bc.getArgsTypes();
        int count = 0;
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

        Solver solver = ctx.mkSolver();
        boolean solutionFound = false;
        BoolExpr expr = null;

        // Prepare Astar
        PriorityQueue<BranchNode> frontier = new PriorityQueue<BranchNode>();
        // PriorityQueue<BranchNode> explored = new PriorityQueue<BranchNode>();

        HashMap<BranchNode, BranchNode> cameFrom = new HashMap<BranchNode, BranchNode>();

        HashMap<BranchNode, Integer> gScore = new HashMap<BranchNode, Integer>();
        gScore.put(startNode, 0);

        HashMap<BranchNode, Integer> fScore = new HashMap<BranchNode, Integer>();
        fScore.put(startNode, startNode.getH());

        frontier.add(startNode);
        
        if (frontier.isEmpty()){
            throw new IllegalArgumentException("Frontier is empty at start");
        }

        // Astar loop
        while (!frontier.isEmpty()) {
            BranchNode currentNode = frontier.poll();
            if(currentNode.equals(targetNode)){
                return reconstructPath(cameFrom, currentNode);
            }
            explored.add(currentNode);
            for (BranchNode neighbor: currentNode.getChildren()) {
                if (explored.contains(neighbor)) {
                    continue;
                }

                BoolExpr pathToNeighbor = reconstructPath(cameFrom, neighbor);
                
                // Explore the neighbor
                if (currentNode.getTrueChild().equals(neighbor)) {
                    pathToNeighbor = ctx.mkAnd(pathToNeighbor, currentNode.getCondition());
                } else if (currentNode.getFalseChild().equals(neighbor)) {
                    pathToNeighbor = ctx.mkAnd(pathToNeighbor, ctx.mkNot(currentNode.getCondition()));
                } 
                args = solverCheck(pathToNeighbor);
                Interpreter.interpretStartToTarget(startNode.getAm(), args, neighbor);
                int tentativeGScore = Interpreter.getActualCost();

                if (tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, currentNode);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + neighbor.getH());
                    if (!frontier.contains(neighbor)) {
                        frontier.add(neighbor);
                    }
                }
            }
        }

        // Assume goal was not found  
        System.out.println("No path to goal was found");   
        throw new IllegalArgumentException("No path to goal was found");     
    }
    
    public static BoolExpr reconstructPath(HashMap<BranchNode, BranchNode> cameFrom, BranchNode currentNode) {
        Context ctx = Interpreter.getCtx();
        BoolExpr totalPath = ctx.mkTrue();

        while (cameFrom.containsKey(currentNode)) {
            BranchNode previousNode = cameFrom.get(currentNode);
            currentNode = cameFrom.get(currentNode);
            if (currentNode.getTrueChild().equals(previousNode)) {
                totalPath = ctx.mkAnd(totalPath, currentNode.getCondition());
            } else if (currentNode.getFalseChild().equals(previousNode)) {
                totalPath = ctx.mkAnd(totalPath, ctx.mkNot(currentNode.getCondition()));
            } else {
                throw new IllegalArgumentException("Previous node is not a child of current node");
            }
        }
        return totalPath;
    }

    // Find paths to an if or ifz condition
    public HashMap<Integer,Integer> findIfPaths(int size, AbsoluteMethod am, Bytecode bc){
        HashMap<Integer,Integer> ifmap = new HashMap<Integer,Integer>();
        JSONObject bytecode = null;
        for(int i = 0; i < size; i++){
            bytecode = (JSONObject) bc.getBytecode().get(i);
            if(bytecode.get("opr").equals("if") || bytecode.get("opr").equals("ifz")){
                ifmap.put(i, (int)bytecode.get("target"));
            }
        }
        return ifmap;
    }

    public String methodstacktoString(){
        String s = "";
        for(AbsoluteMethod am : methodInvocations.keySet()){
            s += am.toString() + "=[";
            for(Reference cd : methodInvocations.get(am)){
                s+= cd.getAM().toString() + " from " + cd.getInstruction() + ", ";
            }
            s += "]\n";
        }

        return s;
    }

    public static HashMap<AbsoluteMethod, ArrayList<BranchNode>> getBranches() {
        return branches;
    }

    public static void setBranches(HashMap<AbsoluteMethod, ArrayList<BranchNode>> branches) {
        Pathcreator.branches = branches;
    }

    public static HashMap<AbsoluteMethod, HashMap<Integer, Integer>> getJumps() {
        return jumps;
    }

    public static void setJumps(HashMap<AbsoluteMethod, HashMap<Integer, Integer>> jumps) {
        Pathcreator.jumps = jumps;
    }

    public static HashMap<AbsoluteMethod, ArrayList<Reference>> getMethodInvocations() {
        return methodInvocations;
    }

    public static void setMethodInvocations(HashMap<AbsoluteMethod, ArrayList<Reference>> methodInvocations) {
        Pathcreator.methodInvocations = methodInvocations;
    }

}

/**
 * InnerPathcreator
 */

class Reference {
    AbsoluteMethod am;
    int instruction;

    public Reference(AbsoluteMethod am, int instruction) {
        this.am = am;
        this.instruction = instruction;
    }
    public AbsoluteMethod getAM(){
        return am;
    }
    public int getInstruction(){
        return instruction;
    }
    public void setAM(AbsoluteMethod am){
        this.am = am;
    }
    public void setInstruction(int instruction){
        this.instruction = instruction;
    }
}



class PathHolder{
    AbsoluteMethod am;
    HashMap<Integer,Integer> map;
    public PathHolder(AbsoluteMethod am, HashMap<Integer,Integer> map){
        this.am = am;
        this.map = map;
    }
    public AbsoluteMethod getAM(){
        return am;
    }
    public HashMap<Integer,Integer> getMap(){
        return map;
    }
    public void setAM(AbsoluteMethod am){
        this.am = am;
    }
    public void setMap(HashMap<Integer,Integer> map){
        this.map = map;
    }
}