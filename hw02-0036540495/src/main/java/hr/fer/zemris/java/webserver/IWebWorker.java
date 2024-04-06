package hr.fer.zemris.java.webserver;

/**
 * Functional interface that represents a "web worker".
 * A web worker is any object that can process a request
 * and is expected to create content for the client.
 *
 * @see RequestContext
 * @see SmartHttpServer
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public interface IWebWorker {
    /**
     * Processes the request and creates content for the client.
     *
     * @param context request context
     * @throws Exception if an error occurs while processing the request
     */
    void processRequest(RequestContext context) throws Exception;
}
