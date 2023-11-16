package concolicastar;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Pathcreator {

    ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
    AbsoluteMethod startam;

    HashMap<Integer,Integer> pcMap = new HashMap<Integer,Integer>();
    /*
     * 
     * [20 -> 15],
     * [14 -> 10],
     * 
     * 
     */
    
    public Pathcreator(ArrayList<JsonFile> files, AbsoluteMethod am) {
        this.startam =am;
        for(JsonFile file : files) {
            JSONArray methods = file.getMethods();
            for (Object obj : methods) {
                JSONObject method = (JSONObject) obj;
                String methodName = (String) method.get("name");
                JSONObject code = (JSONObject) method.get("code");
                Bytecode bc = new Bytecode(file.getFileName(), methodName , (JSONArray) code.get("bytecode"));
                BootstrapMethods bm = new BootstrapMethods(file.getFileName(), methodName, (JSONArray) method.get("bootstrapMethods"));
                bytecodes.add(bc);

            }
        }
    }
    
    public void findPath(int EndIndex,AbsoluteMethod am){
        Bytecode bc = Interpreter.findMethod(am);
        int pc = 0;
        JSONObject bytecode;
        int size = bc.getBytecode().size();
        String opr = "";

        //Wont handle multiple methods calling this method for now
        while (pc < size) {
            bytecode = (JSONObject) bc.getBytecode().get(pc);
            opr = (String)bytecode.get("opr");
            if(am.getMethodName().equals("main") && pc == size ){
                return;
            }
            if(pc == EndIndex && am.getMethodName().equals(startam.getMethodName())){
                System.out.println("Found Start");
                return;
            }
            
            if(opr.equals("if") || opr.equals("ifz")){
                //Split into 2 different worklists
            }

            pc++;
        }
        



    }
}
