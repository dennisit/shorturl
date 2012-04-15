package info.sonicxp.shorturl.dao;

import java.sql.SQLException;

import com.netease.mail.toolbox.simpledao.BaseDAO;
import com.netease.mail.toolbox.simpledao.ConnectDB;

/**
 * @author Sonic
 */
public class UserDao extends BaseDAO {

    private static final String CHECK_PASSWORD = "select count(*) from user where user=? and pass=?";

    public UserDao(ConnectDB connectDb) {
        super.setConnectDB(connectDb);
    }

    public boolean checkPassword(String user, String pass) throws SQLException {
        return super.executeQueryCount(CHECK_PASSWORD, new Object[] { user,
            pass }) >= 1;
    }
}
