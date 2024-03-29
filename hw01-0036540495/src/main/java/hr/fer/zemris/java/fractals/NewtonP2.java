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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This program is a Newton-Raphson iteration-based fractal viewer.
 * It takes at least two roots from the standard input (terminated by the keyword `done`)
 * and then displays the fractal image of the polynomial
 * f(z) = (z - z_1)*(z - z_2)*...*(z - z_n)
 * painted using Newton-Raphson iteration.
 * The program works in parallel using multiple threads by dividing the image into tracks (jobs)
 * and using a fixed number of workers (threads), with the help of {@link java.util.concurrent.ForkJoinPool
 * <p>
 * While starting the program, you can specify the number of workers (threads)
 * using --workers=W or -w W, where W is the number of workers,
 * and the number of tracks (jobs) to which the image will be divided
 * using --tracks=T or -t T, where T is the number of tracks.
 *n
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
public class NewtonP2 {

    private static int NUM_MIN_TRACKS = 16;
    private static final double ROOT_THRESHOLD = 0.002;
    private static final double CONVERGENCE_THRESHOLD = 0.001;
    private static final int MAX_ITER = 16 * 16 * 16;
    private static ComplexRootedPolynomial rootedPolynomial;
    private static ComplexPolynomial polynomial;

    public static void main(String[] args) {
        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
        System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");

        if (args.length == 1 && args[0].startsWith("--mintracks=")) {
            try {
                NUM_MIN_TRACKS = Integer.parseInt(args[0].substring(10));
                if (NUM_MIN_TRACKS < 1) {
                    throw new IllegalArgumentException("Minimum number of tracks must be positive.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid minimum number of tracks: " + args[0]);
            }
        } else if (args.length == 2 && args[0].equals("-m")) {
            try {
                NUM_MIN_TRACKS = Integer.parseInt(args[1].substring(10));
                if (NUM_MIN_TRACKS < 1) {
                    throw new IllegalArgumentException("Minimum number of tracks must be positive.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid minimum number of tracks: " + args[1]);
            }
        } else if (args.length != 0) {
            throw new IllegalArgumentException("Invalid arguments: " + String.join(" ", args));
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
        FractalViewer.show(new NewtonP2Producer());
    }

    private static class NewtonP2Producer implements IFractalProducer {

        ForkJoinPool pool;

        @Override
        public void setup() {
            pool = new ForkJoinPool();
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax,
                            int width, int height,
                            long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Initializing calculation...");

            System.out.println("Minimum number of tracks: " + NUM_MIN_TRACKS);

            short[] data = new short[width * height];

            NewtonP2.NewtonP2Producer.Job job = new NewtonP2.NewtonP2Producer.Job(reMin, reMax, imMin, imMax, width, height, 0, height - 1, MAX_ITER, data, cancel);
            pool.invoke(job);

            Job.counter.set(0);
            System.out.println("Izracun gotov. Idem obavijestiti promatraca tj. GUI!");
            observer.acceptResult(data, (short)(polynomial.order() + 1), requestNo);
        }

        @Override
        public void close() {
            pool.shutdown();
        }

        private static class Job extends RecursiveAction {
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

            static AtomicInteger counter = new AtomicInteger();

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
            public void compute() {
                if ((yMax - yMin) * NUM_MIN_TRACKS <= height) {
                    computeDirect();
                    return;
                }

                invokeAll(
                        new Job(reMin, reMax, imMin, imMax, width, height, yMin, yMin + (yMax - yMin) / 2, m, data, cancel),
                        new Job(reMin, reMax, imMin, imMax, width, height, yMin + (yMax - yMin) / 2 + 1, yMax, m, data, cancel)
                );
            }

            private void computeDirect() {
                System.out.println("Computing directly track " + counter.incrementAndGet() + "...");

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
