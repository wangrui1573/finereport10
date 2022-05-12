package com.fr;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.oltu.oauth2.client.HttpClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthClientResponse;
import org.apache.oltu.oauth2.client.response.OAuthClientResponseFactory;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;

public class SSLConnectionClient implements HttpClient {
    public SSLConnectionClient() {
    }

    public <T extends OAuthClientResponse> T execute(OAuthClientRequest request, Map<String, String> headers, String requestMethod, Class<T> responseClass) throws OAuthSystemException, OAuthProblemException {
        String responseBody = null;
        URLConnection c = null;
        boolean var7 = false;

        int responseCode;
        try {
            URL url = new URL(request.getLocationUri());
            c = url.openConnection();
            responseCode = -1;
            if (c instanceof HttpsURLConnection) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) c;
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init((KeyManager[]) null, new TrustManager[]{new TrustAnyTrustManager()}, new SecureRandom());
                httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                httpsURLConnection.setHostnameVerifier(new TrustAnyHostnameVerifier());
                if (headers != null && !headers.isEmpty()) {
                    Iterator var11 = headers.entrySet().iterator();

                    while (var11.hasNext()) {
                        Entry<String, String> header = (Entry) var11.next();
                        httpsURLConnection.addRequestProperty((String) header.getKey(), (String) header.getValue());
                    }
                }

                if (!OAuthUtils.isEmpty(requestMethod)) {
                    httpsURLConnection.setRequestMethod(requestMethod);
                    if (requestMethod.equals("POST")) {
                        httpsURLConnection.setDoOutput(true);
                        OutputStream ost = httpsURLConnection.getOutputStream();
                        PrintWriter pw = new PrintWriter(ost);
                        pw.print(request.getBody());
                        pw.flush();
                        pw.close();
                    }
                } else {
                    httpsURLConnection.setRequestMethod("GET");
                }

                httpsURLConnection.connect();
                responseCode = httpsURLConnection.getResponseCode();
                InputStream inputStream;
                if (responseCode == 400) {
                    inputStream = httpsURLConnection.getErrorStream();
                } else {
                    inputStream = httpsURLConnection.getInputStream();
                }

                responseBody = OAuthUtils.saveStreamAsString(inputStream);
            }
        } catch (Exception var13) {
            throw new OAuthSystemException(var13);
        }

        return OAuthClientResponseFactory.createCustomResponse(responseBody, c.getContentType(), responseCode, responseClass);
    }

    public void shutdown() {
    }

    static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}