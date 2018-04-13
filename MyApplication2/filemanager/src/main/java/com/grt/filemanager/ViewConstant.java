package com.grt.filemanager;

import android.content.res.Configuration;
import android.support.v4.util.ArrayMap;


public final class ViewConstant {


    private ViewConstant() {
    }

    public static final String FE_CLASS_NAME = "com.grt.filemanager.view.home.MainActivity";

    public static final String DATA_ID = "data_id";
    public static final String FRAGMENT_ID = "id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String PATH = "path";
    public static final String CHILD_PATH = "child_path";
    public static final String CLICK_POSITION = "click_position";
    public static final String STACK = "stack";
    public static final String POSITION = "position";
    public static final String TYPE = "type";
    public static final String SORT_TYPE = "sort_type";
    public static final String SORT_ORDER = "sort_order";
    public static final String RANK_MODE = "rank_mode";
    public static final String GRID_COUNT = "grid_count";
    public static final String ONLY_FOLDER = "only_folder";
    public static final String SHOW_HIDE_FILE = "show_hide_file";
    public static final String FRAGMENT_TITLE = "fragment_title";
    public static final String ACCOUNT_TITLE = "account_title";
    public static final String ORIENTATION = "orientation";
    public static final String CURRENT_ITEM = "current_item";
    public static final String IS_CURRENT_FRAGMENT = "is_current_fragment";
    public static final String ACCOUNT_MGR_TYPE = "account_mgr_type";
    public static final String SEARCH_KEY_WORD = "search_key_word";
    public static final String SEARCH_DATA_ID = "search_data_id";
    public static final String SEARCH_FRAGMENT_ID = "search_fragment_id";
    public static final String SEARCH_CUR_PATH = "search_cur_path";
    public static final String IS_CUT = "is_cut";
    public static final String OPERATION_TITLE = "operation_title";
    public static final String START_PATH = "start_path";
    public static final String LAST_LANGUAGE = "last_language";
    public final static String SHORTCUT_PATH = "shortcut_path";
    public final static String IS_CLASSIFICATION = "is_classification";

    public final static String NET_ACCOUNT_TASK = "net_account_task";
    public static final int MIX_UPLOAD_TIP = 0;

    //choice file
    public static final String RESULT_OK_KEY = "result_ok_key";
    public static final String CONFIRM_PATH = "confirm_path";
    public static final String CONFIRM_FILE_ID = "confirm_file_id";

    public static final String ACCOUNT_LIST_TYPE = "account_list_type";
    public static final int ALL_ACCOUNT = 0;
    public static final int ONLY_LOCAL = 1;
    public static final int SPECIAL_ACCOUNT = 2;

    public static final String FROM_OTHER_APP = "from_other_app";
    public static final String OPEN_DETAIL_WITH_FOLDER = "open_detail_with_folder";
    public static final String ALLOW_DEL_TO_RECYCLE = "allow_del_to_recycle";

    public static final String IS_ENCRYPTED = "is_encrypted";
    public static final String PASSWORD = "password";

    public static final String TXT_ENCODE = "txt_encode";
    public static final String FONT_SIZE = "font_size";
    public static final String FONT_TYPE = "font_type";
    public static final String FONT_STYLE = "font_style";

    public static final String FILE_NAME = "file_name";
    public static final String IS_FIRST_OPEN = "isFirstOpen";
    public static final String LEFT_DATA_STATUS = "leftDataStatus";
    public static final String LEFT_CHANGE_COUNT = "changeCount";

    public static final int LEFT_REMOVE = 1;
    public static final int LEFT_ADD = 2;
    public static final int LEFT_UPDATE = 3;

    //ftp smb 编辑
    public static final String FTP_EDIT = "ftp_edit";
    public static final int UPDATE_FTP = 0;
    public static final int SAVE_FTP = 1;
    public static final int NOT_SAVE_FTP_SMB = 2;

    public static final int ACCOUNT_MGR = 0;
    public static final int ACCOUNT_ADD = 1;

    public static final int START_SAFEBOX = 0;
    public static final int START_CLOUD_AUTH = 1;
    public static final int START_DOWNLOADER = 2;
    public static final int START_FTP_SERVER = 3;
    public static final int START_GCLOUD_LOGIN = 4;
    public static final int START_STORE_PAGE = 5;
    public static final int START_PRIVATE_CLOUD = 6;
    public static final int START_CAMERA = 7;
    public static final int START_AUDIO = 8;
    public static final int START_TRANSFER_LIST = 9;

    public static final int NOT = 0;
    public static final int INIT = -1;
    public static final int RESTORE = -2;
    public static final int KEEP = -3;
    public static final int GO = -4;
    public static final int BACK = -5;
    public static final int FASTBACK = -6;
    public static final int REFRESH = -7;
    public static final int PREVIEW_ARCHIVE = -8;
    public static final int INIT_WITH_PATH = -9;
    public static final int SEARCH = -10;
    public static final int SORT = -11;
    public static final int PREVIEW_ARCHIVE_WITH_PATH = -12;
    public static final int ACCOUNT = -13;
    public static final int FASTFORWARD = -14;
    public static final int REINIT = -15;

