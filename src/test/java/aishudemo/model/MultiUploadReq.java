package aishudemo.model;

import io.openDocAPI.client.model.FileOsinitmultiuploadReq;

public class MultiUploadReq extends FileOsinitmultiuploadReq {

    String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
