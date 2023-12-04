package concolicastar;

import java.util.ArrayList;
import java.util.HashSet;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

import java.time.Instant;

public class App 
{
    public static void main( String[] args )
    {
        
        // BranchNode targetNode 
        //     = new BranchNode(new AbsoluteMethod("Simple", "ifInLoopSimple3"), 38);
        
        compareSearchAlgorithms();
        
    }
    
    public static void compareSearchAlgorithms() {
        ArrayList<JsonFile> files;
        files = Folders.findFiles("projects/course-examples/json",".json");
        Interpreter interpreter = new Interpreter(files);
        Interpreter.setContext(new Context());
        Pathcreator pc = new Pathcreator();

        // Instant start = Instant.now();
        // Instant end = Instant.now();
        // long executionTime = end.toEpochMilli() - start.toEpochMilli();
        // System.out.println("Execution time is: " + executionTime + "milliseconds");
    
        // ** TEST SETUP **
        BranchNode targetNode = new BranchNode(new AbsoluteMethod("Simple", 
            "ifInLoopSimple3"), 63);
            
        // System.out.println("Final expression after performing Astar: " + expr);
        

        /*
         * Concolic execution
         */
        ConcolicExecution.testFunction(interpreter, new AbsoluteMethod("Simple", "ifInLoopSimple3"), targetNode);
        int actualCostConcolic = ConcolicExecution.getActualCost();
        int totalNodesExploredConcolic = ConcolicExecution.getTotalNodesExplored();
        
        /*
         * Astar execution
         */
        Interpreter.setContext(new Context());
        BranchNode startNode = pc.buildHeuristicMap(targetNode);
        BoolExpr expr = pc.aStar(startNode, targetNode, new HashSet<BranchNode>());

        int actualCostAstar = Pathcreator.getActualCost();
        int totalNodesExploredAstar = Pathcreator.getTotalNodesExplored();

        System.out.println("Actual cost of concolic execution: " + actualCostConcolic);
        System.out.println("Nodes explored concolic: " + totalNodesExploredConcolic);

        System.out.println("Actual cost of Astar: " + actualCostAstar);
        System.out.println("Nodes explored Astar: " + totalNodesExploredAstar);

    }
}
