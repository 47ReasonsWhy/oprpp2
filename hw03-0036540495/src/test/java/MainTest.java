import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MainTest {
    static int numWorkers = Runtime.getRuntime().availableProcessors();

    Double calcNorm(double[] elements) {
        int localNumWorkers = Math.min(numWorkers, elements.length);
        int blockSize = (int) Math.ceil((double) elements.length / localNumWorkers);
        List<Future<Double>> results = new ArrayList<>();
        try (ExecutorService pool = Executors.newFixedThreadPool(localNumWorkers)) {

            for (int i = 0; i < localNumWorkers; i++) {
                int start = i * blockSize;
                int end = i + 1 < localNumWorkers ? ((i+1) * blockSize) : elements.length;
                Future<Double> task = pool.submit(new SumSquaresJob(elements, start, end));
                results.add(task);
            }

            double sum = 0;
            for (var result : results) {
                sum += result.get();
            }

            return Math.sqrt(sum);
        } catch (Exception ignored) {
        }

        return null;
    }

    static class SumSquaresJob implements Callable<Double> {
        private final double[] elements;
        private final int start;
        private final int end;

        public SumSquaresJob(double[] elements, int start, int end) {
            this.elements = elements;
            this.start = start;
            this.end = end;
        }

        @Override
        public Double call() {
            double sum = 0;
            for (int i = start; i < end; i++) {
                sum += elements[i] * elements[i];
            }
            return sum;
        }
    }
}
