package concolicastar;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Bytecode {
    
    private AbsoluteMethod am;
    private ArrayList<String> argsType;
    private JSONArray bytecode;

    public Bytecode(String className, String methodName, JSONArray bytecode, JSONArray params) {
        this.am = new AbsoluteMethod(className, methodName);
        this.bytecode = bytecode;
        argsType = new ArrayList<String>();
        for (int i = 0; i < params.size(); i++) {
            JSONObject param = (JSONObject) params.get(i);
            JSONObject typeObject = (JSONObject) param.get("type");
            if ((String) typeObject.get("base") != null) {
                String type = (String) typeObject.get("base");
                argsType.add(type);
            }
        }
    }

    public ArrayList<String> getArgsTypes() {
        return argsType;
    }
    public void setArgsType(ArrayList<String> argsType) {
        this.argsType = argsType;
    }
    public JSONArray getBytecode() {
        return bytecode;
    }
    public void setBytecode(JSONArray bytecode) {
        this.bytecode = bytecode;
    }

    public AbsoluteMethod getAm() {
        return am;
    }
    public void setAm(AbsoluteMethod am) {
        this.am = am;
    }

    public String toString() {
        return am.getClassName() + "." + am.getMethodName() + ":\n" + bytecode.toString();
    }
}

class BootstrapMethods{
    AbsoluteMethod am;
    JSONArray bootstrapMethods;
    public BootstrapMethods(String className, String methodName, JSONArray bootstrapMethods) {
        this.am = new AbsoluteMethod(className, methodName);
        this.bootstrapMethods = bootstrapMethods;
    }
    public JSONArray getBootstrapMethods() {
        return bootstrapMethods;
    }
    public void setBootstrapMethods(JSONArray bootstrapMethods) {
        this.bootstrapMethods = bootstrapMethods;
    }
    public AbsoluteMethod getAm() {
        return am;
    }
    public void setAm(AbsoluteMethod am) {
        this.am = am;
    }
    public String toString() {
        return am.getClassName() + "." + am.getMethodName() + ":\n" + bootstrapMethods.toString();
    }
}