package com.watcherlab.client.download;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;

/**
 * @ClassName NewDownloadAdvanced
 * @Desc            新版日增·日活·日总数据下载demo
 * @Author FCMMY
 * @Date 2019/10/31 10:36
 **/
public class AdvancedDownload {
    /** 数据文件本地存放目录，需要自己指定:示例  D:\TEST_DIR\save */
    private static final String SAVE_PATH = "my dir";
    /** 接口请求地址 */
    private static final String DOWNLOAD_URL = "https://feed.watcherlab.com/api/download/v1/advanced";
    /**
     * 请求token，注册登陆后在用户中心获取
     * 注册地址：https://feed.watcherlab.com/user/register
     * */
    private static final String MY_TOKEN = "my token";
    /** contentType，需要指定 */
    private static final String CONTENT_TYPE = "application/json";
    /** PERIOD，现在数据的时间段 */
    private static final String[] PERIOD = new String[]{"1day","15day","30day"};
    /** 请求参数 */
    private static JSONObject PARAM = new JSONObject();

    /**
     * 数据下载
     */
    private static void download(String type, String dateStr) {
        // 第一次请求：获取数据列表和对应的cursor值
        System.out.println("第一次请求：获取数据列表和对应的cursor值");
        HttpRequest request = new HttpRequest(DOWNLOAD_URL);
        PARAM.put("token",MY_TOKEN);
        PARAM.put("type", type);
        PARAM.put("cursor", 0);
        PARAM.put("date", dateStr);
        request.setMethod(Method.POST).contentType(CONTENT_TYPE).body(PARAM.toString());
        HttpResponse response = request.execute();
        if (response.getStatus() != 200) {
            System.out.println(String.format("ERROR CODE:[%d] %s",response.getStatus(),response.body()));
            return;
        }
        JSONObject dataCursor = JSONObject.parseObject(response.body());
        System.out.println(dataCursor.toJSONString());
        // 如果响应状态不是1，那么打印message中的报错提示信息
        if (dataCursor.getIntValue("code") != 0) {
            System.out.println(String.format("ERROR : %s",dataCursor.getString("message")));
            return;
        }
        // 返回结果中有newly：日增 actively：日活 data：日总量
        getNewly(request,dataCursor,dateStr);
        getActively(request,dataCursor,dateStr);
        getData(request,dataCursor,dateStr);
    }

    /**
     * 获取newly数据
     * @param request           request
     * @param dataCursor        首次请求返回的结果
     *
     */
    private static void getNewly(HttpRequest request, JSONObject dataCursor, String dateStr) {
        JSONObject newly = dataCursor.getJSONObject("data").getJSONObject("newly");
        for (String p : PERIOD) {
            JSONArray pd = newly.getJSONArray(p);
            if (pd != null && pd.size() > 0) {
                for (Object data : pd) {
                    if (data != null) {
                        String filePrefix = dateStr + File.separator + "newly_" + p;
                        secondRequest(request, (JSONObject) data, filePrefix);
                    }
                }
            }
        }
    }

    /**
     * 获取actively数据文件
     * @param request           request
     * @param dataCursor        首次请求返回的结果
     */
    private static void getActively(HttpRequest request, JSONObject dataCursor, String dateStr) {
        JSONObject actively = dataCursor.getJSONObject("data").getJSONObject("actively");
        for (String p : PERIOD) {
            JSONArray pd = actively.getJSONArray(p);
            if (pd != null && pd.size() > 0) {
                for (Object data : pd) {
                    if (data != null) {
                        String filePrefix = dateStr + File.separator + "actively_" + p;
                        secondRequest(request, (JSONObject) data, filePrefix);
                    }
                }
            }
        }
    }

    /**
     * 获取日总量数据（很多是与之前的数据重复的）
     * @param request           request
     * @param dataCursor        首次请求返回的结果
     */
    private static void getData(HttpRequest request, JSONObject dataCursor, String dateStr) {
        JSONArray dataArray = dataCursor.getJSONObject("data").getJSONArray("data");
        if (dataArray != null && dataArray.size() > 0) {
            for (Object data : dataArray) {
                if (data != null) {
                    String filePrefix = dateStr + File.separator + "all";
                    secondRequest(request, (JSONObject) data, filePrefix);
                }
            }
        }

    }

    /**
     * 向服务器发起二次请求下载文件到本地
     * @param request       request
     * @param dataObj       文件类型
     */
    private static void secondRequest(HttpRequest request, JSONObject dataObj, String filePrefix) {
        String dataName = dataObj.getString("dataName");
        int cursor = dataObj.getIntValue("cursor");
        PARAM.put("type", dataName);
        PARAM.put("cursor", cursor);
        HttpResponse response = request.body(PARAM.toString()).execute();
        if (response.getStatus() != 200) {
            System.out.println(String.format("ERROR CODE:[%d] %s",response.getStatus(),response.body()));
            return;
        }
        String fileName = filePrefix + "_" + dataName;
        String destFile = String.format("%s%s%s.json.gz",SAVE_PATH, File.separator,fileName);
        try {
            response.writeBody(destFile);
            File file = new File(destFile);
            String md5Str = DigestUtil.md5Hex(file);
            // 判断MD5是否一直
            if (md5Str.equalsIgnoreCase(dataObj.getString("md5"))) {
                System.out.println("下载成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 下载类型,首次请求type参数必须为all
        String type = "all";
        // 下载时间，需要指定
        String dateStr = "20191124";
        download(type,dateStr);
    }
}
