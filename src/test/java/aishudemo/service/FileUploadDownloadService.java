package aishudemo.service;


import aishudemo.model.MultiUploadReq;
import aishudemo.model.SingleDownloadReq;
import aishudemo.model.SingleUploadReq;

/**
 * @author Dylan.gao
 *
 */
public interface FileUploadDownloadService {
    /**
     * 文件单次上传
     * 
     * @param SingleUploadReq
     * @throws Exception
     */
    public void singleUpload(SingleUploadReq uploadReq) throws Exception;

    /**
     * 文件分块上传
     * 
     * @param MultiUploadReq
     */
    public void multiUpload(MultiUploadReq uploadBigDataReq) throws Exception;

    /**
     * 单文件下载
     * 
     * @param downloadReq
     * @throws Exception
     */
    public void singleDownload(SingleDownloadReq downloadReq) throws Exception;
}
