package hr.fer.oprpp2.servlets.glasanje;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet that loads the band definitions from the file, sets them as an attribute
 * and forwards the request to the /WEB-INF/pages/glasanjeIndex.jsp.
 */
@WebServlet("/glasanje")
public class GlasanjeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Učitaj raspoložive bendove iz datoteke
        req.setAttribute("bendovi", GlasanjeUtil.loadBandDefs(req).values());
        // Pošalji ih JSP-u...
        req.getRequestDispatcher("/WEB-INF/pages/glasanjeIndex.jsp").forward(req, resp);
    }
}
