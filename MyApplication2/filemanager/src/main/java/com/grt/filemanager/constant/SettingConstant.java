package com.grt.filemanager.constant;


public class SettingConstant {

    public static final int SETTING_CHECK_NEW = 1;
    public static final int SETTING_VIEW = 2;
    public static final int SETTING_BACKUP = 3;
    public static final int SETTING_PWD = 4;

    public static final int SETTING_THEME = 5;
    public static final int SETTING_ZIP_ENCODE = 6;
    public static final int SETTING_ROOT = 7;
    public static final int SETTING_REMOVE_AD = 8;

    public static final int SETTING_HELP = 9;
    public static final int SETTING_FEEDBACK = 10;
    public static final int SETTING_ABOUT = 11;
    public static final int SETTING_SHARE_FE = 12;

    public static final int SETTING_LOGOUT = 13;

    public static final int VIEW_SETTING_HIDE = 14;
    public static final int VIEW_SETTING_HEADER = 15;
    public static final int VIEW_SETTING_DIRINFO = 16;

    public static final int SECURITY_SETTING_LOCK = 17;
    public static final int SECURITY_SETTING_CHANGE_PWD = 18;
    public static final int SECURITY_SETTING_CHANGE_EMAIL = 19;

    public static final int BACKUP_SETTING_LOCATION = 20;
    public static final int BACKUP_SETTING_INSTALL = 21;
    public static final int BACKUP_SETTING_UNINSTALL = 22;
    public static final int SETTING_CLEAR_OPEN_TYPE = 23;

    public static final int PRIVATE_CLOUD_FLOW = 24;
    public static final int BUSINESS_VERSION = 25;
    public static final int ADVANCE_VERSION = 26;
    public static final int NORMAL_VERSION = 27;

    public static final int VIEW_SETTING_LANGUAGE = 28;

    public static final int SYNC_SETTING_DIR = 29;
    public static final int SYNC_SETTING_SKIP_HIDE = 30;
    public static final int SYNC_SETTING_AUTO_SYNC = 31;
    public static final int SYNC_SETTING_AUTO_SYNC_DURATION = 32;
    public static final int SYNC_SETTING_IMMEDIATELY_UPLOAD = 33;
    public static final int SYNC_SETTING_POWER = 34;
    public static final int SYNC_SETTING_NET = 35;
    public static final int SYNC_SETTING_HISTORY = 36;
    public static final int SYNC_SETTING_NOTIFY = 37;
    public static final int SYNC_SETTING_ERROR_NOTIFY = 38;
    public static final int SYNC_SETTING_NOTIFY_VIEW = 39;
    public static final int SYNC_SETTING_LOG = 40;
    public static final int SYNC_SETTING_TIMER_TYPE = 41;
    public static final int SETTING_CLOSE_PUSH = 42;
    public static final int USER_LEVEL_DEBUG = 43;

    public static final int VIEW_SETTING_LOGGER = 44;
    public static final int VIEW_SETTING_MEMORY = 45;
    public static final int SETTING_UPDATE = 47;

    public static final int MIX_CLOUD_DIR = 48;
    public static final int SETTING_CLOUD_DIR = 49;
    public static final int SETTING_CHANGE_PSD = 50;

    public static final int CRYPTOGUARD_BY_RESET = 0;//忘记密码
    public static final int CRYPTOGUARD_BY_OPEN = 1;//打开
    public static final int CRYPTOGUARD_BY_UPDATE = 2;//修改密码
    public static final int CRYPTOGUARD_BY_CLOSE = 3;//关闭
    public static final int CRYPTOGUARD_BY_DRAW = 4;//绘制并验证密码
    public static final int CRYPTOGUARD_BY_RESET_NOTSETEMAIL = 5;
    public static final int FORGET_AND_RESET_PSD = 6;//For safe_box forget psd

    public static final String FROM_LOCK_OR_SAFE = "fromLockOrSafe";
    public static final int FROM_APP_LOCK = 1;
    public static final int FROM_SAFE_BOX = 2;

    public static final int LOCK_TYPE_DEFAULT_VALUE = CRYPTOGUARD_BY_DRAW;

    public static final int APP_BACKUP_PATH_RESULT_CODE = 100;
    public static final int APP_LOCK_SET_CODE = APP_BACKUP_PATH_RESULT_CODE + 1;
    public static final int APP_LOCK_CLOSE_MODE = APP_LOCK_SET_CODE + 1;
    public static final int APP_LOCK_RESET_NEW_PASSWORD = APP_LOCK_CLOSE_MODE + 1;
    public static final int APP_LOCK_OPEN_MODE = APP_LOCK_RESET_NEW_PASSWORD + 1;

