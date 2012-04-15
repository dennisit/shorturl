package info.sonicxp.shorturl.service;

import info.sonicxp.shorturl.filter.AdminFilter;
import info.sonicxp.shorturl.util.HttpUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * @author Sonic
 */
public class RequestWrapper {

    public static class RWLogger {
        private static final Logger log = Logger
                .getLogger(RequestWrapper.class);

        private final long time;

        private final String uid;

        private final String tid;

        private final String cmd;

        public RWLogger(String cmd, String uid) {
            this.cmd = cmd;
            this.uid = uid;
            this.time = System.currentTimeMillis();
            this.tid = Long.toHexString(UUID.randomUUID()
                    .getMostSignificantBits());
        }

        public void debug(Logger logger, String msg) {
            StringBuilder sb = new StringBuilder();
            sb.append("[cmd=").append(cmd).append(", msg=").append(msg)
                    .append(", uid=").append(uid).append(", tid=").append(tid)
                    .append(", st=").append(timeExpend()).append("]");
            logger.debug(sb.toString());
        }

        public void warn(Logger logger, String msg) {
            StringBuilder sb = new StringBuilder();
            sb.append("[cmd=").append(cmd).append(", msg=").append(msg)
                    .append(", uid=").append(uid).append(", tid=").append(tid)
                    .append(", st=").append(timeExpend()).append("]");
            logger.warn(sb.toString());
        }

        public void error(Logger logger, String msg) {
            StringBuilder sb = new StringBuilder();
            sb.append("[cmd=").append(cmd).append(", msg=").append(msg)
                    .append(", uid=").append(uid).append(", tid=").append(tid)
                    .append(", st=").append(timeExpend()).append("]");
            logger.error(sb.toString());
        }

        public void fatal(Logger logger, String msg) {
            StringBuilder sb = new StringBuilder();
            sb.append("[cmd=").append(cmd).append(", msg=").append(msg)
                    .append(", uid=").append(uid).append(", tid=").append(tid)
                    .append(", st=").append(timeExpend()).append("]");
            logger.fatal(sb.toString());
        }

        public void error(Logger logger, String msg, Throwable e) {
            StringBuilder sb = new StringBuilder();
            sb.append("[cmd=").append(cmd).append(", msg=").append(msg)
                    .append(", uid=").append(uid).append(", tid=").append(tid)
                    .append(", st=").append(timeExpend()).append("]")
                    .append(", exception: ");
            logger.error(sb.toString(), e);
        }

        public void info(Logger logger, String msg) {
            StringBuilder sb = new StringBuilder();
            sb.append("[cmd=").append(cmd).append(", msg=").append(msg)
                    .append(", uid=").append(uid).append(", tid=").append(tid)
                    .append(", st=").append(timeExpend()).append("]");
            logger.info(sb.toString());
        }

        public void log(String msg) {
            info(log, msg);
        }

        private long timeExpend() {
            return System.currentTimeMillis() - time;
        }
    }

    private final HttpServletRequest req;

    private final HttpServletResponse resp;

    private final String uid;

    private final RWLogger logger;

    public RequestWrapper(HttpServletRequest req, HttpServletResponse resp,
            String cmd) {
        this.req = req;
        this.resp = resp;

        String uid = (String) req.getAttribute(AdminFilter.UID_ATTRIBUTE);
        uid = (uid == null || uid.length() == 0) ? "NULL" : uid;
        this.uid = uid;

        this.logger = new RWLogger(cmd, uid);
        logger.log("RequestURL: " + HttpUtil.getRequestFullURL(req));
    }

    public RWLogger getLogger() {
        return logger;
    }

    public HttpServletRequest getRequest() {
        return req;
    }

    public HttpServletResponse getResponse() {
        return resp;
    }

    public HttpSession getSession() {
        return req.getSession();
    }

    public String getUid() {
        return uid.equals("NULL") ? null : uid;
    }

    public String getPostData() {
        if (!req.getMethod().trim().toUpperCase().equals("POST")) {
            return null;
        }
        try {
            return HttpUtil.getRequestBody(req);
        } catch (IOException e) {
            logger.error(RWLogger.log, "getPostData failed", e);
            return null;
        }
    }

    public String getParameter(String key) {
        return HttpUtil.getStringParameter(req, key);
    }

    public String getParameterOrig(String key) {
        return HttpUtil.getStringParameterWithoutToLower(req, key);
    }

    public String getParameterSafe(String key) {
        String ret = getParameter(key);
        return (ret == null) ? "" : ret;
    }

    public int getParameterAsInt(String key) {
        return getParameterAsInt(key, 0);
    }

    public int getParameterAsInt(String key, int defValue) {
        String ret = getParameter(key);
        try {
            return (ret == null) ? defValue : Integer.parseInt(ret);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public short getParameterAsShort(String key) {
        return getParameterAsShort(key, (short) 0);
    }

    public short getParameterAsShort(String key, short defValue) {
        String ret = getParameter(key);
        try {
            return (ret == null) ? defValue : Short.parseShort(ret);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public long getParameterAsLong(String key) {
        return getParameterAsLong(key, 0);
    }

    public long getParameterAsLong(String key, long defValue) {
        String ret = getParameter(key);
        try {
            return (ret == null) ? defValue : Long.parseLong(ret);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public boolean getParameterAsBoolean(String key) {
        String ret = getParameter(key);
        return (ret == null) ? false : Boolean.parseBoolean(ret);
    }

    public void setResultData(String msg) throws IOException {
        setResultData(msg, "text/plain; charset=UTF-8");
    }

    public void setResultData(String msg, String cntType) throws IOException {
        resp.setContentType(cntType);
        PrintWriter pw = resp.getWriter();
        pw.print(msg);
        pw.print('\n');
        pw.close();
    }

}
