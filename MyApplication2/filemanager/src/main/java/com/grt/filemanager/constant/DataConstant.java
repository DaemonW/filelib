package com.grt.filemanager.constant;

import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class DataConstant {

    public static final int ACCOUNT_DEVICE = 0;
    public static final int LOCAL_DEVICE = 1;
    public static final int ADVANCE_TOOLS_DEVICE = 2;
    //    public static final int TOOLS_DEVICE = 3;
    public static final int NET_DEVICE = 3;
    public static final int CLASS_DEVICE = 4;
    public static final int PUBLIC_NET_DEVICE = 5;
    public static final int SECRET_NET_DEVICE = 6;

    /**
     * 数据源ID.
     */
    public static final int DATA_ID_START = 0x0000;
    public static final int CLASS_DATA_ID = DATA_ID_START + 0x2000;
    public static final int NET_DATA_ID = DATA_ID_START + 0x3000;
    public static final int TOOLS_DATA_ID = DATA_ID_START + 0x4000;
    public static final int ACCOUNT_DATA_ID = DATA_ID_START + 0x5000;

    public static final int MEMORY_DATA_ID = DATA_ID_START + 0x0100;
    public static final int STORAGE_DATA_ID = DATA_ID_START + 0x0200;
    public static final int SEARCH_DATA_ID = DATA_ID_START + 0x0300;

    public static final int IMAGE_DATA_ID = CLASS_DATA_ID + 0x0100;
    public static final int MUSIC_DATA_ID = CLASS_DATA_ID + 0x0200;
    public static final int VIDEO_DATA_ID = CLASS_DATA_ID + 0x0300;
    public static final int DOC_DATA_ID = CLASS_DATA_ID + 0x0400;
    public static final int APP_DATA_ID = CLASS_DATA_ID + 0x0500;
    public static final int ZIP_DATA_ID = CLASS_DATA_ID + 0x0600;
    public static final int DOWNLOAD_ID = CLASS_DATA_ID + 0x0700;

    public static final int ADD_NET_ID = NET_DATA_ID + 0x0010;
    public static final int DROPBOX_ID = NET_DATA_ID + 0x0100;
    public static final int FTP_ID = NET_DATA_ID + 0x0200;
    public static final int SMB_ID = NET_DATA_ID + 0x0300;
    public static final int GOOGLE_DRIVE_ID = NET_DATA_ID + 0x0400;
    public static final int ONE_DRIVE_ID = NET_DATA_ID + 0x0500;
    public static final int MY_CLOUD_ID = NET_DATA_ID + 0x0600;
    public static final int BOX_ID = NET_DATA_ID + 0x0700;
    public static final int YANDEX_ID = NET_DATA_ID + 0x0800;
    public static final int JIANGUOYUN_ID = NET_DATA_ID + 0x0900;
    public static final int SUGARSYNC_ID = NET_DATA_ID + 0x0901;
    public static final int FE_PRIVATE_CLOUD_ID = NET_DATA_ID + 0x0902;

    //New Add by sg 2017/02/06
    public static final int MIX_CLOUD_ID = NET_DATA_ID + 0x0904;
    public static final int SINA_STORAGE_ID = NET_DATA_ID + 0x0905;


    public static final int RECENT_OPEN_ID = TOOLS_DATA_ID + 0x0100;
    public static final int RECYCLE_BIN_ID = TOOLS_DATA_ID + 0x0200;
    public static final int LABEL_ID = TOOLS_DATA_ID + 0x0300;
    public static final int SAFEBOX_ID = TOOLS_DATA_ID + 0x0400;
    public static final int DOWNLOADER_ID = TOOLS_DATA_ID + 0x0500;
    public static final int FTP_SERVER_ID = TOOLS_DATA_ID + 0x0600;
    public static final int SMB_ACCOUNT_ID = TOOLS_DATA_ID + 0x0700;
    public static final int NEW_DOCUMENT_ID = TOOLS_DATA_ID + 0x0800;
//    public static final int SQL_OPERATION_ID = TOOLS_DATA_ID + 0x0900;

    //New Add by sg 2017/02/06
    public static final int CAMERA_ID = TOOLS_DATA_ID + 0x0902;
    public static final int RECORD_SOUND_ID = TOOLS_DATA_ID + 0x0903;
    public static final int TRANSFER_LIST_ID = TOOLS_DATA_ID + 0x0904;


    public static final int STORE_ID = ACCOUNT_DATA_ID + 0x0100;
    public static final int USER_GROUPS_ID = ACCOUNT_DATA_ID + 0x0200;
    public static final int QUICK_GUIDE_ID = ACCOUNT_DATA_ID + 0x0300;


    public static boolean isNetId(int dataId) {
        return dataId < TOOLS_DATA_ID && dataId > NET_DATA_ID;
    }

    //not include MEMORY_DATA_ID
    public static boolean isLocalId(int dataId) {
        return dataId < DOWNLOAD_ID && dataId > MEMORY_DATA_ID;
    }

    public static final int INIT_PATH = 0;
    public static final int RESTORE_PATH = 1;
    public static final int KEEP_PATH = 2;            // 保持当前目录信息
    public static final int GO_PATH = 3;              // 进入目录
    public static final int BACK_PATH = 4;
    public static final int FASTBACK_PATH = 5;
    public static final int REFRESH = 6;
    public static final int SORT = 7;
    public static final int FAST_FORWARD_PATH = 8;
    public static final int REINIT = 9;

    public static final String MIME_TYPE_FOLDER = "folder";
    public static final String MIME_TYPE_LINK = "link";
    public static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
    public static final int DEFAULT_ACCOUNT_ID = 0;

    public static final String PERMISSION = "permission";
    public static final String ALL_CHILDREN_COUNT = "all_children_count";
    public static final String ALL_CHILDREN_SIZE = "all_children_size";
    public static final String LINK = "link";
    public static final String LINK_TYPE = "linkType";
    public static final String VERSION_NAME = "versionName";
    public static final String VERSION_CODE = "versionCode";
    public static final String PACKAGE_NAME = "packageName";
    public static final String SignatureInfo = "signatureInfo";
    public static final String UID = "uid";
    public static final String SOURCE_PATH = "sourcePath";
    public static final String INSTALL_TIME = "install_time";
    public static final String UPDATE_TIME = "update_time";
    public static final String TARGET_SDK_VERSION = "target_sdk_version";
    public static final String BITRATE = "bitrate";
    public static final String IMAGE_RESOLUTION = "image_resolution";
    public static final String MEDIA_DURATION = "media_duration";
    public static final String AUTHOR = "author";
    public static final String ARTIST = "artist";
    public static final String YEAR = "year";
    public static final String TITLE = "title";
    public static final String ALBUM = "album";
    public static final String ALBUMARTIST = "album_artist";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String SYSTEM_APP_COUNT = "systemAppCount";
    public static final String USER_APP_COUNT = "userAppCount";
    public static final String APP_PACKAGE_COUNT = "appPackageCount";
    public static final String OLD_PATH = "oldPath";
    public static final String PATH = "path";
    public static final String EXISTS = "exists";
    public static final String PARENT_ID = "parent_id";
    public static final String FILE_ID = "file_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String IS_ENCRYPTED = "is_encrypted";
    public static final String REF_URL = "refurl";
    public static final String CONTENTS_URL = "contentsUrl";
    public static final String DATA_URL = "dataUrl";
    public static final String CAN_PREVIEW = "can_preview";
    public static final String IS_TOP = "is_top";
    public static final String REAL_PATH = "real_path";
    public static final String INFO  = "info";
    public static final String REFRESH_SELF  = "refresh_self";

    public static final String APP_TYPE = "appType";

    public static final String LABEL_TYPE = "labelType";
    public static final String LABEL_NAME = "labelName";
    public static final int LABEL_ROOT = 1;
    public static final int LABEL = 2;
    public static final int LABEL_FILE = 3;
    public static final int LABEL_COMMON_FILE = 4;

    public static final String LABEL_DEFAUL_FAV_NAME = "Favorite";
    public static final int LABEL_DEFAUL_FAV_COLOR = -1;

    public static final int USER_APP_TYPE = 1;
    public static final int SYSTEM_APP_TYPE = 2;
    public static final int APP_PACKAGE_TYPE = 3;

    public static final String APK_SUFFIX = ".apk";
    public final static String PDF_SUFFIX = ".pdf";

    public static final String ALL_TEXT_TYPE = "allTextType";
    public static final String WORD_TYPE = "wordType";
    public static final String EXCEL_TYPE = "excelType";
    public static final String POWER_POINT_TYPE = "powerPointType";
    public static final String PDF_TYPE = "pdfType";
    public static final String OTHER_TEXT_TYPE = "otherTextType";
    public static final String FROM_DOWNLOAD = "isFromDown";
    public static final String FROM_PHOTOVIEWACTIVITY = "isFromPhotoViewActivity";

    public static final String APP_MIME = "application/vnd.android.package-archive";

    public static final String IS_LABEL = "is_label";
    public static final String LABEL_COLOR = "label_color";
    public static final String LABEL_FILE_DB_ID = "label_file_db_id";
    public static final String LABEL_DB_ID = "label_db_id";
    public static final String LABEL_FILE_COUNT = "label_file_count";

    public static final String ANONYMOUS = "anonymous";
    public static final String SMB_IS_SCANNER = "smb_is_scanner";
    public static final String INCREASE_TIME = "increase_file_time";

    //是否来自fe 打开
    public static final String PHOTO_URIS = "photo_uris";
    public static final String PHOTO_CURRENT_POSITION = "photo_current_position";
    public static final String PHOTO_NET_PATH = "photo_net_path";

    //video
    public static final String VIDEO_CURRENT_POSITION = "video_current_position";
    public static final String VIDEO_SEEK_POSITION = "video_seek_position";
    public static final String VIDEO_NET_PATH = "video_net_path";
    public static final String VIDEO_WINDOW_PLAY = "video_window_play";
    public static final String VIDEO_NETDISC_SERVICE = "video_netdisc_service";

    //music
    public static final String MUSIC_NETDISC_SERVICE = "music_netdisc_service";
    public static final String MUSIC_ACTIVITY_ACTIVE_STATE = "music_activity_active_state";
    public static final String MUSIC_FAVOURATE = "music_favourate";
    public static final String MUSIC_PLAYING_MEDIA_PATH = "music_playing_media_path";
    public static final String MUSIC_NETDISC_MEDIA = "music_netdisc_media";
    public static final String MUSIC_FROM_NOTIFICATION = "music_from_notification";

    //chromecast
    public static final String CHROMECAST_INIT_SERVICE = "chrome_init_service";
    public static final String MEDIA_CHROMECAST_NOW = "media_chromecast_now";

    public static final String IS_GCLOUD_ALL_FOLDER = "is_gcloud_all_folder";
    public static final String USE_BASE_PATH = "use_base_path";
    public static final String STORAGE_PERMISSION_DENIED = "storage_permission_denied";

    public static final int FILE = 0;
    public static final int FOLDER = 1;

    public static final int USB_TYPE = 1;
    public static final int EXT_SD_TYPE = 2;
    public static Set<String> INSTALLED_PKG_CACHEDIR = new HashSet<>();

    public static ArrayList<String> PDF_MIME = new ArrayList<String>() {
        {
            add("application/pdf");
        }
    };

    public static ArrayList<String> ALL_DOCUMENT_MIME = new ArrayList<String>() {
        {
            add("text/plain");
            add("application/msword");
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.template");
            add("application/vnd.ms-excel");
            add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            add("application/vnd.openxmlformats-officedocument.spreadsheetml.template");
            add("application/vnd.ms-powerpoint");
            add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            add("application/vnd.openxmlformats-officedocument.presentationml.template");
            add("application/pdf");
            add("application/umd");
            add("application/xps");
            add("application/chm");
            add("application/help");
            add("application/epub");
        }
    };

    public static ArrayList<String> EXCEL_MIME = new ArrayList<String>() {
        {
            add("application/vnd.ms-excel");
            add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            add("application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        }
    };

    public static ArrayList<String> POWER_POINT_MIME = new ArrayList<String>() {
        {
            add("application/vnd.ms-powerpoint");
            add("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            add("application/vnd.openxmlformats-officedocument.presentationml.template");
        }
    };

    public static ArrayList<String> WORD_MIME = new ArrayList<String>() {
        {
            add("application/msword");
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.template");
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            add("application/vnd.openxmlformats-officedocument.wordprocessingml.template");
            add("application/vnd.ms-word.document.macroEnabled.12");
            add("application/vnd.ms-word.template.macroEnabled.12");
        }
    };

    public static ArrayList<String> OTHER_MIME = new ArrayList<String>() {
        {
            add("text/plain");
//            add("application/pdf");
            add("application/umd");
            add("application/xps");
            add("application/chm");
            add("application/help");
            add("application/epub");
        }
    };

    //COMPRESSE
    public static ArrayList<String> COMPRESSED_MIME = new ArrayList<String>() {
        {
            add("application/zip");
            add("application/rar");
            add("application/x-tar");
            add("application/x-gtar");
            add("application/x-7z-compressed");
            add("application/java-archive");
        }
    };

    public static ArrayList<String> PREVIEW_ARCHIVE_MIME = new ArrayList<String>() {
        {
            add("application/zip");
            add("application/rar");
            add("application/x-tar");
            add("application/x-7z-compressed");
            add("application/java-archive");
            add("application/vnd.android.package-archive");
        }
    };

    public static ArrayList<String> SUPPORT_COMPRESS_MIME = new ArrayList<String>() {
        {
            add("application/zip");
            add("application/x-7z-compressed");
            add("application/x-tar");
        }
    };

    public static ArrayMap<String, String> INCREASE_FILE_PKGNAME = new ArrayMap<String, String>() {
        {
            put("com.baidu.browser.apps", "baidu/flyflow/downloads");
            put("com.UCMobile", "UCDownloads");
            put("org.mozilla.firefox", "Download");
            put("com.tencent.mm", "tencent/MicroMsg/WeiXin");
            put("com.tencent.mobileqq", "tencent/QQfile_recv");
            put("com.tencent.android.qqdownloader", "tencent/tassistant/apk");
            put("com.sina.weibo", "sina/weibo/download");
            put("com.tencent.mtt", "QQBrowser");
            put("com.wandoujia.phoenix2", "wandoujia/app");
            put("com.baidu.appsearch", "BaiduAs4a1d86a3");
            put("com.xiaomi.market", "Android/data/com.xiaomi.market/files/Download/apks");
            put("com.android.chrome", "Download");
            put("cn.wps.moffice", "documents");
            put("com.microsoft.office.word", "Documents");
            put("com.microsoft.office.officehub", "Documents");
            put("com.microsoft.office.excel", "Documents");
            put("com.microsoft.office.powerpoint", "Documents");
            put("com.facebook.katana", "Android/data/com.facebook.katana/files/Download");
        }
    };

    public static ArrayMap<String, ArrayList<String>> docMap =
            new ArrayMap<String, ArrayList<String>>() {
                {
                    put(ALL_TEXT_TYPE, ALL_DOCUMENT_MIME);
                    put(WORD_TYPE, WORD_MIME);
                    put(EXCEL_TYPE, EXCEL_MIME);
                    put(POWER_POINT_TYPE, POWER_POINT_MIME);
                    put(PDF_TYPE, PDF_MIME);
                    put(OTHER_TEXT_TYPE, OTHER_MIME);
                    put(EXCEL_TYPE, EXCEL_MIME);
                }
            };

    public static boolean isDoc(String mimeType) {
        return ALL_DOCUMENT_MIME.contains(mimeType);
    }

    public static boolean isPreviewArchive(String mimeType) {
        return PREVIEW_ARCHIVE_MIME.contains(mimeType);
    }

    public static boolean isSupportCompress(String mimeType) {
        return SUPPORT_COMPRESS_MIME.contains(mimeType);
    }

    public static boolean canConvertToPdf(String mimeType) {
        return EXCEL_MIME.contains(mimeType) ||
                POWER_POINT_MIME.contains(mimeType) ||
                WORD_MIME.contains(mimeType);
    }


    /**
     * 用来描述当前目录下能否进行的操作
     * 第一位标识当前文件能否选中
     * 第二位标识能否复制文件到当前文件夹
     * 第三位表示虚拟目录
     * 第四位标识能否在当前目录创建文件夹
     */
    public static final String OPERATION_PERMISSION = "operation_permission";
    public static final String ALL_ALLOW_PERMISSION = "1111";

    public static String buildOperationPermission(boolean selected, boolean copyTo,
                                                  boolean longPress, boolean createDir) {
        return "".concat(selected ? "1" : "0").concat(copyTo ? "1" : "0")
                .concat(longPress ? "1" : "0").concat(createDir ? "1" : "0");
    }

    /**
     * 分类中使用
     * 用来判断具有虚拟目录的数据源
     * 图片，音乐，视频，应用，文本等
     *
     * @param selected
     * @param copyTo
     * @param longPress
     * @return
     */
    public static String buildOperationPermission(boolean selected, boolean copyTo, boolean longPress) {
        return buildOperationPermission(selected, copyTo, longPress, false);
    }

    public static String buildOperationPermission(boolean selected, boolean copyTo) {
        return buildOperationPermission(selected, copyTo, false);
    }

    public static boolean canCopyTo(String operationPermission) {
        return operationPermission != null &&
                1 == Integer.valueOf(operationPermission.substring(1, 2));
    }

    public static boolean canLongSelected(String operationPermission) {
        return operationPermission != null &&
                1 == Integer.valueOf(operationPermission.substring(0, 1));
    }

    public static boolean canSelectAll(String operationPermission) {
        return operationPermission != null &&
                1 == Integer.valueOf(operationPermission.substring(2, 3));
    }

    public static boolean canCreateDir(String operationPermission) {
        return operationPermission != null &&
                1 == Integer.valueOf(operationPermission.substring(3, 4));
    }

    public static final String IMAGE_JPEG = "image_jpeg";
    public static final String IMAGE = "image";
    public static final String MUSIC = "music";
    public static final String VIDEO = "video";
    public static final String INSTALLED_APP = "installed_app";
    public static final String IS_INSTALLED_APP = "is_installed_app";
    public static final String IS_APK_FILE = "is_apk_file";
    public static final String IS_SYSTEM_APP = "is_system_app";

    public static final String CLICK_TIME = "click_time";

    public static boolean hasFileId(int dataId) {
        boolean result = false;

        switch (dataId) {
            case GOOGLE_DRIVE_ID:
            case MY_CLOUD_ID:
            case SUGARSYNC_ID:
            case MIX_CLOUD_ID:
                result = true;
                break;

            default:
                break;
        }

        return result;
    }

    public static boolean isCloud(int dataId) {
        boolean result = false;

        switch (dataId) {
            case DROPBOX_ID:
            case GOOGLE_DRIVE_ID:
            case ONE_DRIVE_ID:
            case BOX_ID:
            case YANDEX_ID:
            case MY_CLOUD_ID:
            case JIANGUOYUN_ID:
            case SUGARSYNC_ID:
            case SINA_STORAGE_ID:
                result = true;
                break;

            default:
                break;
        }

        return result;
    }

    public static boolean isOurCloud(int dataId) {
        boolean result = false;

        switch (dataId) {
            case MY_CLOUD_ID:
            case FE_PRIVATE_CLOUD_ID:
            case MIX_CLOUD_ID:
                result = true;
                break;

            default:
                break;
        }

        return result;
    }

    public static boolean isSupportSync(int dataId) {
        boolean result;

        switch (dataId) {
            case SMB_ID:
            case FTP_ID:
            case FE_PRIVATE_CLOUD_ID:
            case MY_CLOUD_ID:
                result = false;
                break;

            default:
                result = true;
        }

        return result;
    }

    public static boolean onlyOneAccount(int dataId) {
        boolean result = false;

        switch (dataId) {
            case MY_CLOUD_ID:
            case FE_PRIVATE_CLOUD_ID:
            case MIX_CLOUD_ID:
                result = true;
                break;

            default:
                break;
        }

        return result;
    }

    public static boolean notAllowPaste(int dataId) {
        boolean result = false;

        switch (dataId) {
            case SAFEBOX_ID:
            case MIX_CLOUD_ID:
                result = true;
                break;

            default:
                break;
        }

        return result;
    }

    private static ArrayList<Integer> netIdArray = new ArrayList<Integer>() {
        {
            add(FTP_ID);

//            add(SMB_ACCOUNT_ID);

            add(DROPBOX_ID);
            add(GOOGLE_DRIVE_ID);
            add(ONE_DRIVE_ID);
//            add(BOX_ID);
//            add(YANDEX_ID);
//            add(SUGARSYNC_ID);
            add(SINA_STORAGE_ID);
            add(JIANGUOYUN_ID);
        }
    };

    public static ArrayList<Integer> getNetIdArray() {
        return netIdArray;
    }

//    private static SparseIntArray netIconArray = new SparseIntArray() {
//        {
//            put(FTP_ID, R.drawable.ic_ftp_webtool);
////            put(SMB_ID, R.drawable.ic_smb);
////            put(SMB_ACCOUNT_ID, R.drawable.ic_smb);
//            put(DROPBOX_ID, R.drawable.ic_dropbox);
//            put(GOOGLE_DRIVE_ID, R.drawable.ic_google_drive);
//            put(ONE_DRIVE_ID, R.drawable.ic_one_drive);
////            put(BOX_ID, R.drawable.ic_box);
////            put(YANDEX_ID, R.drawable.ic_yandex_blue);
////            put(MY_CLOUD_ID, R.drawable.ic_gcloud);
////            put(SUGARSYNC_ID, R.drawable.ic_sugarsync);
//            put(JIANGUOYUN_ID, R.drawable.ic_jianguoyun);
//            put(SINA_STORAGE_ID, R.drawable.ic_sinacloud);
//        }
//    };
//
//    public static SparseIntArray getNetIconArray() {
//        return netIconArray;
//    }
//
//    private static SparseIntArray netNameArray = new SparseIntArray() {
//        {
//            put(FTP_ID, R.string.ftp_client);
////            put(SMB_ID, R.string.smb);
////            put(SMB_ACCOUNT_ID, R.string.smb);
//            put(DROPBOX_ID, R.string.dropbox_title);
//            put(GOOGLE_DRIVE_ID, R.string.google_drive);
//            put(ONE_DRIVE_ID, R.string.one_drive);
////            put(BOX_ID, R.string.box);
////            put(YANDEX_ID, R.string.yandex);
//            put(JIANGUOYUN_ID, R.string.jianguoyun);
////            put(SUGARSYNC_ID, R.string.sugarsync);
//            put(SINA_STORAGE_ID, R.string.vdisk);
//        }
//    };
//
//    public static SparseIntArray getNetNameArray() {
//        return netNameArray;
//    }
//
//    private static SparseIntArray allNetNameArray = new SparseIntArray() {
//        {
//            put(FTP_ID, R.string.ftp_client);
////            put(SMB_ID, R.string.smb);
////            put(SMB_ACCOUNT_ID, R.string.smb);
//            put(DROPBOX_ID, R.string.dropbox_title);
//            put(GOOGLE_DRIVE_ID, R.string.google_drive);
//            put(ONE_DRIVE_ID, R.string.one_drive);
////            put(BOX_ID, R.string.box);
////            put(YANDEX_ID, R.string.yandex);
////            put(SUGARSYNC_ID, R.string.sugarsync);
//            put(MY_CLOUD_ID, R.string.gcloud);
//            put(JIANGUOYUN_ID, R.string.jianguoyun);
////            put(FE_PRIVATE_CLOUD_ID, R.string.fe_private_cloud);
//            put(SINA_STORAGE_ID, R.string.vdisk);
//            put(MIX_CLOUD_ID, R.string.mix_cloud);
//        }
//    };
//
//    public static SparseIntArray getAllNetNameArray() {
//        return allNetNameArray;
//    }
//
//    private static SparseIntArray sourceDayIcons = new SparseIntArray() {
//        {
//            put(STORAGE_DATA_ID, R.drawable.ic_extsdcard_left_darwer_day);
//            put(MEMORY_DATA_ID, R.drawable.ic_system_left_darwer_day);
//            put(IMAGE_DATA_ID, R.drawable.ic_photo_left_darwer_day);
//            put(MUSIC_DATA_ID, R.drawable.ic_music_left_darwer_day);
//            put(VIDEO_DATA_ID, R.drawable.ic_video_left_darwer_day);
//            put(APP_DATA_ID, R.drawable.ic_app_left_darwer_day);
//            put(ZIP_DATA_ID, R.drawable.ic_zip_left_darwer_day);
//            put(DOC_DATA_ID, R.drawable.ic_document_left_darwer_day);
//            put(RECENT_OPEN_ID, R.drawable.ic_history_left_darwer_day);
//            put(RECYCLE_BIN_ID, R.drawable.ic_recyclebin_left_darwer_day);
//            put(DOWNLOAD_ID, R.drawable.ic_download_left_darwer_day);
//            put(DOWNLOADER_ID, R.drawable.ic_download_left_darwer_day);
//            put(LABEL_ID, R.drawable.ic_tag_left_darwer_day);
//            put(NEW_DOCUMENT_ID, R.drawable.ic_latest_new_files_gray);
//            put(SAFEBOX_ID, R.drawable.ic_lockbox_left_darwer_day);
//            put(DROPBOX_ID, R.drawable.ic_dropbox_day);
//            put(JIANGUOYUN_ID, R.drawable.ic_jianguoyun_gray);
//            put(GOOGLE_DRIVE_ID, R.drawable.ic_google_drive_day);
//            put(FTP_ID, R.drawable.ic_ftp_webtool_day);
//            put(SMB_ID, R.drawable.ic_smb_day);
//            put(ONE_DRIVE_ID, R.drawable.ic_one_drive_day);
//            put(BOX_ID, R.drawable.ic_box_day);
//            put(YANDEX_ID, R.drawable.ic_yandex_day);
//            put(BOX_ID, R.drawable.ic_box_day);
//            put(YANDEX_ID, R.drawable.ic_yandex_day);
//            put(SUGARSYNC_ID, R.drawable.ic_sugarsync_day);
//            put(MY_CLOUD_ID, R.drawable.ic_mycloud_day);
//            put(ADD_NET_ID, R.drawable.ic_add_left_darwer_day);
//            put(FTP_SERVER_ID, R.drawable.ic_ftpserver_left_darwer_day);
//            put(STORE_ID, R.drawable.ic_account_left_darwer_day);
//            put(FE_PRIVATE_CLOUD_ID, R.drawable.ic_privatecloud_day);
//            put(USER_GROUPS_ID, R.drawable.ic_googleplus_day);
//            put(QUICK_GUIDE_ID, R.drawable.ic_guide_tip_day);
////            put(SQL_OPERATION_ID, R.drawable.ic_database_gray);
//
//            //add
//            put(MIX_CLOUD_ID, R.drawable.ic_hunhe_cloud_day);
//            put(SINA_STORAGE_ID, R.drawable.ic_sinacloud_day);
//            put(TRANSFER_LIST_ID, R.drawable.ic_transfer_list_day);
//        }
//    };
//
//    private static SparseIntArray sourceNightIcons = new SparseIntArray() {
//        {
//            put(STORAGE_DATA_ID, R.drawable.ic_extsdcard_left_darwer_night);
//            put(MEMORY_DATA_ID, R.drawable.ic_system_left_darwer_night);
//            put(IMAGE_DATA_ID, R.drawable.ic_photo_left_darwer_night);
//            put(MUSIC_DATA_ID, R.drawable.ic_music_left_darwer_night);
//            put(VIDEO_DATA_ID, R.drawable.ic_video_left_darwer_night);
//            put(APP_DATA_ID, R.drawable.ic_app_left_darwer_night);
//            put(ZIP_DATA_ID, R.drawable.ic_zip_left_darwer_night);
//            put(DOC_DATA_ID, R.drawable.ic_document_left_darwer_night);
//            put(RECENT_OPEN_ID, R.drawable.ic_history_left_darwer_night);
//            put(RECYCLE_BIN_ID, R.drawable.ic_recyclebin_left_darwer_night);
//            put(DOWNLOAD_ID, R.drawable.ic_download_left_darwer_night);
//            put(DOWNLOADER_ID, R.drawable.ic_download_left_darwer_night);
//            put(LABEL_ID, R.drawable.ic_tag_left_darwer_night);
//            put(NEW_DOCUMENT_ID, R.drawable.ic_latest_new_files_white);
//            put(SAFEBOX_ID, R.drawable.ic_lockbox_left_darwer_night);
//            put(DROPBOX_ID, R.drawable.ic_dropbox_night);
//            put(JIANGUOYUN_ID, R.drawable.ic_jianguoyun_night);
//            put(GOOGLE_DRIVE_ID, R.drawable.ic_google_drive_night);
//            put(FTP_ID, R.drawable.ic_ftp_webtool_night);
//            put(SMB_ID, R.drawable.ic_smb_night);
//            put(ONE_DRIVE_ID, R.drawable.ic_one_drive_night);
//            put(BOX_ID, R.drawable.ic_box_night);
//            put(YANDEX_ID, R.drawable.ic_yandex_night);
//            put(BOX_ID, R.drawable.ic_box_night);
//            put(YANDEX_ID, R.drawable.ic_yandex_night);
//            put(SUGARSYNC_ID, R.drawable.ic_sugarsync_night);
//            put(MY_CLOUD_ID, R.drawable.ic_mycloud_night);
//            put(ADD_NET_ID, R.drawable.ic_add_left_darwer_night);
//            put(FTP_SERVER_ID, R.drawable.ic_ftpserver_left_darwer_night);
//            put(STORE_ID, R.drawable.ic_account_left_darwer_night);
//            put(FE_PRIVATE_CLOUD_ID, R.drawable.ic_privatecloud_night);
//            put(USER_GROUPS_ID, R.drawable.ic_googleplus_night);
//            put(QUICK_GUIDE_ID, R.drawable.ic_guide_tip_night);
////            put(SQL_OPERATION_ID, R.drawable.ic_database_white);
//
//            //add
//            put(MIX_CLOUD_ID, R.drawable.ic_hunhe_cloud_night);
//            put(SINA_STORAGE_ID, R.drawable.ic_sinacloud_night);
//            put(TRANSFER_LIST_ID, R.drawable.ic_transfer_list_night);
//        }
//    };
//
//    public static SparseIntArray getSourceIcons() {
//        int themeId = PreferenceUtils.getPrefInt(SettingConstant.CHANGE_SKIN, SettingConstant.THEME_DEFAULT_VALUE);
//        switch (themeId) {
//            case SettingConstant.DAY_THEME:
//                return sourceDayIcons;
//
//            case SettingConstant.NIGHT_THEME:
//                return sourceNightIcons;
//
//            default:
//                return sourceDayIcons;
//        }
//    }
//
//    private static SparseIntArray sourceHighLightIcons = new SparseIntArray() {
//        {
//            put(STORAGE_DATA_ID, R.drawable.ic_extsdcard_left_darwer);
//            put(MEMORY_DATA_ID, R.drawable.ic_system_left_darwer);
//            put(IMAGE_DATA_ID, R.drawable.ic_photo_left_darwer);
//            put(MUSIC_DATA_ID, R.drawable.ic_music_left_darwer);
//            put(VIDEO_DATA_ID, R.drawable.ic_video_left_darwer);
//            put(APP_DATA_ID, R.drawable.ic_app_left_darwer);
//            put(ZIP_DATA_ID, R.drawable.ic_zip_left_darwer);
//            put(DOC_DATA_ID, R.drawable.ic_document_left_darwer);
//            put(RECENT_OPEN_ID, R.drawable.ic_history_left_darwer);
//            put(RECYCLE_BIN_ID, R.drawable.ic_recyclebin_left_darwer);
//            put(DOWNLOAD_ID, R.drawable.ic_download_left_darwer);
//            put(DOWNLOADER_ID, R.drawable.ic_download_left_darwer);
//            put(LABEL_ID, R.drawable.ic_tag_left_darwer);
//            put(NEW_DOCUMENT_ID, R.drawable.ic_latest_new_files);
//            put(SAFEBOX_ID, R.drawable.ic_lockbox_left_darwer);
//            put(DROPBOX_ID, R.drawable.ic_dropbox);
//            put(JIANGUOYUN_ID, R.drawable.ic_jianguoyun);
//            put(GOOGLE_DRIVE_ID, R.drawable.ic_google_drive);
//            put(FTP_ID, R.drawable.ic_ftp_webtool);
//            put(SMB_ID, R.drawable.ic_smb);
//            put(ONE_DRIVE_ID, R.drawable.ic_one_drive);
//            put(BOX_ID, R.drawable.ic_box);
//            put(YANDEX_ID, R.drawable.ic_yandex_blue);
//            put(SUGARSYNC_ID, R.drawable.ic_sugarsync);
//            put(MY_CLOUD_ID, R.drawable.ic_mycloud);
//            put(ADD_NET_ID, R.drawable.ic_add_left_darwer);
//            put(FTP_SERVER_ID, R.drawable.ic_ftpserver_left_darwer);
//            put(FE_PRIVATE_CLOUD_ID, R.drawable.ic_privatecloud);
//
//            //add
//            put(MIX_CLOUD_ID, R.drawable.ic_hunhe_cloud);
//            put(SINA_STORAGE_ID, R.drawable.ic_sinacloud);
//            put(TRANSFER_LIST_ID, R.drawable.ic_transfer_list);
//        }
//    };
//
//    public static SparseIntArray getSourceHighLightIcons() {
//        return sourceHighLightIcons;
//    }
//
//    private static final SparseBooleanArray needRenameForDel = new SparseBooleanArray() {
//        {
//            put(STORAGE_DATA_ID, true);
//            put(SAFEBOX_ID, true);
//            put(APP_DATA_ID, true);
//            put(MUSIC_DATA_ID, true);
//            put(ZIP_DATA_ID, true);
//            put(DOC_DATA_ID, true);
//            put(RECENT_OPEN_ID, true);
//            put(RECYCLE_BIN_ID, true);
//            put(SEARCH_DATA_ID, true);
//            put(LABEL_ID, true);
//            put(IMAGE_DATA_ID, true);
//            put(VIDEO_DATA_ID, true);
//            put(NEW_DOCUMENT_ID, true);
//            put(DOWNLOAD_ID, true);
//        }
//    };
//
//    public static SparseBooleanArray getNeedRenameForDel() {
//        return needRenameForDel;
//    }
//
//    private static ArrayList<Integer> SecretClouds = new ArrayList<Integer>(){
//        {
//            add(ONE_DRIVE_ID);
//            add(GOOGLE_DRIVE_ID);
//            add(DROPBOX_ID);
//            add(JIANGUOYUN_ID);
//            add(SINA_STORAGE_ID);
//            add(MY_CLOUD_ID);
//        }
//    };
//
//    public static ArrayList<Integer> getSecretClouds() {
//        return SecretClouds;
//    }
}
