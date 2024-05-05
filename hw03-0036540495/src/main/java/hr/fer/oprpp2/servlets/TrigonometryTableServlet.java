package hr.fer.oprpp2.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * A servlet used to generate a table of sin and cos values for integer angles in the range [a, b].
 * The table is stored in the temporary attributes "sinValues" and "cosValues" and then forwarded to the JSP page
 * "/WEB-INF/pages/trigonometric.jsp".
 */
@WebServlet("/trigonometric")
public class TrigonometryTableServlet  extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int a, b;

        try {
            a = Integer.parseInt(req.getParameter("a"));
            b = Integer.parseInt(req.getParameter("b"));
        } catch (NumberFormatException e) {
            a = 0;
            b = 360;
        }

        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        if (b > a + 720) {
            b = a + 720;
        }

        Map<Integer, String> sinValues = new TreeMap<>();
        Map<Integer, String> cosValues = new TreeMap<>();

        for (int i = a; i <= b; i++) {
            sinValues.put(i, String.format("%.5f", Math.sin(Math.toRadians(i))));
            cosValues.put(i, String.format("%.5f", Math.cos(Math.toRadians(i))));
        }

        req.setAttribute("sinValues", sinValues);
        req.setAttribute("cosValues", cosValues);

        req.getRequestDispatcher("/WEB-INF/pages/trigonometric.jsp").forward(req, resp);
    }
}
