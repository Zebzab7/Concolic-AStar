package simple;

/**
 * This class tries to only contain very simple programs.
 */
public class Simple {
    public static void noop() {
        return;
    }

    public static int zero() {
        return 0;
    }

    public static int hundredAndTwo() {
        return 102;
    }

    public static int identity(int a) {
        return a;
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public static int min(int a, int b) {
        int x = 10;
        int c = 20;
        if (a + b <= c) return a;
        else return b;
    }

    public static int factorial(int n) {
        int m = 1;
        while (n > 0) {
            m *= n--;
        }
        return m;
    }

    public static void main(String [] args) {
        System.out.println("noop");
        noop();
        System.out.println("zero: " + zero());
        System.out.println("hundredAndTwo: " + hundredAndTwo());
        System.out.println("indentity/0: " + identity(0));
        System.out.println("indentity/1: " + identity(1));
        System.out.println("add/1,2: " + add(1,2));
        System.out.println("add/1,-2:" + add(1,-2));
        System.out.println("min/1,-2:" + min(1,-2));
        System.out.println("min/1,2:" +  min(1,2));
        System.out.println("factorial/0:" +  factorial(0));
        System.out.println("factorial/1:" + factorial(1));
        System.out.println("factorial/5:" + factorial(5));
    }
}
