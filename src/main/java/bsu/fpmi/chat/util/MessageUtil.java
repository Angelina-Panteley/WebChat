package bsu.fpmi.chat.util;

import bsu.fpmi.chat.model.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import bsu.fpmi.chat.db.ConnectionManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import bsu.fpmi.chat.db.ConnectionManager;
import bsu.fpmi.chat.model.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;
import java.io.IOException;

import org.apache.log4j.Logger;
import bsu.fpmi.chat.dao.MessageDao;
import bsu.fpmi.chat.dao.MessageDaoImpl;

public final class MessageUtil {
	public static final String TOKEN = "token";
	public static final String MESSAGES = "messages";
	public static final String ID = "id";
	private static final String TN = "TN";
	private static final String EN = "EN";
	private static final String MESSAGE = "description";
	private static final String USERNAME = "user";
	private static final String USER_ID = "user_id";
	private static final String DATE = "date";

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	private MessageUtil() {
	}

	public static String getToken(int index) {
		Integer number = index * 8 + 11;
		return TN + number + EN;
	}

	public static int getIndex(String token) {
		return (Integer.valueOf(token.substring(2, token.length() - 2)) - 11) / 8;
	}

	public static JSONObject stringToJson(String data) throws ParseException {
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(data.trim());
	}

	public static Message jsonToMessage(JSONObject json) {

		Object id = json.get(ID);
		Object message = json.get(MESSAGE);
		Object userName = json.get(USERNAME);
		Object user_id = makeNewID(userName.toString());
		Object date = new Date();

		if (id != null && message != null && !id.toString().equals("-1")) {
			return new Message(id.toString(), userName.toString(), message.toString(), user_id.toString(), dateFormat.format(date));
		}
		return null;
	}

	private static int ID_ui = 1;
	private static boolean ser = false;
	private static Logger logger = Logger.getLogger(MessageDaoImpl.class.getName());

	public static int returnOldID() throws FileNotFoundException, IOException, ClassNotFoundException
	{
		FileInputStream fileStream = new FileInputStream("id.txt");
		ObjectInputStream os = new ObjectInputStream(fileStream);
		int oldID = (Integer) os.readObject();
		os.close();
		return oldID;
	}

	public static int makeNewID(String userName)
	{
		try{
			if(!ser)
			{
				ID_ui = 1;
				check(userName);
				ser = true;
				serializeID();
			}
			else
			{
				int oldId = returnOldID();
				ID_ui = oldId+1;
				check(userName);
				serializeID();
			}
		}
		catch(Exception e){}
		finally {return ID_ui;}
	}

	public static void serializeID() throws FileNotFoundException, IOException
	{
		FileOutputStream fileStream = new FileOutputStream("id.txt");
		ObjectOutputStream os = new ObjectOutputStream(fileStream);
		os.writeObject(ID_ui);
		os.close();
	}

	public static void check(String name)
	{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = ConnectionManager.getConnection();
			preparedStatement = connection.prepareStatement("INSERT INTO users (id, name, password) VALUES (?, ?, ?)");
			preparedStatement.setString(1, Integer.toString(ID_ui));
			preparedStatement.setString(2, name);
			preparedStatement.setString(3, "1");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error(e);
				}
			}
		}
	}
}
