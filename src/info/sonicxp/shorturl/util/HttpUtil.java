package info.sonicxp.shorturl.util;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sonic
 */
public class HttpUtil {

    /**
     * 获取请求URL
     * 
     * @param request
     * @return
     */
    public static String getRequestUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        if (scheme == null || scheme.equals("")) {
            scheme = "http";
        }
        String host = request.getServerName();
        if (host == null || host.equals("")) {
            host = "UnknownHost";
        }
        int port = request.getServerPort();
        String uri = request.getRequestURI();
        if (uri == null || uri.equals("")) {
            uri = "/";
        }

        return new StringBuilder(scheme).append("://").append(host).append(":")
                .append(port).append(uri).toString();
    }

    /**
     * 获取完整URL
     * 
     * @param req
     * @return
     */
    public static String getRequestFullURL(HttpServletRequest req) {
        String url = getRequestUrl(req);
        String query = req.getQueryString();
        return url + (query != null ? ("?" + query) : "");
    }

    /**
     * 取得字符串参数
     * 
     * @param req
     * @param cmd
     * @return
     */
    public static String getStringParameter(HttpServletRequest req, String cmd) {
        String x = req.getParameter(cmd);
        if (x != null) {
            x = x.trim().toLowerCase();
        }
        if (x == null || x.equals("")) {
            return null;
        }
        return x;
    }

    /**
     * 取得字符串参数(不改为小写)
     * 
     * @param req
     * @param cmd
     * @return
     */
    public static String getStringParameterWithoutToLower(
            HttpServletRequest req, String cmd) {
        String x = req.getParameter(cmd);
        if (x != null) {
            x = x.trim();
        }
        if (x == null || x.equals("")) {
            return null;
        }
        return x;
    }

    /**
     * 读POST请求Body
     * 
     * @param req
     * @return
     * @throws IOException
     */
    public static String getRequestBody(HttpServletRequest req)
            throws IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int len;
        while ((len = reader.read(buf, 0, buf.length)) != -1) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

}
