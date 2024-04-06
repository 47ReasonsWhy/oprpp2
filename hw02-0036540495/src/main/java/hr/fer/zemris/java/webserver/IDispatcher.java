package hr.fer.zemris.java.webserver;

/**
 * Represents a request dispatcher.
 * Used to dispatch requests to web workers.
 *
 * @see SmartHttpServer
 * @see RequestContext
 * @see IWebWorker
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public interface IDispatcher {
    /**
     * Dispatches the request to the web worker.
     *
     * @param urlPath path of the request
     * @throws Exception if an error occurs while dispatching the request
     */
    void dispatchRequest(String urlPath) throws Exception;
}
