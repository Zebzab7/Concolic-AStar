package concolicastar;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ArrayList<JsonFile> files;
        files = Folders.findFiles("course-02242-examples-main/decompiled/dtu/compute/exec/","Simple.json");
        
        Interpreter interpreter = new Interpreter(files);
        // interpreter.interpret("method");
        interpreter.interpret(new AbsoluteMethod("Simple", "zero"), new Object[] {1, 2});
    }
}
