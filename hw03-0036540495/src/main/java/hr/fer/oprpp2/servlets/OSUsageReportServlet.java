package hr.fer.oprpp2.servlets;

import hr.fer.oprpp2.servlets.util.ChartUtil;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * A servlet that generates a pie chart showing the usage of different operating systems.
 */
@WebServlet("/reportImage")
public class OSUsageReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");

        OutputStream outputStream = resp.getOutputStream();
        Map<String, Double> values = Map.of(
                "Linux", 29.0,
                "Mac", 20.0,
                "Windows", 51.0
        );
        JFreeChart chart = ChartUtil.getChart("Report on OS usage", values);
        int width = 500;
        int height = 350;
        ChartUtils.writeChartAsPNG(outputStream, chart, width, height);
    }

}
