package hr.fer.zemris.java.webserver;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;

/**
 * A simple HTTP server that can serve static files and execute SmartScript scripts and workers in general.
 * All configuration files (server, mime, workers) are located in the config directory.
 * The server supports sessions via cookies. The session lasts for 5 minutes after the last request.
 *
 * @see RequestContext
 * @see IWebWorker
 * @see SmartScriptEngine
 * @see SmartScriptParser
 *
 * @see hr.fer.zemris.java.webserver.workers
 *
 * @version 1.0
 * @author Marko Šelendić
 */

public class SmartHttpServer {
    /**
     * IP address of the server.
     */
    private String address;

    /**
     * Domain name of the server.
     */
    private String domainName;

    /**
     * Port on which the server listens.
     */
    private int port;

    /**
     * Number of worker threads.
     */
    private int workerThreads;

    /**
     * Session timeout in seconds.
     */
    private int sessionTimeout;

    /**
     * Map of supported mime types (extension -> mime type tag for the header).
     */
    private final Map<String,String> mimeTypes = new HashMap<>();

    /**
     * Server thread that listens for incoming requests and dispatches them to worker threads.
     */
    private ServerThread serverThread;

    /**
     * Pool of worker threads.
     */
    private ExecutorService serverWorkerthreadPool;

    /**
     * Pool of threads that clean up expired sessions.
     */
    private ScheduledExecutorService sessionCleanerThreadPool;

    /**
     * Root directory containing files that can be requested,
     * except /documentRoot/private/,
     * which can only be accessed by workers.
     */
    private Path documentRoot;

    /**
     * Map of workers that can be executed.
     */
    private final Map<String,IWebWorker> workersMap = new HashMap<>();

    /**
     * Map of active sessions.
     */
    private final Map<String, SessionMapEntry> sessions = new ConcurrentHashMap<>();

    /**
     * Random number generator for session IDs.
     */
    private final Random sessionRandom = new Random();