    public static final String SHOW_HIDE = "show_hide";
    public static final String SHOW_HEADER = "show_header";
    public static final String HIDE_ADMOB = "hide_admob";
    public static final String CHANGE_SKIN = "change_skin";
    public static final String ROOT = "root";
    public static final String APP_BACKUP_PATH = "app_backup_path";
    public static final String APP_INSTALL_BACKUP = "app_install_backup";
    public static final String APP_UNINSTALL_BACKUP = "app_uninstall_backup";
    public static final String EXTARNAL_SDCARD_LOLLIPOP = "extarnal_sdcard_Lollipop";
    public static final String USB_ROOT_URI = "usb_root_uri";
    public static final String MEDIA_MOUNTED_PATH = "media_mounted_path";
    public static final String SETTING_LANGUAGE = "setting_language";
    public static final String HAS_NEW_VERSION = "has_new_version";
    public static final String SENT_PUSH_TOKEN = "sent_push_token";
    public static final String DEVICE_ID = "device_id";
    public static final String DELETE_TO_RECYCLE = "delete_to_recycle";
    public static final String SHOW_DIR_INFO = "show_dir_info";
    public static final String CHANGE_LANGUAGE = "change_language";
    public static final String DOWNLOADER_SAVE_PATH = "downloader_save_path";
    public static final String CLOSE_PUSH = "close_push";
    public static final String USER_LEVEL = "user_level";
    public static final String SQL_CREATE_TAB_PATH = "sql_create_tab_path";
    public static final String SHOW_MEMORY = "show_memory";

    public static final String FTP_ANONYMOUS = "ftpAnonymous";
    public static final String FTP_USER_NAME = "ftp_user_name";
    public static final String FTP_PASSWORD = "ftp_password";
    public static final String FTP_PORT = "ftp_port";
    public static final String FTP_ENCODING = "ftp_encoding";
    public static final String FTP_SHARE_ALL_PATH = "ftp_share_all_path";
    public static final String FTP_WAKELOCK = "ftp_wakelock";
    public static final String SHOW_PRINT_TIP_DLG = "showPrintTipDlg";

    public static final String CRYPTOGUARD_PASSWORD = "cryptoguard_password";
    public static final String CRYPTOGUARD_OPENED = "cryptoguard_opened";
    public static final String LOCK_TYPE = "lock_type";
    public static final String CRYPTOGUARD_EMAIL = "cryptoguard_email";
    public static final String CRYPTOGUARD_FIND_PASSWORD = "cryptoguard_find_password";
    public static final String CRYPTOGUARD_RESET_PASSWORD = "cryptoguard_reset_password";

    public static final String REPORT_ERROR = "report_error";
    public static final String FIRST_USE_SAFEBOX = "first_use_safebox";

    public static final String DOWNLOADER_TASK_NUM = "downloader_task_num";
    public static final String DOWNLOADER_THREAD_NUM = "downloader_thread_num";
    public static final String DOWNLOADER_ONLYWIFI = "downloader_onlywifi";

    public static final String SHOW_DESC_ACCCOUNT_PERMISSON_DIALOG = "show_desc_acccount_permisson_dialog";
    public static final String SHOW_DESC_WRITE_STORAGE_PER_DIALOG = "show_desc_write_storage_per_dialog";
    public static final String SETTING_CLEAR_PRE_VALUES = "mimetypes";

    public static final String LATEST_PUSH_SHOWTRIAL_CONTENT = "latestPushShowTrialContent";
    public static final String LATEST_PUSH_SHOWTRIAL_PAGE = "latestPushShowTrialPage";

    public static final String SETTING_LEFTDRAWEREXPAND = "setting_leftdrawerexpand";
    public static final String CLOUD_DIR = "cloud_dir";
    public static final String CLOUD_DIR_ID = "cloud_dir_id";

    public static final String ARCHIVE_ENCODE = "archive_encode";
    public static final int ARCHIVE_ENCODE_DEFAULT = 0;
    public static boolean CHANGE_ARCHIVE_ENCODE = false;
    public static final String SHOW_DEVELOPER_LOG = "show_developer_log";

    public static final boolean SHOW_HIDE_DEFAULT_VALUE = false;
    public static final boolean SHOW_HEADER_DEFAULT_VALUE = false;
    public static final boolean HIDE_ADMOB_DEFAULT_VALUE = false;
    public static final boolean ROOT_DEFAULT_VALUE = false;
    public static final boolean APP_BACKUP_DEFAULT_VALUE = false;
    public static final boolean DELETE_TO_RECYCLE_DEFAULT_VALUE = false;
    public static final boolean SHOW_DIR_INFO_DEFAULT_VALUE = false;
    public static final boolean CLOSE_PUSH_DEFAULT_VALUE = false;

    public static final boolean FTP_ANONYMOUS_DEFAULT_VALUE = true;
    public static final String FTP_USER_NAME_DEFAULT_VALUE = "admin";
    public static final String FTP_PASSWORD_DEFAULT_VALUE = "admin";
    public static final int FTP_PORT_DEFAULT_VALUE = 2211;
    public static final String FTP_ENCODING_DEFAULT_VALUE = "UTF-8";
    public static final boolean FTP_WAKELOCK_DEFAULT_VALUE = true;
    public static final boolean FIRST_USE_SAFEBOX_DEFAULT_VALUE = true;


