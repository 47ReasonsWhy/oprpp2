package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A web worker that creates an HTML page with a greeting message and the current time.
 * The greeting message depends on the name parameter sent in the request
 * by the standard by which GET parameters are usually sent.
 * If the name parameter is not sent, the worker will inform the user that they did not send their name.
 * Otherwise, the worker will inform the user of the length of their name.
 *
 * @see IWebWorker
 * @see RequestContext
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class HelloWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        context.setMimeType("text/html");
        String name = context.getParameter("name");
        try {
            context.write("<html><body>");
            context.write("<h1>Hello!!!</h1>");
            context.write("<p>Now is: "+sdf.format(now)+"</p>");
            if (name == null || name.trim().isEmpty()) {
                context.write("<p>You did not send me your name!</p>");
            } else {
                context.write("<p>Your name has " + name.trim().length()
                        +" letters.</p>");
            }
            context.write("</body></html>");
        } catch (IOException ex) {
            // Log exception to servers log...
            // ex.printStackTrace();
            System.err.println("HelloWorker stumbled upon an I/O error while trying to write to the context: "
                    + ex.getMessage());
        }
    }
}
