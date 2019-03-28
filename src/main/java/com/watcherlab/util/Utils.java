package com.watcherlab.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.util.StrUtil;

import java.nio.charset.Charset;

/**
 * @ClassName Utils
 * @Desc
 * @Author watcherlab
 * @Date 2019/3/27 16:40
 **/
public class Utils {
    private static Charset UTF8 = Charset.forName("UTF-8");

    public static String encodeValue(String value) {
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        return Base64Encoder.encode(value,UTF8);
    }

}
