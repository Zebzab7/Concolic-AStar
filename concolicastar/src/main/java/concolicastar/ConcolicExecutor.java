package concolicastar;

import java.util.ArrayList;
import java.time.Instant;

import com.microsoft.z3.BoolExpr;

public class ConcolicExecutor {
    
    public static void main(String[] args) {
        String pathToFiles = "course-02242-examples-main/decompiled/dtu/compute/exec/";
        Instant start = Instant.now();
        run(pathToFiles);
         Instant end = Instant.now();
        long executionTime = end.toEpochMilli() - start.toEpochMilli();
        System.out.println("Execution time is: " + executionTime + "milliseconds");
    }

    public static void run(String pathToFiles) {
        ArrayList<JsonFile> files;
        files = Folders.findFiles(pathToFiles,".json");
        Interpreter interpreter = new Interpreter(files);

        Tests.testList(interpreter);
    }
}
