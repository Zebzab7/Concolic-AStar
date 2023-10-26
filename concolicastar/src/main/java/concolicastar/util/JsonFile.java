package concolicastar.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonFile{
	private JSONObject jsonObject; 
	private String fileName;
	private JSONArray methods;
	public JsonFile(JSONObject jsonObject, String fileName, JSONArray methods){
		this.jsonObject = jsonObject;
		this.fileName = fileName;
		this.methods = methods;
	}
	
	public JSONObject getMethodByName(String name) {
		for(int i = 0; i < methods.size(); i++) {
			JSONObject methodObj = (JSONObject) methods.get(i);
			if(methodObj.get("name").equals(name)) {
				// System.out.println("In loop: ");
				// System.out.println(methodObj);
				return methodObj;
			}
		}
		throw new IllegalArgumentException("Method not found: " + name);
	}
	
	//Getter and setter methods
	public JSONObject getJsonObject(){
		return this.jsonObject;
	}
	public String getFileName(){
		return this.fileName;
	}
	public void setJsonObject(JSONObject jsonObject){
		this.jsonObject = jsonObject;
	}
	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	public JSONArray getMethods(){
		return this.methods;
	}
	public void setMethods(JSONArray methods){
		this.methods = methods;
	}

}