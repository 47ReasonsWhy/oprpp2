package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * A web worker that calculates the sum of two numbers passed to it as parameters.
 * The worker then dispatches the request to the script calc.smscr,
 * which displays the two numbers that were added and their sum,
 * and a different image based on the parity of the sum.
 *
 * @see IWebWorker
 * @see RequestContext
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class SumWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        int a = 1;
        int b = 2;
        try {
            a = Integer.parseInt(context.getParameter("a"));
        } catch (NumberFormatException ignored) {
        }
        try {
            b = Integer.parseInt(context.getParameter("b"));
        } catch (NumberFormatException ignored) {
        }

        int sum = a + b;

        context.setTemporaryParameter("zbroj", String.valueOf(sum));
        context.setTemporaryParameter("varA", String.valueOf(a));
        context.setTemporaryParameter("varB", String.valueOf(b));

        context.setTemporaryParameter("imgName", sum % 2 == 1 ? "images/ein.jpg" : "images/jake.gif");

        context.getDispatcher().dispatchRequest("/private/pages/calc.smscr");
    }
}
