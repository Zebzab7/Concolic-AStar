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
    static HashMap<AbsoluteMethod,ArrayList<BranchNode>> originalBranches;
    static HashMap<AbsoluteMethod,HashMap<Integer,Integer>> jumps;
    
    // Stores the method invoked, as well as a reference to the location from which it was invoked
    static HashMap<AbsoluteMethod,ArrayList<Reference>> methodInvocations;

    public Pathcreator() {
        // bytecodes = Interpreter.getBytecodes();
        jumps = new HashMap<AbsoluteMethod, HashMap<Integer,Integer>>();
        originalBranches = new HashMap<AbsoluteMethod, ArrayList<BranchNode>>();
        methodInvocations = new HashMap<AbsoluteMethod, ArrayList<Reference>>();
        findMethodsContainingMethods();
        System.out.println(methodstacktoString());
    }

    public HashMap<AbsoluteMethod,ArrayList<BranchNode>> createDeepCopyOfBranches() {
        HashMap<AbsoluteMethod,ArrayList<BranchNode>> branchesCopy = new HashMap<AbsoluteMethod,ArrayList<BranchNode>>();
        // Get all branches out into ong long list:
        ArrayList<BranchNode> allNodes = new ArrayList<BranchNode>();
        for (AbsoluteMethod am : originalBranches.keySet()) {
            ArrayList<BranchNode> branchList = originalBranches.get(am);
            for (BranchNode branchNode : branchList) {
                allNodes.add(branchNode);
            }
        }

        // Get new set of nodes
        allNodes = BranchNode.deepCopyBranchNodes(allNodes);

        // Put them back into the hashmap
        for (AbsoluteMethod am : originalBranches.keySet()) {
            ArrayList<BranchNode> branchList = originalBranches.get(am);
            ArrayList<BranchNode> branchListCopy = new ArrayList<BranchNode>();
            for (BranchNode branchNode : branchList) {
                for (BranchNode branchNode2 : allNodes) {
                    if (branchNode.equals(branchNode2)) {
                        branchListCopy.add(branchNode2);
                    }
                }
            }
            branchesCopy.put(am, branchListCopy);
        }

        return branchesCopy;
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
                    if (!originalBranches.containsKey(am)) {
                        originalBranches.put(am, new ArrayList<BranchNode>());
                    }

                    if (target.get("opr").equals("goto")){
                        System.out.println("Target: " + target.toString());
                        Number numTarget = (Number) target.get("target");
                        int targetTarget = numTarget.intValue();
                        if (targetTarget < targetIndex) {
                            originalBranches.get(bc.getAm()).add(new BranchNode("loop", bc.getAm(), instruction, Integer.MAX_VALUE));
                        } else if ( targetTarget > targetIndex) {
                            originalBranches.get(bc.getAm()).add(new BranchNode("if-else", bc.getAm(), instruction, Integer.MAX_VALUE));
                        }
                    } else {
                        originalBranches.get(bc.getAm()).add(new BranchNode("if", bc.getAm(), instruction, Integer.MAX_VALUE));
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
        for (BranchNode branch : originalBranches.get(am)) {
            System.out.println(branch);
        }

        // Find the branch we are looking for
        ArrayList<BranchNode> branchStack = new ArrayList<>();

        targetNode = findBranchNode(am, targetNode, originalBranches);
        targetNode.setH(0);
        branchStack.add(targetNode);

        BranchNode startingBranch = branchStack.get(0);
        
        int cost = 0;
        while (branchStack.size() > 0) {
            BranchNode currentBranch = branchStack.remove(0);
            System.out.println("Current branch in backwards search...: " + currentBranch.toString());
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
    
    public ArrayList<BranchNode> searchMethodInvocations(int cost, AbsoluteMethod am, 
        JSONArray currentBytecode, int initialIndex, BranchNode currentBranch) {
        for (int currentInstructionIndex = initialIndex; currentInstructionIndex >= 0; currentInstructionIndex--) {
            cost++;
            //Has the possible jump or is the top of an If statement

            if (currentInstructionIndex > 0) {
                JSONObject previousJSONObject = (JSONObject) currentBytecode.get(currentInstructionIndex-1);
                if (previousJSONObject.get("opr").equals("goto") ) {
                    //If goto goes forwards: 
                    int target = ((Number) previousJSONObject.get("target")).intValue();
                    if (target > currentInstructionIndex) {
                        if (jumps.containsKey(am)) {
                            if (jumps.get(am).containsKey(currentInstructionIndex)) {
                                currentInstructionIndex = jumps.get(am).get(currentInstructionIndex);
                                for (BranchNode foundBranch : originalBranches.get(am)) {
                                    if (foundBranch.getInstructionIndex() == currentInstructionIndex) {
                                        currentBranch.addParent(foundBranch);
                                        if (foundBranch.getH() > cost) {
                                            foundBranch.setH(cost);
                                            foundBranch.setFalseChild(currentBranch);
                                        }
                                        return new ArrayList<BranchNode>(Arrays.asList(foundBranch));
                                    }
                                }   
                            }
                        }
                    }
                }
            }
 
            JSONObject instructionObject = (JSONObject) currentBytecode.get(currentInstructionIndex);
            if (instructionObject.get("opr").equals("goto")) {
                Number numIndex = (Number) ((JSONObject)currentBytecode.get(currentInstructionIndex)).get("target");
                int targetIndex = numIndex.intValue();
                
                if (targetIndex > currentInstructionIndex) {
                    // If we are jumping forwards, we must be in the else bracket of an if-else
                    // Find the branch, update parent and child reference and cost, then push onto stack
                    for (BranchNode foundBranch : originalBranches.get(am)) {
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
                for (BranchNode foundBranch : originalBranches.get(am)) {
                    if (foundBranch.getInstructionIndex() == currentInstructionIndex) {

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

        System.out.println("\n\nCommencing Astar search!!");

        HashMap<AbsoluteMethod, ArrayList<BranchNode>> newBranches = createDeepCopyOfBranches();

        // Update references to startnode and targetnode to copy versions:
        startNode = findBranchNode(startNode.getAm(), startNode, newBranches);
        targetNode = findBranchNode(targetNode.getAm(), targetNode, newBranches);
        
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
        ArrayList<IntExpr> intExprs = new ArrayList<IntExpr>();
        for (String type : argTypes) {
            switch (type) {
                case "int":
                    IntExpr intExpr = ctx.mkIntConst(alphabet[count]);
                    intExprs.add(intExpr);
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

        // ** INITIALIZE Astar **
        PriorityQueue<BranchNode> frontier = new PriorityQueue<BranchNode>();
        HashMap<BranchNode, ArrayList<BranchNode>> cameFrom = new HashMap<BranchNode, ArrayList<BranchNode>>();
        for (BranchNode branch : newBranches.get(am)) {
            cameFrom.put(branch, new ArrayList<BranchNode>());
        }
        HashMap<BranchNode, ArrayList<BranchNode>> loopsEncountered = new HashMap<BranchNode, ArrayList<BranchNode>>();
        for (BranchNode branch : newBranches.get(am)) {
            loopsEncountered.put(branch, new ArrayList<BranchNode>());
        }

        HashMap<BranchNode, Integer> gScore = new HashMap<BranchNode, Integer>();
        for (BranchNode branch : newBranches.get(am)) {
            gScore.put(branch, Integer.MAX_VALUE);
        }
        gScore.put(startNode, 0);

        HashMap<BranchNode, Integer> fScore = new HashMap<BranchNode, Integer>();
        for (BranchNode branch : newBranches.get(am)) {
            fScore.put(branch, Integer.MAX_VALUE);
        }
        fScore.put(startNode, startNode.getH());

        frontier.add(startNode);
        if (frontier.isEmpty()){
            throw new IllegalArgumentException("Frontier is empty at start");
        }

        int iterations = 0;
        Interpreter.interpretStartToTarget(startNode.getAm(), args, startNode, newBranches);

        // ** RUN Astar ** 
        while (!frontier.isEmpty()) {
            BranchNode currentNode = frontier.poll();
            System.out.println("\n\nPerforming iteration: " + iterations + " with branch: " + currentNode.toString());

            // Check if we have found the target node
            if(currentNode.equals(targetNode)){
                System.out.println("Found path to goal!");
                HashMap<BranchNode, ArrayList<BranchNode>> cameFromCopy = createCameFromCopy(cameFrom);
                return reconstructBoolExpr(cameFromCopy, currentNode);
            }

            // Mark down that this was a loop, if it was a loop
            if(currentNode.getType().equals("loop")){
                System.out.println("Found loop: " + currentNode.toString());
                if(!loopsEncountered.containsKey(currentNode)){
                    loopsEncountered.put(currentNode, new ArrayList<BranchNode>());
                }
                loopsEncountered.get(currentNode).add(currentNode);
            }

            explored.add(currentNode);
            System.out.println("Explored: " + explored.toString());
            
            // Explore the neighbors
            for (BranchNode neighbor: currentNode.getChildren()) {

                System.out.println("Checking out neighbor: " + neighbor);
                if (explored.contains(neighbor)) {
                    System.out.println("Neighbor: " + neighbor.toString() + " has already been explored");

                    // If the currentNode is a loop that has been encountered before 
                    // do a new search with a new explore list
                    ArrayList<BranchNode> loop = loopsEncountered.get(currentNode);
                    if (loop != null && loop.get(loop.size()-1).equals(neighbor)) {

                        // Create copy updated version of explore list and cameFrom
                        HashMap<BranchNode, ArrayList<BranchNode>> cameFromCopy = createCameFromCopy(cameFrom);
                        ArrayList<BranchNode> path = reconstructPath(cameFromCopy, currentNode);
                        HashSet<BranchNode> exploredCopy = pruneExplored(path, loop);

                        System.out.println("Starting new Astar search because loop was encountered...");
                        return ctx.mkAnd(aStar(neighbor, targetNode, exploredCopy));
                    } else {
                        continue;
                    }
                }
                
                //** Calculate cost of taking extra step to neighbors
                // First get the part up until current node
                BoolExpr pathToNeighbor = reconstructBoolExpr(createCameFromCopy(cameFrom), currentNode);
                if (currentNode.getTrueChild() != null && currentNode.getTrueChild().equals(neighbor)) {
                    pathToNeighbor = ctx.mkAnd(pathToNeighbor, currentNode.getConditionExpressedAsInputVariables());
                } else if (currentNode.getFalseChild() != null && currentNode.getFalseChild().equals(neighbor)) {
                    pathToNeighbor = ctx.mkAnd(pathToNeighbor, ctx.mkNot(currentNode.getConditionExpressedAsInputVariables()));
                } else {
                    throw new IllegalArgumentException("Neighbor is not a child of current node");
                }
                
                // System.out.println("Constraints (pathToNeighbor): " + pathToNeighbor.toString());
                // System.out.println("Children: " + currentNode.getChildren().toString());
                // System.out.println("And here: " + currentNode.getConditionExpressedAsInputVariables());

                // System.out.println("Constraints (pathToNeighbor): " + pathToNeighbor.toString());
                args = solverCheck(pathToNeighbor, elements, intExprs);
                // System.out.println("Solution to constraints: " + Arrays.toString(args));

                Interpreter.interpretStartToTarget(startNode.getAm(), args, neighbor, newBranches);
                int tentativeGScore = gScore.get(currentNode) + neighbor.getH();
                if (tentativeGScore < gScore.get(neighbor)) {
                    // Add last condition to node

                    cameFrom.get(neighbor).add(currentNode);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + neighbor.getH());
                    if (!frontier.contains(neighbor)) {
                        frontier.add(neighbor);
                    }
                }
            }
            iterations++;
        }

        // Assume goal was not found  
        System.out.println("No path to goal was found");   
        throw new IllegalArgumentException("No path to goal was found");     
    }

    public BoolExpr BFS(BranchNode startNode, BranchNode targetNode, HashSet<BranchNode> explored) {

        System.out.println("\n\nCommencing BFS search!!");

        HashMap<AbsoluteMethod, ArrayList<BranchNode>> newBranches = createDeepCopyOfBranches();

        // Update references to startnode and targetnode to copy versions:
        startNode = findBranchNode(startNode.getAm(), startNode, newBranches);
        targetNode = findBranchNode(targetNode.getAm(), targetNode, newBranches);
        
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
        ArrayList<IntExpr> intExprs = new ArrayList<IntExpr>();
        for (String type : argTypes) {
            switch (type) {
                case "int":
                    IntExpr intExpr = ctx.mkIntConst(alphabet[count]);
                    intExprs.add(intExpr);
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

        // ** INITIALIZE Astar **
        PriorityQueue<BranchNode> frontier = new PriorityQueue<BranchNode>();
        HashMap<BranchNode, ArrayList<BranchNode>> cameFrom = new HashMap<BranchNode, ArrayList<BranchNode>>();
        for (BranchNode branch : newBranches.get(am)) {
            cameFrom.put(branch, new ArrayList<BranchNode>());
        }
        HashMap<BranchNode, ArrayList<BranchNode>> loopsEncountered = new HashMap<BranchNode, ArrayList<BranchNode>>();
        for (BranchNode branch : newBranches.get(am)) {
            loopsEncountered.put(branch, new ArrayList<BranchNode>());
        }

        HashMap<BranchNode, Integer> gScore = new HashMap<BranchNode, Integer>();
        for (BranchNode branch : newBranches.get(am)) {
            gScore.put(branch, Integer.MAX_VALUE);
        }
        gScore.put(startNode, 0);

        HashMap<BranchNode, Integer> fScore = new HashMap<BranchNode, Integer>();
        for (BranchNode branch : newBranches.get(am)) {
            fScore.put(branch, Integer.MAX_VALUE);
        }
        fScore.put(startNode, startNode.getH());

        frontier.add(startNode);
        if (frontier.isEmpty()){
            throw new IllegalArgumentException("Frontier is empty at start");
        }

        int iterations = 0;
        Interpreter.interpretStartToTarget(startNode.getAm(), args, startNode, newBranches);

        // ** RUN Astar ** 
        while (!frontier.isEmpty()) {
            BranchNode currentNode = frontier.poll();
            System.out.println("\n\nPerforming iteration: " + iterations + " with branch: " + currentNode.toString());

            // Check if we have found the target node
            if(currentNode.equals(targetNode)){
                System.out.println("Found path to goal!");
                HashMap<BranchNode, ArrayList<BranchNode>> cameFromCopy = createCameFromCopy(cameFrom);
                return reconstructBoolExpr(cameFromCopy, currentNode);
            }

            // Mark down that this was a loop, if it was a loop
            if(currentNode.getType().equals("loop")){
                System.out.println("Found loop: " + currentNode.toString());
                if(!loopsEncountered.containsKey(currentNode)){
                    loopsEncountered.put(currentNode, new ArrayList<BranchNode>());
                }
                loopsEncountered.get(currentNode).add(currentNode);
            }

            explored.add(currentNode);
            System.out.println("Explored: " + explored.toString());
            
            // Explore the neighbors
            for (BranchNode neighbor: currentNode.getChildren()) {

                System.out.println("Checking out neighbor: " + neighbor);
                if (explored.contains(neighbor)) {
                    System.out.println("Neighbor: " + neighbor.toString() + " has already been explored");

                    // If the currentNode is a loop that has been encountered before 
                    // do a new search with a new explore list
                    ArrayList<BranchNode> loop = loopsEncountered.get(currentNode);
                    if (loop != null && loop.get(loop.size()-1).equals(neighbor)) {

                        // Create copy updated version of explore list and cameFrom
                        HashMap<BranchNode, ArrayList<BranchNode>> cameFromCopy = createCameFromCopy(cameFrom);
                        ArrayList<BranchNode> path = reconstructPath(cameFromCopy, currentNode);
                        HashSet<BranchNode> exploredCopy = pruneExplored(path, loop);

                        System.out.println("Starting new Astar search because loop was encountered...");
                        return ctx.mkAnd(aStar(neighbor, targetNode, exploredCopy));
                    } else {
                        continue;
                    }
                }
                
                //** Calculate cost of taking extra step to neighbors
                // First get the part up until current node
                BoolExpr pathToNeighbor = reconstructBoolExpr(createCameFromCopy(cameFrom), currentNode);
                if (currentNode.getTrueChild() != null && currentNode.getTrueChild().equals(neighbor)) {
                    pathToNeighbor = ctx.mkAnd(pathToNeighbor, currentNode.getConditionExpressedAsInputVariables());
                } else if (currentNode.getFalseChild() != null && currentNode.getFalseChild().equals(neighbor)) {
                    pathToNeighbor = ctx.mkAnd(pathToNeighbor, ctx.mkNot(currentNode.getConditionExpressedAsInputVariables()));
                } else {
                    throw new IllegalArgumentException("Neighbor is not a child of current node");
                }
                
                // System.out.println("Constraints (pathToNeighbor): " + pathToNeighbor.toString());
                // System.out.println("Children: " + currentNode.getChildren().toString());
                // System.out.println("And here: " + currentNode.getConditionExpressedAsInputVariables());

                // System.out.println("Constraints (pathToNeighbor): " + pathToNeighbor.toString());
                args = solverCheck(pathToNeighbor, elements, intExprs);
                // System.out.println("Solution to constraints: " + Arrays.toString(args));

                Interpreter.interpretStartToTarget(startNode.getAm(), args, neighbor, newBranches);
                int tentativeGScore = gScore.get(currentNode) + neighbor.getH();
                if (tentativeGScore < gScore.get(neighbor)) {
                    // Add last condition to node

                    cameFrom.get(neighbor).add(currentNode);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, tentativeGScore + neighbor.getH());
                    if (!frontier.contains(neighbor)) {
                        frontier.add(neighbor);
                    }
                }
            }
            iterations++;
        }

        // Assume goal was not found  
        System.out.println("No path to goal was found");   
        throw new IllegalArgumentException("No path to goal was found");     
    }

    public Element[] solverCheck(BoolExpr expr, ArrayList<Element> elements, ArrayList<IntExpr> intExprs){
        Context ctx = Interpreter.getCtx();
        Solver solver = ctx.mkSolver();
        solver.add(expr);

        // ArrayList<Element> elements = new ArrayList<Element>();
        if(solver.check() == Status.SATISFIABLE){
            Model model = solver.getModel();
            //Update the values of the elements if they changed
            for (int i = 0; i < elements.size(); i++) {
                Element e = elements.get(i);
                switch (e.getType()) {
                    case "int":
                        IntExpr intExpr = intExprs.get(i);
                        if (model.getConstInterp(intExpr) != null) {
                            e.setValue(Integer.parseInt(model.getConstInterp(intExpr).toString()));
                            e.setSymbolicValue(intExpr);
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

    public HashMap<BranchNode, ArrayList<BranchNode>> createCameFromCopy(HashMap<BranchNode, ArrayList<BranchNode>> cameFrom) {
        HashMap<BranchNode, ArrayList<BranchNode>> cameFromCopy = new HashMap<BranchNode, ArrayList<BranchNode>>();
        for (BranchNode branchNode : cameFrom.keySet()) {
            ArrayList<BranchNode> cameFromList = cameFrom.get(branchNode);
            ArrayList<BranchNode> cameFromListCopy = new ArrayList<BranchNode>();
            for (BranchNode branchNode2 : cameFromList) {
                cameFromListCopy.add(branchNode2);
            }
            cameFromCopy.put(branchNode, cameFromListCopy);
        }
        return cameFromCopy;
    }
    
    public static ArrayList<BranchNode> reconstructPath(HashMap<BranchNode, ArrayList<BranchNode>> cameFrom, BranchNode currentNode) {
        Context ctx = Interpreter.getCtx();
        ArrayList<BranchNode> totalPath = new ArrayList<BranchNode>(Arrays.asList(currentNode));
        
        while (cameFrom.get(currentNode) != null && cameFrom.get(currentNode).size() > 0) {
            ArrayList<BranchNode> cameFromList = cameFrom.get(currentNode);
            BranchNode cameFromNode = cameFromList.remove(cameFromList.size()-1);
            // Possibly also remove from keyset?
            currentNode = cameFromNode;
            totalPath.add(0, cameFromNode);
        }
        return totalPath;
    }


    // Reconstruct the path from the cameFrom map
    public static BoolExpr reconstructBoolExpr(HashMap<BranchNode, ArrayList<BranchNode>> cameFrom, BranchNode currentNode) {
        System.out.println("Reconstructing bool expression...\n" );
        Context ctx = Interpreter.getCtx();
        // BoolExpr totalPath = currentNode.getLastCondition();
        BoolExpr totalPath = ctx.mkTrue();
        ArrayList<BoolExpr> conditionList = currentNode.getConditions();

        while (conditionList != null && conditionList.size() > 0) {
            System.out.println("Current node: " + currentNode.toString());
            System.out.println("Current condition: " + conditionList);
            System.out.println(" came from list " + cameFrom.get(currentNode));
            System.out.println("conditions list : " + currentNode.getConditions().toString());
            totalPath = ctx.mkAnd(totalPath, currentNode.popLastCondition());
            currentNode = cameFrom.get(currentNode).remove(cameFrom.get(currentNode).size()-1);
            conditionList = currentNode.getConditions();
        }
        System.out.println("Returning bool expression: " + totalPath.toString());
        return totalPath;
    }

    public static HashSet<BranchNode> pruneExplored(ArrayList<BranchNode> exploredPath, ArrayList<BranchNode> loop) {
        HashSet<BranchNode> explored = new HashSet<BranchNode>();
        ArrayList<BranchNode> pathCopy = new ArrayList<BranchNode>(exploredPath);
        BranchNode node = loop.get(loop.size()-1);
        boolean found = false;
        for (int i = exploredPath.size()-1; !found ; i--) {
            if (exploredPath.get(i).equals(node)) {
                found = true;
            }
            pathCopy.remove(i);
        }
        for (BranchNode branchNode : pathCopy) {
            explored.add(branchNode);
        }
        return explored;
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


    
    // Find the particular branchNode    
    public static BranchNode findBranchNode(AbsoluteMethod am, BranchNode targetNode, 
        HashMap<AbsoluteMethod, ArrayList<BranchNode>> branches) {
        for (BranchNode branch : branches.get(am)) {
            if (branch.equals(targetNode)) {
                System.out.println("Found target node: " + targetNode);
                return branch;
            }
        }
        return null;
    }

    public static BranchNode findBranchNodeByAMAndIndex(AbsoluteMethod am, int instructionIndex, 
        HashMap<AbsoluteMethod, ArrayList<BranchNode>> branches) {
        for (BranchNode branch : branches.get(am)) {
            if (branch.getInstructionIndex() == instructionIndex) {
                return branch;
            }
        }
        return null;
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

    public static HashMap<AbsoluteMethod, ArrayList<BranchNode>> getOriginalBranches() {
        return originalBranches;
    }

    public static void setOriginalBranches(HashMap<AbsoluteMethod, ArrayList<BranchNode>> branches) {
        Pathcreator.originalBranches = branches;
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