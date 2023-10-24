package dtu.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Hello world!
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );
        //findFiles(path);
        ArrayList<File> files;
        files = Folders.findFiles("course-02242-examples-main","Simple.json");
        System.out.println(files.size());
        for(File file : files){
//            System.out.println(file.getPath());
            ParseJson pj = new ParseJson();
            String jsonStr = pj.getStr(file);
//            System.out.println(jsonStr);
            pj.jsonParse(jsonStr);
        }
    }
}
