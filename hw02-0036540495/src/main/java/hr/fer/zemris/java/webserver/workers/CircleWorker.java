package hr.fer.zemris.java.webserver.workers;

import hr.fer.zemris.java.webserver.IWebWorker;
import hr.fer.zemris.java.webserver.RequestContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A web worker that creates a PNG image of a red circle with a white background (hmm, sounds familiar...).
 *
 * @see IWebWorker
 * @see RequestContext
 *
 * @version 1.0
 * @author Marko Šelendić
 */
public class CircleWorker implements IWebWorker {
    @Override
    public void processRequest(RequestContext context) {
        BufferedImage bim = new BufferedImage(300, 200, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = bim.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 300, 200);
        g2d.setColor(Color.RED);
        g2d.fillOval(90, 40, 120, 120);
        g2d.dispose();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bim, "png", bos);
            context.setStatusCode(200);
            context.setStatusText("OK");
            context.setMimeType("image/png");
            context.setContentLength((long) bos.size());
            context.write(bos.toByteArray());
        } catch (IOException e) {
            // e.printStackTrace();
            System.err.println("CircleWorker stumbled upon an I/O error while trying to write to the context: "
                    + e.getMessage());
            context.setStatusCode(500);
            context.setStatusText("Internal Server Error");
        }
    }
}
