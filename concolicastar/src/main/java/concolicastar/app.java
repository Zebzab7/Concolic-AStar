package concolicastar;

import java.util.ArrayList;

public class App 
{
    public static void main( String[] args )
    {
        ArrayList<JsonFile> files;
        files = Folders.findFiles("projects/course-examples/json",".json");

        Interpreter interpreter = new Interpreter(files);
        // interpreter.interpret("method");
        Tests.testList(interpreter);
    }
}
