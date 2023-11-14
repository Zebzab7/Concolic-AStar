package concolicastar;

import java.util.ArrayList;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ArrayList<JsonFile> files;
        files = Folders.findFiles("course-02242-examples-main/decompiled/dtu/compute/exec/",".json");

        Interpreter interpreter = new Interpreter(files);
        // interpreter.interpret("method");
        Tests.testList(interpreter);
    }
}
