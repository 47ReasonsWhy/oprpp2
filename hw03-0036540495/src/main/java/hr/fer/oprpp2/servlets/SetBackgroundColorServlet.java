package hr.fer.oprpp2.servlets;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

/**
 * A servlet that sets the background color of the page read from the request parameter "color".
 * The color is stored in the session and the user is redirected to the index.jsp page.
 */
@WebServlet("/setcolor")
public class SetBackgroundColorServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String color = req.getParameter("color");
        if (color == null || color.isBlank()) {
            color = "white";
        }

        req.getSession().setAttribute("pickedBgColor", color);

        resp.sendRedirect("index.jsp");
    }

}