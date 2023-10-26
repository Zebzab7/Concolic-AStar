package concolicastar.Model;


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



//Type defined and Object not used
class Element{
    String type;
    Object value;
    public Element(String type, Object value){
        this.type = type;
        this.value = value;
    }
    public String getType(){
        return type;
    }
    public Object getValue(){
        return value;
    }
    public void setType(String type){
        this.type = type;
    }
    public void setValue(Object value){
        this.value = value;
    }
    public String toString(){
        return "Type: "+type+" Value: "+value;
    }

}

