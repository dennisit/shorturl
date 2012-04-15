package info.sonicxp.shorturl.service;

import info.sonicxp.shorturl.util.Log4jConfigurator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * @author Sonic
 */
public class WebListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(WebListener.class);

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        logger.info("context destroyed.");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        ServletContext ctx = arg0.getServletContext();
        Log4jConfigurator.configurate(ctx.getRealPath(ctx
                .getInitParameter("log4j")));
        logger.info("context initialized.");
    }

}
