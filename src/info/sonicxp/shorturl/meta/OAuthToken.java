package info.sonicxp.shorturl.meta;

/**
 * @author sonic
 */
public class OAuthToken {

    private String token;

    private String secret;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public OAuthToken(String token, String secret) {
        this.token = token;
        this.secret = secret;
    }

}
