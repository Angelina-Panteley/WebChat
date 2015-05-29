package bsu.fpmi.chat.model;

import org.json.simple.JSONObject;

public class Message {
	private String id;
	private String description;
	private String user;
    private String user_id;
    private String date;

    public Message(String id, String userName, String message, String user_id, String date) {
    	this.id = id;
    	this.user = userName;
    	this.description = message;
        this.user_id = user_id;
        this.date = date;
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
    public String getDate() {
        return date;
    }

    public String getUser_id() {
        return user_id;
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
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setDate(String date) {
        this.date = date;
    }
}