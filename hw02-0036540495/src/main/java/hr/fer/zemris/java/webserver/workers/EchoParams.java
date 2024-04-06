package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * A web worker that echoes the parameters passed to it in an HTML table.
 * The table has two columns: one for the parameter name and one for the parameter value.
 *
 * @see IWebWorker
 * @see RequestContext
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class EchoParams implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<html lang=\"en\">");
        sb.append("<head>")
                .append("<title>Echo parameters</title>")
                .append("<meta charset=\"UTF-8\" />")
                .append("</head>");

        sb.append("<body>");
        sb.append("<h1>Parameters:</h1>");
        sb.append("<table border=\"1\" cellpadding=\"5\">");

        sb.append("<thead>");
        sb.append("<tr><th>Name</th><th>Value</th></tr>");
        sb.append("</thead>");

        sb.append("<tbody>");
        for (String name : context.getParameterNames()) {
            sb.append("<tr><td>").append(name).append("</td><td>").append(context.getParameter(name)).append("</td></tr>");
        }
        sb.append("</tbody>");

        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");

        sb.append("\n");

        context.setStatusCode(200);
        context.setStatusText("OK");
        context.setMimeType("text/html");
        context.setContentLength((long) sb.toString().getBytes().length);
        context.write(sb.toString());
    }
}
