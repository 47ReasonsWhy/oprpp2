package hr.fer.zemris.math;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a complex number and provides methods for working with complex numbers.
 * Created complex numbers are immutable.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class Complex {
    /**
     * Real part of the complex number.
     */
    private final double re;

    /**
     * Imaginary part of the complex number.
     */
    private final double im;

    /**
     * Returns real part of the complex number.
     *
     * @return real part of the complex number
     */
    public double getRe() {
        return re;
    }

    /**
     * Returns imaginary part of the complex number.
     *
     * @return imaginary part of the complex number
     */
    public double getIm() {
        return im;
    }

    /**
     * 0
     */
    public static final Complex ZERO = new Complex(0, 0);

    /**
     * 1
     */
    public static final Complex ONE = new Complex(1,0);

    /**
     * -1
     */
    public static final Complex ONE_NEG = new Complex(-1,0);

    /**
     * i
     */
    public static final Complex IM = new Complex(0,1);

    /**
     * -i
     */
    public static final Complex IM_NEG = new Complex(0,-1);

    public Complex() {
        this.re = 0;
        this.im = 0;
        /* it is not possible to return the cached value with the constructor,
        only with the static factory method, so we have to create a new object */
    }

    /**
     * Constructs a new complex number from the given real and imaginary part.
     *
     * @param re real part of the complex number
     * @param im imaginary part of the complex number
     */
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Returns the module of the complex number (distance from the origin, |a+bi| = sqrt(a^2 + b^2)).
     *
     * @return module of the complex number
     */
    public double module() {
        return Math.sqrt(re*re + im*im);
    }

    /**
     * Multiplies this complex number with the given complex number.
     *
     * @param c complex number to multiply with
     * @return the result of the multiplication
     */
    public Complex multiply(Complex c) {
        return new Complex(this.re*c.re - this.im*c.im, this.re*c.im + this.im*c.re);
    }

    /**
     * Divides this complex number with the given complex number.
     *
     * @param c complex number to divide with
     * @return the result of the division
     * @throws IllegalArgumentException if the given complex number is zero
     */
    public Complex divide(Complex c) {
        if (c.re == 0 && c.im == 0) throw new IllegalArgumentException("Division by zero.");
        double denominator = c.re*c.re + c.im*c.im;
        return new Complex((this.re*c.re + this.im*c.im)/denominator, (this.im*c.re - this.re*c.im)/denominator);
    }

    /**
     * Adds this complex number with the given complex number.
     *
     * @param c complex number to add with
     * @return the result of the addition
     */
    public Complex add(Complex c) {
        return new Complex(this.re + c.re, this.im + c.im);
    }

    /**
     * Subtracts this complex number with the given complex number.
     *
     * @param c complex number to subtract with
     * @return the result of the subtraction
     */
    public Complex sub(Complex c) {
        return new Complex(this.re - c.re, this.im - c.im);
    }

    /**
     * Returns the negation of this complex number.
     *
     * @return the result of the negation
     */
    public Complex negate() {
        return new Complex(-this.re, -this.im);
    }

    /**
     * Returns the complex number raised to the given power.
     * Can only raise to non-negative integers.
     *
     * @param n power to raise the complex number to
     * @return the result of the power
     * @throws IllegalArgumentException if the given power is negative
     */
    public Complex power(int n) {
        if (n < 0) throw new IllegalArgumentException("Exponent must be non-negative integer.");
        double r = Math.pow(this.module(), n);
        double angle =  Math.atan2(this.im, this.re);
        return new Complex(r * Math.cos(angle * n), r * Math.sin(angle * n));
    }

    /**
     * Returns the n-th root of this complex number.
     * Can only return roots of positive integers.
     *
     * @param n root to calculate
     * @return the result of the root
     * @throws IllegalArgumentException if the given root is non-positive
     */
    public List<Complex> root(int n) {
        if (n <= 0) throw new IllegalArgumentException("Root must be positive integer.");
        double r = Math.pow(this.module(), 1.0/n);
        double theta = Math.atan2(this.im, this.re);
        ArrayList<Complex> roots = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double angle = (theta + 2*i*Math.PI) / n;
            roots.add(new Complex(r * Math.cos(angle), r * Math.sin(angle)));
        }
        return roots;
    }

    /**
     * Returns the string representation of this complex number.
     * Format: a+bi, a-bi
     *
     * @return the string representation of this complex number
     */
    @Override
    public String toString() {
        if (this.im < 0) {
            return this.re + "-i" + (-this.im);
        } else {
            return this.re + "+i" + this.im;
        }
    }

    /**
     * Parses the given string into a complex number.
     *
     * @param s string to parse
     * @return the complex number parsed from the given string
     * @throws NullPointerException if the given string is null
     * @throws NumberFormatException if the given string is not parsable into a complex number
     */
    public static Complex parseComplex(String s) throws NumberFormatException {
        String regex = "\\s*[+-]?\\s*i\\s*";
        String[] split = s.strip().split(regex);
        int p = 1;
        switch (split.length) {
            case 0 -> {
                if (s.indexOf('-') == 0) {
                    p = -1;
                }
                return new Complex(0, p);
            }
            case 1 -> {
                if (s.isEmpty()) {
                    throw new IllegalArgumentException("Invalid complex number format.");
                }
                if (!s.contains("i")) {
                    p = 0;
                }
                if (s.substring(1).contains("-")) {
                    p = -1 * p;
                }
                return new Complex(Double.parseDouble(split[0]), p);
            }
            case 2 -> {
                if (s.substring(1).contains("-")) {
                    p = -1;
                }
                return new Complex(Double.parseDouble(split[0]), p * Double.parseDouble(split[1]));
            }
            default -> throw new IllegalArgumentException("Invalid complex number format.");
        }
    }
}
