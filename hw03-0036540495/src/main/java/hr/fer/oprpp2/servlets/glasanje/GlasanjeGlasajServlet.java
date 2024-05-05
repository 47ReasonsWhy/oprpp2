package hr.fer.oprpp2.servlets.glasanje;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * A servlet that handles voting for a band.
 * Updates the voting results file with the new vote, then redirects the user to the voting results page.
 *
 * @see GlasanjeUtil
 * @see GlasanjeRezultatiServlet
 */
@WebServlet("/glasanje-glasaj")
public class GlasanjeGlasajServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Zabiljezi glas...
        Path filePath = Path.of(req.getServletContext().getRealPath("/WEB-INF/glasanje-rezultati.txt"));
        // Napravi datoteku ako je potrebno; ažuriraj podatke koji su u njoj...
        Map<Integer, Integer> votes = GlasanjeUtil.loadBandVotes(req);
        int id = Integer.parseInt(req.getParameter("id"));
        votes.put(id, votes.getOrDefault(id, 0) + 1);
        // Spremi ih u datoteku...
        Files.writeString(filePath, votes.entrySet().stream()
                .map(entry -> entry.getKey() + "\t" + entry.getValue())
                .reduce((a, b) -> a + "\n" + b).orElse(""),
                StandardOpenOption.CREATE
        );
        // Kad je gotovo, pošalji redirect pregledniku I dalje NE generiraj odgovor
        resp.sendRedirect(req.getContextPath() + "/glasanje-rezultati");
    }
}
