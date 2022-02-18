package model;

public class GMConnection {
    private static final String INCORRECT_DATA = "INCORRECT DATA";
    private static final String INCORRECT_PASSWORD = "INCORRECT PASSWORD";
    private static final String LOG_IN = "LOG IN";
    private static final String SIGN_UP = "SIGN UP";
    private static final String BANNED = "BANNED";
    private static final String USER_EXIST = "USER EXISTS";
    private static final String LOG_IN_OK = "LOG_IN_OK";
    private static final String ACTIVE_USERS = "ACTIVE_USERS";
    private static final String CHANGE_PASSWORD = "CHANGE_PASSWORD";
    private static final String CHANGE_NAME = "CHANGE_NAME";
    private static final String REGISTER_DATE="REGISTER_DATE";
    private static final String CLOSE_SESSION="CLOSE_SESSION";
    private static final String CHECK_NAME="CHECK_NAME";
    private static final String CHANGE_NAME_OK ="CHANGE_NAME_OK";
    private static final String CHANGE_NAME_FAIL ="CHANGE_NAME_FAIL";

    public static  String getINCORRECT_DATA() {
        return INCORRECT_DATA;
    }

    public static String getINCORRECT_PASSWORD() {
        return INCORRECT_PASSWORD;
    }

    public static String getLOG_IN() {
        return LOG_IN;
    }

    public static String getSIGN_UP() {
        return SIGN_UP;
    }

    public static String getBANNED() {
        return BANNED;
    }

    public static String getUSER_EXIST() {
        return USER_EXIST;
    }

    public static String getLOG_IN_OK() {
        return LOG_IN_OK;
    }

    public static String getACTIVE_USERS() {
        return ACTIVE_USERS;
    }

    public static String getCHANGE_PASSWORD() {
        return CHANGE_PASSWORD;
    }

    public static String getCHANGE_NAME() {
        return CHANGE_NAME;
    }

    public static String getREGISTER_DATE(){
        return REGISTER_DATE;
    }

    public static String getCLOSE_SESSION(){
        return CLOSE_SESSION;
    }

    public static String getCHECK_NAME(){
        return CHECK_NAME;
    }
    public static String getCHANGE_NAME_OK(){
        return CHANGE_NAME_OK;
    }

    public static String getCHANGE_NAME_FAIL(){
        return CHANGE_NAME_FAIL;
    }
}
