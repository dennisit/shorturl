package info.sonicxp.shorturl.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.netease.mail.toolbox.simpledao.BaseDAO;
import com.netease.mail.toolbox.simpledao.ConnectDB;
import com.netease.mail.toolbox.simpledao.RowHandler;

/**
 * @author Sonic
 */
public class UrlDao extends BaseDAO {

    private static final String GET_LONG_URL = "select orig from url where short=?";

    private static final String GET_SHORT_URL = "select short from url where orig=?";

    private static final String SET_LONG_URL = "insert into url(short, orig) values(?,?)";

    public UrlDao(ConnectDB connectDb) {
        super.setConnectDB(connectDb);
    }

    public String getLongUrl(String s) throws SQLException {
        List<String> urls = super.executeQuery(GET_LONG_URL, new Object[] { s }, new RowHandler<String>() {
            @Override
            public String processRow(ResultSet rs) throws SQLException {
                return rs.getString(1);
            }
        });

        if (urls.size() < 1) {
            return null;
        }

        String orig = urls.get(0);
        StringBuilder sb = new StringBuilder(orig);
        return sb.reverse().toString();
    }

    public void setLongUrl(String s, String l) throws SQLException {
        StringBuilder sb = new StringBuilder(l);
        super.executeInsert(SET_LONG_URL, new Object[] { s, sb.reverse().toString() });
    }

    public String getShortUrl(String l) throws SQLException {
        String dbLong = new StringBuilder(l).reverse().toString();
        List<String> urls = super.executeQuery(GET_SHORT_URL, new Object[] { dbLong }, new RowHandler<String>() {
            @Override
            public String processRow(ResultSet resultSet) throws SQLException {
                return resultSet.getString(1);
            }
        });

        if (urls.size() < 1) {
            return null;
        }
        return urls.get(0);
    }

}
