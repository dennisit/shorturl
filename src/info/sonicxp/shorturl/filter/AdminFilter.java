package info.sonicxp.shorturl.filter;

import info.sonicxp.shorturl.dao.DaoFactory;
import info.sonicxp.shorturl.util.Base64;
import info.sonicxp.shorturl.util.PasswordUtils;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Sonic
 */
public class AdminFilter implements Filter {

    private static final Logger logger = Logger.getLogger(AdminFilter.class);

    public static final String UID_ATTRIBUTE = "__uid__";

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;
        String uri = httpReq.getRequestURI();
        if (uri.startsWith(path)) {
            // 需要验证
            String auth = httpReq.getHeader("Authorization");
            if (auth == null || auth.length() == 0) {
                httpResp.setHeader("WWW-Authenticate",
                        "Basic realm=\"url shortener\"");
                httpResp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String[] p = StringUtils.split(auth, ' ');
            if (p.length != 2 || !p[0].equals("Basic")) {
                httpResp.setHeader("WWW-Authenticate",
                        "Basic realm=\"url shortener\"");
                httpResp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // 用户名密码验证
            byte[] ds = Base64.decode(p[1]);
            if (ds != null) {
                String userpass = new String(ds, "UTF-8");
                String user = StringUtils.substringBefore(userpass, ":");
                String pass = StringUtils.substringAfter(userpass, ":");
                String encode = PasswordUtils.encodePassword(pass);
                try {
                    if (DaoFactory.getUserDao().checkPassword(user, encode)) {
                        // 成功
                        httpReq.setAttribute(UID_ATTRIBUTE, user);
                        chain.doFilter(req, resp);
                        return;
                    }
                } catch (SQLException e) {
                    logger.error("check password failed.", e);
                    httpResp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
            }

            httpResp.setHeader("WWW-Authenticate",
                    "Basic realm=\"url shortener\"");
            httpResp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } else {
            chain.doFilter(req, resp);
            return;
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        this.path = arg0.getServletContext().getContextPath() + "/!";
    }

    private String path;
}
