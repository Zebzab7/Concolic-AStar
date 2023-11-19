package concolicastar;

import java.util.ArrayList;

public class Branch {

    // While? If? For?
    String type;
    AbsoluteMethod am;
    int instructionIndex;

    Branch parent;
    ArrayList<Branch> children;
    int cost;

    public Branch(String type, AbsoluteMethod am, int instructionIndex, int cost) {
        this.type = type;
        this.am = am;
        this.instructionIndex = instructionIndex;
        parent = null;
        children = null;
        cost = 0;
    }

    public Branch() {}

    public void addChild(Branch child) {
        if (children == null) {
            children = new ArrayList<Branch>();
        }
        children.add(child);
    }

    public AbsoluteMethod getAm() {
        return am;
    }
    public ArrayList<Branch> getChildren() {
        return children;
    }
    public int getInstructionIndex() {
        return instructionIndex;
    }
    public Branch getParent() {
        return parent;
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
    public void setChildren(ArrayList<Branch> children) {
        this.children = children;
    }
    public void setInstructionIndex(int instructionIndex) {
        this.instructionIndex = instructionIndex;
    }
    public void setParent(Branch parent) {
        this.parent = parent;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Branch [am=" + am + ", children=" + children + ", cost=" + cost + ", instructionIndex="
                + instructionIndex + ", parent=" + parent + ", type=" + type + "]";
    }

}

