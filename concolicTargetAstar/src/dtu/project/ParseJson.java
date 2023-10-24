package dtu.project;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;


public class ParseJson {

//    json->string
    public String getStr(File jsonFile){
        String jsonStr = "";
        try {
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void jsonParse(String jsonStr) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        //read json file and parse to a java object
        JsonObject jo = objectMapper.readValue(jsonStr,JsonObject.class);
//        Value v = objectMapper.readValue(jsonStr,Value.class);

        System.out.println(jo);
    }

}
