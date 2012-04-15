package info.sonicxp.shorturl.service.thirdparty;

import info.sonicxp.shorturl.dao.DaoFactory;
import info.sonicxp.shorturl.dao.ShareDao;
import info.sonicxp.shorturl.meta.OAuthToken;
import info.sonicxp.shorturl.meta.Pair;
import info.sonicxp.shorturl.meta.ShareType;
import info.sonicxp.shorturl.util.LRUCache;

import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * @author sonic
 */
public class TwitterService {

    private static final Logger logger = Logger.getLogger(TwitterService.class);

    private static final String consumerKey = "b54WCDzjW187gKYWhe9ZQ";

    private static final String consumerSecret = "6CKVrwQ8TH24LH4UFBwAyo3ykfA6sBrn9hxiIp4VqM";

    private LRUCache<String, Pair<Twitter, String>> requestTokenCache;

    private LRUCache<String, Twitter> twitterInstanceCache;

    private ShareDao shareDao;

    @PostConstruct
    void init() {
        twitterInstanceCache = new LRUCache<String, Twitter>(1000);
        requestTokenCache = new LRUCache<String, Pair<Twitter, String>>(1000);
        shareDao = DaoFactory.getShareDao();
    }

    private Twitter getTwitterInstance(AccessToken accessToken) {
        Twitter twitter;
        if (accessToken == null) {
            twitter = new TwitterFactory().getInstance();
        } else {
            twitter = new TwitterFactory().getInstance(accessToken);
        }
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        return twitter;
    }

    private Twitter getTwitterInstance() {
        return getTwitterInstance(null);
    }

    public String getAuthorizeUrl(String user) throws TwitterException {
        Twitter twitter = getTwitterInstance();
        RequestToken requestToken = twitter.getOAuthRequestToken();
        requestTokenCache.put(requestToken.getToken(),
                new Pair<Twitter, String>(twitter, user));
        return requestToken.getAuthorizationURL();
    }

    public void afterAuthorized(String requestTokenString)
            throws TwitterException {
        Pair<Twitter, String> user = requestTokenCache.get(requestTokenString);
        if (user == null) {
            throw new TwitterException("No Such RequestToken.");
        }
        Twitter twitter = user.getFirst();
        AccessToken accessToken = twitter.getOAuthAccessToken();
        try {
            storeAccessToken(user.getSecond(), accessToken);
        } catch (SQLException e) {
            logger.error("Store AccessToken failed.", e);
            throw new TwitterException("Store AccessToken failed.");
        }
    }

    public void updateStatus(String user, String status)
            throws TwitterException {
        Twitter twitter = twitterInstanceCache.get(user);
        if (twitter == null) {
            AccessToken accessToken = getAccessToken(user);
            twitter = getTwitterInstance(accessToken);
            twitterInstanceCache.put(user, twitter);
        }
        twitter.updateStatus(status);
    }

    public boolean checkAuthorized(String user) {
        if (twitterInstanceCache.get(user) != null) {
            return true;
        }
        AccessToken accessToken = getAccessToken(user);
        if (accessToken != null) {
            Twitter twitter = getTwitterInstance(accessToken);
            twitterInstanceCache.put(user, twitter);
            return true;
        }
        return false;
    }

    private void storeAccessToken(String user, AccessToken accessToken)
            throws SQLException {
        OAuthToken token = new OAuthToken(accessToken.getToken(),
                accessToken.getTokenSecret());
        shareDao.addToken(user, ShareType.TWITTER, token);
    }

    private AccessToken getAccessToken(String user) {
        OAuthToken token;
        try {
            token = shareDao.getToken(user, ShareType.TWITTER);
        } catch (SQLException e) {
            logger.error("Get AccessToken failed.", e);
            return null;
        }
        if (token == null) {
            return null;
        }
        return new AccessToken(token.getToken(), token.getSecret());
    }
}
