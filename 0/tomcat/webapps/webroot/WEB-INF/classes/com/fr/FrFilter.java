package com.fr;

import com.fr.data.NetworkHelper;
import com.fr.decision.mobile.terminal.TerminalHandler;
import com.fr.decision.webservice.utils.DecisionServiceConstants;
import com.fr.decision.webservice.v10.login.LoginService;
import com.fr.general.ComparatorUtils;
import com.fr.log.FineLoggerFactory;
import com.fr.security.JwtUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.web.Device;
import org.jasig.cas.client.validation.Assertion;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Zed on 2018/9/11.
 */
public class FrFilter implements Filter {

    public FrFilter() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        FineLoggerFactory.getLogger().info("fr cas login");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession(true);
        FineLoggerFactory.getLogger().info("URL:" + req.getRequestURI());
        String username;
        //获取cas传递过来的username
        Object object = req.getSession().getAttribute("_const_cas_assertion_");
        if (object != null) {
            Assertion assertion = (Assertion) object;
            username = assertion.getPrincipal().getName();
        } else {
            username = (String) session.getAttribute("edu.yale.its.tp.cas.client.filter.user");
        }

        try {
            //用户名为空，登录请求有问题，直接报错
            if (StringUtils.isNotEmpty(username)) {
                FineLoggerFactory.getLogger().error("username:" + username);
                //获取请求携带的token
                Object oldToken = session.getAttribute(DecisionServiceConstants.FINE_AUTH_TOKEN_NAME);

                //token不存在，或者token过期了，走后台登录方法
                if (oldToken == null || !checkTokenValid(req, (String) oldToken, username)) {
                    login(req, res, session, username);
                    filterChain.doFilter(req, res);
                } else {
                    //放行
                    filterChain.doFilter(req, res);
                    FineLoggerFactory.getLogger().info("no need");
                }
            } else {
                throw new Exception("username is empty");
            }
        } catch (Exception e) {
            FineLoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * 后台登录方法
     */
    private void login(HttpServletRequest req, HttpServletResponse res, HttpSession session, String username) throws Exception {
        String token = LoginService.getInstance().login(req, res, username);
        session.setAttribute(DecisionServiceConstants.FINE_AUTH_TOKEN_NAME, token);
        FineLoggerFactory.getLogger().info("fr FrFilter is over with username is ###" + username);
    }

    /**
     * 校验token是否有效
     */
    private boolean checkTokenValid(HttpServletRequest req, String token, String currentUserName) {
        try {
            //当前登录用户和token对应的用户名不同，需要重新生成token
            if (!ComparatorUtils.equals(currentUserName, JwtUtils.parseJWT(token).getSubject())) {
                FineLoggerFactory.getLogger().info("username changed：" + currentUserName);
                return false;
            }
            Device device = NetworkHelper.getDevice(req);
            LoginService.getInstance().loginStatusValid(token, TerminalHandler.getTerminal(req, device));
            return true;
        } catch (Exception ignore) {
        }

        return false;
    }

    @Override
    public void destroy() {

    }
}
