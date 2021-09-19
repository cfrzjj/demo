package test;

import aishudemo.model.SingleUploadReq;
import aishudemo.service.FileUploadDownloadService;
import aishudemo.util.APIInstanceManager;
import aishudemo.util.OSSException;
import io.openDocAPI.client.ApiException;
import io.openDocAPI.client.api.DefaultApi;
import io.openDocAPI.client.model.FileGetinfobypathReq;

public class test {
    private FileUploadDownloadService fileUploadService;
    private APIInstanceManager apiInstceManager;

    public void fileSingleUploadDemo() {
        // 设置传参
        SingleUploadReq body = new SingleUploadReq();
        // String filePath = "/root/PremiumSoft.zip";
        String filePath = "D:/PremiumSoft.zip";
        String parentId = getDocidByPath("test/测试");
        body.setDocid(parentId);
        body.setFilePath(filePath);
        body.setOndup(2L);

        // 调用接口
        try {
            fileUploadService.singleUpload(body);
        } catch (ApiException e) {
            System.out.println(e.getCode() + " " + e.getResponseBody() + " " + e.getResponseHeaders());
            e.printStackTrace();
        } catch (OSSException e) {
            System.out.println(e.getErrCode() + " " + e.getErrBody() + " " + e.getErrHeaders());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDocidByPath(String namePath) {
        DefaultApi apiInstance = apiInstceManager.getAPIInstanceWithoutToken();
        FileGetinfobypathReq getinfobypathBody = new FileGetinfobypathReq();
        getinfobypathBody.setNamepath(namePath);
        String docId = "";
        try {
            docId = apiInstance.fileGetinfobypathPost(getinfobypathBody).getDocid();
        } catch (ApiException e) {
            System.out.println(e.getCode() + " " + e.getResponseBody() + " " + e.getResponseHeaders());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return docId;
    }

}
