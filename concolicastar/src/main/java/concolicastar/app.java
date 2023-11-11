package concolicastar;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.*;
import org.json.simple.parser.*;
import com.microsoft.z3.*;
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
        files = Folders.findFiles("course-02242-examples-main/decompiled/dtu/compute/exec/",".json");
        Interpreter interpreter = new Interpreter(files);
        // interpreter.interpret("method");
        Tests.testList(interpreter);
    }
}
