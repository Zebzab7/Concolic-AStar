package dtu.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
        ArrayList<File> files;
        files = Folders.findFiles("course-02242-examples-main");
        for(File file : files){
            System.out.println(file.getPath());
        }
    }
}
