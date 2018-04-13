package com.grt.filemanager;

import android.util.SparseArray;

import com.grt.filemanager.model.ProgressData;

public class OperationConstant {

    public static final String OPERATION_ID = "operation_id";
    public static final String OPERATION_TYPE = "operation_type";
    public static final String SRC_PATH = "src_path";
    public static final String DES_FOLDER_PATH = "des_folder_path";
    public static final String FRAGMENT_ID = "fragment_id";
    public static final String DATA_ID = "data_id";
    public static final String ACCOUNT_ID = "account_id";
    public static final String CREATE_FOLDER = "create_folder";
    public static final String IS_CONFLICT = "is_conflict";
    public static final String IS_CUT = "is_cut";

    public static final String PROGRESS_CMD = "progress_cmd";
    public static final String DIALOG_TYPE = "dialog_type";

    //archive
    public static final String FILE_NAME = "file_name";
    public static final String SELECTED_LIST = "selected_list";
    public static final String PASSWORD = "password";
    public static final String CHARSET = "charset";
    public static final String DISPLAY_PASSWORD = "display_password";
    public static final String DELETE_AFTER = "delete_after";
    public static final String COMPRESS_LEVEL = "compress_level";
    public static final String COMPRESS_ARGS = "compress_args";

    public static final String ARCHIVE_TYPE = "archive_type";
    public static final int ZIP = 1;
    public static final int SEVEN_ZIP = 2;
    public static final int TAR = 3;

    public static final int SHOW_BOTTOM_VIEW = 1;
    public static final int UPDATE_PROGRESS = 2;
    public static final int HIDE_BOTTOM_VIEW = 3;

    public static final int PROGRESS_DIALOG = 1;
    public static final int CONFLICT_DIALOG = 2;
    public static final int FINISH_DIALOG = 3;

    public static final int CANCEL = 1;
    public static final int SKIP = 2;
    public static final int OVERWRITE = 3;

    public static final int STOP = 0;
    public static final int RUNNING = 1;

    public static final int BOTTOM_MENU_PEEK_ITEM_NUMBER = 6;

    //Delete
    public static final String DEL_PATH = "del_path";
    public static final String DEL_LABEL_FILE_ID = "del_label_file_id";
    public static final String DEL_FILE_ID = "del_file_id";

    //Android 7.0
    public static final String NOUGAT_AUTHOITY = "com.grt.filemanager.fileProvider";

    public static final int COPY = 1;
    public static final int MEMORY_COPY = 2;
    public static final int UPLOAD = 3;
    public static final int PASTE = 4;
    public static final int DELETE = 5;
    public static final int SHREDDER = 6;
    public static final int CUT = 7;
    public static final int MOVE = 8;
    public static final int MOVE_USE_RENAME = 9;
    public static final int CALC_LOCAL_FOLDER = 10;
    public static final int DELETE_LOCAL_CACHE = 11;
    public static final int DELETE_BY_COUNT = 12;
    public static final int MOVE_TO_NET = 13;
    public static final int COPY_IN_NET = 14;
    public static final int MOVE_FROM_NET = 15;
    public static final int MOVE_MEMORY = 16;
    public static final int DELETE_LABEL_FILE = 17;
    public static final int DELETE_TO_RECYCLE = 18;
    public static final int RESTORE_FROM_RECYCLE = 19;
    public static final int BACKUP = 20;
    public static final int DELETE_BY_ID = 21;
    public static final int COPY_ID_FILE = 22;
    public static final int MOVE_ID_FILE_TO_NET = 23;
    public static final int SINGLE_THREAD_UPLOAD = 24;
    public static final int SINGLE_THREAD_DOWNLOAD = 25;
    public static final int SINGLE_THREAD_DEL = 26;
    public static final int COMPRESS = 27;
    public static final int EXTRACT = 28;
    public static final int MOVE_TO_SAFE_BOX = 29;
    public static final int CONVERT_TO_PDF = 30;
    public static final int BACKUP_TO_GCLOUD = 31;
    public static final int RENAME_TO_DELETE = 32;
    public static final int COPY_TO = 33;
    public static final int MOVE_TO = 34;
    public static final int PRIVATE_CLOUD_UPLOAD = 35;
    public static final int PRIVATE_CLOUD_DOWNLOAD = 36;
    public static final int SINGLE_THREAD_COPY_INNETWORK = 37;
    public static final int SECRET_UPLOAD = 38;
    public static final int SECRET_DOWNLOAD = 39;
    public static final int SECRET_DELETE = 40;
    public static final int ONLY_SHREDDER = 41;

    public static final int CANCEL_WORK = 100;
    public static final int FINISH_WORK = 101;
    public static final int STOP_SERVICE = 102;

    private static SparseArray<ProgressData> progressArray = new SparseArray<>();

    public static SparseArray<ProgressData> getProgressArray() {
        return progressArray;
    }

//    private static final SparseBooleanArray useRenameArray = new SparseBooleanArray() {
//        {
//            put(DataConstant.STORAGE_DATA_ID, true);
//            put(DataConstant.IMAGE_DATA_ID, true);
//            put(DataConstant.MUSIC_DATA_ID, true);
//            put(DataConstant.VIDEO_DATA_ID, true);
//            put(DataConstant.ZIP_DATA_ID, true);
//            put(DataConstant.DOC_DATA_ID, true);
//        }
//    };

//    public static SparseBooleanArray getUseRenameArray() {
//        return useRenameArray;
//    }
}