    //检测状态
    public static final String CHECK_PROKEY_STATUS = "fecps";//实体app-prokey
    public static final String CHECK_SERVER_FUN_STATUS = "fecsfus";//服务器功能

    public static final String PROKEY_LICENSE_STATUS = "fepls";//实体app是否正版
    public static final String SERVER_SUBS_LEVEL = "fessl";//订阅等级
    public static final String OPEN_PROKEY_BY_PRODUCT_GOOGLE = "opbpg";//google购买的单个商品打开prokey功能

    //用于商店页面显示 //boolean
    public static final String GOOGLE_SUBS_PERSONAL_M = "gspm";
    public static final String GOOGLE_SUBS_PERSONAL_Y = "gspy";
    public static final String GOOGLE_SUBS_ADVANCED_M = "gsam";
    public static final String GOOGLE_SUBS_ADVANCED_Y = "gsay";
    public static final String GOOGLE_SUBS_FLAGSHIP_M = "gsfm";
    public static final String GOOGLE_SUBS_FLAGSHIP_Y = "gsfy";

    //控制各个功能
    public static final String FE_INAPP_REMOVEAD = "firmad";
    public static final String FE_INAPP_RECYLEBIN = "fircy";
    public static final String FE_INAPP_UNLIMITEDTAG = "fiultag";
    public static final String FE_INAPP_SAFEBOX = "fisafe";
    public static final String FE_INAPP_FILESHREDER = "fifshed";
    public static final String FE_INAPP_PRINTER = "fipter";
    public static final String FE_INAPP_PROKEY = "fipro";
    public static final String FE_INAPP_SQLEDITOR = "fiaaqedffitlor";

    //控制Google pay各个功能
    public static final String FE_G_INAPP_REMOVEAD = "fgirmad";
    public static final String FE_G_INAPP_RECYLEBIN = "figrcy";
    public static final String FE_G_INAPP_UNLIMITEDTAG = "fiugltag";
    public static final String FE_G_INAPP_SAFEBOX = "fisagfe";
    public static final String FE_G_INAPP_FILESHREDER = "fifshged";
    public static final String FE_G_INAPP_PRINTER = "fiptger";
    public static final String FE_G_INAPP_PROKEY = "fiprgo";
    public static final String FE_G_INAPP_SQLEDITOR = "figgqedeeitlor";

    //用于对订阅进行验证
    public static final String CHECK_PURCHASE_TOKEN_DATE = "cptd";
    public static final String CHECK_PURCHASE_REFRESH_TOKEN = "cprt";
    public static final String CHECK_PURCHASE_ACCESS_TOKEN = "cpat";

    public static final int FTP_NO_SLEEP_KEY = 1;
    public static final int FTP_ANONYMOUS_KEY = 2;
    public static final int FTP_USER_KEY = 3;
    public static final int FTP_PORT_KEY = 4;
    public static final int FTP_ENCODE_KEY = 5;

    public static final int DAY_THEME = 0;
    public static final int NIGHT_THEME = 1;

    //设置页面ITEM显示类型，大致分为4种，其他根据细节要求进行修改
    public static final int SETTING_SHOW_ARROW = 1;
    public static final int SETTING_SHOW_SWITCH = 2;
    public static final int SETTING_NO_WIDGET = 3;
    public static final int SETTING_SHOW_BUTTON = 4;
    public static final int SETTING_SHOW_TEXT = 5;
    public static final int SETTING_NO_DIVIDING = 6;
    public static final int SETTING_START_LAYOUT = 7;
    public static final int SETTING_CHECK_NEW_LAYOUT = 8;

    //购买缓存数据
    public static final String PURCHASED_CACHE_DATA = "pcacheData";
    //google消费缓存数据
    public static final String CONSUME_CACHE_DATA = "cmcacheData";
    public static final String FIRST_SINA = "firstSina";

    public static final int THEME_DEFAULT_VALUE = DAY_THEME;
    public static final String GOOGLE_AUTHORIZATION_RESPONSE = "glresponse1";
    public static final String GOOGLE_TOKEN_RESPONSE = "glToke2";
    public static final String GOOGLE_TOKEN_EXCEPTION = "glTokeException";

//    private static SparseIntArray themeArray = new SparseIntArray() {
//        {
//            put(DAY_THEME, R.style.DayTheme);
//            put(NIGHT_THEME, R.style.NightTheme);
//        }
//    };

//    public static SparseIntArray getThemeArray() {
//        return themeArray;
//    }

//    private static final ArrayList<Integer> mLanguageList = new ArrayList<Integer>() {
//        {
//            add(R.string.language_default);
//            add(R.string.language_usa);
//            add(R.string.setting_language_tip);
//        }
//    };

//    public static ArrayList<Integer> getLanguageList() {
//        return mLanguageList;
//    }

}
