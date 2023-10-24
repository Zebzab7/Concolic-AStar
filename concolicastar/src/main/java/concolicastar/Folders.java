package concolicastar;


import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.json.simple.*;
import org.json.simple.parser.*;



public class Folders {
    //Finds Json files at the given path and all subdirectories
    public static ArrayList<JsonFile> findFiles(String Path,String endswith){
        ArrayList<File> subfolders = new ArrayList<File>();
		ArrayList<File> allfolders = new ArrayList<File>();
		ArrayList<File> files = new ArrayList<File>();
		allfolders.add(new File(Path));
		subfolders.add(new File(Path));
        		//Finds all subfolders
		while(findSubFolders(subfolders) != null) {
			subfolders = findSubFolders(subfolders);
			for(File folder : subfolders) {
				allfolders.add(folder);
			}
		}

		//Finds all files in all folders
		for(File folder : allfolders) {
			File[] localFiles = new File(folder.getPath()).listFiles(File::isFile);
			if(localFiles != null) {
				for(File file : localFiles) {
					//Finds all java files
					if(file.getName().endsWith(endswith)) {
						files.add(file);
						
					}
				}
			}
		}
		ArrayList<JsonFile> jsonFiles = fromFileToJson(files);
        return jsonFiles;
    }
    private static ArrayList<File> findSubFolders(ArrayList<File> subfolders) {
		if(subfolders.size() == 0) {
			return null;
		}
		ArrayList<File> folders = new ArrayList<File>();
		for(File folder : subfolders) {
			File[] subfolder = folder.listFiles(File::isDirectory);
			for (File file : subfolder) {
				folders.add(file);
			}
		}
		return folders;
	}
    //Prints files and folders for ease of use
	public static void PrintFileFolder(ArrayList<File> files) {
		for(File file : files){
			System.out.println(file.getPath());
		}
	}
	public static ArrayList<JsonFile> fromFileToJson(ArrayList<File> files){
		JSONParser parser = new JSONParser();
		ArrayList<JsonFile> jsonFiles = new ArrayList<JsonFile>();
		try{
			for(File f : files){
				JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(f.getAbsolutePath()));
				//Splits string based on / to get the name of the file
				String[] split = jsonObj.get("name").toString().split(Pattern.quote("/"));
				jsonFiles.add(new JsonFile(jsonObj,split[split.length-1],(JSONArray) jsonObj.get("methods")));
				
			}
		}catch(Exception e){
			System.out.println("Error");
			System.out.println(e);
		}
		return jsonFiles;
	}
	//This is a List of jsons with the given key
	public static JSONArray getJsonArray(JSONObject obj ,String key){
		return  (JSONArray) obj.get(key);
	}
	//This gives a Object from a List of Jsons. 
	public static JSONObject getJsonObjectFromArray(JSONArray arr, int index){
		return (JSONObject) arr.get(index);
	}


}
class JsonFile{
	private JSONObject jsonObject; 
	private String fileName;
	private JSONArray methods;
	public JsonFile(JSONObject jsonObject, String fileName, JSONArray methods){
		this.jsonObject = jsonObject;
		this.fileName = fileName;
		this.methods = methods;
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