package com.fr;

import java.security.*;
import javax.net.ssl.*;
import com.fr.third.org.hsqldb.lib.*;
import java.net.*;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil
{
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";
    private static final int CONNECTTIMEOUT = 5000;
    private static final int READTIMEOUT = 5000;

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    private static HttpURLConnection getConnection(final URL url, final String method, final String ctype) throws IOException {
        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
            }
            catch (Exception e) {
                throw new IOException(e);
            }
            final HttpsURLConnection connHttps = (HttpsURLConnection)url.openConnection();
            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });
            conn = connHttps;
        }
        else {
            conn = (HttpURLConnection)url.openConnection();
        }
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent", "quantangle- apiclient-java");
        conn.setRequestProperty("Content-Type", ctype);
        conn.setRequestProperty("Connection", "Keep-Alive");
        return conn;
    }
    
    public static String doGet(final String url, final Map<String, String> params) throws IOException {
        return doGet(url, params, "UTF-8");
    }
    
    public static String doGet(String url, final Map<String, String> params, final String charset) throws IOException {
        if (StringUtil.isEmpty(url) || params == null) {
            return null;
        }
        String response = "";
        url = url + "?" + buildQuery(params, charset);
        HttpURLConnection conn = null;
        final String ctype = "application/x-www-form- urlencoded;charset=" + charset;
        conn = getConnection(new URL(url), "GET", ctype);
        response = getResponseAsString(conn);
        return response;
    }
    
    public static String doPost(final String url, final Map<String, String> params) throws IOException {
        return doPost(url, params, 5000, 5000);
    }
    
    public static String doPost(final String url, final Map<String, String> params, final int connectTimeOut, final int readTimeOut) throws IOException {
        return doPost(url, params, "UTF-8", connectTimeOut, readTimeOut);
    }
    
    public static String doPost(final String url, final Map<String, String> params, final String charset, final int connectTimeOut, final int readTimeOut) throws IOException {
        HttpURLConnection conn = null;
        String response = "";
        final String ctype = "application/x-www-form- urlencoded;charset=" + charset;
        conn = getConnection(new URL(url), "POST", ctype);
        conn.setConnectTimeout(connectTimeOut);
        conn.setReadTimeout(readTimeOut);
        conn.getOutputStream().write(buildQuery(params, charset).getBytes(charset));
        response = getResponseAsString(conn);
        return response;
    }
    
    public static String buildQuery(final Map<String, String> params, final String charset) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                sb.append("&");
            }
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value)) {
                try {
                    sb.append(key).append("=").append(URLEncoder.encode(value, charset));
                }
                catch (UnsupportedEncodingException ex) {}
            }
        }
        return sb.toString();
    }
    
    public static Map<String, String> splitQuery(final String query, final String charset) {
        final Map<String, String> ret = new HashMap<String, String>();
        if (!StringUtil.isEmpty(query)) {
            final String[] split2;
            final String[] splits = split2 = query.split("\\&");
            for (final String split : split2) {
                final String[] keyAndValue = split.split("\\=");
                boolean flag = true;
                for (int i = 0, len = keyAndValue.length; i < len; ++i) {
                    if (StringUtil.isEmpty(keyAndValue[i])) {
                        flag = false;
                        break;
                    }
                }
                if (flag && keyAndValue.length == 2) {
                    try {
                        ret.put(keyAndValue[0], URLDecoder.decode(keyAndValue[1], charset));
                    }
                    catch (UnsupportedEncodingException ex) {}
                }
            }
        }
        return ret;
    }
    
    private static byte[] getTextEntry(final String fieldName, final String fieldValue, final String charset) throws IOException {
        final StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
        entry.append(fieldValue);
        return entry.toString().getBytes(charset);
    }
    
    private static byte[] getFileEntry(final String fieldName, final String fileName, final String mimeType, final String charset) throws IOException {
        final StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\";filename=\"");
        entry.append(fileName);
        entry.append("\"\r\nContent-Type:");
        entry.append(mimeType);
        entry.append("\r\n\r\n");
        return entry.toString().getBytes(charset);
    }
    
    private static String getResponseAsString(final HttpURLConnection conn) throws IOException {
        final String charset = getResponseCharset(conn.getContentType());
        final InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        }
        final String msg = getStreamAsString(es, charset);
        if (StringUtil.isEmpty(msg)) {
            throw new IOException("{\"" + conn.getResponseCode() + "\":\"" + conn.getResponseMessage() + "\"}");
        }
        throw new IOException(msg);
    }
    
    private static String getStreamAsString(final InputStream input, final String charset) throws IOException {
        final StringBuilder sb = new StringBuilder();
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new InputStreamReader(input, charset));
            String str;
            while ((str = bf.readLine()) != null) {
                sb.append(str);
            }
            return sb.toString();
        }
        finally {
            if (bf != null) {
                bf.close();
                bf = null;
            }
        }
    }
    
    private static String getResponseCharset(final String ctype) {
        String charset = "UTF-8";
        if (!StringUtil.isEmpty(ctype)) {
            final String[] split;
            final String[] params = split = ctype.split("\\;");
            for (String param : split) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    final String[] pair = param.split("\\=");
                    if (pair.length == 2) {
                        charset = pair[1].trim();
                    }
                }
            }
        }
        return charset;
    }
}
