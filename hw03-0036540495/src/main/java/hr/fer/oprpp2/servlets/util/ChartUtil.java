package hr.fer.oprpp2.servlets.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.util.Map;

/**
 * Utility class for creating charts from arbitrary data sets using JFreeChart library
 */
public class ChartUtil {
    /**
     * Creates a chart
     */
    public static <K extends Comparable<K>> JFreeChart getChart(String title, Map<K, Double> values) {

        JFreeChart chart = ChartFactory.createPieChart(
                title,
                createDataset(values),
                true,
                true,
                false
        );

        PiePlot<?> plot = (PiePlot<?>) chart.getPlot();
        plot.setStartAngle(90);
        plot.setForegroundAlpha(0.7f);
        return chart;

    }

    /**
     * Creates a sample dataset
     */
    private static <K extends Comparable<K>> PieDataset<K> createDataset(Map<K, Double> values) {
        DefaultPieDataset<K> result = new DefaultPieDataset<>();
        for (Map.Entry<K, Double> entry : values.entrySet()) {
            result.setValue(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
