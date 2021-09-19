package aishudemo.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Vector;

public class OSSHelper {

    public class OSSReqResult {
        public HttpRequestBase request;
        public CloseableHttpResponse response;
    }

    private CloseableHttpClient ossClient;

    public OSSHelper() throws Exception {
        // 采用绕过验证的方式处理https请求
        SSLContext sslcontext = createIgnoreVerifySSL();

        // 设置协议http和https对应的处理socket链接工厂的对象
        SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(sslcontext,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", ssl).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        // 创建自定义的httpclient对象
        ossClient = HttpClients.custom().setConnectionManager(connManager).build();
    }

    public OSSReqResult SendReqToOSS(String method, String url, Vector<String> headers, HttpEntity body)
            throws Exception {
        // set method
        RequestBuilder reqBuilder = RequestBuilder.create(method);
        // set url
        reqBuilder.setUri(url);
        // set body
        if (body != null) {
            reqBuilder.setEntity(body);
        }
        // set headers
        System.out.println(headers);
        for (int i = 0; i < headers.size(); i++) {
            String[] kv = headers.get(i).split(": ", 2);
            reqBuilder.addHeader(kv[0], kv[1]);
        }

        OSSReqResult result = new OSSReqResult();
        result.request = (HttpRequestBase) reqBuilder.build();
        result.response = this.ossClient.execute(result.request);
        int resCode = result.response.getStatusLine().getStatusCode();

        // 若为错误返回码则抛出异常
        if (resCode < 200 || resCode >= 300) {
            String errHeaders = "";
            for (int i = 0; i < result.response.getAllHeaders().length; i++) {
                errHeaders += result.response.getAllHeaders()[i].toString() + " ";
            }
            String errBody = EntityUtils.toString(result.response.getEntity(), "utf-8");
            throw new OSSException(resCode, errBody, errHeaders);
        }
        // result.request.releaseConnection();

        return result;
    }

    /**
     * 绕过验证
     * 
     * @return
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext sc = SSLContext.getInstance("TLS");

        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {}

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) throws CertificateException {}

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sc.init(null, new TrustManager[] { trustManager }, null);
        return sc;
    }

}
