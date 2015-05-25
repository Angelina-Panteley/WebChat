package bsu.fpmi.chat.controller;
        import bsu.fpmi.chat.util.ServletUtil;
        import org.apache.log4j.Logger;
        import org.json.simple.JSONObject;
        import org.json.simple.parser.ParseException;

        import javax.servlet.ServletException;
        import javax.servlet.annotation.WebServlet;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;
        import java.io.File;
        import java.io.IOException;
        import java.io.PrintWriter;

        import static bsu.fpmi.chat.util.MessageUtil.*;

@WebServlet(urlPatterns = {"/index"}, asyncSupported = true)
public final class User extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(User.class.getName());

    @Override
    public void init() {
      boolean flag;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String data = ServletUtil.getMessageBody(request);
        logger.info("Signing...");
        try {
            JSONObject json = stringToJson(data);
            String login = (String) (json.get("login"));
            String password = (String) (json.get("password"));
            String doWhat = (String) (json.get("doWhat"));

            response.setContentType(ServletUtil.APPLICATION_JSON);
            PrintWriter out = response.getWriter();

            if(doWhat.equals("signin")) {
                if (true) {
                    out.print(formResponse("true"));
                    logger.info("Signing is successful");
                } else {
                    out.print(formResponse("false"));
                    logger.info("Signing failed");
                }
            } else if (doWhat.equals("signup")) {
                if (true) {
                    out.print(formResponse("true"));
                    logger.info("Registration is successful");
                } else {
                    out.print(formResponse("false"));
                    logger.info("Registration failed");
                }
            }

            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ParseException e) {
            logger.error("Invalid user message " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @SuppressWarnings("unchecked")
    private String formResponse(String response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("answer", response);
        return jsonObject.toJSONString();
    }
}