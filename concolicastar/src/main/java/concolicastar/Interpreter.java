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

    public ProgramStack interpret(AbsoluteMethod am, Element[] args) {
        if (args == null) {
            args = new Element[0];
        }
        Bytecode bc = findMethod(am);
        ProgramStack stack = new ProgramStack(new Stack(), new Stack(), am, 0);
        for (Element el : args) {
            stack.getLv().push(el);
        }

        while (stack.getPc() < bc.getBytecode().size()) {
            JSONObject bytecode = (JSONObject) bc.getBytecode().get(stack.getPc());
            String oprString = (String) bytecode.get("opr");
            
            Operations op = new Operations(bytecode);
            stack = Operations.doOperation(stack, oprString);
            if (oprString.equals("return")) {
                return stack;
            }
            stack.setPc(stack.getPc() + 1);
            // System.out.println(stack.toString());
        }
        return stack;
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
