package concolicastar;

import java.util.ArrayList;
import java.time.Instant;

public class App 
{
    public static void main( String[] args )
    {
        Instant start = Instant.now();
        ArrayList<JsonFile> files;
        files = Folders.findFiles("projects/course-examples/json",".json");

        Interpreter interpreter = new Interpreter(files);

        Pathcreator pc = new Pathcreator();
        pc.buildHeuristicMap(new BranchNode("if", new AbsoluteMethod("Simple", "ifInLoop"), 6, 0));
        
        // interpreter.interpret("method");
        //Tests.testList(interpreter);
        Instant end = Instant.now();
        long executionTime = end.toEpochMilli() - start.toEpochMilli();
        System.out.println("Execution time is: " + executionTime + "milliseconds");
    }
}
