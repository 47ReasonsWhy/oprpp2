package hr.fer.zemris.java.fractals;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
import hr.fer.zemris.math.Complex;
import hr.fer.zemris.math.ComplexPolynomial;
import hr.fer.zemris.math.ComplexRootedPolynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This program is a Newton-Raphson iteration-based fractal viewer.
 * It takes at least two roots from the standard input (terminated by the keyword `done`)
 * and then displays the fractal image of the polynomial
 * f(z) = (z - z_1)*(z - z_2)*...*(z - z_n)
 * painted using Newton-Raphson iteration.
 * The program works in parallel using multiple threads by dividing the image into tracks (jobs)
 * and using a fixed number of workers (threads), with the help of {@link ExecutorService}
 * <p>
 * While starting the program, you can specify the number of workers (threads)
 * using --workers=W or -w W, where W is the number of workers,
 * and the number of tracks (jobs) to which the image will be divided
 * using --tracks=T or -t T, where T is the number of tracks.
 *
 * @see hr.fer.zemris.math.Complex
 * @see hr.fer.zemris.math.ComplexPolynomial
 * @see hr.fer.zemris.math.ComplexRootedPolynomial
 * @see hr.fer.zemris.java.fractals.viewer.FractalViewer
 * @see hr.fer.zemris.java.fractals.viewer.IFractalProducer
 * @see hr.fer.zemris.java.fractals.viewer.IFractalResultObserver
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class NewtonP1 {

    private static int NUM_WORKERS = Runtime.getRuntime().availableProcessors();
    private static int NUM_TRACKS = 4 * Runtime.getRuntime().availableProcessors();
    private static final double ROOT_THRESHOLD = 0.002;
    private static final double CONVERGENCE_THRESHOLD = 0.001;
    private static final int MAX_ITER = 16 * 16 * 16;
    private static ComplexRootedPolynomial rootedPolynomial;
    private static ComplexPolynomial polynomial;

    public static void main(String[] args) {
        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
        System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");

        boolean workersSpecified = false;
        boolean tracksSpecified = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--workers=")) {
                if (workersSpecified) {
                    throw new IllegalArgumentException("Cannot specify workers twice.");
                }
                try {
                    int workers = Integer.parseInt(args[i].substring(10));
                    if (workers < 1) {
                        throw new IllegalArgumentException("Number of workers must be positive.");
                    }
                    NUM_WORKERS = workers;
                    workersSpecified = true;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number of workers.");
                }
            } else if (args[i].equals("-w")) {
                if (workersSpecified) {
                    throw new IllegalArgumentException("Cannot specify workers twice.");
                }
                if (i == args.length - 1) {
                    throw new IllegalArgumentException("Invalid number of workers.");
                }
                try {
                    int workers = Integer.parseInt(args[++i]);
                    if (workers < 1) {
                        throw new IllegalArgumentException("Number of workers must be positive.");
                    }
                    NUM_WORKERS = workers;
                    workersSpecified = true;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number of workers.");
                }
            } else if (args[i].startsWith("--tracks=")) {
                if (tracksSpecified) {
                    throw new IllegalArgumentException("Cannot specify tracks twice.");
                }
                try {
                    int tracks = Integer.parseInt(args[i].substring(9));
                    if (tracks < 1) {
                        throw new IllegalArgumentException("Number of tracks must be positive.");
                    }
                    NUM_TRACKS = tracks;
                    tracksSpecified = true;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number of tracks.");
                }
            } else if (args[i].equals("-t")) {
                if (tracksSpecified) {
                    throw new IllegalArgumentException("Cannot specify tracks twice.");
                }
                if (i == args.length - 1) {
                    throw new IllegalArgumentException("Invalid number of tracks.");
                }
                try {
                    int tracks = Integer.parseInt(args[++i]);
                    if (tracks < 1) {
                        throw new IllegalArgumentException("Number of tracks must be positive.");
                    }
                    NUM_TRACKS = tracks;
                    tracksSpecified = true;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number of tracks.");
                }
            } else {
                throw new IllegalArgumentException("Invalid argument.");
            }
        }


        short i = 1;
        List<Complex> roots = new ArrayList<>();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.printf("Root %d> ", i);
            String line = sc.nextLine();
            if (line.equals("done")) break;

            Complex c;
            try {
                c = Complex.parseComplex(line);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid complex number format.");
            }
            roots.add(c);
            i++;
        }

        rootedPolynomial = new ComplexRootedPolynomial(Complex.ONE, roots.toArray(new Complex[0]));
        polynomial = rootedPolynomial.toComplexPolynomial();

        System.out.println("Image of fractal will appear shortly. Thank you.");
        FractalViewer.show(new NewtonP1Producer());
    }

    private static class NewtonP1Producer implements IFractalProducer {

        ExecutorService pool;

        @Override
        public void setup() {
            pool = Executors.newFixedThreadPool(NUM_WORKERS);
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax,
                            int width, int height,
                            long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Initializing calculation...");

            NUM_TRACKS = Math.min(NUM_TRACKS, height);
            System.out.println("Number of workers: " + NUM_WORKERS);
            System.out.println("Number of tracks: " + NUM_TRACKS);

            short[] data = new short[width * height];
            int heightPerTrack = height / NUM_TRACKS;

            List<Future<?>> results = new ArrayList<>();

            for (int i = 0; i < NUM_TRACKS; i++) {
                int yMin = i * heightPerTrack;
                int yMax = (i + 1) * heightPerTrack - 1;
                if (i == NUM_TRACKS - 1) {
                    yMax = height - 1;
                }
                NewtonP1.NewtonP1Producer.Job job = new NewtonP1.NewtonP1Producer.Job(reMin, reMax, imMin, imMax, width, height, yMin, yMax, MAX_ITER, data, cancel);
                results.add(pool.submit(job));
            }

            for (Future<?> result : results) {
                while (true) {
                    try {
                        result.get();
                        break;
                    } catch (InterruptedException ignored) {
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            System.out.println("Izracun gotov. Idem obavijestiti promatraca tj. GUI!");
            observer.acceptResult(data, (short)(polynomial.order() + 1), requestNo);
        }

        @Override
        public void close() {
            pool.shutdown();
        }

        private static class Job implements Runnable {
            double reMin;
            double reMax;
            double imMin;
            double imMax;
            int width;
            int height;
            int yMin;
            int yMax;
            int m;
            short[] data;
            AtomicBoolean cancel;

            public Job(double reMin, double reMax, double imMin, double imMax,
                       int width, int height, int yMin, int yMax,
                       int m, short[] data, AtomicBoolean cancel) {
                super();
                this.reMin = reMin;
                this.reMax = reMax;
                this.imMin = imMin;
                this.imMax = imMax;
                this.width = width;
                this.height = height;
                this.yMin = yMin;
                this.yMax = yMax;
                this.m = m;
                this.data = data;
                this.cancel = cancel;
            }

            @Override
            public void run() {
                int offset = yMin * width;
                for (int y = yMin; y <= yMax; y++) {
                    if (cancel.get()) break;
                    for (int x = 0; x < width; x++) {
                        double cre = (double) x / ((double) width - 1.0) * (reMax - reMin) + reMin;
                        double cim = (height - 1.0 - y) / ((double) height - 1) * (imMax - imMin) + imMin;
                        Complex zn = new Complex(cre, cim);
                        Complex znOld;
                        int iter = 0;
                        do {
                            znOld = new Complex(zn.getRe(), zn.getIm());
                            zn = zn.sub(polynomial.apply(zn).divide(polynomial.derive().apply(zn)));
                            iter++;
                        } while (zn.sub(znOld).module() > CONVERGENCE_THRESHOLD && iter < MAX_ITER);
                        int index = rootedPolynomial.indexOfClosestRootFor(zn, ROOT_THRESHOLD);
                        data[offset++] = (short) (index + 1);
                    }
                }
            }
        }
    }
}