    public static ArrayMap<Integer, Boolean> scrollType = new ArrayMap<Integer, Boolean>() {
        {
            put(KEEP, true);
            put(BACK, true);
            put(FASTBACK, true);
            put(RESTORE, true);
        }
    };

    public static final int POSITION_ARG = 10000;

    public static final String MSG_CONTENT = "msg_content";
    public static final String MSG_ARGS = "msg_args";
    public static final int SHOW_MSG = 1;
    public static final int SHOW_BOTTOM_LOADING = 2;
    public static final int HIDE_BOTTOM_LOADING = 3;
    public static final int SHOW_BOTTOM_MESSAGE = 4;
    public static final int CLOSE_SOFT_INPUT = 5;
    public static final int SHOW_BOTTOM_LOADING_CANCLEBUTTON = 6;
    public static final int START_LOGIN = 7;
    public static final int NOT_ENOUGH_DIALOG = 8;

    public static final String MSG_TYPE = "msg_type";
    public static final int NORMAL_TYPE = 1;
    public static final int DONE_TYPE = 2;
    public static final int ERROR_TYPE = 3;
    public static final int NORMAL_MIDDLE_TYPE = 4;

    public static final int DEVELOPING = 1;
    public static final int CONVERT_TO_PDF_FAILED = 2;
    public static final int CONVERT_TO_PDF_SUCCESS = 3;
    public static final int FORBIDDEN_UPLOAD_0_FILE = 4;
    public static final int NOT_SUPPORT_ROOT = 5;
    public static final int NOT_SUPPORT_RW = 6;
    public static final int DISCONNECT = 7;
    public static final int NEED_SHOW_PURCHASE = 8;

    //Floating button
    public static final int HIDE_FLM = 1;
    public static final int HIDE_CREATE_DIR_FILE = 2;
    public static final int CHANGE_FLOATING_LAYOUT = 3;

    //HELP
    public static final String FE_HELP_TYPE = "fe_help_type";
    public static final String FE_HELP_TITILE = "fe_help_titile";
    public static final String FE_HELP_URL = "fe_help_url";
    public static final int FE_HELP = 0;
    public static final int PRINT_HELP = 1;
    public static final int HISTORY_CHANGE_LOG = 2;
    public static final int PUSH_WEBPAGE = 3;
    public static final int PRIVATE_POLICY = 4;

    public static final String NEW_INTENT_ACTION = "new_intent_action";
    public static final int CREATE_SHORTCUT_ACTION = 1;
    public static final int PREVIEW_ARCHIVE_ACTION = 2;
    public static final int EXTRACT_ARCHIVE_ACTION = 3;
    public static final int ADD_USB_MASS_STORAGE = 4;
    public static final int OPEN_WEB_ACTIVITY = 5;
    public static final int DISPLAY_DATA = 6;
    public static final int ADD_EXTERNAL_SDCARD = 7;


    public static final String NEW_INTENT_PATH = "new_intent_path";
    public static final String JIKE_ZHUSHOU_PKG = "com.geeksoft.wps";
    public static final String GOOGLE_PLAY_PKG = "com.android.vending";
    public static final String GOOGLE_PLUS_PKG = "com.google.android.apps.plus";

    public static boolean MAIN_STARTED = false;

    /**
     * 排列方式.
     */
    public static final int LIST_MODE = 1;
    public static final int GRID_MODE = 2;

    //平铺模式下 不同数据源 显示个数
    public static final int LIST_MODE_COUNT = -1;
    public static final int COMMON_COUNT = 3;
    public static final int SPECIAL_COUNT = 2;
    public static final int LANDSCAPE_LIST_COUNT = 2;
    public static final int LANDSCAPE_GRID_COUNT = 6;

    public static int getGridCount(int orientation, int rankMode) {
        int gridCount = COMMON_COUNT;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (rankMode == LIST_MODE) {
                gridCount = COMMON_COUNT;
            }
        } else {
            if (rankMode == LIST_MODE) {
                gridCount = LANDSCAPE_LIST_COUNT;
            } else {
                gridCount = LANDSCAPE_GRID_COUNT;
            }
        }

        return gridCount;
    }

    public static final String ARGS_DATAACTIVITY ="args_to_DataActivity";

    public static final String DATA_TYPE = "data_type";
    public static final String OTG = "otg";
    public static final String LOCAL = "local";

    public static final int CREATE_FILE_OR_FOLDER = 1000;
    public static final int DELETE_FILE_OR_FOLDER = 1001;
    public static final int PASTE = 1002;

    public static final int NO_DEVICE = 1003;
    public static final int NO_AUTHORIZE = 1004;
    public static final int AUTHORIZED = 1005;
    public static final int OTG_REMOVED = 1006;
}
