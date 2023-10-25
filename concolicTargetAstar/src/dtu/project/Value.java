package dtu.project;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//map error, ignore unrecognized property
@JsonIgnoreProperties(ignoreUnknown = true)
public class Value {
    private String type;
    private int value;


    public Value() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "value{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
