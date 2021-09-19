package aishudemo.util;

import aishudemo.conf.LoginInfo;
import io.openDocAPI.client.ApiClient;
import io.openDocAPI.client.Configuration;
import io.openDocAPI.client.api.DefaultApi;
import io.openDocAPI.client.model.Auth1GetnewReq;
import io.openDocAPI.client.model.Auth1GetnewRes;

public class TokenManager {

    private String tokenId;

    public String getTokenId() {
        return tokenId;
    }

    /**
     * 构造函数
     */
    public TokenManager() throws Exception {

        login();
        // 启动自检测线程，保证tokenId永远有效
        /*long refreshTokenIdTime = 2 * 60 * 60 * 1000 - 10 * 60 * 1000; // tokenId刷新时间(单位ms) 默认1小时50分;
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(refreshTokenIdTime);
                    login();
                } catch (ApiException e) {
                    System.out.println(e.getCode() + " " + e.getResponseBody() + " " + e.getResponseHeaders());
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    /**
     * 登陆
     * 
     * @return tokenid
     */
    public void login() throws Exception {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://" + LoginInfo.hostIp + ":" + LoginInfo.port + "/api/v1");
        defaultClient.setVerifyingSsl(false);
        DefaultApi apiInstance = new DefaultApi(defaultClient);

        Auth1GetnewReq body = new Auth1GetnewReq();
        body.setAccount(LoginInfo.userName);
        body.setPassword(CommonUtil.RSAEncode(LoginInfo.password));

        Auth1GetnewRes result = apiInstance.auth1GetnewPost(body);
        System.out.println(result);

        this.tokenId = result.getTokenid();
    }
}
