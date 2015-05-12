package bsu.fpmi.chat.model;

import org.json.simple.JSONObject;

public class Message {
	private String id;
	private String description;
	private String user;

	private static int counter = 0;

    	
    public Message(String id, String userName, String message) {
    	this.id = id;
    	this.user = userName;
    	this.description = message;

    }
	
    public Message(JSONObject json) {
    	this.id = (String)json.get("id");
        this.user = (String)json.get("user");
        this.description = (String)json.get("description");

    }
    	
    public String getId() {
    	return id;
    }
    
    public String getUserName() {
    	return user;
    }
    	
    public String getDescription() {
    	return description;
    }
    	
    public void setId(int id) {
    	this.id = String.valueOf(id);
    }

    public void setUserName(String userName) {
    	this.user = userName;
   	}

    public void setDescription(String message) {
    	this.description = message;
    }
}