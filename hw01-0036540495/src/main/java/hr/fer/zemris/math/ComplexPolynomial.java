package hr.fer.zemris.math;

import java.util.Arrays;
import java.util.List;

/**
 * A class representing a complex polynomial of the form
 * <p>
 * f(z) = z_n * z^n + z_(n-1) * z^n-1 + ... + z_2 * z^2 + z_1 * z + z_0
 * <p>
 * where z_n, z_(n-1), ..., z_2, z_1, z_0 are complex numbers.
 * <p>
 * Provides methods for multiplying, deriving and applying the polynomial.
 *
 * @see Complex
 * @see ComplexRootedPolynomial
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class ComplexPolynomial {
    /**
     * List of factors z_0, z_1, ..., z_n of the polynomial.
     */
    private final List<Complex> factors;

    /**
     * Returns the list of factors of the polynomial.
     *
     * @return list of factors of the polynomial
     */
    public List<Complex> getFactors() {
        return factors;
    }

    /**
     * Constructs a new {@code ComplexPolynomial} from the given factors.
     *
     * @param factors list of factors of the polynomial
     */
    public ComplexPolynomial(Complex ...factors) {
        this.factors = List.of(factors);
    }

    /**
     * Returns the order of the polynomial.
     *
     * @return order of the polynomial
     */
    public short order() {
        return (short) (factors.size() - 1);
    }

    /**
     * Multiplies this polynomial with the given polynomial.
     *
     * @param p polynomial to multiply with
     * @return result of the multiplication
     */
    public ComplexPolynomial multiply(ComplexPolynomial p) {
        Complex[] newFactors = new Complex[this.order() + p.order() + 1];
        Arrays.fill(newFactors, Complex.ZERO);
        for (int i = 0; i < this.factors.size(); i++) {
            for (int j = 0; j < p.factors.size(); j++) {
                newFactors[i + j] = newFactors[i + j].add(this.factors.get(i).multiply(p.factors.get(j)));
            }
        }
        return new ComplexPolynomial(newFactors);
    }

    /**
     * Computes the first derivative of this polynomial.
     *
     * @return first derivative of this polynomial
     */
    public ComplexPolynomial derive() {
        Complex[] newFactors = new Complex[this.order()];
        for (int i = 0; i < newFactors.length; i++) {
            newFactors[i] = this.factors.get(i + 1).multiply(new Complex(i + 1, 0));
        }
        return new ComplexPolynomial(newFactors);
    }

    /**
     * Computes the value of this polynomial at the given point.
     *
     * @param z point to compute the value at
     * @return value of this polynomial at the given point
     */
    public Complex apply(Complex z) {
        Complex result = Complex.ZERO;
        for (int i = 0; i < this.factors.size(); i++) {
            result = result.add(this.factors.get(i).multiply(z.power(i)));
        }
        return result;
    }

    /**
     * Returns the string representation of this polynomial.
     * <p>
     * Format: (z_n) * z^n + (z_(n-1)) * z^n-1 + ... + (z_2) * z^2 + (z_1) * z + (z_0)
     *
     * @return string representation of this polynomial
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = this.factors.size() - 1; i >= 0; i--) {
            sb.append("(").append(this.factors.get(i)).append(")");
            if (i != 0) {
                sb.append("*z^").append(i).append("+");
            }
        }
        return sb.toString();
    }
}
