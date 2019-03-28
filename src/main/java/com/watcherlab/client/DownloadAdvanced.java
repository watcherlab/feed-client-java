package com.watcherlab.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.watcherlab.constant.Constant;

import java.io.*;
import java.util.HashMap;

/**
 * @ClassName DownloadAdvanced
 * @Desc    合作伙伴下载数据API 客户端demo
 * @Author watcherlab
 * @Date 2019/3/26 12:07
 **/
public class DownloadAdvanced {
    /** 数据文件本地存放目录，需要自己指定 */
    private static final String SAVE_PATH = "D:\\TEST_DIR\\save";
    /** 接口请求地址 */
    private static final String DOWNLOAD_URL = "https://feed.watcherlab.com/api/download/advanced";
    /** 请求token，注册登陆后在用户中心获取，注册地址：https://feed.watcherlab.com/#/user/register */
    private static final String MY_TOKEN = "41eec916c3b46e41e9457ba9525dddbdd15d35f422e102b6fa12768c2c9cc0ac";
    /** 下载时间，需要指定 */
    private static final String DATE_STR = "20190324";

    /**
     * 数据下载
     */
    private static void download() {
        HashMap<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("token",MY_TOKEN);
        paramMap.put("type", "all");
        paramMap.put("cursor", 0);
        paramMap.put("date", DATE_STR);
        HashMap<String, JSONObject> dataCursorMap = new HashMap<>(20);
        // 第一次请求：获取数据列表和对应的cursor值
        System.out.println("第一次请求：获取数据列表和对应的cursor值");
        HttpResponse response1 = HttpRequest.post(DOWNLOAD_URL).form(paramMap).execute();
        if (response1.getStatus() != 200) {
            System.out.println(String.format("ERROR CODE:[%d] %s",response1.getStatus(),response1.body()));
            return;
        }
        JSONObject dataCursor = JSONUtil.parseObj(response1.body());
        // 如果响应状态不是1，那么打印message中的报错提示信息
        if (dataCursor.getInt("status") != 1) {
            System.out.println(String.format("ERROR : %s",dataCursor.getStr("message")));
            return;
        }
        JSONArray dataArr = JSONUtil.parseArray(dataCursor.get("data"));
        for (Object dataObj : dataArr) {
            JSONObject dataJsonObj = (JSONObject) dataObj;
            dataCursorMap.put(dataJsonObj.getStr("dataName"), dataJsonObj);
        }
        String cursor = dataCursorMap.get(Constant.ALL).getStr("cursor");
        String dataName = Constant.ALL;
        // 第二次请求：下载文件指定文件，demo中以下载all为例，需要对type和cursor重新赋值
        System.out.println("第二次请求：下载指定文件，demo中以下载all为例，需要对type和cursor重新赋值");
        paramMap.put("type", dataName);
        paramMap.put("cursor", cursor);
        HttpResponse response2 = HttpRequest.post(DOWNLOAD_URL).form(paramMap).execute();
        if (response2.getStatus() != 200) {
            System.out.println(String.format("ERROR CODE:[%d] %s",response2.getStatus(),response2.body()));
            return;
        }
        String destFile = String.format("%s%s%s.json.gz",SAVE_PATH,File.separator,dataName);
        response2.writeBody(destFile);
        System.out.println("下载成功");
    }

    /**
     * 程序入口
     * @param args
     */
    public static void main(String[] args) {
        download();
    }
}
