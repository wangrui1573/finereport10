package com.fr;

import com.fr.decision.webservice.v10.login.LoginService;
import com.fr.json.JSONException;
import net.sf.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;
import java.io.IOException;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

public class myfilter implements Filter {
    public myfilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        OAuthAuthzResponse oAuthResponse = null;
        HttpSession session = req.getSession(true);
        String tokenLocation = "https:/www.example.com/oauth/token";
        String redirectURI = "http://localhost:8075/webroot/decision";
        String clientId = "dzs.hotelbi.local";
        String clientSecret = "da3e9b941b6dc724847c8426a323ddc8";
        String code = WebUtils.getHTTPRequestParameter(req, "code");
        System.out.println("code:" + code);
        String Token = "";

        try {
            if (!StringUtils.isEmpty(code)) {
                oAuthResponse = OAuthAuthzResponse.oauthCodeAuthzResponse(req);
                System.out.println("oAuthResponse.getCode()=" + oAuthResponse.getCode());


                JSONObject jsonParams = new JSONObject();
                jsonParams.put("client_id", clientId);
                jsonParams.put("client_secret", clientSecret);
                jsonParams.put("redirect_uri", redirectURI);
                jsonParams.put("code", oAuthResponse.getCode());
                jsonParams.put("grant_type", "authorization_code");
                // 通过接收到的授权码到SSO站点申请令牌
                String returnParams = postByJsonParameters(tokenLocation, jsonParams);

                JSONObject jsonResponse = new JSONObject();
                jsonResponse = jsonResponse.fromObject(returnParams);
                String accessToken = jsonResponse.getString("access_token");

                System.out.println("Token" + accessToken);

                JSONObject jsonParams1 = new JSONObject();
                jsonParams1.put("access_token", accessToken);
                String result = postByJsonParameters("https://oauth.shu.edu.cn/rest/user/getLoggedInUser", jsonParams1);
                System.out.println("result = " + result);
                String username = "";
                if (!StringUtils.isEmpty(result)) {
                    JSONObject json = new JSONObject();
                    json = json.fromObject(result);
                    username = json.getString("username");
                    System.out.println("username" + username);
                }

                if (StringUtils.isNotEmpty(username)) {
                    String token = LoginService.getInstance().login(req, res, username);
                    req.setAttribute("fine_auth_token", token);
                }
                res.addCookie(new Cookie("access_token", Token));
                filterChain.doFilter(req, res);
            } else {
                System.out.println("code is empty!");
            }
        } catch (OAuthProblemException var20) {
            var20.printStackTrace();
        } catch (OAuthSystemException var21) {
            var21.printStackTrace();
        } catch (JSONException var22) {
            var22.printStackTrace();
        } catch (Exception var23) {
            var23.printStackTrace();
        }

    }
    /**
     * 模拟post请求方法，请求参数为json
     *
     * @param url
     * @param params
     * @return
     */
    public static String postByJsonParameters(String url, JSONObject params) {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        Set<String> keySet = params.keySet();
        try {
            NameValuePair[] postData = new NameValuePair[keySet.size()];
            int postDataIndex = 0;
            for (String key : keySet) {
                postData[postDataIndex++] = new NameValuePair(key, params.getString(key));
            }
            postMethod.getParams().setContentCharset("UTF-8");
            postMethod.addParameters(postData);

            httpClient.executeMethod(postMethod);
            if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
                String response = new String(postMethod.getResponseBodyAsString());
                return response;
            } else {
                return null;
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            postMethod.releaseConnection();
        }
        return null;
    }
    public void destroy() {
    }
}