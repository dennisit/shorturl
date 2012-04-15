package info.sonicxp.shorturl.dao;

import java.util.Properties;

import com.netease.mail.toolbox.simpledao.ConnectDB;
import com.netease.mail.toolbox.simpledao.impl.ProxoolConnectDB;

/**
 * @author Sonic
 */
public class DaoFactory {

    private static ProxoolConnectDB connectDb = null;

    public static synchronized ConnectDB getConnectDB() {
        if (connectDb == null) {
            String user = "shorturl";
            String pass = "shorturl";
            String url = "jdbc:mysql://127.0.0.1/shorturl?useUnicode=true&characterEncoding=utf-8";
            connectDb = new ProxoolConnectDB();
            connectDb.setAlias("shorturl");
            connectDb.setUrl(url);
            connectDb.setDriverClassName("com.mysql.jdbc.Driver");
            Properties info = new Properties();
            info.setProperty("user", user);
            info.setProperty("password", pass);
            info.setProperty("proxool.maximum-connection-count", "30");
            connectDb.setInfo(info);
        }
        return connectDb;
    }

    public static UrlDao getUrlDao() {
        return new UrlDao(getConnectDB());
    }

    public static UserDao getUserDao() {
        return new UserDao(getConnectDB());
    }
}
