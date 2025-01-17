package com.didiglobal.knowframework.observability.conponent.http;

import com.didiglobal.knowframework.observability.Observability;
import com.didiglobal.knowframework.observability.common.constant.Constant;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    // 连接超时时间, 单位: ms
    private static int CONNECT_TIME_OUT = 15000;

    // 读取超时时间, 单位: ms
    private static int READ_TIME_OUT = 3000;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_DELETE = "DELETE";

    private static final String CHARSET_UTF8 = "UTF-8";

    private static final String FILE_PARAM = "filecontent";

    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    private static final Tracer tracer = Observability.getTracer(HttpUtils.class.getName());

    private static final TextMapPropagator textMapPropagator = Observability.getTextMapPropagator();

    private static final TextMapSetter<HttpURLConnection> setter = URLConnection::setRequestProperty;

    public static String get(String url, Map<String, String> params) throws Exception {
        return sendRequest(url, METHOD_GET, params, null, null);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        return sendRequest(url, METHOD_GET, params, headers, null);
    }

    public static String get(String url, Map<String, String> params, Map<String, String> headers, String content) throws Exception {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_GET, params, headers, in);
    }

    public static String postForString(String url, String content, Map<String, String> headers) throws Exception {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_POST, null, headers, in);
    }

    public static String putForString(String url, String content, Map<String, String> headers) throws Exception {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_PUT, null, headers, in);
    }

    public static String deleteForString(String url, String content, Map<String, String> headers) throws Exception {
        InputStream in = null;
        try {
            if (content != null && !content.isEmpty()) {
                in = new ByteArrayInputStream(content.getBytes(CHARSET_UTF8));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return sendRequest(url, METHOD_DELETE, null, headers, in);
    }

    /**
     * @param url        请求的链接, 只支持 http 和 https 链接
     * @param method     GET or POST
     * @param headers    请求头 (将覆盖默认请求), 可以为 null
     * @param bodyStream 请求内容, 流将自动关闭, 可以为 null
     * @return 返回响应内容的文本
     * @throws Exception http 响应 code 非 200, 或发生其他异常均抛出异常
     */
    private static String
    sendRequest(String url,
                                      String method,
                                      Map<String, String> params,
                                      Map<String, String> headers,
                                      InputStream bodyStream) throws Exception {
        HttpURLConnection conn = null;
        Span span = tracer.spanBuilder(
                String.format("%s.%s", HttpUtils.class.getName(), "sendRequest")
        ).setSpanKind(SpanKind.CLIENT).startSpan();
        try (Scope scope = span.makeCurrent()) {
            /*
             * inject info into span
             */
            span.setAttribute(Constant.ATTRIBUTE_KEY_HTTP_METHOD, method);
            span.setAttribute(Constant.ATTRIBUTE_KEY_COMPONENT, Constant.ATTRIBUTE_VALUE_COMPONENT_HTTP);

            String paramUrl = setUrlParams(url, params);
            // 打开链接
            URL urlObj = new URL(paramUrl);
            span.setAttribute(Constant.ATTRIBUTE_KEY_HTTP_URL, url.toString());

            conn = (HttpURLConnection) urlObj.openConnection();

            // 设置conn属性
            setConnProperties(conn, method, headers);

            // Inject the request with the current Context/Span.
            textMapPropagator.inject(Context.current(), conn, setter);

            // 设置请求内容
            if (bodyStream != null) {
                conn.setDoOutput(true);
                copyStreamAndClose(bodyStream, conn.getOutputStream());
            }
            String result = handleResponseBodyToString(conn.getInputStream());
            span.setStatus(StatusCode.OK);
            return result;
        } catch (IOException ex) {
            String exceptionMessage = "无法连接至远程地址: " + url;
            span.setStatus(StatusCode.ERROR, exceptionMessage);
            throw new Exception(exceptionMessage, ex);
        } catch (Exception ex) {
            String exceptionMessage = "未知错误: " + ex.getMessage();
            span.setStatus(StatusCode.ERROR, exceptionMessage);
            throw new Exception(exceptionMessage, ex);
        } finally {
            try {
                closeConnection(conn);
            } finally {
                span.end();
            }
        }
    }

    private static String setUrlParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder sb = new StringBuilder(url).append('?');
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static void setConnProperties(HttpURLConnection conn,
                                          String method,
                                          Map<String, String> headers) throws Exception {
        // 设置连接超时时间
        conn.setConnectTimeout(CONNECT_TIME_OUT);

        // 设置读取超时时间
        conn.setReadTimeout(READ_TIME_OUT);

        // 设置请求方法
        if (method != null && !method.isEmpty()) {
            conn.setRequestMethod(method);
        }

        // 添加请求头
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        if (headers == null || headers.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private static String handleResponseBodyToString(InputStream in) throws Exception {
        ByteArrayOutputStream bytesOut = null;
        try {
            bytesOut = new ByteArrayOutputStream();
            copyStreamAndClose(in, bytesOut);
            return new String(bytesOut.toByteArray(), CHARSET_UTF8);
        } finally {
            closeStream(bytesOut);
        }
    }

    private static void copyStreamAndClose(InputStream in, OutputStream out) {
        try {
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(in);
            closeStream(out);
        }
    }

    private static void closeConnection(HttpURLConnection conn) {
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception e) {
                LOGGER.error("close connection failed", e);
            }
        }
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception e) {
                LOGGER.error("close stream failed", e);
            }
        }
    }
}
