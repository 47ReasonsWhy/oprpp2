package hr.fer.zemris.math;

import java.util.List;

/**
 * A class representing a complex polynomial in its rooted form:
 * <p>
 * f(z) = z_0 * (z-z_1) * (z-z_2) * ... * (z-z_n)
 * <p>
 * where z_0, z_1, ..., z_n are complex numbers,
 * and z_1, z_2, ..., z_n are roots of the polynomial.
 * <p>
 * Provides methods for applying the polynomial and converting it to {@link ComplexPolynomial} type.
 * 
 * @see Complex
 * @see ComplexPolynomial
 * 
 * @version 1.0
 * @author Marko Šelendić
 */
public class ComplexRootedPolynomial {
    /**
     * Constant factor z_0 of the polynomial.
     */
    private final Complex constant;
    
    /**
     * List of roots z_1, z_2, ..., z_n of the polynomial.
     */
    private final List<Complex> roots;

    /**
     * Returns the constant factor z_0 of the polynomial.
     * 
     * @return constant factor z_0 of the polynomial
     */
    public Complex getConstant() {
        return constant;
    }

    /**
     * Returns the list of roots z_1, z_2, ..., z_n of the polynomial.
     * 
     * @return list of roots z_1, z_2, ..., z_n of the polynomial
     */
    public List<Complex> getRoots() {
        return roots;
    }

    /**
     * Constructs a new {@code ComplexRootedPolynomial} from the given constant and roots.
     * 
     * @param constant constant factor z_0 of the polynomial
     * @param roots list of roots z_1, z_2, ..., z_n of the polynomial
     */
    public ComplexRootedPolynomial(Complex constant, Complex ... roots) {
        this.constant = constant;
        this.roots = List.of(roots);
    }

    /**
     * Computes polynomial value at given point z.
     * 
     * @param z point at which to compute the polynomial value
     * @return polynomial value at given point z
     */
    public Complex apply(Complex z) {
        Complex result = constant;
        for (Complex root : roots) {
            result = result.multiply(z.sub(root));
        }
        return result;
    }

    /**
     * Converts this polynomial to {@link ComplexPolynomial} type.
     *
     * @return {@link ComplexPolynomial} type of this polynomial
     */
    public ComplexPolynomial toComplexPolynomial() {
        // result = z0 * (-z1 + z^1) * (-z2 + z^1) * (-z3 + z^1) * ...
        ComplexPolynomial result = new ComplexPolynomial(constant);
        for (Complex root : roots) {
            result = result.multiply(new ComplexPolynomial(root.negate(), Complex.ONE));
        }
        return result;
    }

    /**
     * Returns a string representation of this polynomial.
     * <p>
     * Format: (z_0) * (z-(z_1)) * (z-(z_2)) * ... * (z-(z_n))
     *
     * @return a string representation of this polynomial
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(constant).append(")");
        for (Complex root : roots) {
            sb.append("*(z-(").append(root).append("))");
        }
        return sb.toString();
    }

    /**
     * Computes index of closest root for given complex number z that is within the given threshold.
     * <p>
     * If there is no such root, returns -1.
     *
     * @param z         complex number for which to compute the index of closest root
     * @param threshold threshold within which the root must be
     * @return index of closest root for given complex number z that is within the given threshold,
     *         or -1 if there is no such root
     */
    public int indexOfClosestRootFor(Complex z, double threshold) {
        int index = -1;
        double minDistance = threshold;
        for (int i = 0; i < roots.size(); i++) {
            double distance = z.sub(roots.get(i)).module();
            if (distance < minDistance) {
                minDistance = distance;
                index = i;
            }
        }
        return index;
    }
}
