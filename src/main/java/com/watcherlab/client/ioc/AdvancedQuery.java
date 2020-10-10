package com.watcherlab.client.ioc;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.watcherlab.client.config.UrlConfig;

/**
 * @ClassName AdvancedQuery
 * @Desc    高级查询接口，客户端代码示例
 * @Author watcherlab
 * @Date 2019/3/27 17:05
 **/
public class AdvancedQuery {
    //private static final String URL = "https://feed.watcherlab.com/api/query/v1/advanced";
    private static final String URL = "/api/query/v1/advanced";
    /**
     * 请求token，注册登陆后在用户中心获取
     * 注册地址：https://feed.watcherlab.com/user/register
     * */
    private static final String MY_TOKEN = "my token";
    /** contentType，需要指定 */
    private static final String CONTENT_TYPE = "application/json";
    /**
     * 高级查询接口客户端请求方法示例
     * @param value     待查询数据
     * @return
     */
    public static JSONObject query(String value) {
        if (StrUtil.isEmpty(value)) {
            System.out.println("提交的参数不能为null和空字符串");
            return null;
        }

        JSONObject param = new JSONObject();
        param.put("token",MY_TOKEN);
        param.put("data", value);
        String url = UrlConfig.getHost()+URL;
        HttpRequest request = new HttpRequest(url);
        request.setMethod(Method.POST).contentType(CONTENT_TYPE).body(param.toJSONString());
        HttpResponse response = request.execute();
        if (response.getStatus() != 200) {
            System.out.println(String.format("apt报告查询异常：%s", response.body()));
            return null;
        }
        JSONObject result = JSON.parseObject(response.body());
        int code = result.getIntValue("code");
        if (code != 0) {
            System.err.println(result.getString("msg"));
            return null;
        } else {
            return result.getJSONObject("data");
        }
    }

    public static void main(String[] args) {
        String value = "101.27.20.237";
        System.out.println(query(value));
    }
}
