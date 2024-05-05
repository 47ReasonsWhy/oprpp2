package hr.fer.oprpp2.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web listener that sets the application start time attribute in the servlet context (application scope).
 */
@WebListener
public class AppInfoListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Long appStartTime = System.currentTimeMillis();
        sce.getServletContext().setAttribute("appStartTime", appStartTime);
    }
}
