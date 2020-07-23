package com.bins.rpc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author leo-bin
 * @date 2020/7/23 18:33
 * @apiNote Rpc响应码
 */
@AllArgsConstructor
@Getter
@ToString
public enum RpcResponseCode {

    SUCCESS(200, "调用方法成功"),

    FAIL(500, "调用方法失败"),

    NOT_FOUND_METHOD(500, "未找到指定方法"),

    NOT_FOUND_CLASS(500, "未找到指定类");

    private final int code;
    private final String message;

}
