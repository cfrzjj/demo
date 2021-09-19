package aishudemo.util;

import aishudemo.conf.LoginInfo;
import io.openDocAPI.client.ApiClient;
import io.openDocAPI.client.Configuration;
import io.openDocAPI.client.api.DefaultApi;

public class APIInstanceManager {

    private DefaultApi apiInstanceWithToken;
    private DefaultApi apiInstanceWithoutToken;
    private TokenManager tokenManager;

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public APIInstanceManager() throws Exception {
        tokenManager = new TokenManager();

        String basePath = "https://" + LoginInfo.hostIp + ":" + LoginInfo.port + "/api/v1";
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath(basePath);
        defaultClient.setVerifyingSsl(false);
        defaultClient.setAccessToken(tokenManager.getTokenId());
        apiInstanceWithToken = new DefaultApi(defaultClient);

        ApiClient tmp = Configuration.getDefaultApiClient();
        tmp.setBasePath(basePath);
        tmp.setVerifyingSsl(false);
        apiInstanceWithoutToken = new DefaultApi(tmp);
    }

    /**
     * 根据LoginInfo信息创建apiHelper类，报头设置tokenId，用https协议获取API调用类实例
     * 
     * @return https协议获取API调用类实例
     */
    public DefaultApi getAPIInstanceWithToken() throws Exception {
        apiInstanceWithToken.getApiClient().setAccessToken(tokenManager.getTokenId());
        return apiInstanceWithToken;
    }

    /**
     * 根据LoginInfo信息创建apiHelper类，报头不设置tokenId，用https协议获取API调用类实例
     * 
     * @return https协议获取API调用类实例
     */
    public DefaultApi getAPIInstanceWithoutToken() {
        return apiInstanceWithoutToken;
    }

}
