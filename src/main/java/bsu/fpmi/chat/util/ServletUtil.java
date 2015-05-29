package bsu.fpmi.chat.util;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;


public class ServletUtil {
	public static final String APPLICATION_JSON = "application/json";
	public static final String ID = "id";
	private static final String MESSAGE = "description";
	private static final String USERNAME = "user";

	private ServletUtil() {
	}
	
	public static String getMessageBody(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

	public static String getMessageBodyForEdit(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		sb.replace(sb.indexOf("id"),sb.indexOf("user"),"id\":"+request.getParameter(ID)+",\"");
		return sb.toString();
	}

}
