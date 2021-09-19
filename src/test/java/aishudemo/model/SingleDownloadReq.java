package aishudemo.model;

import io.openDocAPI.client.model.FileOsdownloadReq;

public class SingleDownloadReq extends FileOsdownloadReq {
    String savePath;

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
