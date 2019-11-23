package com.example.wtfbrowser.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

    public static boolean isUrl(String input){
        //匹配http https + www
        String regex = "(((https|http)?://)?([a-z0-9]+[.])|(www.))"
                + "\\w+[.|\\/]([a-z0-9]{0,})?[[.]([a-z0-9]{0,})]+((/[\\S&&[^,;\u4E00-\u9FA5]]+)+)?([.][a-z0-9]{0,}+|/?)";

        Pattern pattern = Pattern.compile(regex.trim());
        Matcher matcher = pattern.matcher(input.trim());
        return matcher.matches();
    }
}
