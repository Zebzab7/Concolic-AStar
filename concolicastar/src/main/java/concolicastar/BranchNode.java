package concolicastar;

import java.util.ArrayList;

import com.microsoft.z3.BoolExpr;

public class BranchNode implements Comparable<BranchNode> {

    // While? If? For?
    private String type;

    private AbsoluteMethod am;
    private int instructionIndex;

    private ArrayList<BranchNode> parents;
    private BranchNode trueChild;
    private BranchNode falseChild;
    private int h;
    private int cost;

    private boolean evaluationToChild;
    private boolean evaluationToParent;
    
    ArrayList<BoolExpr> condition;

    public BranchNode(String type, AbsoluteMethod am, int instructionIndex, int cost) {
        this.type = type;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parents = null;
        trueChild = null;
        falseChild = null;
        this.cost = cost;
        this.h = cost;
    }

    public BranchNode(AbsoluteMethod am, int instructionIndex) {
        this.type = null;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parents = null;
        trueChild = null;
        falseChild = null;
        this.h = 0;
    }

    public BranchNode(String type, AbsoluteMethod am, int instructionIndex) {
        this.type = type;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parents = null;
        trueChild = null;
        falseChild = null;
        h = 0;
    }

    public ArrayList<BranchNode> getChildren(){
        ArrayList<BranchNode> children = new ArrayList<>();
        if (trueChild != null) {
            children.add(trueChild);
        }
        if (falseChild != null) {
            children.add(falseChild);
        }
        return children;
    }
    public void setTrueChild(BranchNode child) {
        trueChild = child;
    }
    public void setFalseChild(BranchNode child) {
        falseChild = child;
    }
    public BranchNode getTrueChild() {
        return trueChild;
    }
    public BranchNode getFalseChild() {
        return falseChild;
    }
    public void setEvaluationToParent(boolean evaluationToParent) {
        this.evaluationToParent = evaluationToParent;
    }
    public boolean getEvaluationToParent() {
        return evaluationToParent;
    }
    public void setEvaluationToChild(boolean evaluation) {
        this.evaluationToChild = evaluation;
    }
    public boolean getEvaluateToTrue() {
        return evaluationToChild;
    }
    public AbsoluteMethod getAm() {
        return am;
    }
    public int getInstructionIndex() {
        return instructionIndex;
    }
    public ArrayList<BranchNode> getParent() {
        return parents;
    }
    public String getType() {
        return type;
    }
    public int getH() {
        return h;
    }
    public void setAm(AbsoluteMethod am) {
        this.am = am;
    }
    public void setInstructionIndex(int instructionIndex) {
        this.instructionIndex = instructionIndex;
    }
    public void addParent(BranchNode parent) {
        if (parents == null) {
            parents = new ArrayList<BranchNode>();
        }
        if (!parents.contains(parent)) {
            this.parents.add(parent);
        }
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setH(int cost) {
        this.h = cost;
    }
    public void addCondition(BoolExpr condition) {
        if (this.condition == null) {
            this.condition = new ArrayList<BoolExpr>();
        }
        this.condition.add(condition);
    }
    public BoolExpr getLastCondition() {
        return condition.get(condition.size()-1);
    }
    public ArrayList<BoolExpr> getCondition() {
        return condition;
    }
    public int getCost() {
        return cost;
    }
    public void setCost(int actualCost) {
        this.cost = actualCost;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BranchNode) {
            BranchNode bn = (BranchNode) obj;
            if (bn.getAm().equals(this.am) && bn.getInstructionIndex() == this.instructionIndex) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return am.hashCode() + instructionIndex;
    }

    @Override
    public String toString() {
        return "\nBranch>: \n" +
                "Type: " + type + "\n" +
                "Method: " + am.toString() + "\n" +
                "Instruction Index: " + instructionIndex + "\n" +
                "Condition: " + condition + "\n" +
                "Cost: " + h + "\n";
    }

    @Override
    public int compareTo(BranchNode o) {
        return Integer.compare(this.h , o.h);
    }
}

