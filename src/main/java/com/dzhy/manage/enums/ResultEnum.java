package com.dzhy.manage.enums;

import lombok.Getter;

/**
 * @ClassName ResultEnum
 * @Description 请求返回码枚举
 * @Author alex
 * @Date 2018/10/30
 **/
@Getter
public enum ResultEnum {

    /**
     * 统一回复，请求成功
     */
    SUCCESS(0, "请求成功"),

    /**
     * 统一回复，请求失败
     */
    ERROR(1, "请求失败"),

    /**
     * 统一回复，参数错误
     */
    ILLEGAL_PARAMETER(2, "参数错误"),

    ADD_ERROR(1000, "添加失败"),

    UPDATE_ERROR(1001, "更新失败"),

    DELETE_ERROR(1002, "删除失败"),

    NOT_FOUND(1003, "未找到结果"),

    UNUSABLE_NAME(1004, "名称不可用"),

    ILLEGAL_FILE_TYPE(1005, "文件类型错误"),

    IS_EXIST(1006, "该条目已存在"),

    EXPORT_ERROR(1007, "导出失败"),

    IMPORT_ERROR(1008, "导入失败"),

    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
