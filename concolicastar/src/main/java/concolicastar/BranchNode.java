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
    private int cost;
    
    BoolExpr condition;

     public BranchNode(String type, AbsoluteMethod am, int instructionIndex, int cost) {
        this.type = type;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parents = null;
        trueChild = null;
        falseChild = null;
        this.cost = 0;
    }

    public BranchNode(AbsoluteMethod am, int instructionIndex) {
        this.type = null;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parents = null;
        trueChild = null;
        falseChild = null;
        this.cost = 0;
    }

    public BranchNode(String type, AbsoluteMethod am, int instructionIndex) {
        this.type = type;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parents = null;
        trueChild = null;
        falseChild = null;
        cost = 0;
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
    public int getCost() {
        return cost;
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
        this.parents.add(parent);
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void setCondition(BoolExpr condition) {
        this.condition = condition;
    }
    public BoolExpr getCondition() {
        return condition;
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
        return "Branch: \n" +
                "Type: " + type + "\n" +
                "Method: " + am.toString() + "\n" +
                "Instruction Index: " + instructionIndex + "\n" +
                "Cost: " + cost + "\n";
    }

    @Override
    public int compareTo(BranchNode o) {
        return Integer.compare(this.cost , o.cost);
    }
}

