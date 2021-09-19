package aishudemo.model;

import io.openDocAPI.client.model.FileOsbeginuploadReq;

public class SingleUploadReq extends FileOsbeginuploadReq {
    String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
