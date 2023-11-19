package concolicastar;

import java.util.ArrayList;
import java.time.Instant;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Instant start = Instant.now();
        ArrayList<JsonFile> files;
        files = Folders.findFiles("projects/course-examples/json",".json");

        Interpreter interpreter = new Interpreter(files);

        Pathcreator pc = new Pathcreator();
        pc.buildHeuristicMap(new AbsoluteMethod("Simple", "ifInLoop"), 3);
        
        // interpreter.interpret("method");
        //Tests.testList(interpreter);
        Instant end = Instant.now();
        long executionTime = end.toEpochMilli() - start.toEpochMilli();
        System.out.println("Execution time is: " + executionTime + "milliseconds");
    }
}
