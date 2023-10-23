package concolicastar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class app 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        //findFiles(path);
        ArrayList<File> files;
        files = Folders.findFiles("course-02242-examples-main",".json");
        System.out.println(files.size());
        for(File file : files){
            System.out.println(file.getPath());
        }
        
    }
}
