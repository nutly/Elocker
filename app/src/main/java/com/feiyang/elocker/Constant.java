package com.feiyang.elocker;

public class Constant {
    public final static String CURRENT_RELEASE = "1.1";
    //public final static String BASE_REQUEST_URL = "https://180.166.27.198";
    public final static String BASE_REQUEST_URL = "https://10.61.2.177";
    //public final static String BASE_REQUEST_URL = "https://10.0.10.100";

    /*消息类型（Message.what）*/
    public final static int MESSAGE_lOCKER_LIST = 0;
    public final static int MESSAGE_AUTHORIZATION_LIST = 1;
    public final static int MESSAGE_LOGIN_STATUS = 2;
    public final static int MESSAGE_CHANGE_PASS_STATUS = 3;
    public final static int MESSAGE_ACCOUNT = 4;
    public final static int MESSAGE_ADD_LOCKER_STATUS = 5;
    public final static int MESSAGE_GET_REGISTER_CODE_STATUS = 6;
    public final static int MESSAGE_USER_REGISTER_STATUS = 7;
    public final static int MESSAGE_RESET_PASS_STATUS = 8;
    public final static int MESSAGE_GET_RESET_PASS_CODE_STATUS = 9;

    public final static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String PROPERTY_FILE_NAME = "userinfo";

    public final static String APPID = "App-Id";
    public final static String TOKEN = "Token";
    public final static String APIKEY = "Api-Key";
    public final static String USERAGENT = "User-Agent";
    public final static String APPVERSION = "App-Version";

    public final static int MIN_PHONE_NUM_LENGTH = 6;
    public final static int MAX_PHONE_NUM_LENGTH = 15;
    public final static int VERIFICATION_CODE_LENGTH = 6;
    public final static int MIN_PASSWORD_LENGTH = 6;
}
