package concolicastar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Pathcreator {

    ArrayList<Bytecode> bytecodes;
    
    // Store branches
    HashMap<AbsoluteMethod,ArrayList<Branch>> branches;
    HashMap<AbsoluteMethod,HashMap<Integer,Integer>> jumps;

    // Stores the method invoked, as well as a reference to the location from which it was invoked
    HashMap<AbsoluteMethod,ArrayList<Reference>> methodInvocations = new HashMap<AbsoluteMethod,ArrayList<Reference>>();

    public Pathcreator() {
        // for(JsonFile file : files) {
        //     JSONArray methods = file.getMethods();
        //     for (Object obj : methods) {
        //         JSONObject method = (JSONObject) obj;
        //         String methodName = (String) method.get("name");
        //         JSONObject code = (JSONObject) method.get("code");
        //         JSONArray args = (JSONArray) method.get("params");
        //         Bytecode bc = new Bytecode(file.getFileName(), methodName, (JSONArray) code.get("bytecode"), args);
        //         bytecodes.add(bc);
        //     }
        // }

        bytecodes = Interpreter.getBytecodes();
        jumps = new HashMap<AbsoluteMethod, HashMap<Integer,Integer>>();
        branches = new HashMap<AbsoluteMethod, ArrayList<Branch>>();
        findMethodsContainingMethods();
        System.out.println(methodstacktoString());
    }

    public void findMethodsContainingMethods(){
        int instruction = 0;
        for(Bytecode bc : bytecodes){
            for(Object obj : bc.getBytecode()){
                instruction++;
                JSONObject bytecode = (JSONObject) obj;
                //Asuming that all invokestatic are methods and none are other things.
                
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
                        branches.put(am, new ArrayList<Branch>());
                    }

                    if (target.get("opr").equals("goto")){
                        System.out.println("Target: " + target.toString());
                        Number numTarget = (Number) target.get("target");
                        int targetTarget = numTarget.intValue();
                        if (targetTarget < targetIndex) {
                            branches.get(bc.getAm()).add(new Branch("loop", bc.getAm(), instruction, Integer.MAX_VALUE));
                        } else if ( targetTarget > targetIndex) {
                            branches.get(bc.getAm()).add(new Branch("if-else", bc.getAm(), instruction, Integer.MAX_VALUE));
                        } else {
                            branches.get(bc.getAm()).add(new Branch("if", bc.getAm(), instruction, Integer.MAX_VALUE));
                        } 
                    }
                }
                
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
            }
            instruction=0;
        }
    }

    // Traverse backwards, passing in which branch we are searching for
    public void buildHeuristicMap(AbsoluteMethod am, int instructionIndex) {
        System.out.println("Branches: " + branches.toString());
        // Find the branch we are looking for
        for (Branch branch : branches.get(am)) {
            if (branch.getInstructionIndex() == instructionIndex) {
                branch.setCost(0);
            }
        }
        // HashMap<Reference, Integer> heuristicMap = new HashMap<Reference, Integer>();
        // methodInvocations.get(am).forEach((ref) -> {
        //     heuristicMap.put(ref, Integer.MAX_VALUE);
        // });
        Bytecode bc = Interpreter.findMethod(am);
        JSONArray bcArray = (JSONArray) bc.getBytecode();
        
        calculateHeuristic(bcArray, instructionIndex, am);
        System.out.println("Heuristic map: " + branches.toString());
    }

    public int calculateHeuristic(JSONArray bytecode, int instructionIndex, AbsoluteMethod am) {
        return 1;
        // // If statements and loops should be counted extra 
        // if (jumps.containsKey(am) && jumps.get(am).containsKey(instructionIndex)) {
        //     int index = jumps.get(am).get(instructionIndex);
        //     return 10 + calculateHeuristic(bytecode, index, am);
        // }
        // return 1 + calculateHeuristic(bytecode, instructionIndex-1, am);
    }
    // public int heuristic(AbsoluteMethod am, Branch branch) {
    //     HashMap<Reference, Integer> heuristicMap = new HashMap<Reference, Integer>();
    //     methodInvocations.get(am).forEach((ref) -> {
    //         heuristicMap.put(ref, Integer.MAX_VALUE);
    //     });
    //     for (int i = 0; i < bytecodes.size(); i++) {
    //         Bytecode bc = bytecodes.get(i);
    //         JSONArray bcArray = (JSONArray) bc.getBytecode();
    //         if (bc.getAm().equals(am)){
    //             return calculateHeuristic(bcArray, bcArray.size()-1, am);
    //         }
    //     }
    //     return -1;
    // }
    
    // // Recursively travel paths in a backwards manner

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