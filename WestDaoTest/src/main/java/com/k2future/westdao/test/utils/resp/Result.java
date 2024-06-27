package com.k2future.westdao.test.utils.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Result<T> {

    private T data;
    private String message;
    private String status;

    public Result(T data, String message, String status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    /**
     * 成功请求返回JSON格式
     * @param t data
     * @return result
     */
    public static <T> Result<T> successResult(T t) {
        return new Result<T>(t,"success","200");
    }

    /**
     * 成功请求返回JSON格式
     * @return result
     */
    public static <T> Result<T> successResult() {
        return new Result<T>(null,"success","200");
    }

    /**
     * 失败后返回结果JSON格式
     * @param message messag e
     * @param t data
     * @return Result
     */
    public static <T> Result<T> failedResult(String message,T t) {
        return new Result<T>(t,message,"500");
    }
    /**
     * 失败后返回结果JSON格式
     * @param message messag e
     * @return Result
     */
    public static <T> Result<T> failedResult(String message) {
        return new Result<T>(null,message,"500");
    }

    /**
     *
     * @param message message
     * @param t data
     * @return Result
     */
    public static <T> Result<T> failedResult501(String status,String message,T t) {
        return new Result<T>(t,message,status);
    }


}
