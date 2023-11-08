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