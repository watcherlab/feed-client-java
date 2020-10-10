package com.watcherlab.client.apt;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.watcherlab.client.config.UrlConfig;

/**
 * @ClassName AdvancedAptQuery
 * @Desc            Apt报告查询
 * @Author FCMMY
 * @Date 2019/12/2 10:56
 **/
public class AdvancedAptQuery {

    private static String URL = "/api/query/v1/aptnotes/advanced";
    /**
     * 请求token，注册登陆后在用户中心获取
     * 注册地址：https://feed.watcherlab.com/user/register
     * */
    private static final String MY_TOKEN = "my token";

    /** contentType，需要指定 */
    private static final String CONTENT_TYPE = "application/json";
    /**
     * 查询apt报告查询
     * @param param     待查询数据
     * @return
     */
    public static JSONArray query(JSONObject param) {
        if (param == null) {
            System.out.println("提交的参数不能为null和空字符串");
            return null;
        }
        String url = UrlConfig.getHost()+URL;

        HttpRequest request = new HttpRequest(url);
        request.setMethod(Method.POST).contentType(CONTENT_TYPE).header("token",MY_TOKEN).body(param.toString());
        HttpResponse response = request.execute();
        JSONObject result = JSON.parseObject(response.body());
        int code = result.getIntValue("code");
        if (response.getStatus() != 200) {
            System.out.println(String.format("apt报告查询异常：%s", result.getString("msg")));
        }

        if (code != 0) {
            System.err.println(result.getString("msg"));
            return null;
        } else {
            return result.getJSONArray("data");
        }
    }

    public static void main(String[] args) {
        JSONObject param = new JSONObject();
        /*
            {
              "search": "ioc",
              "alias": "APT组织",
              "vender": "厂商",
              "time_from": "APT报告发布起始时间",
              "time_to": "APT报告截至起始时间",
              "operandi": "攻击手法",
              "industry": "被攻击行业",
              "region": "被攻击国家地区"
            }
         */
        param.put("search", "4eb98172f2b41c8f490faae207c4dea8");
        param.put("alias", "");
        param.put("vender", "");
        param.put("time_from", "2016-01-01");
        param.put("time_to", "2019-11-01");
        param.put("operandi", "");
        param.put("industry", "");
        param.put("region", "");
        System.out.println(query(param));
    }
}
