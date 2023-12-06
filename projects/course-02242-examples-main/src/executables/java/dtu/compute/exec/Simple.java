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
    public static int example(int n) {
        int x = 0;
        // boolean flag = false;
        while (n > 0) {
            if (n == 1) {
                x = 1;
                if (x > 0) {  // Search point
                    x = -1;
                }
            }
            if (n > 5 && x == 1) {
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
            n++;
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
        if (n > 0) {
            x++;
            x++;
            x++;
            x++;
            x++;
            x++;
            if (n > 3) {
                x++;
                x++;
                x++;
                x++;
                x++;
                x++;
                if(n > 5) {  // Search point
                    x *= 5;
                    x++;
                    x++;
                    x++;
                    if (n > 200) {
                        x += 94;
                    } else {
                        x += 0;
                    }
                } else {
                    x += 20;
                    x++;
                    x++;
                    x++;
                    x++;
                    x++;
                    x++;
                    if (n > 50) {
                        x += 1;
                    } 
                }
            } else { 
                x++;
                x++;
                x++;
                if(n > 0) {  // Search point
                    x++;
                    x++;
                    x++;
                    x++;
                    x *= 6;
                    if (n > 30) {
                        x += 10;
                    } else {
                        x++;
                        x++;
                        x++;
                        x++;
                        x += 20;
                    }
                } else {
                    x++;
                    x++;
                    x++;
                    x++;
                    if (n > 100) {
                        x += 11;
                    } 
                }
            }
            n++;
        }
        return x;
    }

    public static int testWide(int x){
        if(x>0){
            return 0;
        }else if (x>1){
            return 1;
        }else if (x>2){
            return 2;
        }else if (x>3){
            return 3;
        }else if (x>4){
            return 4;
        }else if (x>5){
            return 5;
        }else if (x>6){
            return 6;
        }else if (x>7){
            return 7;
        }else if (x>8){
            return 8;
        }else if (x>9){
            return 9;
        }else if (x>10){
            return 10;
        }else if (x>11){
            return 11;
        }else if (x>12){
            return 12;
        }else if (x>13){
            return 13;
        }else if (x>14){
            return 14;
        }else if (x>15){
            return 15;
        }else if (x>16){
            return 16;
        }else if (x>17){
            return 17;
        }else if (x>18){
            return 18;
        }else if (x>19){
            return 19;
        }else{
            return 20;
        }
    }
    
    public static int testDeep(int x){
        if(x>0){
            if(x>1){
                if(x>2){
                    if(x>3){
                        if(x>4){
                            if(x>5){
                                if(x>6){
                                    if(x>7){
                                        if(x>8){
                                            if(x>9){
                                                if(x>10){
                                                    if(x>11){
                                                        if(x>12){
                                                            if(x>13){
                                                                if(x>14){
                                                                    if(x>15){
                                                                        if(x>16){
                                                                            if(x>17){
                                                                                if(x>18){
                                                                                    if(x>19){
                                                                                        if(x>20){
                                                                                            return 21;
                                                                                        }
                                                                                    }else{
                                                                                        return 19;
                                                                                    }
                                                                                }else{
                                                                                    return 19;
                                                                                }
                                                                            }else{
                                                                                return 18;
                                                                            }
                                                                        }else{
                                                                            return 17;
                                                                        }
                                                                    }else{
                                                                        return 16;
                                                                    }
                                                                }else{
                                                                    return 15;
                                                                }
                                                            }else{
                                                                return 14;
                                                            }
                                                        }else{
                                                            return 13;
                                                        }
                                                    }else{
                                                        return 12;
                                                    }
                                                }else{
                                                    return 11;
                                                }
                                            }else{
                                                return 10;
                                            }
                                        }else{
                                            return 9;
                                        }
                                    }else{
                                        return 8;
                                    }
                                }else{
                                    return 6;
                                }
                            }else{
                                return 5;
                            }
                        }else{
                            return 4;
                        }
                    }else{
                        return 3;
                    }
                }else{
                    return 2;
                }
            }else{
                return 1;
            }
        }else if (x>1){
            return 1;
        }
        return 0;
    }


    // @Case 
    // public static int ifInLoopSimple3(int n) {
    //     int x = 10;
    //     int y = 1;
    //     while (n > 0) {
    //         if (y > 0) {
    //             if(n > 0) {  // Search point
    //                 x *= 5;
    //             }
    //         } else { 
    //             if(n > 0) {  // Search point
    //                 x *= 6;
    //             }
    //         }
    //         y = -1;
    //         n--;
    //     }
    //     return x;
    // }

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
