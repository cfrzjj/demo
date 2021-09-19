package aishudemo.util;

public class OSSException extends Exception {
    private static final long serialVersionUID = -8577512139532352966L;

    private int errCode;
    private String errBody;
    private String errHeaders;

    public OSSException(int errCode, String errBody, String errHeaders) {
        this.errCode = errCode;
        this.errBody = errBody;
        this.errHeaders = errHeaders;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrBody() {
        return errBody;
    }

    public void setErrBody(String errBody) {
        this.errBody = errBody;
    }

    public String getErrHeaders() {
        return errHeaders;
    }

    public void setErrHeader(String errHeaders) {
        this.errHeaders = errHeaders;
    }

}
