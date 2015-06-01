package bsu.fpmi.chat.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/chat.html", "/chat"})
public class LoginFilter implements Filter {

    //@Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    //@Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
        } else {
            chain.doFilter(req, res);
            // response.sendRedirect(request.getContextPath() + "/chat.html");
        }
    }

    //@Override
    public void destroy() {
    }

}
