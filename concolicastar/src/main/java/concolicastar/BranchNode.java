package concolicastar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

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

    ArrayList<BoolExpr> lastConditions;
    BoolExpr conditionExpressedAsInputVariables;

    private static Context ctx = Interpreter.getCtx();

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
    public void addLastCondition(BoolExpr condition) {
        if (this.lastConditions == null) {
            this.lastConditions = new ArrayList<BoolExpr>();
        }
        this.lastConditions.add(condition);
    }
    public BoolExpr getLastCondition() {
        return lastConditions.get(lastConditions.size()-1);
    }
    public ArrayList<BoolExpr> getConditions() {
        return lastConditions;
    }
    public int getCost() {
        return cost;
    }
    public void setCost(int actualCost) {
        this.cost = actualCost;
    }

    public void setConditionExpressedAsInputVariables(BoolExpr conditionExpressedAsInputVariables) {
        this.conditionExpressedAsInputVariables = conditionExpressedAsInputVariables;
    }

    public BoolExpr getConditionExpressedAsInputVariables() {
        return conditionExpressedAsInputVariables;
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

    public static ArrayList<BranchNode> deepCopyBranchNodes(ArrayList<BranchNode> originalNodes) {
        Map<BranchNode, BranchNode> copyMap = new HashMap<>();

        // First, create a shallow copy of each node and store it in the map
        for (BranchNode node : originalNodes) {
            BranchNode copy = new BranchNode(node.getType(), node.getAm(), node.getInstructionIndex(), node.getH());
            if (node.getConditionExpressedAsInputVariables() != null) {
                copy.setConditionExpressedAsInputVariables(node.getConditionExpressedAsInputVariables());
            }
            copyMap.put(node, copy);
        }

        // Now, update the references in the copies
        for (BranchNode original : originalNodes) {
            BranchNode copy = copyMap.get(original);

            if (original.getTrueChild() != null) {
                copy.setTrueChild(copyMap.get(original.getTrueChild()));
            }
            if (original.getFalseChild() != null) {
                copy.setFalseChild(copyMap.get(original.getFalseChild()));
            }
            if (original.getParent() != null) {
                for (BranchNode parent : original.getParent()) {
                    copy.addParent(copyMap.get(parent));
                }
            }

            // Copy last conditions
            if (original.getConditions() != null) {
                for (int i = original.getConditions().size() - 1; i >= 0; i--) {
                    copy.addLastCondition(original.getConditions().get(i));
                }
            }
        }

        // Return the new list of copied nodes
        return new ArrayList<>(copyMap.values());
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
                "LastCondition: " + lastConditions + "\n" +
                "Condition: " + conditionExpressedAsInputVariables + "\n" + 
                "Cost: " + h + "\n";
    }

    @Override
    public int compareTo(BranchNode o) {
        return Integer.compare(this.h , o.h);
    }
}

