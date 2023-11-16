package concolicastar;

import java.util.ArrayList;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;

public class ProgramStack {
    Stack lv;
    Stack op;
    AbsoluteMethod am;
    int pc;
    BoolExpr boolExpr;

    public ProgramStack(Stack lv, Stack op, AbsoluteMethod am, int pc, BoolExpr boolExpr) {
        this.lv = lv;
        this.op = op;
        this.am = am;
        this.pc = pc;
        this.boolExpr = boolExpr;
    }

    public Stack getLv() {
        return lv;
    }
    public Stack getOp() {
        return op;
    }
    public AbsoluteMethod getAm() {
        return am;
    }
    public int getPc() {
        return pc;
    }
    public BoolExpr getBoolExpr() {
        return boolExpr;
    }
    public void setBoolExpr(BoolExpr expr) {
        this.boolExpr = expr;
    }
    public void setAm(AbsoluteMethod am) {
        this.am = am;
    }
    public void setLv(Stack lv) {
        this.lv = lv;
    }
    public void setOp(Stack op) {
        this.op = op;
    }
    public void setPc(int pc) {
        this.pc = pc;
    }

    public ProgramStack setLvAOp(ProgramStack stack){
        this.lv = stack.getLv();
        this.op = stack.getOp();
        return stack;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("lv: " + lv.toString() + "\n");
        sb.append("op: " + op.toString() + "\n");
        sb.append("am: " + am.toString() + "\n");
        sb.append("pc: " + pc + "\n");
        sb.append("expressions: " + boolExpr.toString() + "\n");
        return sb.toString();
    }
}

//Type defined and Object not used
class Element {
    String type;
    Object value;
    Expr<?> symbolicValue;
    public Element(String type, Object value, Expr<?> symbolicValue){
        this.type = type;
        this.value = value;
        this.symbolicValue = symbolicValue;
    }
    public String getType(){
        return type;
    }
    public Object getValue(){
        return value;
    }
    public Expr<?> getSymbolicValue(){
        return symbolicValue;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setValue(Object value){
        this.value = value;
    }
    public void setSymbolicValue(Expr<Sort> symbolicValue){
        this.symbolicValue = symbolicValue;
    }
    public String toString(){
        return "Type: "+type+" Value: "+value + " SymbolicValue: "+symbolicValue;
    }
}


class Stack {
    public Element element;
    private ArrayList<Element> stack = new ArrayList<>();

    // Push an item onto the stack
    public void push(Element item) {
        stack.add(item);
    }

    // Pop an item from the top of the stack
    public Element pop() {
        if (isEmpty()) {
            throw new IllegalArgumentException("Stack is empty");
        }
        return stack.remove(stack.size() - 1);
    }

    // Peek at the top item without removing it
    public Element peek() {
        if (isEmpty()) {
            throw new IllegalArgumentException("Stack is empty");
        }
        return stack.get(stack.size() - 1);
    }

    //get specific Element in stack
    public Element get(int index){
        return stack.get(index);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    // Return the size of the stack
    public int size() {
        return stack.size();
    }

    public void insert(Integer index, Element element) {
        stack.add(index, element);
    }

    public void replace(int index, Element el) {
        stack.set(index, el);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (Element element : stack) {
            sb.append(element.toString() + ", ");
        }
        sb.append("]");
        return sb.toString();
    }
}


