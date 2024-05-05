package hr.fer.oprpp2.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet that returns the css code for the background color of the page read from the session.
 */
@WebServlet("/getcolor")
public class GetBackgroundColorServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Object color = req.getSession().getAttribute("pickedBgColor");
        if (color == null || color.toString().isBlank()) {
            color = "white";
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/css");

        resp.getWriter().write("body { background-color: " + color + "; }");
    }

}
