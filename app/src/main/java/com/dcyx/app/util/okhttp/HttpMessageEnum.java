package com.dcyx.app.util.okhttp;

/**
 * Created by xue on 2018/5/8.
 */

public enum HttpMessageEnum {
    NETWORK_ERROR(-1,"网络错误，请检查网络！"),
    RESPONSE_EMPTY_ERROR(-2,"服务端响应数据为空！"),
    SERVERS_ERROR(-5,"服务端返回数据失败！"),
    OTHER_ERROR(-3,"网络错误，请检查网络！"),
    SERVER_NOT_FOUND(-4,"找不到服务！");
    int  mCode;
    String mInforDesc;
    HttpMessageEnum(int code, String infordesc){
        this.mCode = code;
        this.mInforDesc = infordesc;
    }

    public String getmInforDesc() {
        return mInforDesc;
    }

    public void setmInforDesc(String mInforDesc) {
        this.mInforDesc = mInforDesc;
    }

    public int getmCode() {
        return mCode;
    }

    public void setmCode(int mCode) {
        this.mCode = mCode;
    }
}
