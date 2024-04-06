package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * A web worker that sets the background color of the page to the color specified in the request parameter "bgcolor".
 * The color must be a 6-digit hexadecimal number (without the leading hashtag "#", e.g. "FF0000" for red).
 * If the color is valid, the background color is stored in the persistent parameters of the context.
 * Otherwise, the background color will not be changed.
 *
 *
 * @see IWebWorker
 * @see RequestContext
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class BgColorWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        String bgcolor = context.getParameter("bgcolor");
        String message = "Background color not updated.";
        String info = "Check if the given color is a 6-digit hexadecimal number" +
                " (without the leading hashtag \"#\", e.g. \"FF0000\" for red).";
        if (bgcolor != null && bgcolor.matches("[0-9a-fA-F]{6}")) {
            context.setPersistentParameter("bgcolor", bgcolor);
            message = "Background color updated.";
            info = "The new background color is: #" + bgcolor;
        }

        StringBuilder sb = new StringBuilder();

        sb.append("<html lang=\"hr\">");

        sb.append("<head>");
        sb.append("<title>Background color setter</title>");
        sb.append("<meta charset=\"UTF-8\" />");
        sb.append("<style>" + "body { background-color: #").append(context.getPersistentParameter("bgcolor")).append("; }")
                .append("a { color:#0000FF; }")
                .append("a:hover { color:#FFFF00; }")
                .append("</style>");
        sb.append("</head>");

        sb.append("<body>");
        sb.append("<h1>").append(message).append("</h1>");
        sb.append("<p>").append(info).append("</p>");
        sb.append("<a href=\"/index2.html\">Back to index</a>");
        sb.append("</body>");

        sb.append("</html>");
        sb.append("\r\n");

        context.setStatusCode(200);
        context.setStatusText("OK");
        context.setMimeType("text/html");
        context.setContentLength((long) sb.toString().getBytes().length);
        context.write(sb.toString());
    }
}
