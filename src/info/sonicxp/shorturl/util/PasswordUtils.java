package info.sonicxp.shorturl.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

/**
 * @author Sonic
 */
public class PasswordUtils {

    public static final Logger logger = Logger.getLogger(PasswordUtils.class);

    public static String encodePassword(String p) {
        MessageDigest sha1, md5;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("No such algorithm!", e);
            return "";
        }

        try {
            byte[] sha1bytes = sha1.digest(p.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(new String(
                    Hex.encodeHex(sha1bytes)));
            byte[] md5bytes = md5.digest(sb.reverse().toString()
                    .getBytes("UTF-8"));
            return new String(Hex.encodeHex(md5bytes, true));
        } catch (UnsupportedEncodingException e) {
            logger.error("Not support UTF-8!", e);
            return "";
        }
    }

}
