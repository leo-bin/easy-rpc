package com.bins.rpc.utils;

import com.bins.rpc.config.ThreadPoolConfig;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author leo-bin
 * @date 2020/7/23 20:27
 * @apiNote 自定义线程池工厂
 */
@Slf4j
public class ThreadPoolFactoryUtil {

    /**
     * 通过线程池名字的前缀来区分不同的线程池
     */
    private static Map<String, ExecutorService> threadPools = new ConcurrentHashMap<>(16);

    private ThreadPoolFactoryUtil() {
    }

    /**
     * 根据名字定制线程池，存在就返回，不存在就创建
     */
    public static ExecutorService createThreadPoolIfAbsent(String threadNamePrefix) {
        ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();
        ExecutorService threadPool = createThreadPoolIfAbsent(threadPoolConfig, threadNamePrefix, false);
        log.info("线程池：{}已经创建", threadNamePrefix);
        return threadPool;
    }

    public static ExecutorService createThreadPoolIfAbsent(ThreadPoolConfig threadPoolConfig,
                                                           String threadNamePrefix, Boolean daemon) {
        //如果map中有直接就返回，不存在执行后面的逻辑-创建一个线程池并存map
        ExecutorService threadPool = threadPools.computeIfAbsent(threadNamePrefix, k ->
                createThreadPool(threadPoolConfig, threadNamePrefix, daemon)
        );
        //如果拿到的线程池已经被关掉了或者被终节了那就重建一个并更新map
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            threadPools.remove(threadNamePrefix);
            threadPool = createThreadPool(threadPoolConfig, threadNamePrefix, daemon);
            threadPools.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }


    /**
     * 创建线程池
     */
    public static ExecutorService createThreadPool(ThreadPoolConfig threadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(),
                threadPoolConfig.getMaximumPoolSize(), threadPoolConfig.getKeepAliveTime(),
                threadPoolConfig.getUnit(), threadPoolConfig.getWorkQueue(), threadFactory);
    }


    /**
     * 创建线程工厂
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix == null) {
            threadNamePrefix = "Default ThreadFactory";
        }
        if (daemon) {
            return new ThreadFactoryBuilder()
                    .setNameFormat(threadNamePrefix + "-%d")
                    .setDaemon(daemon).build();
        } else {
            return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
        }
    }


    /**
     * shutDown 所有线程池
     */
    public static void shutDownAllThreadPool() {
        log.info("calling shutDownAllThreadPool method");
        threadPools.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("shut down thread pool [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Thread pool never terminated");
                executorService.shutdownNow();
            }
        });
    }


    /**
     * 打印线程池的状态
     */
    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("============ThreadPool Status=============");
            log.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
            log.info("Active Threads: [{}]", threadPool.getActiveCount());
            log.info("Number of Tasks : [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());
            log.info("===========================================");
        }, 0, 1, TimeUnit.SECONDS);
    }
}
