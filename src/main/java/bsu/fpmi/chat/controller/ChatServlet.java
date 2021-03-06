package bsu.fpmi.chat.controller;

import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import bsu.fpmi.chat.storage.xml.XMLHistoryUtil;

import org.apache.log4j.Logger;

import bsu.fpmi.chat.model.Message;
import bsu.fpmi.chat.model.MessageStorage;
import bsu.fpmi.chat.util.ServletUtil;
import static bsu.fpmi.chat.util.MessageUtil.*;
import bsu.fpmi.chat.dao.MessageDao;
import bsu.fpmi.chat.dao.MessageDaoImpl;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.xml.sax.SAXException;


@WebServlet(urlPatterns = {"/chat"}, asyncSupported = true)
public final class ChatServlet extends HttpServlet {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm ");
	private List<AsyncContext> contexts = new LinkedList<AsyncContext>();
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ChatServlet.class.getName());
	private MessageDao messageDao;

	@Override
	public void init() {
		try {
			this.messageDao = new MessageDaoImpl();
			messageDao.addDeletedUser();
			messageDao.loadHistory();
		//	getUserId();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private String getUserId() throws IOException, ClassNotFoundException
	{
		FileInputStream fileStream = new FileInputStream("user.txt");
		ObjectInputStream os = new ObjectInputStream(fileStream);
		String user_id = os.readObject().toString();
		os.close();
		return user_id;
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = request.getParameter(TOKEN);
		if (token != null && !"".equals(token)) {
			int index = getIndex(token);

			logger.info("doGet: token="+token+"; index="+index);
			response.setCharacterEncoding("UTF-8");
			final AsyncContext as = request.startAsync(request, response);
			as.setTimeout(10*60*1000);
			contexts.add(as);
			as.addListener(new AsyncListener() {
				public void onComplete(AsyncEvent event) throws IOException {

					logger.info("complete");
					contexts.remove(as);
				}

				public void onTimeout(AsyncEvent event) throws IOException {
					logger.info("timeout");
					HttpServletResponse resp = (HttpServletResponse) as.getResponse();
					String messages = formResponse();
					resp.setContentType(ServletUtil.APPLICATION_JSON);
					PrintWriter out = resp.getWriter();
					out.print(messages);///token+listOfJSON
					out.flush();
					as.complete();
				}

				public void onError(AsyncEvent event) throws IOException {
					logger.info("error");
					HttpServletResponse resp = (HttpServletResponse) as.getResponse();
					String messages = formResponse();
					resp.setContentType(ServletUtil.APPLICATION_JSON);
					PrintWriter out = resp.getWriter();
					out.print(messages);///token+listOfJSON
					out.flush();
					as.complete();
					//contexts.remove(as);
				}

				public void onStartAsync(AsyncEvent event) throws IOException {
				}
			});
			completeAs(contexts);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "'token' parameter needed");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String data = ServletUtil.getMessageBody(request);
		try {
			List<AsyncContext> asyncContexts = new ArrayList<AsyncContext>(this.contexts);
			this.contexts.clear();

			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);

			//message.setId(MessageStorage.getSize() + 1);
			MessageStorage.addMessage(message);

			Date currentDate = new Date();
			logger.info("doPost: " + message.getUserName() + " : " + message.getDescription());
			message.setUser_id(getUserId());
			messageDao.add(message);
			response.setStatus(HttpServletResponse.SC_OK);
			completeAs(contexts);
		} catch (ParseException e) {
			logger.error("Invalid user message " + e.getMessage());
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		catch(ClassNotFoundException e){logger.error(e);}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String data = ServletUtil.getMessageBodyForEdit(request);

		try {
			JSONObject json = stringToJson(data);
			Message message = jsonToMessage(json);
			String uid = messageDao.getUserIdWhereId(message);
			if(getUserId().equals(uid)) {
				String id = message.getId();
				Date currentDate = new Date();
				messageDao.update(message.getId(), message.getDescription());
				logger.info("doPut: " + message.getUserName() + " : " + message.getDescription());
				Message mesToUpdate = MessageStorage.getMessageById(id);
				if (mesToUpdate != null) {
					mesToUpdate.setDescription(message.getDescription());
					response.setStatus(HttpServletResponse.SC_OK);
					completeAs(contexts);
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Task does not exist");
				}
			}
		} catch (ParseException e) {//| ParserConfigurationException | SAXException | TransformerException | XPathExpressionException e) {
			logger.error(e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		catch(ClassNotFoundException e)
		{logger.error(e);}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String id = request.getParameter(ID);
		Date currentDate = new Date();
		try{
		if(getUserId().equals(MessageStorage.getMessageById(id).getUser_id())) {

			messageDao.delete(Integer.valueOf(id));
			Message mToDelete = MessageStorage.getMessageById(id);
			logger.info("doDelete: " + mToDelete.getUserName() + " : " + mToDelete.getDescription());
			if (id != null && !"".equals(id)) {
				mToDelete.setDescription("");
				mToDelete.setUserName("");
				logger.info("Message was successfully deleted");
				completeAs(contexts);
			} else {
				logger.info("Message ID is out of bounds");
			}
		}}
		catch(ClassNotFoundException e)
		{logger.error(e);}
	}
	private void completeAs(List<AsyncContext> contexts) throws IOException {
		for (AsyncContext context : contexts) {
			HttpServletResponse resp = (HttpServletResponse)context.getResponse();
			String messages = formResponse();
			resp.setContentType(ServletUtil.APPLICATION_JSON);
			PrintWriter out = resp.getWriter();
			out.print(messages);///token+listOfJSON
			out.flush();
			context.complete();
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
