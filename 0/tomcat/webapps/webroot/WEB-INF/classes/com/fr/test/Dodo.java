package com.fr.test;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public  class Dodo implements Filter {
    public void destroy() {
        // TODO Auto-generated method stub

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)

            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String referer = req.getHeader("referer");
        //下面的IP地址是正常页面请求
        if(null != referer && (referer.trim().startsWith("http://localhost:8080")||referer.trim().startsWith("http://dev.fanruan.com/detail.html"))){
            System.out.println("正常页面请求"+referer);
            chain.doFilter(req, resp);
            //下面的就是出现不是正常页面请求的时候跳转
        }else{
            System.out.println("盗链"+referer);
            req.getRequestDispatcher("/LdapLogin.jsp").forward(req, resp);
        }
    }
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }
}