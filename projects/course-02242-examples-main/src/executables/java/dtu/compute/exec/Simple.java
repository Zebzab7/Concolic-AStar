package dtu.compute.exec;

/**
 * This class tries to only contain very simple programs.
 */
public class Simple {

    @Case
    public static void noop() {
        return;
    }

    @Case
    public static int zero() {
        return 0;
    }

    @Case
    public static int hundredAndTwo() {
        return 102;
    }

    @Case
    public static int identity(int a) {
        return a;
    }

    @Case
    public static int add(int a, int b) {
        return a + b;
    }

    @Case
    public static int min(int a, int b) {
        if (a <= b) return a;
        else return b;
    }

    @Case
    public static int factorial(int n) {
        int m = 1;
        while (n > 0) {
            m *= n--;
        }
        return m;
    }

    @Case 
    public static int someFunction(int n) {
        int x = 0;
        if (n > 10) {
            x *= 2;
        } else {
            x *= 3;
        }
        x *= 4;
        return x;
    }

    @Case 
    public static int ifInLoop(int n) {
        int x = 1;
        while (n > 0) {
            if (n > 10) {
                x *= 2;
                if(x > 0) {
                    x *= 5;
                }
            } else {
                x *= 3;
                if(x > 0) {
                    x *= 6;
                }
            }
            n--;
        }
        return x;
    }

    @Case 
    public static int ifInLoop2(int n) {
        int b = 10;
        int x = n + b;
        while (n > 0 && n <= 10) {
            if (n > 5) {
                x *= 2;
                if(x > 0) {  // Search point
                    x *= 5;
                }
            } else { 
                x *= 3;
                if(x > 0) {  // Search point
                    x *= 6;
                }
            }
            n--;
        }
        return x;
    }

    @Case 
    public static int ifInLoopSimple(int n) {
        int x = 10;
        while (n > 0 && n <= 3) {
            if (n > 2) {
                if(n > 0) {  // Search point
                    x *= 5;
                }
            } else { 
                if(n > 0) {  // Search point
                    x *= 6;
                }
            }
            n--;
        }
        return x;
    }

    @Case 
    public static int ifInLoopSimple2(int n) {
        int x = 10;
        while (n > 0) {
            if (n > 10) break;
            if (n > 2) {
                if(n > 0) {  // Search point
                    x *= 5;
                }
            } else { 
                if(n > 0) {  // Search point
                    x *= 6;
                }
            }
            n--;
        }
        return x;
    }

    @Case 
    public static int ifInLoopSimple3(int n) {
        int x = 10;
        while (n > 0) {
            if (n > 2) {
                if(n > 0) {  // Search point
                    x *= 5;
                }
            } else { 
                if(n > 0) {  // Search point
                    x *= 6;
                }
            }
            n--;
        }
        return x;
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
