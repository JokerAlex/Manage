package com.dzhy.manage.dto;

import com.dzhy.manage.enums.ResultEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

/**
 * @ClassName ResponseDTO
 * @Description 返回类
 * @Author alex
 * @Date 2018/10/30
 **/
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {
    private int status;
    private String msg;
    private T data;

    private ResponseDTO(int status){
        this.status = status;
    }
    private ResponseDTO(int status, T data){
        this.status = status;
        this.data = data;
    }

    private ResponseDTO(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ResponseDTO(int status, String msg){
        this.status = status;
        this.msg = msg;
    }

    /**
     * 判断请求是否成功
     * 注解使之不在json序列化结果当中
     * @return
     */
    @JsonIgnore
    public boolean isOk(){
        return this.status == ResultEnum.SUCCESS.getCode();
    }

    public static <T> ResponseDTO<T> isSuccess(){
        return new ResponseDTO<T>(ResultEnum.SUCCESS.getCode());
    }

    public static <T> ResponseDTO<T> isSuccess(String msg){
        return new ResponseDTO<T>(ResultEnum.SUCCESS.getCode(),msg);
    }

    public static <T> ResponseDTO<T> isSuccess(T data){
        return new ResponseDTO<T>(ResultEnum.SUCCESS.getCode(),data);
    }

    public static <T> ResponseDTO<T> isSuccess(String msg, T data){
        return new ResponseDTO<T>(ResultEnum.SUCCESS.getCode(),msg,data);
    }


    public static <T> ResponseDTO<T> isError(){
        return new ResponseDTO<T>(ResultEnum.ERROR.getCode(),ResultEnum.ERROR.getMessage());
    }


    public static <T> ResponseDTO<T> isError(String errorMessage){
        return new ResponseDTO<T>(ResultEnum.ERROR.getCode(),errorMessage);
    }

    public static <T> ResponseDTO<T> isError(int errorCode, String errorMessage){
        return new ResponseDTO<T>(errorCode,errorMessage);
    }

}