    /**
     * Creates and starts a new server with the specified configuration file.
     * Additionally, a session cleaner is started that removes expired sessions every 5 minutes.
     *
     * @param configFileName path to the configuration file
     */
    public SmartHttpServer(String configFileName) {
        Properties properties = new Properties();
        try (InputStream is = Files.newInputStream(Path.of(configFileName))) {
            properties.load(is);
        } catch (IOException e) {
            System.err.println("Error while reading configuration file.");
            return;
        }

        address = properties.getProperty("server.address");
        domainName = properties.getProperty("server.domainName");
        port = Integer.parseInt(properties.getProperty("server.port"));
        workerThreads = Integer.parseInt(properties.getProperty("server.workerThreads"));
        documentRoot = Path.of(properties.getProperty("server.documentRoot"));
        Path mimeConfig = Path.of(properties.getProperty("server.mimeConfig"));
        sessionTimeout = Integer.parseInt(properties.getProperty("session.timeout"));
        Path workers = Path.of(properties.getProperty("server.workers"));

        Properties mimeProperties = new Properties();
        try (InputStream is = Files.newInputStream(mimeConfig)) {
            mimeProperties.load(is);
        } catch (IOException e) {
            System.err.println("Error while reading mime configuration file.");
            return;
        }
        for (String key : mimeProperties.stringPropertyNames()) {
            mimeTypes.put(key, mimeProperties.getProperty(key));
        }

        Properties workersProperties = new Properties();
        try (InputStream is = Files.newInputStream(workers)) {
            workersProperties.load(is);
        } catch (IOException e) {
            System.err.println("Error while reading workers configuration file.");
            return;
        }
        for (String path : workersProperties.stringPropertyNames()) {
            String fqcn = workersProperties.getProperty(path);
            try {
                Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
                Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
                IWebWorker iww = (IWebWorker) newObject;
                workersMap.put(path, iww);
            } catch (ClassNotFoundException |
                     NoSuchMethodException |
                     SecurityException |
                     InstantiationException |
                     IllegalAccessException |
                     IllegalArgumentException |
                     InvocationTargetException e) {
                System.err.println("Error while creating non-ext worker instance.");
            }
        }

        start();
        System.out.println("Server started at http://" + domainName + ":" + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
            System.out.println("Server stopped.");
        }));
    }

    /**
     * Starts the server (if one not already running)
     * and initializes the thread pool (if one does not already exist).
     */
    protected synchronized void start() {

        if (serverThread == null) {
            serverThread = new ServerThread();
            serverThread.start();
        }

        if (serverWorkerthreadPool == null) {
            serverWorkerthreadPool = Executors.newFixedThreadPool(workerThreads);
        }

        if (sessionCleanerThreadPool == null) {
            sessionCleanerThreadPool = Executors.newScheduledThreadPool(1);
            sessionCleanerThreadPool.scheduleAtFixedRate(() -> {
                synchronized (sessions) {
                    sessions.entrySet().removeIf(entry ->
                            entry.getValue().validUntil * 1000 < System.currentTimeMillis()
                    );
                }
            }, 0, 5, TimeUnit.MINUTES);
        }
    }

    /**
     * Stops the server and shuts down the thread pool.
     */
    protected synchronized void stop() {
        serverThread.interrupt();
        serverWorkerthreadPool.shutdown();
        sessionCleanerThreadPool.shutdown();
    }

    /**
     * Server thread that listens for incoming requests and dispatches them to worker threads.
     */
    protected class ServerThread extends Thread {
        @Override
        public void run() {
            // Open serverSocket on specified port
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket client = serverSocket.accept();
                    ClientWorker cw = new ClientWorker(client);
                    serverWorkerthreadPool.submit(cw);
                }
            } catch (IOException e) {
                System.err.println("Error while opening server socket.");
            }
        }
    }

    /**
     * A runnable worker that processes the client request.
     */
    private class ClientWorker implements Runnable, IDispatcher {
        /**
         * Client socket.
         */
        private final Socket csocket;

        /**
         * Input stream from the client socket.
         */
        private InputStream istream;

        /**
         * Output stream to the client socket.
         */
        private OutputStream ostream;

        /**
         * HTTP version (only HTTP/1.1 supported).
         */
        private String version;

        /**
         * HTTP method (only GET supported).
         */
        private String method;

        /**
         * Host name from the request.
         */
        private String host;

        /**
         * Map of normal parameters (request parameters).
         */
        private final Map<String,String> params = new HashMap<>();

        /**
         * Map of temporary parameters (for example set by workers).
         */
        private final Map<String,String> tempParams = new HashMap<>();

        /**
         * Map of persistent parameters (from session).
         */
        private Map<String,String> permParams = new HashMap<>();

        /**
         * List of output cookies.
         */
        private final List<RequestContext.RCCookie> outputCookies = new ArrayList<>();

        /**
         * Session ID.
         */
        private String SID;

        /**
         * Request context.
         */
        private RequestContext context = null;

        /**
         * Creates a new client worker with the specified socket.
         *
         * @param csocket client socket
         */
        public ClientWorker(Socket csocket) {
            super();
            this.csocket = csocket;
        }

        /**
         * Processes the client request.
         * Reads it, extracts headers, checks and sets up the session,
         * fills the map of normal parameters with parameters from the parameter string,
         * and dispatches the request.
         * There are three ways in which the request can be dispatched:
         * <ul>
         *     <li>Execute a worker XXX if the requested path starts with /ext/XXX</li>
         *     <li>Execute a worker if the requested path is mapped to a worker</li>
         *     <li>Return the requested file if it exists and is readable
         *         (if the file is a SmartScript script, then execute it and show the result)</li>
         * </ul>
         */
        @Override
        public void run() {
            // Obtain input stream and output stream from socket
            try {
                istream = csocket.getInputStream();
                ostream = csocket.getOutputStream();
            } catch (IOException e) {
                System.err.println("Error while obtaining input/output stream from socket.");
                return;
            }

            // Then read complete request header from your client in separate method...
            Optional<byte[]> request;
            try {
                request = readGetRequest();
            } catch (IOException e) {
                System.err.println("Error while reading request from client.");
                return;
            }
            if (request.isEmpty()) {
                return;
            }
            String requestStr = new String(request.get(), StandardCharsets.US_ASCII);

            // Extract headers from request
            List<String> headers = extractHeaders(requestStr);

            // If header is invalid (less then a line at least) return response status 400
            String[] firstLine = headers.getFirst().split(" ");
            if(headers.isEmpty() || firstLine.length != 3 ) {
                sendEmptyResponse(ostream, 400, "Bad request");
                return;
            }

            // Extract (method, requestedPath, version) from firstLine
            method = firstLine[0].toUpperCase();
            String requestedPath = firstLine[1];
            version = firstLine[2].toUpperCase();

            // If method is not GET or version not HTTP/1.0 or HTTP/1.1 return response status 400
            if (!method.equals("GET") || (!version.equals("HTTP/1.0") && !version.equals("HTTP/1.1"))) {
                sendEmptyResponse(ostream, 400, "Bad request");
            }
            // Wouldn't it be better to return 405 Method Not Allowed and 505 HTTP Version Not Supported accordingly?

            // Go through headers, and if there is header “Host: xxx”, assign host property
            // to trimmed value after “Host:”; else, set it to server’s domainName.
            // If xxx is of form some-name:number, just remember “some-name”-part.
            for (String line : headers) {
                if (line.startsWith("Host:")) {
                    host = line.substring(5).strip();
                    if (host.contains(":")) {
                        host = host.split(":")[0];
                    }
                    break;
                }
            }
            if (host == null) {
                host = domainName;
            }

            // Split requestedPath to path and parameterString
            String paramString = "";
            if (requestedPath.contains("?")) {
                String[] parts = requestedPath.split("\\?");
                requestedPath = parts[0];
                paramString = parts[1];
            }

            // Check and set up session
            checkSession(headers);

            // Fill the map of normal parameters with parameters from paramString
            try {
                parseParameters(paramString);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid parameter format.");
                sendEmptyResponse(ostream, 400, "Bad request");
                return;
            }

            // Dispatch the request
            try {
                internalDispatchRequest(requestedPath, true);
            } catch (Exception e) {
                System.err.println("Error while dispatching request.");
            }

            // Flush and close the streams
            try {
                ostream.flush();
                ostream.close();
                istream.close();
                csocket.close();
            } catch (IOException e) {
                System.err.println("Error while flushing and closing streams.");
            }
        }

        /**
         * Reads the request from the input stream via implemented state machine.
         *
         * @return list of lines in the request
         * @throws IOException if an error occurs while reading the request
         *
         * @author izv. prof. dr. sc. Marko Čupić
         */
        private Optional<byte[]> readGetRequest() throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int state = 0;
            l: while (true) {
                int b = istream.read();
                if (b == -1) {
                    if(bos.size() != 0) {
                        throw new IOException("Incomplete header received.");
                    }
                    return Optional.empty();
                }
                if (b != 13) {
                    bos.write(b);
                }
                switch (state) {
                    case 0 -> {
                        if (b == 13)      state = 1;
                        else if (b == 10) state = 3;
                    }
                    case 1 -> {
                        if (b == 10)      state = 2;
                        else              state = 0;
                    }
                    case 2 -> {
                        if (b == 13)      state = 3;
                        else              state = 0;
                    }
                    case 3 -> {
                        if (b == 10)      break l;
                        else              state = 0;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + state);
                }
            }
            return Optional.of(bos.toByteArray());
        }

        /**
         * Extracts headers from the GET request.
         *
         * @param requestHeader request header
         * @return list of headers
         */
        private List<String> extractHeaders(String requestHeader) {
            List<String> headers = new ArrayList<>();
            String[] lines = requestHeader.split("\n");

            for (String line : lines) {
                if (line.isEmpty()) break;
                char c = line.charAt(0);
                if (c == 9 || c == 32) { // tab or space
                    headers.set(headers.size() - 1, headers.getLast() + line);
                } else {
                    headers.add(line);
                }
            }

            return headers;
        }

        /**
         * Checks the session by extracting the session ID from the headers.
         * If the session ID is not found, or there is no session mapped to the ID,
         * or the session has expired, a new session is created.
         * In any case, the session is updated to last for 5 minutes after the last request,
         * and the session ID is set as a cookie.
         * If the session is valid, the persistent parameters are set to the session parameters.
         *
         * @param headers list of headers
         * @throws IllegalArgumentException if the cookie format is invalid
         */
        private void checkSession(List<String> headers) {
            synchronized (sessions) {
                String sidCandidate = null;
                x:
                for (String line : headers) {
                    if (!line.startsWith("Cookie:")) {
                        continue;
                    }
                    String[] cookies = line.substring(7).split(";");
                    for (String cookie : cookies) {
                        if (cookie.split("=")[0].strip().equals("sid")) {
                            if (cookie.split("=").length != 2) {
                                throw new IllegalArgumentException("Invalid cookie format.");
                            }
                            sidCandidate = cookie.split("=")[1].replaceAll("\"", "").strip();
                            break x;
                        }
                    }
                }
                if (sidCandidate == null) {
                    createNewSession();
                    return;
                }
                SessionMapEntry session = sessions.get(sidCandidate);
                if (session == null || !session.host.equals(host)) {
                    createNewSession();
                    return;
                }
                if (session.validUntil * 1000 < System.currentTimeMillis()) {
                    sessions.remove(sidCandidate);
                    createNewSession();
                    return;
                }
                session.validUntil = System.currentTimeMillis() / 1000 + sessionTimeout;
                permParams = session.map;
                SID = sidCandidate;
            }
        }

        /**
         * Creates a new session with a random session ID and adds it to the session map.
         * The session lasts for 5 minutes after the last request.
         * The session ID is a 20-character string consisting of ASCII uppercase letters
         * and is set as a cookie.
         */
        private void createNewSession() {
            synchronized (sessions) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 20; i++) {
                    sb.append((char) (sessionRandom.nextInt('Z' - 'A' + 1) + 'A'));
                }
                SID = sb.toString();
                SessionMapEntry session = new SessionMapEntry(
                        SID,
                        host,
                        System.currentTimeMillis() / 1000 + sessionTimeout,
                        new ConcurrentHashMap<>()
                );
                sessions.put(SID, session);
                outputCookies.add(new RequestContext.RCCookie("sid", SID, host, "/", null, true));
            }
        }

        /**
         * Parses the parameter string and updates the map of normal parameters with the parsed parameters.
         *
         * @param paramString parameter string
         * @throws IllegalArgumentException if the parameter format is invalid
         */
        private void parseParameters(String paramString) throws IllegalArgumentException {
            if (paramString.isEmpty()) {
                return;
            }
            if (!paramString.matches("[^=&]+=[^=&]+(&[^=&]+=[^=&]+)*")) {
                throw new IllegalArgumentException("Invalid parameter format.");
            }
            String[] pairs = paramString.split("&");
            for (String pair : pairs) {
                String[] split = pair.split("=");
                params.put(
                        URLDecoder.decode(split[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(split[1], StandardCharsets.UTF_8)
                );
            }
        }

        /**
         * Sends an empty response (a response with empty body) with the specified status code and status text.
         *
         * @param ostream output stream
         * @param statusCode status code
         * @param statusText status text
         */
        private void sendEmptyResponse(OutputStream ostream, int statusCode, String statusText) {
            if (context == null) {
                context = new RequestContext(ostream, params, permParams, outputCookies);
            }
            context.setStatusCode(statusCode);
            context.setStatusText(statusText);

            try {
                context.write("");
                ostream.flush();
                ostream.close();
                csocket.close();
            } catch (IOException ignored) {
            }
        }

        @Override
        public void dispatchRequest(String urlPath) throws Exception {
            internalDispatchRequest(urlPath, false);
        }

        /**
         * Dispatches the request.
         * There are three ways in which the request can be dispatched:
         * <ul>
         *     <li>Execute a worker XXX if the requested path starts with /ext/XXX</li>
         *     <li>Execute a worker if the requested path is mapped to a worker</li>
         *     <li>Return the requested file if it exists and is readable
         *         (if the file is a SmartScript script, then execute it and show the result)</li>
         * </ul>
         *
         * @param urlPath requested path
         * @param directCall if true, signals that the request does not have access to /private/ folder, and
         *                   if false, signals that the request is dispatched from a worker, which has access
         * @throws Exception if an error occurs while dispatching the request
         */
        private void internalDispatchRequest(String urlPath, boolean directCall) throws Exception {

            // Do not let direct calls into /private/ folder
            if (urlPath.startsWith("/private/") && directCall) {
                sendEmptyResponse(ostream, 404, "Not found");
                return;
            }

            // If requestedPath starts with /ext/XXX, assume XXX is the name of an existing worker class,
            // then load it and execute it
            if (urlPath.startsWith("/ext/")) {
                String fqcn = "hr.fer.zemris.java.webserver.workers." + urlPath.substring(5);
                try {
                    Class<?> referenceToClass = this.getClass().getClassLoader().loadClass(fqcn);
                    Object newObject = referenceToClass.getDeclaredConstructor().newInstance();
                    IWebWorker iww = (IWebWorker) newObject;
                    if (context == null) {
                        context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, SID);
                    }
                    iww.processRequest(context);
                    return;
                } catch (ClassNotFoundException e) {
                    System.err.println("Worker class not found.");
                    sendEmptyResponse(ostream, 404, "Not found");
                    return;
                } catch (NoSuchMethodException |
                         SecurityException |
                         InstantiationException |
                         IllegalAccessException |
                         IllegalArgumentException |
                         InvocationTargetException e) {
                    System.err.println("Error while creating ext worker instance.");
                    sendEmptyResponse(ostream, 500, "Internal server error");
                    return;
                }
            }

            // Otherwise, is requestedPath is mapped to a worker, execute the worker
            if (workersMap.containsKey(urlPath)) {
                if (context == null) {
                    context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, SID);
                }
                workersMap.get(urlPath).processRequest(context);
                return;
            }

            // normalizedRequestedPath = resolve path (without the leading slash) with respect to documentRoot
            Path normalizedRequestedPath = documentRoot.resolve(urlPath.substring(1)).normalize();

            // If requestedPath is not below documentRoot, return response status 403 forbidden
            if (!normalizedRequestedPath.startsWith(documentRoot)) {
                sendEmptyResponse(ostream, 403, "Forbidden");
                return;
            }

            // Check if requestedPath exists, is file and is readable; if not, return status 404 not found
            if (!Files.exists(normalizedRequestedPath) || !Files.isReadable(normalizedRequestedPath) || Files.isDirectory(normalizedRequestedPath)) {
                sendEmptyResponse(ostream, 404, "Not found");
                return;
            }

            // Else extract the file extension
            int index = urlPath.lastIndexOf('.');
            String extension = index == -1 ? "" : urlPath.substring(index + 1);

            // Check if extension is .smscr, and if so, execute the script
            if (extension.equals("smscr")) {
                if (context == null) {
                    context = new RequestContext(ostream, params, permParams, outputCookies, tempParams, this, SID);
                }
                new SmartScriptEngine(
                        new SmartScriptParser(Files.readString(normalizedRequestedPath)).getDocumentNode(),
                        context
                ).execute();
                return;
            }

            // Else, find in mimeTypes map appropriate mimeType for current file extension
            // (if no mime type found, assume application/octet-stream)
            String mimeType = mimeTypes.getOrDefault(extension, "application/octet-stream");

            // Set mime-type, set status to 200 OK
            if (context == null) {
                context = new RequestContext(ostream, params, permParams, outputCookies);
            }
            context.setStatusCode(200);
            context.setStatusText("OK");
            context.setMimeType(mimeType);

            // Set content length, open file, read its content and write it to rc
            try {
                context.setContentLength(Files.size(normalizedRequestedPath));
                context.write(Files.readAllBytes(normalizedRequestedPath));
            } catch (IOException e) {
                System.err.println("Error while reading file content.");
            }
        }
    }

    /**
     * Entry in the session map.
     */
    private static class SessionMapEntry {
        /**
         * Session ID.
         */
        String sid;

        /**
         * Host domain name or IP address.
         */
        String host;

        /**
         * Time until the session is valid.
         */
        long validUntil;

        /**
         * Map of session parameters.
         */
        Map<String, String> map;

        /**
         * Creates a new session map entry with the specified parameters.
         *
         * @param sid session ID
         * @param host host domain name or IP address
         * @param validUntil time until the session is valid
         * @param map map of session parameters
         */
        public SessionMapEntry(String sid, String host, long validUntil, Map<String, String> map) {
            this.sid = sid;
            this.host = host;
            this.validUntil = validUntil;
            this.map = map;
        }
    }

    /**
     * Starts the server with configuration file given as the only command-line argument.
     * If no arguments are given, config/server.properties is used.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        String configFileName = args.length == 0 ? "config/server.properties" : args[0];
        new SmartHttpServer(configFileName);
    }
}
