package com.watcherlab.client.gb;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.watcherlab.client.config.UrlConfig;

/**
 * @ClassName GbQuery
 * @Desc        国标数据查询
 * @Author FCMMY
 * @Date 2019/11/4 13:21
 **/
public class GbQuery {
    //private static final String DOWNLOAD_URL = "https://feed.watcherlab.com/api/query/v1/gbt";
    private static final String DOWNLOAD_URL = "/api/query/v1/gbt";
    /**
     * 请求token，注册登陆后在用户中心获取
     * 注册地址：https://feed.watcherlab.com/user/register
     * */
    private static final String MY_TOKEN = "my token";
    /** contentType 必须是json格式 */
    private static final String CONTENT_TYPE = "application/json";

    /**
     * 查询单条IOC
     * @param value         IOC值
     * @return
     *      返回国标格式的数据
     */
    public static String singleQuery(String value) {
        JSONObject param = new JSONObject();
        param.put("type", "data");
        param.put("data", value);
        param.put("cursor", 0);

        String url = UrlConfig.getHost()+DOWNLOAD_URL;
        HttpRequest request = new HttpRequest(url);
        request.setMethod(Method.POST).contentType(CONTENT_TYPE).header("token",MY_TOKEN).body(param.toString());
        try {
            HttpResponse response = request.execute();
            if (response.getStatus() != 200) {
                System.out.println(String.format("ERROR CODE:[%d] %s", response.getStatus(), response.body()));
                return null;
            } else {
                JSONObject resultObj = JSONObject.parseObject(response.body());
                int code = resultObj.getIntValue("code");
                if (code != 0) {
                    System.out.println(response.body());
                } else {
                    return resultObj.getString("data");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据可观测数据的标志号查询
     * @param value             可观测数据标志号，目前标志号暂不提供
     * @return
     *          放回响应结果
     */
    private static String queryById(String value) {
        JSONObject observation = new JSONObject();
        JSONArray objectArr = new JSONArray();
        JSONObject param = new JSONObject();
        param.put("type", "id");
        param.put("data", value);
        param.put("cursor", 0);
        int cursor = 0;
        String url = UrlConfig.getHost()+DOWNLOAD_URL;
        HttpRequest request = new HttpRequest(url);
        request.setMethod(Method.POST).contentType(CONTENT_TYPE).header("token",MY_TOKEN);
        JSONObject result = getResponse(request, param);
        if (result != null) {
            JSONObject firstData = result.getJSONObject("data");
            cursor = firstData.getIntValue("cursor");
            observation = firstData.getJSONObject("observation");
            param.put("cursor", cursor);
            while (cursor != -1) {
                JSONObject secondResult = getResponse(request, param);
                if (secondResult != null) {
                    JSONObject secondData = secondResult.getJSONObject("data");
                    // 一次请求返回的数据，如果针对每次请求的数据做处理，可以在此处操作
                    JSONArray objects = secondData.getJSONArray("value");
                    objectArr.addAll(objects);
                    cursor = secondData.getIntValue("cursor");
                    param.put("cursor", cursor);
                } else {
                    System.out.println("服务器没有响应数据");
                    return null;
                }
            }
            // 将获取的数据放到observation中
            JSONArray objects = observation.getJSONArray("object");
            if (objects != null && objects.size() > 0){
                JSONObject object = objects.getJSONObject(0);
                object.put("value",objectArr);
            }
        } else {
            System.out.println("服务器没有响应数据");
            return null;
        }
        return observation.toJSONString();
    }

    /**
     * 发送请求并获取响应结果
     * @param request           request
     * @param param             参数
     * @return
     *          响应结果
     */
    private static JSONObject getResponse(HttpRequest request, JSONObject param) {
        try {
            request.body(param.toJSONString());
            long start = System.currentTimeMillis();
            HttpResponse response = request.execute();
            System.out.println("本次请求共计耗时: "+(System.currentTimeMillis() - start));
            if (response.getStatus() != 200) {
                System.out.println(String.format("ERROR CODE:[%d] %s", response.getStatus(), response.body()));
                return null;
            } else {
                JSONObject resultObj = JSONObject.parseObject(response.body());
                int code = resultObj.getIntValue("code");
                if (code != 0) {
                    System.out.println(response.body());
                    return null;
                } else {
                    System.out.println(resultObj.toJSONString());
                    return resultObj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        // 单条数据国标查询
        System.out.println(singleQuery("144.217.166.59"));
    }
}
