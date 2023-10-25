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
        //findFiles(path);
        ArrayList<JsonFile> files;
        files = Folders.findFiles("course-02242-examples-main",".json");
        for(JsonFile file : files) {
            System.out.println(file.getMethods());
            break;
        }

    }
}
