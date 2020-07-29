package com.bins.rpc.registry.zk.util;

import com.bins.rpc.enums.RpcConfigEnum;
import com.bins.rpc.exception.RpcException;
import com.bins.rpc.utils.PropertiesFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leo-bin
 * @date 2020/7/28 23:18
 * @apiNote 使用开源的Curator作为zk的客户端
 */
@Slf4j
public final class CuratorUtils {

    private static CuratorFramework zkClient;

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTRY_ROOT_PATH = "/easy-rpc";

    private static String defaultZookeeperAddress = "127.0.0.1:2181";
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>(16);

    private CuratorUtils() {
    }


    /**
     * 获取zk客户端，实现单例
     */
    public static CuratorFramework getZkClient() {
        //如果用户配置了zk地址，那就使用用户设置的属性，否则使用默认的地址
        Properties properties = PropertiesFileUtil.readPropertiesFile(RpcConfigEnum.RPC_CONFIG_PATH.getConfigValue());
        if (properties != null) {
            defaultZookeeperAddress = properties.getProperty(RpcConfigEnum.ZK_ADDRESS.getConfigValue());
        }

        //如果zk客户端实例已经初始化并启动好了，那就用现存的
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        //定义重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);

        //没有就创建一个(重试策略，连接地址，授权信息)
        zkClient = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString(defaultZookeeperAddress)
                .authorization("digest", "user:115118".getBytes())
                .build();

        zkClient.start();

        return zkClient;
    }

    /**
     * 创建一个持久化节点
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            //对path进行去重判断，防止创建重复路径
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("节点路径已经存在，节点路径是：{}", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("节点已经创建成功！路径是：{}", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取某个节点下的所有子节点
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        //已经存在直接返回
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }

        List<String> result;
        String servicePath = ZK_REGISTRY_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            //给这个节点注册一个监听器
            registerWatcher(zkClient, servicePath, rpcServiceName);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
        return result;
    }


    /**
     * 给某个节点注册一个监听器
     */
    public static void registerWatcher(CuratorFramework zkClient, String path, String rpcServiceName) {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            //如果服务所在地址有变动就重新读取配置并更新服务地址
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(path);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };

        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e.getCause());
        }
    }


    /**
     * 清除所有注册服务信息
     */
    public static void clearRegistry(CuratorFramework zkClient) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                zkClient.delete().forPath(p);
            } catch (Exception e) {
                throw new RpcException(e.getMessage(), e.getCause());
            }
        });
        log.info("所有注册的服务都已经清除:[{}]", REGISTERED_PATH_SET.toString());
    }
}
