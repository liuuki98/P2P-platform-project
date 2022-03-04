package com.liuuki.srb.vo;

import lombok.Data;

@Data
public class R<T> {
    private Integer code;
    private String message;
    private T data;

    private R(){};//私有化构造器

    /**
     * 返回请求 成功 时候的code状态码和message提示信息
     * @return
     */
    public static R success(){
        R r =new R();
        r.setCode(ResultEnum.SUCCESS.getCode());
        r.setMessage(ResultEnum.SUCCESS.getMessage());
        return r;
    }

    /**
     * 返回请求 失败 时候的code状态码和message提示信息
     * @return
     */
    public static R error(){
        R r =new R();
        r.setCode(ResultEnum.ERROR.getCode());
        r.setMessage(ResultEnum.ERROR.getMessage());
        return r;
    }

    /**
     * 设置特定结果
     */
    public static R setResult(ResultEnum responseEnum){
        R r = new R();
        r.setCode(responseEnum.getCode());
        r.setMessage(responseEnum.getMessage());
        return r;
    }

    public R message(String message){

        this.setMessage(message);
        return this;
    }

    public R code(Integer code){
        this.code(code);
        return this;
    }

    public R<T> data(T data){
        this.setData(data);
        return this;
    }



}
