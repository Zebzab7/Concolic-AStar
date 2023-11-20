package concolicastar;

import java.util.ArrayList;

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
        interpreter.setContext(new Context());

        Pathcreator pc = new Pathcreator();
        BranchNode targetNode 
            = new BranchNode("if", new AbsoluteMethod("Simple", "ifInLoop2"), 31, 0);
        BranchNode startNode = pc.buildHeuristicMap(targetNode);
        pc.aStar(startNode, targetNode);

        // interpreter.interpret("method");
        //Tests.testList(interpreter);
        Instant end = Instant.now();
        long executionTime = end.toEpochMilli() - start.toEpochMilli();
        System.out.println("Execution time is: " + executionTime + "milliseconds");
    }
}
