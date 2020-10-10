package com.watcherlab.client.config;

import cn.hutool.core.util.StrUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/**
 * @ClassName UrlConfig
 * @Desc        url配置
 * @Author ld
 * @Date 2020-10-10 15:07:13
 **/
public class UrlConfig {

    /**
     * 默认协议
     */
    private static String pre = "https://";
    /**
     * 默认地址
     */
    private static String host = "feed.watcherlab.com";

    /**
     * 获取地址
     *      例:https://feed.watcherlab.com
     * @return
     */
    public static String getHost() {
        try {
            //通过类加载器 加载配置文件
            Properties p = new Properties();
            InputStream in = UrlConfig.class.getClassLoader().getResourceAsStream("feed-config.properties");
            p.load(in);
            String hostName = p.getProperty("hostName");

            if ( StrUtil.isNotBlank(hostName)) {
                host = hostName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return pre+host;
        }
    }

}
