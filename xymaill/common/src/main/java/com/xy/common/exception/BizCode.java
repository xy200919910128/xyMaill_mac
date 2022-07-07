package com.xy.common.exception;

public enum BizCode {
    UNKNOWN_EXCEPTION(10000,"未知异常"),
    RUNTIME_EXCEPTION(10001,"程序运行的异常"),
    VALID_EXCEPTION(10002,"数据校验异常"),
    SMS_VALID_EXCEPTION(11001,"短信验证码频率过快异常"),
    PRODUCT_UP_EXCEPTION(12000,"商品上架异常");

    private Integer code;
    private String msg;

    private BizCode(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
