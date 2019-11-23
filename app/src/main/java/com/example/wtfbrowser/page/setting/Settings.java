package com.example.wtfbrowser.page.setting;

public class Settings {
    public static final String headOfHTTP = "http://";
    public static final String headOfHTTPS = "https://";
    public static final String headOfWWW = "www.";
    public static final String endOfCom = ".com";
    public static final String search_baidu(String input){
        return "https://www.baidu.com/s?wd="+input+"&ie=UTF-8";
    }
    public static final int DOUBLE_PRESS_BACK_GAP = 2000;
    public static final String HOME_URL = "https://www.baidu.com";
}