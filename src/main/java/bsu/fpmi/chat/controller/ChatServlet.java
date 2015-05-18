package bsu.fpmi.chat.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import bsu.fpmi.chat.util.MessageUtil;
import bsu.fpmi.chat.storage.xml.XMLHistoryUtil;

import org.apache.log4j.Logger;

import bsu.fpmi.chat.model.Message;
import bsu.fpmi.chat.model.MessageStorage;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import bsu.fpmi.chat.util.ServletUtil;

import org.xml.sax.SAXException;

import static bsu.fpmi.chat.util.MessageUtil.*;

@WebServlet("/chat")
public final class ChatServlet extends HttpServlet {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm ");

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ChatServlet.class.getName());

	@Override
	public void init() {
		try {
			loadHistory();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter(TOKEN);
	if (token != null && !"".equals(token)) {
			int index = getIndex(token);

			logger.info("doGet: token="+token+"; index="+index);
			String messages = formResponse();
			response.setContentType(ServletUtil.APPLICATION_JSON);
			PrintWriter out = response.getWriter();
			out.print(messages);///token+listOfJSON
			out.flush();
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);

			message.setId(MessageStorage.getSize() + 1);
			MessageStorage.addMessage(message);

			Date currentDate = new Date();
			logger.info("doPost: "+ message.getUserName() + " : " + message.getDescription());
			XMLHistoryUtil.getInstance().addMessageToXML(message, currentDate);
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (ParseException e) {
			logger.error("Invalid user message " + e.getMessage());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String data = ServletUtil.getMessageBody(request);

		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			String id = message.getId();
			Date currentDate = new Date();
			logger.info("doPut: "+ message.getUserName() + " : " + message.getDescription());
			Message mesToUpdate = MessageStorage.getMessageById(id);
			if (mesToUpdate != null) {
				mesToUpdate.setDescription(message.getDescription());
				XMLHistoryUtil.editMessageInXML(mesToUpdate.getId(), mesToUpdate.getDescription());
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task does not exist");
			}
		} catch (ParseException e) {//| ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter(ID);
		Date currentDate = new Date();
		Message mToDelete = MessageStorage.getMessageById(id);
		logger.info("doDelete: " + mToDelete.getUserName() + " : " + mToDelete.getDescription());
		if (id != null && !"".equals(id)) {
			mToDelete.setDescription("");
			mToDelete.setUserName("");
			mToDelete.setId(-Integer.valueOf(mToDelete.getId()));
			logger.info("Message was successfully deleted");
			XMLHistoryUtil.getInstance().deleteMessageFromXML(id);
		} else {
			logger.info("Message ID is out of bounds");
		}
	}

	@SuppressWarnings("unchecked")
	private String formResponse() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(MESSAGES, MessageStorage.getJSONlist());
		jsonObject.put(TOKEN, getToken(MessageStorage.getSize()));
		return jsonObject.toJSONString();
	}

	private void loadHistory() throws SAXException, IOException, ParserConfigurationException, TransformerException  {
		if (XMLHistoryUtil.doesStorageExist()) {
			XMLHistoryUtil.oldHistory(logger);
		} else {
			XMLHistoryUtil.startWritingToXML();
		}
	}
}
