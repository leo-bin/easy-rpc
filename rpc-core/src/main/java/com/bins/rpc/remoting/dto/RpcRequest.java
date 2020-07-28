package com.bins.rpc.remoting.dto;

import com.bins.rpc.entity.RpcServiceProperties;
import com.bins.rpc.enums.RpcMessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author leo-bin
 * @date 2020/7/23 18:22
 * @apiNote Rpc消息请求体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private RpcMessageTypeEnum rpcMessageTypeEnum;
    private RpcServiceProperties serviceProperties;
}
