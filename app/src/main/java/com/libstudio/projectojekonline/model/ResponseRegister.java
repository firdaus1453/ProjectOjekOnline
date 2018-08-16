package com.libstudio.projectojekonline.model;

import com.google.gson.annotations.SerializedName;

public class ResponseRegister {
    // TODO 8 MEMBUAT POJO RESPONSE REGISTER
    @SerializedName("result")
    private String result;

    @SerializedName("msg")
    private String msg;

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}