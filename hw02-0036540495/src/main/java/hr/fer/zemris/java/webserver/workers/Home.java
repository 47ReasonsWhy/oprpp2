package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

/**
 * Home page web worker.
 * Unless set differently by the session, the background color is set to 7F7F7F.
 * The home page is displayed by dispatching the request to the /private/pages/home.smscr script.
 *
 * @see IWebWorker
 * @see RequestContext
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class Home implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) throws Exception {
        String background = context.getPersistentParameter("bgcolor");
        if (background == null) background = "7F7F7F";
        context.setTemporaryParameter("background", background);
        context.getDispatcher().dispatchRequest("/private/pages/home.smscr");
    }
}
