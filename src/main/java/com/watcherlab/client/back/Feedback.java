package com.watcherlab.client.back;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.watcherlab.client.config.UrlConfig;

/**
 * @ClassName Feedback
 * @Desc        用户反馈api demo
 * @Author FCMMY
 * @Date 2019/12/5 17:18
 **/
public class Feedback {
    //private static final String DOWNLOAD_URL = "https://feed.watcherlab.com/api/feedback/v1";
    private static final String DOWNLOAD_URL = "/api/feedback/v1";
    /**
     * 请求token，注册登陆后在用户中心获取
     * 注册地址：https://feed.watcherlab.com/user/register
     * */
    private static final String MY_TOKEN = "my token";
    /** contentType 必须是json格式 */
    private static final String CONTENT_TYPE = "application/json";

    public static void back(JSONObject feedBack) {
        String url = UrlConfig.getHost()+DOWNLOAD_URL;

        HttpRequest request = new HttpRequest(url);
        request.setMethod(Method.POST).contentType(CONTENT_TYPE).header("token",MY_TOKEN).body(feedBack.toString());
        try {
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                System.out.println(String.format("ERROR CODE:[%d] %s", response.getStatus(), response.body()));
            } else {
                JSONObject resultObj = JSONObject.parseObject(response.body());
                int code = resultObj.getIntValue("code");
                if (code != 0) {
                    System.out.println(response.body());
                } else {
                    System.out.println("感谢反馈...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JSONObject feedBackObj = new JSONObject();
        // 反馈的ioc数据
        feedBackObj.put("data", "2.3.3.3");
        // 是否被命中，只能是boolean值
        feedBackObj.put("hit", true);
        // 命中次数，只能是整型
        feedBackObj.put("hit_number", 100);
        // 是否误报，仅允许boolean值
        feedBackObj.put("misuse", true);
        // 误报次数，仅允许整型
        feedBackObj.put("misuse_number", 10);
        // 上报原因
        feedBackObj.put("reason", "feed back api test!");
        // 反馈信息的设备
        feedBackObj.put("device", "Firewall test");
        Feedback.back(feedBackObj);
    }
}
