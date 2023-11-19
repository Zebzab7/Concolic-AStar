package concolicastar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Pathcreator {

    ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
    AbsoluteMethod startam;
    HashMap<AbsoluteMethod,ArrayList<classdiag>> methodStack = new HashMap<AbsoluteMethod,ArrayList<classdiag>>();
    ArrayList<PathHolder> pathStack = new ArrayList<PathHolder>();

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
                JSONArray args = (JSONArray) method.get("params");
                Bytecode bc = new Bytecode(file.getFileName(), methodName , (JSONArray) code.get("bytecode"), args);
                bytecodes.add(bc);

            }
        }
        findMethodsContainingMethods();
        System.out.println(methodstacktoString());
    }
    public void findMethodsContainingMethods(){
        int invoke =0;
        for(Bytecode bc : bytecodes){
            for(Object obj : bc.getBytecode()){
                invoke++;
                JSONObject bytecode = (JSONObject) obj;
                //Asuming that all invokestatic are methods and none are other things.
                if(bytecode.get("opr").equals("invoke") && bytecode.get("access").equals("static")){
                    JSONObject method = (JSONObject) bytecode.get("method");
                    String className = (String) ((JSONObject) method.get("ref")).get("name");  
                    String methodName = (String) (method.get("name"));
                    AbsoluteMethod am = new AbsoluteMethod(className,methodName);

                    //Checks if it exists, if not create new Arraylist object
                    if(!methodStack.containsKey(am)){
                        methodStack.put(am, new ArrayList<>());
                    }
                    //adds to methodstack
                    methodStack.get(am).add(new classdiag(bc.getAm(),invoke));
                    
                }
            }
            invoke=0;
        }
    }

    //Quickcheck if already in map
    private boolean doesExist(ArrayList<PathHolder> map, AbsoluteMethod am){
        for(PathHolder ph : map){
            if(ph.getAM().equals(am)){
                return true;
            }
        }
        return false;
    }

    public HashMap<Integer,Integer> findIfPaths(int size, AbsoluteMethod am, Bytecode bc){
        HashMap<Integer,Integer> ifmap = new HashMap<Integer,Integer>();
        JSONObject bytecode = null;
        for(int i = 0; i < size; i++){
            bytecode = (JSONObject) bc.getBytecode().get(i);
            if(bytecode.get("opr").equals("if") || bytecode.get("opr").equals("ifz")){
                ifmap.put(i, (int)bytecode.get("target"));
            }
        }
        return ifmap;
    }

    public void findPath(int EndIndex,AbsoluteMethod am){
        Bytecode bc = Interpreter.findMethod(am);
        int pc = 0;
        JSONObject bytecode;
        int size = bc.getBytecode().size();
        String opr = "";

        HashMap<Integer,Integer> pcMap = findIfPaths(size,am,bc);
        pathStack.add(new PathHolder(am,pcMap));
        System.out.println(pcMap.toString());

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
    public String methodstacktoString(){
        String s = "";
        for(AbsoluteMethod am : methodStack.keySet()){
            s += am.toString() + "=[";
            for(classdiag cd : methodStack.get(am)){
                s+= cd.getAM().toString() + " from " + cd.getInstruction() + ", ";
            }
            s += "]\n";
        }

        return s;
    }


}

/**
 * InnerPathcreator
 */

class classdiag {
    AbsoluteMethod am;
    int instruction;

    public classdiag(AbsoluteMethod am, int instruction) {
        this.am = am;
        this.instruction = instruction;
    }
    public AbsoluteMethod getAM(){
        return am;
    }
    public int getInstruction(){
        return instruction;
    }
    public void setAM(AbsoluteMethod am){
        this.am = am;
    }
    public void setInstruction(int instruction){
        this.instruction = instruction;
    }

    public String methodStacktoString(){
        methodStack.toString();
        
        return "";

    }

    
}

class PathHolder{
    AbsoluteMethod am;
    HashMap<Integer,Integer> map;
    public PathHolder(AbsoluteMethod am, HashMap<Integer,Integer> map){
        this.am = am;
        this.map = map;
    }
    public AbsoluteMethod getAM(){
        return am;
    }
    public HashMap<Integer,Integer> getMap(){
        return map;
    }
    public void setAM(AbsoluteMethod am){
        this.am = am;
    }
    public void setMap(HashMap<Integer,Integer> map){
        this.map = map;
    }
}