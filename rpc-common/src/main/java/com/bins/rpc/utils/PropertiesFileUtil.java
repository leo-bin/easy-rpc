package com.bins.rpc.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author leo-bin
 * @date 2020/7/28 23:25
 * @apiNote 读取文件工具类
 */
@Slf4j
public final class PropertiesFileUtil {

    private PropertiesFileUtil() {
    }

    /**
     * 读取文件中的属性
     */
    public static Properties readPropertiesFile(String fileName) {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String rpcConfigPath = rootPath + fileName;
        Properties properties = null;
        try (FileInputStream fileInputStream = new FileInputStream(rpcConfigPath)) {
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            log.error("occur exception when read properties file [{}]", fileName);
        }
        return properties;
    }
}
