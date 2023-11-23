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
        Instant start = Instant.now();
        ArrayList<JsonFile> files;
        files = Folders.findFiles("projects/course-examples/json",".json");

        Interpreter interpreter = new Interpreter(files);
        Interpreter.setContext(new Context());

        Pathcreator pc = new Pathcreator();
        BranchNode targetNode 
            = new BranchNode(new AbsoluteMethod("Simple", "ifInLoopSimple3"), 15);
        BranchNode startNode = pc.buildHeuristicMap(targetNode);
        BranchNode testNode = Pathcreator.findBranchNodeByAMAndIndex(new AbsoluteMethod("Simple", "ifInLoopSimple3"), 6,
            Pathcreator.getOriginalBranches());
        // System.out.println("Testing child of " + testNode);
        // System.out.println("True child: " + testNode.getTrueChild());
        // System.out.println("False child: " + testNode.getFalseChild());
        BoolExpr expr = pc.aStar(startNode, targetNode, new HashSet<BranchNode>());
        System.out.println("Final expression after performing Astar: " + expr);

        // interpreter.interpret("method");
        // ConcolicExecution.testList(interpreter);
        Instant end = Instant.now();
        long executionTime = end.toEpochMilli() - start.toEpochMilli();
        System.out.println("Execution time is: " + executionTime + "milliseconds");
    }
}
