package concolicastar;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Interpreter {

    ArrayList<Bytecode> bytecodes = new ArrayList<Bytecode>();
    
    public Interpreter(ArrayList<JsonFile> files) {
        for(JsonFile file : files) {
            JSONArray methods = file.getMethods();
            for (Object obj : methods) {
                JSONObject method = (JSONObject) obj;
                String methodName = (String) method.get("name");
                JSONObject code = (JSONObject) method.get("code");
                Bytecode bc = new Bytecode(file.getFileName(), methodName , (JSONArray) code.get("bytecode"));
                bytecodes.add(bc);
            }
        }
    }

    public void interpret(AbsoluteMethod am) {
        Bytecode bc = findMethod(am);
        System.out.println(bc);
    }

    public Bytecode findMethod(AbsoluteMethod am) {
        for (Bytecode bytecode : bytecodes) {
            if (bytecode.getAm().equals(am)) {
                return bytecode;
            }
        }
        throw new IllegalArgumentException("Method not found: " + am.getMethodName());
    }

    public void bytecodesToString() {
        for (Bytecode bytecode : bytecodes) {
            System.out.println(bytecode.toString());
            System.out.println();
        }
    }

    public void loadFiles() {
        
    }
}
