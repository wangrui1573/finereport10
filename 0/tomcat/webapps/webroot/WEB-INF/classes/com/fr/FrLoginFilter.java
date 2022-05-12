package com.fr;

import com.fr.decision.authority.data.User;
import com.fr.decision.webservice.exception.user.UserNotExistException;
import com.fr.decision.webservice.v10.login.LoginService;
import com.fr.decision.webservice.v10.login.TokenResource;
import com.fr.decision.webservice.v10.user.UserService;
import com.fr.log.FineLoggerFactory;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrLoginFilter implements Filter {
    public FrLoginFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse res = (HttpServletResponse)servletResponse;
        String username = WebUtils.getHTTPRequestParameter(req, "username");

        try {
            if (StringUtils.isNotEmpty(username)) {
                FineLoggerFactory.getLogger().error("username:" + username);
                User user = UserService.getInstance().getUserByUserName(username);
                if (user == null) {
                    throw new UserNotExistException();
                }

                String oldToken = TokenResource.COOKIE.getToken(req);
                if (oldToken == null) {
                    String token = LoginService.getInstance().login(req, res, username);
                    req.setAttribute("fine_auth_token", token);
                    filterChain.doFilter(req, res);
                } else {
                    filterChain.doFilter(req, res);
                }
            } else {
                filterChain.doFilter(req, res);
            }
        } catch (Exception var10) {
            FineLoggerFactory.getLogger().error(var10.getMessage(), var10);
        }

    }

    public void destroy() {
    }
}