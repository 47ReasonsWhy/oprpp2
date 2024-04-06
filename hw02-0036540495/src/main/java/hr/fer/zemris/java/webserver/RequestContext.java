package hr.fer.zemris.java.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Models the context of an HTTP (GET) request.
 * It is used to generate a response to the client:
 * <ul>
     * <li>set encoding, status code, status text, mime type, content length</li>
     * <li>define parameters, persistent parameters, temporary parameters</li>
     * <li>add cookies and session ID</li>
     * <li>write the body of the response</li>
 * </ul>
 * Used by the {@link SmartHttpServer} to generate a response to the client.
 *
 * @see SmartHttpServer
 * @see IDispatcher
 * @see RCCookie
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class RequestContext {
    /**
     * Represents a cookie that will be sent to the client.
     * It has a name, value, domain, path, max age and http-only flag.
     */
    public record RCCookie(String name, String value, String domain, String path, Integer maxAge, boolean httpOnly) {}

    /**
     * The output stream to write the response to.
     */
    private final OutputStream outputStream;

    /**
     * The charset used to encode the response.
     */
    private Charset charset;

    /**
     * The encoding of the response (UTF-8 by default).
     */
    private String encoding;

    /**
     * The status code of the response (200 by default).
     */
    private int statusCode;

    /**
     * The status text/message of the response (OK by default).
     */
    private String statusText; // statusMessage ?

    /**
     * The mime type of the response (text/html by default).
     */
    private String mimeType;

    /**
     * The content length of the response.
     */
    private Long contentLength;

    /**
     * The parameters of the request (GET parameters).
     */
    private final Map<String, String> parameters;

    /**
     * The temporary parameters of the request (set by workers).
     */
    private Map<String, String> temporaryParameters;

    /**
     * The persistent parameters of the request (from the session).
     */
    private final Map<String, String> persistentParameters;

    /**
     * The cookies that will be sent to the client.
     */
    private final List<RCCookie> outputCookies;

    /**
     * Flag that indicates whether the header has been generated.
     */
    private boolean headerGenerated;

    /**
     * The dispatcher used to dispatch the request.
     */
    private IDispatcher dispatcher;

    /**
     * The session ID.
     */
    private String sid;

    /**
     * Constructs a new {@link RequestContext} with the given parameters.
     *
     * @param outputStream the output stream to write the response to
     * @param parameters the parameters of the request
     * @param persistentParameters the persistent parameters of the request
     * @param outputCookies the cookies that will be sent to the client
     */
    public RequestContext(
            OutputStream outputStream,
            Map<String, String> parameters,
            Map<String, String> persistentParameters,
            List<RCCookie> outputCookies
    ) {
        this.outputStream = outputStream;
        this.encoding = "UTF-8";
        this.statusCode = 200;
        this.statusText = "OK";
        this.mimeType = "text/html";
        this.contentLength = null;
        this.parameters = parameters == null ? new HashMap<>() : parameters;
        this.temporaryParameters = new HashMap<>();
        this.persistentParameters = persistentParameters == null ? new HashMap<>() : persistentParameters;
        this.outputCookies = outputCookies == null ? new ArrayList<>() : outputCookies;
        this.headerGenerated = false;
        this.dispatcher = null;
        this.sid = null;
    }

    /**
     * Constructs a new {@link RequestContext} with the given parameters.
     *
     * @param outputStream the output stream to write the response to
     * @param parameters the parameters of the request
     * @param persistentParameters the persistent parameters of the request
     * @param outputCookies the cookies that will be sent to the client
     * @param temporaryParameters the temporary parameters of the request
     * @param dispatcher the dispatcher used to dispatch the request
     * @param sid the session ID
     */
    public RequestContext(
            OutputStream outputStream,
            Map<String, String> parameters,
            Map<String, String> persistentParameters,
            List<RCCookie> outputCookies,
            Map<String, String> temporaryParameters,
            IDispatcher dispatcher,
            String sid
    ) {
        this(outputStream, parameters, persistentParameters, outputCookies);
        this.temporaryParameters = temporaryParameters;
        this.dispatcher = dispatcher;
        this.sid = sid;
    }

    /**
     * Sets the encoding of the response.
     *
     * @param encoding the encoding of the response
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Sets the status code of the response.
     *
     * @param statusCode the status code of the response
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Sets the status text of the response.
     *
     * @param statusText the status text of the response
     */
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    /**
     * Sets the mime type of the response.
     *
     * @param mimeType the mime type of the response
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Sets the content length of the response.
     *
     * @param contentLength the content length of the response
     */
    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Returns the parameter with the given name.
     *
     * @param name the name of the parameter
     * @return the parameter with the given name
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Returns the names of the parameters as an unmodifiable set.
     *
     * @return the names of the parameters
     */
    public Set<String> getParameterNames() {
        return Collections.unmodifiableSet(parameters.keySet());
    }

    /**
     * Returns the persistent parameter with the given name.
     *
     * @param name the name of the persistent parameter
     * @return the persistent parameter with the given name
     */
    public String getPersistentParameter(String name) {
        return persistentParameters.get(name);
    }

    /**
     * Returns the names of the persistent parameters as an unmodifiable set.
     *
     * @return the names of the persistent parameters
     */
    public Set<String> getPersistentParameterNames() {
        return Collections.unmodifiableSet(persistentParameters.keySet());
    }

    /**
     * Sets the persistent parameter with the given name and value.
     *
     * @param name the name of the persistent parameter
     * @param value the value of the persistent parameter
     */
    public void setPersistentParameter(String name, String value) {
        persistentParameters.put(name, value);
    }

    /**
     * Removes the persistent parameter with the given name.
     *
     * @param name the name of the persistent parameter
     */
    public void removePersistentParameter(String name) {
        persistentParameters.remove(name);
    }

    /**
     * Returns the temporary parameter with the given name.
     *
     * @param name the name of the temporary parameter
     * @return the temporary parameter with the given name
     */
    public String getTemporaryParameter(String name) {
        return temporaryParameters.get(name);
    }

    /**
     * Returns the names of the temporary parameters as an unmodifiable set.
     *
     * @return the names of the temporary parameters
     */
    public Set<String> getTemporaryParameterNames() {
        return Collections.unmodifiableSet(temporaryParameters.keySet());
    }

    /**
     * Sets the temporary parameter with the given name and value.
     *
     * @param name the name of the temporary parameter
     * @param value the value of the temporary parameter
     */
    public void setTemporaryParameter(String name, String value) {
        temporaryParameters.put(name, value);
    }

    /**
     * Removes the temporary parameter with the given name.
     *
     * @param name the name of the temporary parameter
     */
    public void removeTemporaryParameter(String name) {
        temporaryParameters.remove(name);
    }

    /**
     * Adds a new cookie to the response.
     *
     * @param cookie the cookie to add
     */
    public void addRCCookie(RCCookie cookie) {
        outputCookies.add(cookie);
    }

    /**
     * Returns the dispatcher used to dispatch the request.
     *
     * @return the dispatcher used to dispatch the request
     */
    public IDispatcher getDispatcher() {
        return dispatcher;
    }

    /**
     * Returns the session ID.
     *
     * @return the session ID
     */
    public String getSessionID() {
        return sid;
    }

    /**
     * Writes the given data to the output stream.
     * If any write method is called for the first time,
     * the header of the response is generated.
     *
     * @param data the data to write
     * @return this request context
     * @throws IOException if an I/O error occurs
     */
    public RequestContext write(byte[] data) throws IOException {
        return write(data, 0, data.length);
    }

    /**
     * Writes the given data to the output stream.
     * If any write method is called for the first time,
     * the header of the response is generated.
     *
     * @param data the data to write
     * @param offset the offset in the data array
     * @param len the length of the data to write
     * @return this request context
     * @throws IOException if an I/O error occurs
     */
    public RequestContext write(byte[] data, int offset, int len) throws IOException {
        if (!headerGenerated) {
            generateHeader();
        }
        outputStream.write(data, offset, len);
        return this;
    }

    /**
     * Writes the given text to the output stream
     * using the charset encoding of the context.
     * If any write method is called for the first time,
     * the header of the response is generated.
     *
     * @param text the text to write
     * @return this request context
     * @throws IOException if an I/O error occurs
     */
    public RequestContext write(String text) throws IOException {
        if (!headerGenerated) {
            generateHeader();
        }
        outputStream.write(text.getBytes(charset));
        return this;
    }

    /**
     * Generates the header of the response.
     *
     * @throws IOException if an I/O error occurs
     */
    private void generateHeader() throws IOException {
        charset = Charset.forName(encoding);
        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusText).append("\r\n");
        header.append("Content-Type: ").append(mimeType).append(mimeType.startsWith("text/") ? ("; charset=" + encoding) : "").append("\r\n");
        if (contentLength != null) {
            header.append("Content-Length: ").append(contentLength).append("\r\n");
        }
        for (RCCookie cookie : outputCookies) {
            header.append("Set-Cookie: ").append(cookie.name()).append("=\"").append(cookie.value()).append("\"");
            if (cookie.domain() != null) {
                header.append("; Domain=").append(cookie.domain());
            }
            if (cookie.path() != null) {
                header.append("; Path=").append(cookie.path());
            }
            if (cookie.maxAge() != null) {
                header.append("; Max-Age=").append(cookie.maxAge());
            }
            if (cookie.httpOnly()) {
                header.append("; HttpOnly");
            }
            header.append("\r\n");
        }
        header.append("Connection: close\r\n");
        header.append("\r\n");
        outputStream.write(header.toString().getBytes(StandardCharsets.ISO_8859_1));
        headerGenerated = true;
    }
}
