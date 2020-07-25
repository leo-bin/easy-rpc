package com.bins.rpc.entity;

import lombok.*;

import java.io.Serializable;


/**
 * @author leo-bin
 * @date 2020/7/25 17:12
 * @apiNote Rpc服务特有属性
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcServiceProperties implements Serializable {
    /**
     * 服务名（统一使用接口名）
     */
    private String serviceName;
    /**
     * 服务专属标签，为了取分同一接口下的多个实现类
     */
    private String tag;

    /**
     * 简单通过服务接口名+专属标签生成唯一服务名
     */
    public String getUniqueServiceName() {
        return serviceName + "-" + tag;
    }
}
