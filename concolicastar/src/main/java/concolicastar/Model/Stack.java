package concolicastar.Model;

import java.util.ArrayList;

public class Stack {    
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
    public Number popNum(){
        if(isEmpty()){
            throw new IllegalArgumentException("Stack is empty");
        }
        if(stack.get(stack.size()-1) instanceof Number){
            return (Number) stack.remove(stack.size()-1);
        }
        throw new IllegalArgumentException("Top of stack is not a number");
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