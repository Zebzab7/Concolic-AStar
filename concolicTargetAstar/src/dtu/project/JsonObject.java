package dtu.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

//map error, ignore unrecognized property
@JsonIgnoreProperties(ignoreUnknown = true)

public class JsonObject {
    private String name;
    private List<String> access;
    private List<String> typeparams;
    private Super superStructure;
    private List<String> interfaces;
    private List<Method> methods;
    private List<Field> fields;

    // Getters and setters
    public JsonObject() {
    }

    public class Method {
        private String name;
        private List<String> access;
        private List<String> typeparams;
        private List<String> params;
        private ReturnType returns;
        private Code code;

        public Method() {
        }

        // Getters and setters
        public class ReturnType {
            private String type;
            private List<String> annotations;

            public ReturnType() {
            }

            // Getters and setters
        }
        public class Code {
            @JsonIgnore
            private int maxStack;

            @JsonIgnore
            private int maxLocals;

            private List<String> exceptions;

            @JsonIgnore
            private String stackMap;

            private List<Bytecode> bytecode;

            public Code() {
            }
            // Getters and setters
            public class Bytecode {
                private int offset;
                private String opr;
                private String type;
                private int index;
                private String condition;
                private String access;

                public Bytecode() {
                }
                // Getters and setters
            }
        }
    }

    public class Super {
        private String name;
        private String inner;
        private List<String> args;
        private List<String> annotations;

        public Super() {
        }
        // Getters and setters
    }

    public class Field {
        String name;
        public Field() {
        }
    }

    @Override
    public String toString() {
        return "ClassStructure{" +
                "name='" + name + '\'' +
                ", access=" + access +
                ", typeparams=" + typeparams +
                // ... (include other properties here)
                ", methods=" + methods +
                ", fields=" + fields +
                '}';
    }
}






