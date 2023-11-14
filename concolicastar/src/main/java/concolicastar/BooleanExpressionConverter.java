package concolicastar;

import org.json.simple.JSONObject;

import com.microsoft.z3.BoolExpr;

public class BooleanExpressionConverter {

    public static BoolExpr createIfExpression(Bytecode bc, ProgramStack stack) {
        int pc = stack.getPc();
        JSONObject instruction = (JSONObject) bc.getBytecode().get(pc);
        
        // BoolExpr condition = unfoldCondition(pc-1) + unfoldCondition(pc-2);
        return null;
    }

    // public BoolExpr unfoldCondition() {
        
    // }
}
