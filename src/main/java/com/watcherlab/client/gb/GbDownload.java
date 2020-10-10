package com.watcherlab.client.gb;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.watcherlab.client.config.UrlConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * @ClassName GbDownload
 * @Desc        国标数据下载示例代码
 *              url：https://feed.watcherlab.com/api/download/v1/gbt
 * @Author FCMMY
 * @Date 2019/11/4 13:22
 **/
public class GbDownload {
    //private static final String DOWNLOAD_URL = "https://feed.watcherlab.com/api/download/v1/gbt";
    private static final String DOWNLOAD_URL = "/api/download/v1/gbt";
    /**
     * 请求token，注册登陆后在用户中心获取
     * 注册地址：https://feed.watcherlab.com/user/register
     * */
    private static final String MY_TOKEN = "my token";
    /** contentType，需要指定 */
    private static final String CONTENT_TYPE = "application/json";
    /** OUT_PATH，下载结果输出目录，需要指定，例：D:\TEST_DIR\api */
    private static final String OUT_PATH = "my dir";
    /** 请求参数 */
    private static JSONObject PARAM = new JSONObject();

    /**
     * 下载国标数据方法示例
     * @param type      下载的channel类型
     * @param clazz     需要下载国标中哪种元素的情报，目前仅支持"可观测数据(observation)
     * @param date      指定日期
     */
    private void download(String type, String clazz, String date) {
        // 第一次请求：获取数据列表和对应的cursor值
        System.out.println("第一次请求：获取数据列表和对应的cursor值");
        String url = UrlConfig.getHost()+DOWNLOAD_URL;
        HttpRequest request = new HttpRequest(url);
        PARAM.put("type", type);
        // 目前仅支持可观测数据的国标的下载
        PARAM.put("class",clazz);
        PARAM.put("cursor", 0);
        PARAM.put("date", date);
        File outFile = getFile(type, clazz, date);
        request.setMethod(Method.POST).contentType(CONTENT_TYPE).header("token",MY_TOKEN);
        JSONObject result = getResponse(request, PARAM);

        int cursor = 0;
        if (result != null) {
            cursor = result.getJSONObject("data").getIntValue("cursor");
            while (cursor != -1) {
                PARAM.put("cursor", cursor);
                JSONObject dataResult = getResponse(request, PARAM);
                if (dataResult != null) {
                    JSONObject data = dataResult.getJSONObject("data");
                    if (data == null) {
                        continue;
                    }
                    cursor = data.getIntValue("cursor");
                    JSONArray valueArr = data.getJSONArray("value");
                    StringBuilder temp = new StringBuilder();
                    for (Object value : valueArr) {
                        temp.append(((JSONObject) value).toJSONString()).append("\n");
                    }
                    writeData(outFile, temp);
                    PARAM.put("cursor", cursor);
                }
            }
        }
    }

    /**
     * 发送请求并获取响应结果
     * @param request       request
     * @param param         参数
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
                System.out.println(resultObj.toJSONString());
                int code = resultObj.getIntValue("code");
                if (code != 0) {
                    System.out.println(response.body());
                    return null;
                } else {
                    return resultObj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取要保存的本地文件
     * @param type      数据类型
     * @param clazz     channel
     * @param date      日期
     * @return
     *      本地要保存的文件
     */
    private File getFile(String type, String clazz, String date) {
        File fileDir = new File(OUT_PATH);
        if (!fileDir.exists()) {
            if (!fileDir.mkdirs()) {
                System.out.println("创建结果目录失败：" + OUT_PATH);
            }
        }
        // OUT_PATH/observation-ip_c2-20191122.json
        String outFilePath = String.format("%s%s%s-%s-%s.json", OUT_PATH, File.separator, clazz, type, date);
        File outFile = new File(outFilePath);
        if (!outFile.exists()) {
            try {
                if (!outFile.createNewFile()) {
                    System.out.println("情报输出文件创建失败：" + outFilePath);
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outFile;
    }

    /**
     * 写入文件
     * @param outFile      文件路径
     * @param sb           需要写入的内容
     */
    private void writeData(File outFile, StringBuilder sb) {
        FileOutputStream outputStream;
        try {
            if (outFile == null) {
                System.err.println("结果输出文件获取失败，文件为null！");
                return;
            }
            if (outFile.isDirectory()) {
                System.err.println("结果输出文件不能是一个目录！");
                return;
            }
            if (!outFile.exists()) {
                if (!outFile.createNewFile()) {
                    System.out.println("情报输出文件创建失败：" + outFile);
                    return;
                }
            }
            outputStream = new FileOutputStream(outFile, true);
            FileChannel fileChannel = outputStream.getChannel();
            CharBuffer charBuffer = CharBuffer.allocate(1024 * 1024);
            String str2 = new String(sb);
            charBuffer.put(str2);
            charBuffer.flip();
            Charset charset = Charset.defaultCharset();
            ByteBuffer byteBuffer = charset.encode(charBuffer);
            try {
                while (byteBuffer.hasRemaining()) {
                    fileChannel.write(byteBuffer);
                }
                sb.delete(0, sb.length());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    fileChannel.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        // 下载的channel类型，需要指定，可选数据类型参考：ChannelList.java
        String type = "ip_c2";
        // 需要下载国标中哪种元素的情报，需要指定，目前仅支持"可观测数据(observation)"
        String clazz = "observation";
        // 下载时间，需要指定
        String dataStr = "20191120";
        new GbDownload().download(type,clazz,dataStr);
    }

}
