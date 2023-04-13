package com.blog.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private Map map = new HashMap();

    //这里提供了几个静态方法来返回一个Result对象
    //之前是直接用构造器的set方法也可以实现同样的功能
    //不过此种方式的复用性更高
    public static <T> Result<T> success(T obj){
        Result<T> r = new Result<>();
        r.setCode(1);
        r.setData(obj);
        return r;
    }

    public static <T> Result<T> error(String msg){
        Result<T> r = new Result<>();
        r.setCode(0);
        r.setMsg(msg);
        return r;
    }

    public Result<T> add(String key,String value){
        this.map.put(key,value);
        return this;
    }
}