package info.sonicxp.shorturl.service;

import info.sonicxp.shorturl.dao.DaoFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Sonic
 */
public class WebService extends HttpServlet {

    private static final long serialVersionUID = 5104343373727150896L;

    private static final Logger logger = Logger.getLogger(WebService.class);

    private final Map<String, Method> requestMapping = new HashMap<String, Method>();

    private String path;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        path = config.getServletContext().getContextPath();

        try {
            for (Method m: WebService.class.getMethods()) {
                if (m.isAnnotationPresent(RequestMapping.class)) {
                    String url = m.getAnnotation(RequestMapping.class).value();
                    requestMapping.put((path + url).toLowerCase(), m);
                }
            }
        } catch (SecurityException e) {
            logger.error("WebService getMethods failed.", e);
        }

        logger.info("WebService servlet loaded.");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        Method method = requestMapping.get(uri.toLowerCase());

        // 短链接
        if (method == null) {
            String sstr = StringUtils.substringAfter(uri, path + "/");
            RequestWrapper rw = new RequestWrapper(req, resp, sstr);
            String orig = null;
            try {
                orig = DaoFactory.getUrlDao().getLongUrl(sstr);
            } catch (SQLException e) {
                rw.getLogger().error(logger, "get long url failed", e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            if (orig != null) {
                rw.getLogger().info(logger, "REDIRECT:" + orig);
                resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                resp.setHeader("Location", orig);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                rw.getLogger().error(logger, uri + " not found!");
            }
            return;
        }

        // 设置接口
        RequestWrapper rw = new RequestWrapper(req, resp, method.getName());
        String result = null;
        try {
            result = (String) method.invoke(this, rw);
        } catch (IllegalArgumentException e) {
            rw.getLogger().error(logger, "invoke error.", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (IllegalAccessException e) {
            rw.getLogger().error(logger, "invoke error.", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (InvocationTargetException e) {
            rw.getLogger().error(logger, "invoke method throws exception", e);
            throw new ServletException(e.getTargetException());
        }

        if (result != null) {
            rw.setResultData(result);
            rw.getLogger().info(logger, "RET:" + result);
        }
    }

    @RequestMapping("/!f/add.do")
    public String addUrl(RequestWrapper rw) {
        String orig = rw.getParameterOrig("url");
        String sstr = rw.getParameterOrig("short");

        if (orig == null) {
            return new JSONObject().element("code", 400)
                    .element("msg", "url can't be null").toString();
        }

        try {
            orig = URLDecoder.decode(orig, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            rw.getLogger().error(logger, "add url failed", e);
            return new JSONObject().element("code", 500).toString();
        }

        boolean genFlag = false;
        if (sstr == null) {
            genFlag = true;
            sstr = generateRandomUrl();
        }

        try {
            DaoFactory.getUrlDao().setLongUrl(sstr, orig);
        } catch (SQLException e) {
            // 可能是短Url重复，若自动生成则重试一次，否则返回错误
            if (!genFlag) {
                return new JSONObject().element("code", 400)
                        .element("msg", "duplicated short url").toString();
            } else {
                sstr = generateRandomUrl();
                try {
                    DaoFactory.getUrlDao().setLongUrl(sstr, orig);
                } catch (SQLException e1) {
                    rw.getLogger().error(logger, "add url failed", e1);
                    return new JSONObject().element("code", 500).toString();
                }
            }
        }

        return new JSONObject().element("code", 200).element("short", sstr)
                .toString();
    }

    private static final String shortKeys = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_~";

    private static final int shortLength = 6;

    private String generateRandomUrl() {
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < shortLength; ++i) {
            sb.append(shortKeys.charAt(rnd.nextInt(shortKeys.length())));
        }
        return sb.toString();
    }

}
