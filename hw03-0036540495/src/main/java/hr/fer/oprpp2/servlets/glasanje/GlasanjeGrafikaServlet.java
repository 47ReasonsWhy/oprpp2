package hr.fer.oprpp2.servlets.glasanje;

import hr.fer.oprpp2.models.Band;
import hr.fer.oprpp2.servlets.util.ChartUtil;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * A servlet used to generate a pie chart of the votes on the most famous bands.
 */
@WebServlet("/glasanje-grafika")
public class GlasanjeGrafikaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("image/png");

        OutputStream outputStream = resp.getOutputStream();

        Map<Integer, Band> bands = GlasanjeUtil.loadBandsWithVotes(req);
        Map<String, Double> votes = new HashMap<>();
        bands.forEach((id, band) -> votes.put(band.getName(), (double) band.getVotes()));
        LinkedHashMap<String, Double> votesSorted = new LinkedHashMap<>();
        votes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> votesSorted.put(x.getKey(), x.getValue()));

        JFreeChart chart = ChartUtil.getChart("Votes on most famous bands", votesSorted);
        int width = 500;
        int height = 350;
        ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
    }
}
