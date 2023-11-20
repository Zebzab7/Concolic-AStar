package concolicastar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import java.util.PriorityQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.microsoft.z3.Expr;

public class Pathcreator {

    ArrayList<Bytecode> bytecodes;
    
    // Store branches
    HashMap<AbsoluteMethod,ArrayList<BranchNode>> branches;
    HashMap<AbsoluteMethod,HashMap<Integer,Integer>> jumps;

    // Stores the method invoked, as well as a reference to the location from which it was invoked
    HashMap<AbsoluteMethod,ArrayList<Reference>> methodInvocations = new HashMap<AbsoluteMethod,ArrayList<Reference>>();

    public Pathcreator() {
        bytecodes = Interpreter.getBytecodes();
        jumps = new HashMap<AbsoluteMethod, HashMap<Integer,Integer>>();
        branches = new HashMap<AbsoluteMethod, ArrayList<BranchNode>>();
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
                        } else {
                            branches.get(bc.getAm()).add(new BranchNode("if", bc.getAm(), instruction, Integer.MAX_VALUE));
                        } 
                    }
                }
                instruction++;
            }
            instruction=0;
        }
    }

    // Traverse backwards, passing in which branch we are searching for
    public void buildHeuristicMap(BranchNode targetNode) {
        AbsoluteMethod am = targetNode.getAm();

        // Find the branch we are looking for
        ArrayList<BranchNode> branchStack = new ArrayList<>();
        for (BranchNode branch : branches.get(am)) {
            if (branch.equals(targetNode)) {
                targetNode = branch;
                targetNode.setCost(0);
                branchStack.add(branch);
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

            ArrayList<BranchNode> foundBranches 
                = searchMethodInvocations(cost, currentAM, currentBytecode, instructionIndex, currentBranch);

            for (BranchNode branchNode : foundBranches) {
                branchStack.add(branchNode);
            }

            BranchNode startforASatr = null;
            if (foundBranches.size() == 0) {
                startforASatr = branchStack.get(branchStack.size()-1);
                break;
            }
        }
        // aStar(startforASatr,currentBranch);
        System.out.println("here");
        MakeGraph.generateGraph(startingBranch);
        System.out.println(startingBranch.toString());
    }
    
    public ArrayList<BranchNode> searchMethodInvocations(int cost, AbsoluteMethod am, 
        JSONArray currentBytecode, int instructionIndex, BranchNode currentBranch) {
        for (int i = instructionIndex; i >= 0; i--) {
            cost++;
            if (jumps.containsKey(am) && jumps.get(am).containsKey(instructionIndex)) {

                // Find the branch, update parent and child reference and cost, then push onto stack
                for (BranchNode foundBranch : branches.get(am)) {
                    if (foundBranch.getInstructionIndex() == instructionIndex) {
                        foundBranch.children.add(currentBranch);
                        currentBranch.parents.add(foundBranch);
                        if (foundBranch.getCost() > cost) {
                            foundBranch.setCost(cost);
                        }
                        return new ArrayList<BranchNode>(Arrays.asList(foundBranch));
                    }
                }
            }
        }

        ArrayList<BranchNode> methodBranches = new ArrayList<BranchNode>();
        // If we reach here, we have not found a branch, so we must search for method invocations
        for (AbsoluteMethod aMethod : methodInvocations.keySet()) {
            ArrayList<BranchNode> branches 
                = searchMethodInvocations(cost, aMethod, currentBytecode, instructionIndex, currentBranch);
            methodBranches.addAll(branches);
        }
        return methodBranches;
    }

    // public BoolExpr aStar(BranchNode startNode, BranchNode targetNode) {
    //     buildHeuristicMap(targetNode);
    //     Context ctx = Interpreter.getCtx();
    //     BoolExpr expr = ctx.mkTrue();

    //     PriorityQueue<BranchNode> openList = new PriorityQueue<>();
    //     PriorityQueue<BranchNode> closedList = new PriorityQueue<>();
        
    //     openList.add(startNode);

    //     if (openList.isEmpty()){
    //         return null;
    //     }

        
        
    //     if (startNode.children.size()>0) {
    //         int subCost = 0;
    //         for (BranchNode child : startNode.children) {
    //             // check the cost
    //             openList.add(child);
    //             if () {
    //                 BoolExpr addExpr = ctx.mkAnd();
    //             }
    //         }
    //     }
        
    //     return expr;
    // }

    //Find paths to an if or ifz condition
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