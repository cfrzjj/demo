package aishudemo.service.impl;

import aishudemo.model.MultiUploadReq;
import aishudemo.model.SingleDownloadReq;
import aishudemo.model.SingleUploadReq;
import aishudemo.service.FileUploadDownloadService;
import aishudemo.util.APIInstanceManager;
import aishudemo.util.OSSHelper;
import aishudemo.util.OSSHelper.OSSReqResult;
import io.openDocAPI.client.ApiResponse;
import io.openDocAPI.client.api.DefaultApi;
import io.openDocAPI.client.model.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FileUploadDownloadServiceImpl implements FileUploadDownloadService {

    private OSSHelper ossHttpHelper;
    private APIInstanceManager apiInstceManager;
    private long minSize; // 分块上传块最小单位大小。

    public long getMinSize() {
        return minSize;
    }

    public void setMinSize(long minSize) {
        this.minSize = minSize;
    }

    public FileUploadDownloadServiceImpl(APIInstanceManager apiInstceManager) throws Exception {
        ossHttpHelper = new OSSHelper();
        this.apiInstceManager = apiInstceManager;
        minSize = 4 * 1024 * 1024; // 默认为4M
    }

    @Override
    public void singleUpload(SingleUploadReq uploadReq) throws Exception {
        DefaultApi apiInstance = apiInstceManager.getAPIInstanceWithToken();

        // 调用 osbeginupload API
        File uploadFile = new File(uploadReq.getFilePath());
        uploadReq.setLength(uploadFile.length());
        uploadReq.setName(uploadFile.getName());
        FileOsbeginuploadReq osbeginuploadBody = new FileOsbeginuploadReq();
        osbeginuploadBody = uploadReq;

        FileOsbeginuploadRes osbeginuploadResult = apiInstance.fileOsbeginuploadPost(osbeginuploadBody);
        System.out.println(osbeginuploadResult);

        // 根据服务器返回的对象存储请求，向对象存储上传数据
        InputStreamEntity body = new InputStreamEntity(new FileInputStream(uploadFile), uploadFile.length());
        Vector<String> headers = new Vector<String>();
        List<String> authRequestList = osbeginuploadResult.getAuthrequest();
        for (int i = 2; i < authRequestList.size(); ++i) {
            String header = authRequestList.get(i);
            headers.add(header);
        }
        ossHttpHelper.SendReqToOSS(authRequestList.get(0), authRequestList.get(1), headers, body);

        // 调用osendupload API
        FileOsenduploadReq osenduploadBody = new FileOsenduploadReq();
        osenduploadBody.setDocid(osbeginuploadResult.getDocid());
        osenduploadBody.setRev(osbeginuploadResult.getRev());
        FileOsenduploadRes osenduploadResult = apiInstance.fileOsenduploadPost(osenduploadBody);
        System.out.println(osenduploadResult);

    }

    @Override
    public void multiUpload(MultiUploadReq multiUploadReq) throws Exception {
        DefaultApi apiInstance = apiInstceManager.getAPIInstanceWithToken();
        File uploadFile = new File(multiUploadReq.getFilePath());

        // 获取对象存储信息，判断得出分块的大小
        FileOsoptionRes result = apiInstance.fileOsoptionPost();
        System.out.println(result);
        long partMinSize = result.getPartminsize();
        if (partMinSize <= this.minSize) {
            partMinSize = this.minSize;
        }
        long partMaxSize = result.getPartmaxsize();
        long partMaxNum = result.getPartmaxnum();
        long partSize = partMinSize;
        int partCount;

        while (true) {
            if (partSize > partMaxSize) {
                throw new Exception("上传文件过大!");
            }
            partCount = (int) (uploadFile.length() / partSize);
            if (uploadFile.length() == 0 || uploadFile.length() % partSize != 0) {
                ++partCount;
            }
            if (partCount <= partMaxNum) {
                break;
            }
            partSize += partMinSize;
        }

        // 调用 osinitmultiupload API
        multiUploadReq.setLength(uploadFile.length());
        multiUploadReq.setName(uploadFile.getName());

        FileOsinitmultiuploadReq osinitmultiuploadBody = new FileOsinitmultiuploadReq();
        osinitmultiuploadBody = multiUploadReq;
        FileOsinitmultiuploadRes osinitmultiuploadResult = apiInstance.fileOsinitmultiuploadPost(osinitmultiuploadBody);
        System.out.println(osinitmultiuploadResult);

        String retDocId = osinitmultiuploadResult.getDocid();
        String retRev = osinitmultiuploadResult.getRev();
        String retUploadId = osinitmultiuploadResult.getUploadid();

        // 调用 osuploadpart API
        String parts = "1-" + partCount;
        FileOsuploadpartReq osuploadpartBody = new FileOsuploadpartReq();
        osuploadpartBody.setDocid(retDocId);
        osuploadpartBody.setRev(retRev);
        osuploadpartBody.setUploadid(retUploadId);
        osuploadpartBody.setParts(parts);

        FileOsuploadpartRes osuploadpartResult = apiInstance.fileOsuploadpartPost(osuploadpartBody);
        System.out.println(osuploadpartResult);

        // 根据服务器返回的对象存储请求，向对象存储分块上传数据
        byte[] buf = new byte[(int) partSize];
        FileInputStream fis = new FileInputStream(uploadFile);
        int partIndex = 1; // 记录当前处理的是第几个part
        FileOscompleteuploadReqPartinfo partInfo = new FileOscompleteuploadReqPartinfo();
        do {
            int writeSize = fis.read(buf);
            Vector<String> headers = new Vector<String>();
            List<String> authRequestList = osuploadpartResult.getAuthrequests().get(String.valueOf(partIndex));
            for (int i = 2; i < authRequestList.size(); ++i) {
                String header = authRequestList.get(i);
                headers.add(header);
            }
            ByteArrayEntity body = new ByteArrayEntity(buf, 0, writeSize);
            OSSReqResult ossResult = ossHttpHelper.SendReqToOSS(authRequestList.get(0), authRequestList.get(1), headers, body); // 上传块
            // 获取etag,由于报头中"etag"可能为"Etag","ETag","ETAG"等情况，故这里对报头key值进行遍历，将key值变为大写后与"ETAG"进行比较，若相等则让etag等于其value，推出循环。0
            String etag = null;

            Header[] headerArray = ossResult.response.getAllHeaders();
            for (int i = 0; i < headerArray.length; ++i) {
                String key = headerArray[i].getName();
                if (key.toUpperCase().equals("ETAG")) {
                    etag = headerArray[i].getValue();
                    i = headerArray.length;
                }
            }
            ossResult.response.close();
            ArrayList<Object> tempList = new ArrayList<>();
            tempList.add(etag);
            tempList.add(writeSize);
            partInfo.put(String.valueOf(partIndex), tempList);
            ++partIndex;
        } while (partIndex <= partCount);
        fis.close();

        // 调用 oscompleteupload API
        FileOscompleteuploadReq oscompleteuploadBody = new FileOscompleteuploadReq();
        oscompleteuploadBody.setDocid(retDocId);
        oscompleteuploadBody.setRev(retRev);
        oscompleteuploadBody.setUploadid(retUploadId);
        oscompleteuploadBody.setPartinfo(partInfo);

        String[] completeuploadInfo = new String[2];
        ApiResponse<String> oscompleteuploadResult = apiInstance
                .fileOscompleteuploadPostWithHttpInfo(oscompleteuploadBody);
        String boundary = oscompleteuploadResult.getHeaders().get("Content-Type").get(0).split(";", 2)[1].split("=",
                2)[1];
        completeuploadInfo = oscompleteuploadResult.getData().split("--" + boundary);
        System.out.println(oscompleteuploadResult);

        // 根据服务器返回的索引信息和对象存储请求，向对象存储上传索引信息对块文件进行合并
        StringEntity body = new StringEntity(completeuploadInfo[1].replaceAll("\r\n", ""), "UTF-8");
        JSONObject returnJson = JSONObject.fromObject(completeuploadInfo[2].replaceAll("\r\n", ""));
        JSONArray authRequest = returnJson.getJSONArray("authrequest");
        String method = (String) authRequest.get(0);
        String url = (String) authRequest.get(1);
        Vector<String> headers = new Vector<String>();
        for (int i = 2; i < authRequest.size(); i++) {
            headers.add((String) authRequest.get(i));
        }
        ossHttpHelper.SendReqToOSS(method, url, headers, body);

        // 调用osendupload API
        FileOsenduploadReq osenduploadBody = new FileOsenduploadReq();
        osenduploadBody.setDocid(retDocId);
        osenduploadBody.setRev(retRev);
        FileOsenduploadRes osenduploadResult = apiInstance.fileOsenduploadPost(osenduploadBody);
        System.out.println(osenduploadResult);

    }

    @Override
    public void singleDownload(SingleDownloadReq downloadReq) throws Exception {
        DefaultApi apiInstance = apiInstceManager.getAPIInstanceWithToken();

        // 调用 osdownload API
        FileOsdownloadReq osdownloadBody = new FileOsdownloadReq();
        osdownloadBody = downloadReq;
        FileOsdownloadRes osdownloadResult = apiInstance.fileOsdownloadPost(osdownloadBody);
        System.out.println(osdownloadResult);

        // 根据服务器返回的对象存储请求，向对象存储下载数据
        File saveFile = new File(downloadReq.getSavePath() + "\\" + osdownloadResult.getName());
        if (saveFile.exists()) {
            throw new Exception("下载路径存在同名文件，下载失败。");
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile));
        Vector<String> headers = new Vector<String>();
        List<String> authRequestList = osdownloadResult.getAuthrequest();
        for (int i = 2; i < authRequestList.size(); ++i) {
            String header = authRequestList.get(i);
            headers.add(header);
        }
        OSSReqResult ossResult = ossHttpHelper.SendReqToOSS(authRequestList.get(0), authRequestList.get(1), headers, null);
        BufferedInputStream bis = new BufferedInputStream(ossResult.response.getEntity().getContent());
        int len = -1;
        byte[] bytes = new byte[1024];
        while ((len = bis.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
        }
        bis.close();
        ossResult.request.releaseConnection();
        bos.close();
    }

}
