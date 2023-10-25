package concolicastar;

public class AbsoluteMethod {
    public String className;
    public String methodName;

    public AbsoluteMethod(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public boolean equals(Object o) {
        if (o instanceof AbsoluteMethod) {
            AbsoluteMethod am = (AbsoluteMethod) o;
            return am.getClassName().equals(className) && am.getMethodName().equals(methodName);
        }
        return false;
    }

    public String getClassName() {
        return className;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public void setMethodName(String name) {
        this.methodName = name;
    }

    public String toString() {
        return className + "." + methodName;
    }
}
