package concolicastar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MakeGraph{

    public MakeGraph(){

    }
    public static void generateGraph(BranchNode startingBranch ){
        String s = "graphs//Astar.dot";
        try{
            File myFile = new File(s);
            if(myFile.createNewFile()){
                System.out.println("Created file at path:");
            }else{
                System.out.println("File exists");
            }
            
        }catch(IOException io){
            System.out.println("Error IN IO");
            io.printStackTrace();
        }
        try{
            FileWriter fw = new FileWriter(s);
            fw.write("diagraph G \n ");
            int counter = 0;
            for (BranchNode branch : startingBranch.getChildren()) {
                fw.write("start ->"+ "node:" + counter + ";");
                
            }
            fw.close();
        }catch(IOException io){
            System.out.println("Error in writing to file");
            io.printStackTrace();
        }
        
    }
    public void getChildren(BranchNode branch,int counter,FileWriter fw) throws IOException{
        if(branch.getChildren()==null){
            return;
        }
        int num = counter;
        for (BranchNode b: branch.getChildren()) {
        counter++;
            fw.write("node:" + num + "-> node:" + counter + ";");
            getChildren(b,counter,fw);
        }
        return;
    }

}