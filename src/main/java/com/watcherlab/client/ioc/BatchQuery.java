package com.watcherlab.client.ioc;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.watcherlab.client.config.UrlConfig;

/**
 * @ClassName BatchQuery
 * @Desc        批量查询接口客户端请求方法demo
 * @Author FCMMY
 * @Date 2019/11/29 18:05
 **/
public class BatchQuery {
    private static final String URL = "/api/query/v1/many";

    /**
     * 请求token，注册登陆后在用户中心获取
     * 注册地址：https://feed.watcherlab.com/user/register
     * */
    private static final String MY_TOKEN = "my token";

    /** contentType，需要指定 */
    private static final String CONTENT_TYPE = "application/json";
    /**
     * 批量查询接口客户端请求方法示例
     * @param iocs     待查询数据
     * @return
     */
    public static JSONObject query(JSONArray iocs) {
        if (iocs == null) {
            System.out.println("提交的参数不能为null");
            return null;
        }
        String url = UrlConfig.getHost()+URL;
        HttpRequest request = new HttpRequest(url);
        request.setMethod(Method.POST).contentType(CONTENT_TYPE).header("token",MY_TOKEN).body(iocs.toJSONString());
        HttpResponse response = request.execute();
        JSONObject result = JSON.parseObject(response.body());
        int code = result.getIntValue("code");
        if (response.getStatus() != 200 || code != 0) {
            System.out.println(String.format("批量查询异常：%s", result.getString("msg")));
            return null;
        } else {
            return result.getJSONObject("data");
        }
    }

    public static void main(String[] args) {
        JSONArray params = new JSONArray();
        params.add("1.198.108.16");
        params.add("ecrimen.com");
        params.add("001@dra0.dc7.us");
        params.add("http://oracellbd.com/dfjkgy7");
        params.add("019478880c0a2d4a023ff7dfcbac9099");

        JSONObject data = query(params);
        // 解析结果
        if (data != null) {
            // 包括ip、domain、ssl、email、url、hash每个数据类型对应的是一个数组
            JSONObject iocObj = data.getJSONObject("iocs");
            if (iocObj != null) {
                System.out.println(iocObj.toJSONString());
            }
            // rules:suricata 和 yara规则
            JSONObject rules = data.getJSONObject("rules");
            if (rules != null) {
                System.out.println(rules.toJSONString());
            }
            // other:其他信息
            JSONObject other = data.getJSONObject("other");
            if (other != null) {
                System.out.println(other.toJSONString());
            }
        }

    }
}
