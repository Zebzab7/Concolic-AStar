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
            fw.write("digraph G{ \n ");
            int counter = 0;
            BranchNode bfalse = startingBranch.getFalseChild();
            BranchNode btrue = startingBranch.getTrueChild();
            if(btrue!=null){
                fw.write("start ->" + counter + " [label=\"" + btrue.getH() +"\"];" + "\n");
                counter ++;
                getChildren(btrue,counter,fw);
            }
            if(bfalse !=null) {
                fw.write("start ->" + counter + " [label=\"" + bfalse.getH() +"\"];" + "\n");
                counter++;
                getChildren(bfalse,counter,fw);
            }
            fw.write("\n}");
            fw.close();
        }catch(IOException io){
            System.out.println("Error in writing to file");
            io.printStackTrace();
        }
        
    }
    public static void getChildren(BranchNode branch,int counter,FileWriter fw) throws IOException{
        int num = counter;

        BranchNode btrue = branch.getTrueChild();
        BranchNode bfalse = branch.getFalseChild();
        if (btrue != null) {
            counter++;
            fw.write("" + num + "->" + counter + " [label=\"" + btrue.getH() +"\"];" + "\n");
            getChildren(btrue,counter,fw);
        }
        if (bfalse != null) {
            counter ++;
            fw.write("" + num + "->" + counter + " [label=\""+ bfalse.getH() +"\"];" + "\n");
            getChildren(bfalse,counter,fw);
        }
        return;
    }

}