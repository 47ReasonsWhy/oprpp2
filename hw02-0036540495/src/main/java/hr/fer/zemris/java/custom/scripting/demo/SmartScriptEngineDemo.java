package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.zemris.java.custom.scripting.exec.SmartScriptEngine;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demonstrates the usage of SmartScriptEngine class.
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class SmartScriptEngineDemo {

    public static void main(String[] args) {
        SmartScriptEngineDemo ssed = new SmartScriptEngineDemo();
        ssed.osnovni("osnovni.smscr");
        System.out.println("\n --- \n");
        ssed.zbrajanje();
        System.out.println("\n --- \n");
        ssed.brojPoziva();
        System.out.println("\n --- \n");
        ssed.osnovni("fibonacci.smscr");
        System.out.println("\n --- \n");
        ssed.osnovni("fibonaccih.smscr");
    }

    private void osnovni(String filename) {
        String documentBody = this.loader(filename);
        Map<String,String> parameters = new HashMap<>();
        Map<String,String> persistentParameters = new HashMap<>();
        List<RequestContext.RCCookie> cookies = new ArrayList<>();
        // create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }

    private void zbrajanje() {
        String documentBody = this.loader("zbrajanje.smscr");
        Map<String,String> parameters = new HashMap<>();
        Map<String,String> persistentParameters = new HashMap<>();
        List<RequestContext.RCCookie> cookies = new ArrayList<>();
        parameters.put("a", "4");
        parameters.put("b", "2");
        // create engine and execute it
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(),
                new RequestContext(System.out, parameters, persistentParameters, cookies)
        ).execute();
    }

    private void brojPoziva() {
        String documentBody = this.loader("brojPoziva.smscr");
        Map<String,String> parameters = new HashMap<>();
        Map<String,String> persistentParameters = new HashMap<>();
        List<RequestContext.RCCookie> cookies = new ArrayList<>();
        persistentParameters.put("brojPoziva", "3");
        RequestContext rc = new RequestContext(System.out, parameters, persistentParameters, cookies);
        new SmartScriptEngine(
                new SmartScriptParser(documentBody).getDocumentNode(), rc
        ).execute();
        System.out.println("Vrijednost u mapi: " + rc.getPersistentParameter("brojPoziva"));
    }

    private String loader(String filename) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String path = "hr/fer/zemris/java/custom/scripting/exec/smscr/";
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(path + filename)) {
            if (is == null) return null;
            byte[] buffer = new byte[1024];
            while (true) {
                int r = is.read(buffer);
                if (r < 1) break;
                bos.write(buffer, 0, r);
            }
            return bos.toString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            return null;
        }
    }
}
