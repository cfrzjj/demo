package test;

import aishudemo.util.OSSHelper;
import aishudemo.util.OSSHelper.OSSReqResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import io.openDocAPI.client.model.FileOsbeginuploadRes;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.entity.InputStreamEntity;
import org.junit.jupiter.api.Test;

import javax.net.ssl.*;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestFileupload {
    private OSSHelper ossHttpHelper;
    static
    {
        try
        {
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier
                    (
                            new HostnameVerifier()
                            {
                                public boolean verify(String urlHostName, SSLSession session)
                                {
                                    return true;
                                }
                            }
                    );
        } catch (Exception e)  {}
    }

    @Test
    public  void fileUpload(){

    }
    /**
     * 发送https请求
//     * @param requestUrl 请求地址
//     * @param requestMethod 请求方式（GET、POST）
//     * @param outputStr 提交的数据
     * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
     */
    @Test
    public  void httpsRequest() {
        try{
            String requestUrl = "http://10.111.163.88:9998/v1/auth1?method=extloginclient";
            String requestMethod = "POST";
            String outputStr = "";
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            String str1 = "efort366ca93c-15c9-11ec-a637-a0369f5f53f6chenfanrong";
            String s = this.encryptToMD5(str1);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("account","chenfanrong");
            jsonObject.put("appid","efort");
            jsonObject.put("key",s);
            String key = "";

            outputStr = jsonObject.toJSONString();

            if(null != outputStr){
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            //获取token
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null){
                buffer.append(str);
            }
            JSONObject jsonAuth = JSON.parseObject(buffer.toString());
            Object userid =   jsonAuth.get("userid");
            Object tokenid = jsonAuth.get("tokenid");
            //获取gns
            StringBuffer stringBuffer = this.httpsGns(userid.toString(), tokenid.toString());
            JSONObject jsonGns = JSON.parseObject(stringBuffer.toString());
            Object docid =   jsonGns.get("docid");
            Object client_mtime =   jsonGns.get("client_mtime");
            File file = new File("D:\\EfortPLM安装资料包\\windchill 11 M030\\Java基础核心总结.pdf");
            StringBuffer httpUploadRes = httpUpload(userid.toString(), tokenid.toString(), docid.toString(),client_mtime.toString(),file);
            JSONObject jsonUploadRes = JSON.parseObject(httpUploadRes.toString());
            Object docidRes =   jsonUploadRes.get("docid");
            Object rev =   jsonGns.get("rev");
            Object rev1 =   jsonUploadRes.get("rev");
            String md5Hex = DigestUtils.md5Hex(new FileInputStream("D:\\EfortPLM安装资料包\\windchill 11 M030\\Java基础核心总结.pdf"));

            FileOsbeginuploadRes fileOsbeginuploadRes = JSONObject.toJavaObject(jsonUploadRes, FileOsbeginuploadRes.class);
             aa(file, fileOsbeginuploadRes);

//            FileOsbeginuploadRes osbeginuploadResult



            StringBuffer httpUploadEndRes = httpUploadEnd(userid.toString(), tokenid.toString(), docidRes.toString(), rev1.toString(), md5Hex);

            if(conn.getResponseCode() == conn.HTTP_OK){
//                message.setCode(0);
//                message.setContent(buffer.toString());
                System.out.println("============"+buffer.toString()+"==============");
            }else{
//                message.setCode(-1);
//                message.setContent("获取返回状态不对,返回状态为:" + conn.getResponseCode());
            }

            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();

        }catch (ConnectException ce){
            ce.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * MD5加密之方法一
     * @explain 借助apache工具类DigestUtils实现
     * @param str
     *            待加密字符串
     * @return 16进制加密字符串
     */
    public  String encryptToMD5(String str) {
//        DigestUtils.
        return DigestUtils.md5Hex(str);
    }




    /**
     * 获取gns
     * 发送http请求
     * @param userid
     * @param tokenid
     */
//    @Test
    public  StringBuffer httpsGns(String userid, String tokenid) {
        StringBuffer buffer = new StringBuffer();
        try{
//            http://<domain name>:9123/v1/file?method=<method>&userid=<userid>&tokenid=<tokenid>
            String requestUrl = "http://10.111.163.88:9123/v1/file?method=getinfobypath&userid=" + userid + "&tokenid=" + tokenid ;
            String requestMethod = "POST";
            String outputStr = "";
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("namepath","陈繁荣");
            String key = "";

            outputStr = jsonObject.toJSONString();

            if(null != outputStr){
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }

            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null){
                buffer.append(str);
            }
            if(conn.getResponseCode() == conn.HTTP_OK){
//                message.setCode(0);
//                message.setContent(buffer.toString());
                System.out.println("============"+buffer.toString()+"==============");
            }else{
//                message.setCode(-1);
//                message.setContent("获取返回状态不对,返回状态为:" + conn.getResponseCode());
            }

            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();


        }catch (ConnectException ce){
            ce.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer;
    }


    /**
     * 获取gns
     * 发送http请求
     * @param userid
     * @param tokenid
     */
//    @Test
    public  StringBuffer httpUpload(String userid, String tokenid, String docid, String client_mtime ,File file) {
        StringBuffer buffer = new StringBuffer();
        try{
//            http://<domain name>:9123/v1/file?method=<method>&userid=<userid>&tokenid=<tokenid>
            String requestUrl = "http://10.111.163.88:9123/v1/file?method=osbeginupload&userid=" + userid + "&tokenid=" + tokenid ;
            String requestMethod = "POST";
            String outputStr = "";
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("client_mtime",client_mtime);
            jsonObject.put("docid",docid);
            jsonObject.put("name","Java基础核心总结.pdf");
            jsonObject.put("length",file.length());
            jsonObject.put("ondup",3);//判断文件是否重名 1重名时抛出异常 2重名时重命名 3重名时删除重新上传
            outputStr = jsonObject.toJSONString();
            if(null != outputStr){
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null){
                buffer.append(str);
            }
            if(conn.getResponseCode() == conn.HTTP_OK){
                System.out.println("============"+buffer.toString()+"==============");
            }else{
//                message.setCode(-1);
//                message.setContent("获取返回状态不对,返回状态为:" + conn.getResponseCode());
            }

            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            conn.disconnect();


        }catch (ConnectException ce){
            ce.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer;
    }


    /**
     * 获取gns
     * 发送http请求
     * @param userid
     * @param tokenid
     */
//    @Test
    public  StringBuffer httpUploadEnd(String userid, String tokenid, String docidRes,String rev, String  md5Hex) {
        StringBuffer buffer = new StringBuffer();
        try{
//            http://<domain name>:9123/v1/file?method=<method>&userid=<userid>&tokenid=<tokenid>
            String requestUrl = "http://10.111.163.88:9123/v1/file?method=osendupload&userid=" + userid + "&tokenid=" + tokenid ;
            String requestMethod = "POST";
            String outputStr = "";
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("docid","gns:\\/\\/F9C5DE0EAD3A49319B2CD1CA35E1CF04");
//            jsonObject.put("docid",tmp);
            jsonObject.put("docid",docidRes);
            jsonObject.put("rev",rev);
//            jsonObject.put("md5",md5Hex);
            String key = "";
            outputStr = jsonObject.toJSONString();
            if(null != outputStr){
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null){
                buffer.append(str);
            }
            if(conn.getResponseCode() == conn.HTTP_OK){
//                message.setCode(0);
//                message.setContent(buffer.toString());
                System.out.println("============"+buffer.toString()+"==============");
            }else{
//                message.setCode(-1);
//                message.setContent("获取返回状态不对,返回状态为:" + conn.getResponseCode());
            }

            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();


        }catch (ConnectException ce){
            ce.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer;
    }


    public void aa(File uploadFile, FileOsbeginuploadRes osbeginuploadResult)throws Exception{
        DataInputStream in = null;
        OutputStream out = null;
        StringBuffer stringBuffer = new StringBuffer();
        // 根据服务器返回的对象存储请求，向对象存储上传数据
        InputStreamEntity body = new InputStreamEntity(new FileInputStream(uploadFile), uploadFile.length());
        Vector<String> headers = new Vector<String>();
        List<String> authRequestList = osbeginuploadResult.getAuthrequest();
        for (int i = 2; i < authRequestList.size(); ++i) {
            String header = authRequestList.get(i);
            headers.add(header);
        }
        ossHttpHelper = new OSSHelper();
        String s = authRequestList.get(1);
//        String s1 = s.replaceAll("cloud.efortwh.com", "10.111.163.88");
        HttpsURLConnection connection = createConnection(authRequestList.get(0),s, headers);
//        HttpsURLConnection connection = createConnection(authRequestList.get(0), authRequestList.get(1), headers);

        out = connection.getOutputStream();
        in = new DataInputStream(new FileInputStream(uploadFile));

        int bytes = 0;
        byte[] buffer = new byte[1024];
        while ((bytes = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytes);
        }
        out.flush();
//        OSSReqResult ossReqResult = ossHttpHelper.SendReqToOSS(authRequestList.get(0), authRequestList.get(1), headers, body);
        //文件上传完成
        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String str = null;
        while ((str = bufferedReader.readLine()) != null){
            stringBuffer.append(str);
        }
        if(connection.getResponseCode() == connection.HTTP_CREATED){
//                message.setCode(0);
//                message.setContent(buffer.toString());
            System.out.println("============"+stringBuffer.toString()+"==============");
        }else {
            System.err.println("上传失败");
        }

//        OSSReqResult ossReqResult = null;
//        return ossReqResult;
    }


    /**
     * 生成http连接
     * @param method http请求方式
     * @return httpURLConnection
     * @throws IOException 连接生成失败
     */
    private  HttpsURLConnection createConnection(String method, String urlPath, Vector<String> headers) throws IOException {
        URL url = new URL(urlPath);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        httpsURLConnection.setRequestMethod(method);
        httpsURLConnection.setRequestProperty("Charsert", "UTF-8");
        httpsURLConnection.setDoInput(true);// 允许输入
        httpsURLConnection.setDoOutput(true);// 允许输出
        httpsURLConnection.setUseCaches(false); // 不允许使用缓存
        httpsURLConnection.setRequestProperty("Content-type", "file/*");
        // set headers
        for (int i = 0; i < headers.size(); i++) {
            String[] kv = headers.get(i).split(": ", 2);
            httpsURLConnection.setRequestProperty(kv[0], kv[1]);
//            reqBuilder.addHeader(kv[0], kv[1]);
        }

        return httpsURLConnection;
    }


    private static void trustAllHttpsCertificates()
            throws NoSuchAlgorithmException, KeyManagementException
    {
        TrustManager[] trustAllCerts = new TrustManager[1];
        trustAllCerts[0] = new TrustAllManager();
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(
                sc.getSocketFactory());
    }

    private static class TrustAllManager implements X509TrustManager{

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
//            implements X509TrustManager
//    {
//        public X509Certificate[] getAcceptedIssuers()
//        {
//            return null;
//        }
//        public void checkServerTrusted(X509Certificate[] certs,
//                                       String authType)
//                throws CertificateException
//        {
//        }
//        public void checkClientTrusted(X509Certificate[] certs,
//                                       String authType)
//                throws CertificateException
//        {
//        }
//    }

    public static void getUploadInformation(String obj, HttpURLConnection connection) throws IOException, JSONException {

        StringBuffer sbuffer=null;
        try {
            OutputStream out = connection.getOutputStream();//向对象输出流写出数据，这些数据将存到内存缓冲区中
            out.write(obj.toString().getBytes());            //out.write(new String("测试数据").getBytes());            //刷新对象输出流，将任何字节都写入潜在的流中
            out.flush();
            // 关闭流对象,此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中
            out.close();
            //读取响应
            if (connection.getResponseCode()==200)            {
                // 从服务器获得一个输入流
                InputStreamReader   inputStream =new InputStreamReader(connection.getInputStream());//调用HttpURLConnection连接对象的getInputStream()函数, 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
                BufferedReader reader = new BufferedReader(inputStream);
                String lines;
                sbuffer= new StringBuffer("");
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), "utf-8");
                    sbuffer.append(lines);                }
                reader.close();
            }else{
            }
            //断开连接
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
