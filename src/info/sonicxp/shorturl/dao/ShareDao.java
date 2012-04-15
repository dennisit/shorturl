package info.sonicxp.shorturl.dao;

import info.sonicxp.shorturl.meta.OAuthToken;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.netease.mail.toolbox.simpledao.BaseDAO;
import com.netease.mail.toolbox.simpledao.ConnectDB;
import com.netease.mail.toolbox.simpledao.RowHandler;

/**
 * @author sonic
 */
public class ShareDao extends BaseDAO {

    private static final String GET_TOKEN = "select token, secret from share where user=? and type=?";

    private static final String ADD_TOKEN = "insert into share(user, type, token, secret) values (?,?,?,?)";

    public ShareDao(ConnectDB connectDb) {
        super.setConnectDB(connectDb);
    }

    public OAuthToken getToken(String user, String type) throws SQLException {
        List<OAuthToken> data = super.executeQuery(GET_TOKEN, new Object[] {
            user, type }, new RowHandler<OAuthToken>() {
            @Override
            public OAuthToken processRow(ResultSet rs) throws SQLException {
                return new OAuthToken(rs.getString(1), rs.getString(2));
            }
        });
        return data == null ? null : data.get(0);
    }

    public boolean addToken(String user, String type, OAuthToken token)
            throws SQLException {
        int result = super.executeUpdate(ADD_TOKEN, new Object[] { user, type,
            token.getToken(), token.getSecret() });
        return result == 1;
    }
}
