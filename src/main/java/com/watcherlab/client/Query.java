package com.watcherlab.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.watcherlab.util.Utils;

/**
 * @ClassName Query
 * @Desc    普通用户查询接口，客户端代码请求示例
 * @Author watcherlab
 * @Date 2019/3/27 16:33
 **/
public class Query {
    /** 请求地址*/
    private static final String URL = "https://feed.watcherlab.com/api/query/";

    /**
     * 客户端请求
     * @param value     请求数据
     * @return
     */
    public static String query(String value) {
        String commitValue = Utils.encodeValue(value);
        if (StrUtil.isEmpty(commitValue)) {
            System.out.println("提交的参数不能为null和空字符串");
            return null;
        }
        String queryUrl = URL + commitValue;
        return HttpUtil.get(queryUrl);
    }

    public static void main(String[] args) {
        String value = "1.198.89.248";
        System.out.println(query(value));
    }
}
