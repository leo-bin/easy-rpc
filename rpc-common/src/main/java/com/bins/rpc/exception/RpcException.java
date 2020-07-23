package com.bins.rpc.exception;

import com.bins.rpc.enums.RpcErrorMessageEnum;

/**
 * @author leo-bin
 * @date 2020/7/23 20:20
 * @apiNote 自定义rpc异常
 */
public class RpcException extends RuntimeException {

    public RpcException(RpcErrorMessageEnum errorMessageEnum, String detail) {
        super(errorMessageEnum.getMessage() + " :" + detail);
    }

    public RpcException(RpcErrorMessageEnum errorMessageEnum) {
        super(errorMessageEnum.getMessage());
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
