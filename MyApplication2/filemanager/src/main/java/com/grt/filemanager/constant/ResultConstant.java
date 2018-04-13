package com.grt.filemanager.constant;

/**
 * 用于存放文件操作的各种结果
 */
public class ResultConstant {

    public static final int SUCCESS = 1;

    public static final int FAILED = 2;

    public static final int EXIST = 3;

    public static final int FORBIDDEN = 4;

    public static final int PASSWORD_ERROR = 5;

    public static final int CANCEL = 6;

    /**
     * 存储空间不足
     */
    public static final int NOT_ENOUGH = 7;

    /**
     * 功能还在开发中
     */
    public static final int DEVELOPING = 8;

    /**
     * 网络通讯失败
     */
    public static final int NET_ERROR = 9;

    /**
     * GCloud 云存储不允许上传0字节大小文件
     */
    public static final int FORBIDDEN_UPLOAD_0_FILE = 10;

    /**
     * GCloud token 过期
     */
    public static final int TOKEN_EXPIRE = 11;

    /**
     * 需要升级账户
     */
    public static final int NEED_UPDATE_LEVEL = 12;

    /**
     * 私有云连接断开
     */
    public static final int CONNECT_ERROR = 13;


    /**
     * 同步时文件大小限制
     */
    public static final int FILE_SIZE_LIMIT = 14;

    /**
     * url包含空格
     */
    public static final int URL_HAS_SPACE = 15;

    /**
     * 新浪云不能上传至根目录
     */
    public static final int NOT_UPLOAD_TO_ROOT = 16;


    /**
     * 云盘未登陆
     */
    public static final int NOT_LOGIN = 17;

}
