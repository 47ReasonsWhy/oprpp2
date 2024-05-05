package hr.fer.oprpp2.servlets.glasanje;

import hr.fer.oprpp2.models.Band;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * A servlet that handles the display of voting results.
 * It reads the voting results from the file, sorts them by the number of votes
 * and redirects the user to the JSP page that displays the results.
 */
@WebServlet("/glasanje-rezultati")
public class GlasanjeRezultatiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<Integer, Band> bendovi = GlasanjeUtil.loadBandsWithVotes(req);
        // Sortiraj bendove po broju glasova
        List<Band> bendoviSorted = new ArrayList<>(bendovi.values().stream()
                .sorted(Comparator.comparingInt(Band::getVotes).reversed())
                .toList()
        );
        // Istakni pobjednike
        int maxVotes = bendoviSorted.get(0).getVotes();
        List<Band> pobjednici = new ArrayList<>(bendoviSorted.stream()
                .takeWhile(band -> band.getVotes() == maxVotes)
                .toList()
        );
        // Spremi ih u request
        req.setAttribute("bendovi", bendoviSorted);
        req.setAttribute("pobjednici", pobjednici);
        // Pošalji ih JSP-u
        req.getRequestDispatcher("/WEB-INF/pages/glasanjeRez.jsp").forward(req, resp);
    }
}
