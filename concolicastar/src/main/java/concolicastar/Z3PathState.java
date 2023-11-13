package concolicastar;

import java.util.HashMap;
import java.util.Map;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;

class Z3PathState {
    private Context ctx;
    private Map<String, Expr> variables;
    private Solver solver;

    public Z3PathState() {
        this.ctx = new Context();
        this.variables = new HashMap<>();
        this.solver = ctx.mkSolver();
    }

    public void declareVariable(String name, Sort sort) {
        Expr variable = ctx.mkConst(name, sort);
        variables.put(name, variable);
    }

    public void addConstraint(BoolExpr constraint) {
        solver.add(constraint);
    }

    public Model solve() {
        if (solver.check() == Status.SATISFIABLE) {
            return solver.getModel();
        } else {
            return null; // No solution found
        }
    }

    public Expr getVariable(String name) {
        return variables.get(name);
    }

    public Context getContext() {
        return ctx;
    }

}
