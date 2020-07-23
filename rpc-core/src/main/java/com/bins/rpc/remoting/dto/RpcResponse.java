package com.bins.rpc.remoting.dto;

import com.bins.rpc.enums.RpcResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author leo-bin
 * @date 2020/7/23 18:22
 * @apiNote Rpc消息响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private Integer code;
    private String message;
    private T data;

    /**
     * 封装请求成功之后的实体
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setCode(RpcResponseCode.SUCCESS.getCode());
        response.setMessage(RpcResponseCode.SUCCESS.getMessage());
        if (data != null) {
            response.setData(data);
        }
        return response;
    }

    /**
     * 封装请求失败的实体
     */
    public static <T> RpcResponse<T> fail(RpcResponseCode rpcResponseCode) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(rpcResponseCode.getCode());
        response.setMessage(rpcResponseCode.getMessage());
        return response;
    }
}
