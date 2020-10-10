package com.watcherlab.client.ioc;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.watcherlab.client.config.UrlConfig;

/**
 * @ClassName AnonymousQuery
 * @Desc        匿名查询demo
 * @Author FCMMY
 * @Date 2019/11/29 16:41
 **/
public class AnonymousQuery {
    /** 请求地址*/
    //private static final String URL = "https://feed.watcherlab.com/api/query/v1/";
    private static String URL = "/api/query/v1/";
    /**
     * 客户端请求
     * @param value     请求数据
     * @return
     */
    public static JSONObject query(String value) {
        String commitValue = HexUtil.encodeHexStr(value);
        if (StrUtil.isEmpty(commitValue)) {
            System.out.println("提交的参数不能为null和空字符串");
            return null;
        }
        String url = UrlConfig.getHost()+URL;
        String queryUrl = url + commitValue;
        String responseStr = HttpUtil.get(queryUrl);
        JSONObject result = JSON.parseObject(responseStr);
        int code = result.getIntValue("code");
        if (code != 0) {
            System.err.println(result.getString("msg"));
            return null;
        } else {
            return result.getJSONObject("data");
        }
    }

    public static void main(String[] args) {
        String value = "1.198.89.248";
        System.out.println(query(value));
    }
}
