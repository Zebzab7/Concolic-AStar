package concolicastar;

import java.util.ArrayList;

public class ProgramStack {
    Stack lv;
    Stack op;
    AbsoluteMethod am;
    int pc;
    
    public ProgramStack(Stack lv, Stack op, AbsoluteMethod am, int pc) {
        this.lv = lv;
        this.op = op;
        this.am = am;
        this.pc = pc;
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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("lv: " + lv.toString() + "\n");
        sb.append("op: " + op.toString() + "\n");
        sb.append("am: " + am.toString() + "\n");
        sb.append("pc: " + pc + "\n");
        return sb.toString();
    }
}

class Stack {
    private ArrayList<Object> stack = new ArrayList<>();

    // Push an item onto the stack
    public void push(Object item) {
        stack.add(item);
    }

    // Pop an item from the top of the stack
    public Object pop() {
        if (isEmpty()) {
            throw new IllegalArgumentException("Stack is empty");
        }
        return stack.remove(stack.size() - 1);
    }

    // Peek at the top item without removing it
    public Object peek() {
        if (isEmpty()) {
            throw new IllegalArgumentException("Stack is empty");
        }
        return stack.get(stack.size() - 1);
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    // Return the size of the stack
    public int size() {
        return stack.size();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (Object object : stack) {
            sb.append(object.toString() + ", ");
        }
        sb.append("]");
        return sb.toString();
    }
}