package concolicastar;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;

public class JacksonExample {

    public static void main(String[] args) {
        try {
            // Create a simple object for demonstration
            Person person = new Person("John", 30);
            JSONObject obj = new JSONObject();
            obj.put("name", "mkyong.com");
            obj.put("age", 100);
            System.out.println(obj);
            // Convert Java object to JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(person);
            System.out.println("JSON Representation: " + jsonString);

            // Convert JSON string back to Java object
            Person parsedPerson = objectMapper.readValue(jsonString, Person.class);
            System.out.println("Parsed Person's Name: " + parsedPerson.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Person {
        private String name;
        private int age;

        // Necessary for Jackson
        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}