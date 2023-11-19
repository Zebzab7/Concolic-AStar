package concolicastar;

import java.util.ArrayList;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ArrayList<JsonFile> files;
        files = Folders.findFiles("projects/course-examples/json",".json");

        Interpreter interpreter = new Interpreter(files);
        Pathcreator pc = new Pathcreator(files, new AbsoluteMethod("Simple","main"));
        
        // interpreter.interpret("method");
        //Tests.testList(interpreter);
    }
}
