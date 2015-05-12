package bsu.fpmi.chat.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import org.json.simple.JSONObject;

public final class MessageStorage {
	private static final List<Message> INSTANSE = Collections.synchronizedList(new ArrayList<Message>());

	private MessageStorage() {
	}
	public static List<Message> getStorage() {
		return INSTANSE;
	}

	public static void addMessage(Message message) {
		INSTANSE.add(message);
	}
	public static void addAll(List<Message> messages) {
		INSTANSE.addAll(messages);
	}

	public static int getSize() {
		return INSTANSE.size();
	}

	public static List<JSONObject> getJSONlist() {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		for(Message i : INSTANSE) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", i.getId());
			jsonObject.put("user", i.getUserName());
			jsonObject.put("description", i.getDescription());

			jsonList.add(jsonObject);
		}
		return jsonList;
	}
	public static List<Message> getSubMesByIndex(int index) {
		return INSTANSE.subList(index, INSTANSE.size());
	}
	public static Message getMessageById(String id) {
		for (Message message : INSTANSE) {
			if (message.getId().equals(id)) {
				return message;
			}
		}
		return null;
	}

}
