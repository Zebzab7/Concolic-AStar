package concolicastar;

import java.util.ArrayList;

public class BranchNode implements Comparable<BranchNode> {

    // While? If? For?
    String type;

    AbsoluteMethod am;
    int instructionIndex;

    ArrayList<BranchNode> parents;
    ArrayList<BranchNode> children;
    int cost;

    public BranchNode(String type, AbsoluteMethod am, int instructionIndex, int cost) {
        this.type = type;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parents = null;
        children = null;
        this.cost = cost;
    }

    public BranchNode(String type, AbsoluteMethod am, int instructionIndex) {
        this.type = type;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parents = null;
        children = null;
        cost = 0;
    }

    public void addChild(BranchNode child) {
        if (children == null) {
            children = new ArrayList<BranchNode>();
        }
        children.add(child);
    }

    public AbsoluteMethod getAm() {
        return am;
    }
    public ArrayList<BranchNode> getChildren() {
        return children;
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
    public void setChildren(ArrayList<BranchNode> children) {
        this.children = children;
    }
    public void setInstructionIndex(int instructionIndex) {
        this.instructionIndex = instructionIndex;
    }
    public void addParent(BranchNode parent) {
        this.parents.add(parent);
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCost(int cost) {
        this.cost = cost;
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

