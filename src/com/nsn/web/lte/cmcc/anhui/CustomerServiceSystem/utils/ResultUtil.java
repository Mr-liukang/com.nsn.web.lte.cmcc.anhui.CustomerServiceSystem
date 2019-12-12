package com.nsn.web.lte.cmcc.anhui.CustomerServiceSystem.utils;


public class ResultUtil {


    public static Result<?> getSuccessResult(Object data) {
        Result<?> result = new Result();
        result.setCode(1);
        result.setMsg("请求成功");
        result.setData(data);
        return result;
    }

    public static Result<?> getJtjcResult(Object data) {
        Result<?> result = new Result();
        result.setCode(1);
        result.setMsg("请求成功");
        result.setData(data);
        return result;
    }

    public static Result<?> getSuccessResult(String msg, Object data) {
        Result<?> result = new Result();
        result.setCode(1);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    public static Result<?> getErorsResult(String msg) {
        Result<?> result = new Result();
        result.setCode(0);
        result.setMsg(msg);
        return result;
    }

    public static Result<?> getErorsResult(String msg, Object data) {
        Result<?> result = new Result();
        result.setCode(0);
        result.setData(data);
        result.setMsg(msg);
        return result;
    }

    public static Result<?> getResult(Integer code, String msg) {
        Result<?> result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static Result<?> getResult(Integer code, String msg, Integer count, Object data) {
        Result<?> result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setCount(count);
        result.setData(data);
        return result;
    }


}
