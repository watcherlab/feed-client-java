package com.watcherlab.client;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.watcherlab.util.Utils;

import java.util.HashMap;

/**
 * @ClassName AdvancedQuery
 * @Desc    高级查询接口，客户端代码示例
 * @Author watcherlab
 * @Date 2019/3/27 17:05
 **/
public class AdvancedQuery {
    private static final String URL = "https://feed.watcherlab.com/api/query/advanced";
    /**
     * 请求token，注册登陆后在用户中心获取,格式：41eec916c3b46e41e9457ba9525dddbdd15d35f422e102b6fa12768c2c9fffff，
     * 注册地址：https://feed.watcherlab.com/#/user/register
     * */
    private static final String MY_TOKEN = "41eec916c3b46e41e9457ba9525dddbdd15d35f422e102b6fa12768c2c9cc0ac";

    /**
     * 高级查询接口客户端请求方法示例
     * @param value     待查询数据
     * @return
     */
    public static String query(String value) {
        String commitValue = Utils.encodeValue(value);
        if (StrUtil.isEmpty(commitValue)) {
            System.out.println("提交的参数不能为null和空字符串");
            return null;
        }
        HashMap<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("token",MY_TOKEN);
        paramMap.put("data", commitValue);
        return HttpUtil.post(URL,paramMap);
    }

    public static void main(String[] args) {
        String value = "101.27.20.237";
        System.out.println(query(value));
    }
}
