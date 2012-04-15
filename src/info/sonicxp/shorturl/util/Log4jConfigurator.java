package info.sonicxp.shorturl.util;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * @author Sonic
 */
public class Log4jConfigurator {

    public static void configurate(String path) {
        if (path != null) {
            if (path.endsWith(".properties")) {
                PropertyConfigurator.configureAndWatch(path);
            } else if (path.endsWith(".xml")) {
                DOMConfigurator.configureAndWatch(path);
            } else {
                System.err.println("log4j initialize failed, path: " + path);
            }
        } else {
            System.err.println("log4j initialize failed, path: null");
        }
    }
}
