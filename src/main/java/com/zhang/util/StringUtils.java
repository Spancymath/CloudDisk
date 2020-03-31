package com.zhang.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {

    //格式化日期
    public static String simpleDate(Date date) {
        //把日期格式化成字符串的一个帮助类
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return sdf.format(date);
    }

    //6位定长字符串，不足前边补0
    public static String fiexLengthFileName(String name) {
        if (name == null) name = "";
        String replaceStr = "000000";
        StringBuilder sb = new StringBuilder();
        sb.append(replaceStr, 0, replaceStr.length() - name.length())
                .append(name);
        return sb.toString();
    }
}
