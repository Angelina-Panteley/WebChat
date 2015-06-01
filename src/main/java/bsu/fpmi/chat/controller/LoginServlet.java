package bsu.fpmi.chat.controller;
import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bsu.fpmi.chat.dao.MessageDao;
import bsu.fpmi.chat.dao.MessageDaoImpl;
import bsu.fpmi.chat.util.ServletUtil;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(LoginServlet.class.getName());
    private static final long serialVersionUID = 1L;

    private MessageDao messageDao;

    @Override
    public void init() {
        this.messageDao = new MessageDaoImpl();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        // TODO Credential Check
        logger.info(login + " Authorization...");
        //response.setContentType(ServletUtil.APPLICATION_JSON);
        PrintWriter out = response.getWriter();
        String id;


            if (messageDao.isUserExist(login, password)) {
                id = messageDao.getUserId(login, password);
                saveUserId(id);
                out.print(formResponse("true"));
                logger.info("Signing is successful");
            } else {
                out.print(formResponse("false"));
                logger.info("Signing failed");
            }
            HttpSession session = request.getSession();
        session.setAttribute("user", login);
        response.sendRedirect(request.getContextPath() + "/chat.html");
        out.flush();
         response.setStatus(HttpServletResponse.SC_OK);

    }
    private void saveUserId(String id) throws FileNotFoundException, IOException
    {
        FileOutputStream fileStream = new FileOutputStream("user.txt");
        ObjectOutputStream os = new ObjectOutputStream(fileStream);
        os.writeObject(id);
        os.close();
    }

    @SuppressWarnings("unchecked")
    private String formResponse(String response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("answer", response);
        return jsonObject.toJSONString();
    }
}
