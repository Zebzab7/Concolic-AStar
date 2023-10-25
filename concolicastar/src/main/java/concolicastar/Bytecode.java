package concolicastar;

import org.json.simple.JSONArray;

public class Bytecode {
    
    AbsoluteMethod am;
    JSONArray bytecode;

    public Bytecode(String className, String methodName, JSONArray bytecode) {
        this.am = new AbsoluteMethod(className, methodName);
        this.bytecode = bytecode;
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
