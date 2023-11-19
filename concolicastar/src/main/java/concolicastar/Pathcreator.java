package concolicastar;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Pathcreator {

    ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
    AbsoluteMethod startam;
    ArrayList<AbsoluteMethod> methodStack = new ArrayList<AbsoluteMethod>();
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
    }
    public ArrayList<AbsoluteMethod> findMethodsContainingMethods(AbsoluteMethod am){
        ArrayList<AbsoluteMethod> methods = new ArrayList<AbsoluteMethod>();
        for(Bytecode bc : bytecodes){
            for(Object obj : bc.getBytecode()){
                JSONObject bytecode = (JSONObject) obj;
                if(bytecode.get("opr").equals("invoke")){
                    if(bytecode.get("name").equals(am.getMethodName())){
                        methods.add(bc.getAm());
                        //return methods;   //Idea is found one, good enough.
                    }
                }
            }
        }
        return methods;
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