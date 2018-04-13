package com.grt.filemanager.orm.helper;

import com.grt.filemanager.orm.dao.base.DBCore;
import com.grt.filemanager.orm.dao.base.DaoSession;
import com.grt.filemanager.orm.helper.implement.AppFolderHelper;
import com.grt.filemanager.orm.helper.implement.AppHelper;
import com.grt.filemanager.orm.helper.implement.BoxAccountHelper;
import com.grt.filemanager.orm.helper.implement.BoxFileHelper;
import com.grt.filemanager.orm.helper.implement.DownloadMusicHelper;
import com.grt.filemanager.orm.helper.implement.DownloadedFileHelper;
import com.grt.filemanager.orm.helper.implement.DropboxAccountHelper;
import com.grt.filemanager.orm.helper.implement.DropboxFileHelper;
import com.grt.filemanager.orm.helper.implement.FavourateMusicHelper;
import com.grt.filemanager.orm.helper.implement.FePrivateCloudAccountHelper;
import com.grt.filemanager.orm.helper.implement.FePrivateCloudFileHelper;
import com.grt.filemanager.orm.helper.implement.FragmentInfoHelper;
import com.grt.filemanager.orm.helper.implement.FtpAccountHelper;
import com.grt.filemanager.orm.helper.implement.FtpFileInfoHelper;
import com.grt.filemanager.orm.helper.implement.GCloudAccountHelper;
import com.grt.filemanager.orm.helper.implement.GCloudFileHelper;
import com.grt.filemanager.orm.helper.implement.GoogleDriveAccountHelper;
import com.grt.filemanager.orm.helper.implement.GoogleDriveFileHelper;
import com.grt.filemanager.orm.helper.implement.LabelFileHelper;
import com.grt.filemanager.orm.helper.implement.LabelHelper;
import com.grt.filemanager.orm.helper.implement.MixCloudFileHelper;
import com.grt.filemanager.orm.helper.implement.OneDriveAccountHelper;
import com.grt.filemanager.orm.helper.implement.OneDriveFileHelper;
import com.grt.filemanager.orm.helper.implement.PartitionHelper;
import com.grt.filemanager.orm.helper.implement.RecentMusicHelper;
import com.grt.filemanager.orm.helper.implement.RecentOpenHelper;
import com.grt.filemanager.orm.helper.implement.SearchHelper;
import com.grt.filemanager.orm.helper.implement.SinaStorageAccountHelper;
import com.grt.filemanager.orm.helper.implement.SinaStorageFileHelper;
import com.grt.filemanager.orm.helper.implement.SmbAccountHelper;
import com.grt.filemanager.orm.helper.implement.SmbFileHelper;
import com.grt.filemanager.orm.helper.implement.StatisticsHelper;
import com.grt.filemanager.orm.helper.implement.StorageHelper;
import com.grt.filemanager.orm.helper.implement.SugarSyncAccountHelper;
import com.grt.filemanager.orm.helper.implement.SugarSyncFileHelper;
import com.grt.filemanager.orm.helper.implement.SyncHistoryHelper;
import com.grt.filemanager.orm.helper.implement.SyncInfoHelper;
import com.grt.filemanager.orm.helper.implement.SyncSettingHelper;
import com.grt.filemanager.orm.helper.implement.WebDavAccountHelper;
import com.grt.filemanager.orm.helper.implement.WebDavHelper;
import com.grt.filemanager.orm.helper.implement.YanDexAccountHelper;
import com.grt.filemanager.orm.helper.implement.YanDexFileHelper;

import java.util.ArrayList;
import java.util.List;

public class DbUtils {

    /*
    杂项数据
     */
    private static FragmentInfoHelper fragmentHelper;
    private static StatisticsHelper statisticsHelper;
    private static AppFolderHelper appFolderHelper;

    /*
    本地磁盘缓存数据
     */
    private static StorageHelper storageHelper;
    private static AppHelper appHelper;
    private static SearchHelper searchHelper;
    private static RecentOpenHelper recentOpenHelper;
    private static LabelHelper labelHelper;
    private static LabelFileHelper labelFileHelper;
    private static FavourateMusicHelper favourateMusicHelper;
    private static RecentMusicHelper recentMusicHelper;
    private static DownloadMusicHelper downloadMusicHelper;
    private static DownloadedFileHelper downloadedFileHelper;


    /*
    网络缓存数据
     */
    private static DropboxAccountHelper dropboxAccountHelper;
    private static DropboxFileHelper dropboxFileHelper;

    private static GoogleDriveAccountHelper googleAccountHelper;
    private static GoogleDriveFileHelper googleFileHelper;

    private static OneDriveAccountHelper oneDriveAccountHelper;
    private static OneDriveFileHelper oneDriveFileHelper;

    private static YanDexAccountHelper yanDexAccountHelper;
    private static YanDexFileHelper yanDexFileHelper;

    private static BoxAccountHelper boxAccountHelper;
    private static BoxFileHelper boxFileHelper;

    private static FtpAccountHelper ftpAccountHelper;
    private static FtpFileInfoHelper ftpFileInfoHelper;

    private static SmbFileHelper smbFileHelper;
    private static SmbAccountHelper smbAccountHelper;

    private static GCloudFileHelper gcloudFileHelper;
    private static GCloudAccountHelper gcloudAccountHelper;

    private static WebDavHelper webDavFileHelper;
    private static WebDavAccountHelper webDavAccountHelper;

    private static SugarSyncAccountHelper sugarsyncAccountHelper;
    private static SugarSyncFileHelper sugarsyncFileHelper;

    private static FePrivateCloudAccountHelper fePrivateCloudAccountHelper;
    private static FePrivateCloudFileHelper fePrivateCloudFileHelper;

    private static SinaStorageAccountHelper sinaStorageAccountHelper;
    private static SinaStorageFileHelper sinaStorageFileHelper;

    private static MixCloudFileHelper mixCloudFileHelper;

    private static PartitionHelper partitionHelper;

    /**
     * 同步缓存信息，同步设置，同步历史
     */
    private static SyncInfoHelper syncInfoHelper;
    private static SyncSettingHelper syncSettingHelper;
    private static SyncHistoryHelper syncHistoryHelper;

    private static DaoSession session;


    public static FragmentInfoHelper getFragmentHelper() {
        if (fragmentHelper == null || fragmentHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            fragmentHelper = new FragmentInfoHelper(session.getFragmentInfoDao());
        }
        return fragmentHelper;
    }

    public static StorageHelper getStorageHelper() {
        if (storageHelper == null || storageHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            storageHelper = new StorageHelper(session.getStorageDao());
        }
        return storageHelper;
    }

    public static StatisticsHelper getStatisticsHelper() {
        if (statisticsHelper == null || statisticsHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            statisticsHelper = new StatisticsHelper(session.getStatisticsDao());
        }
        return statisticsHelper;
    }

    public static AppHelper getAppHelper() {
        if (appHelper == null || appHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            appHelper = new AppHelper(session.getAppDao());
        }
        return appHelper;
    }

    public static SearchHelper getSearchHelper() {
        if (searchHelper == null || searchHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            searchHelper = new SearchHelper(session.getSearchDao());
        }
        return searchHelper;
    }

    public static AppFolderHelper getAppFolderHelper() {
        if (appFolderHelper == null || appFolderHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            appFolderHelper = new AppFolderHelper(session.getAppFolderDao());
        }
        return appFolderHelper;
    }

    public static DropboxAccountHelper getDropboxAccountHelper() {
        if (dropboxAccountHelper == null || dropboxAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            dropboxAccountHelper = new DropboxAccountHelper(session.getDropboxAccountDao());
        }
        return dropboxAccountHelper;
    }

    public static SmbFileHelper getSmbFileHelper() {
        if (smbFileHelper == null || smbFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            smbFileHelper = new SmbFileHelper(session.getFeSmbFileDao());
        }
        return smbFileHelper;
    }

    public static SmbAccountHelper getSmbAccountHelper() {
        if (smbAccountHelper == null || smbAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            smbAccountHelper = new SmbAccountHelper(session.getSmbAccountDao());
        }
        return smbAccountHelper;
    }

    public static DropboxFileHelper getDropboxFileHelper() {
        if (dropboxFileHelper == null || dropboxFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            dropboxFileHelper = new DropboxFileHelper(session.getDropboxFileDao());
        }
        return dropboxFileHelper;
    }

    public static BoxFileHelper getBoxFileHelper() {
        if (boxFileHelper == null || boxFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            boxFileHelper = new BoxFileHelper(session.getBoxFileDao());
        }
        return boxFileHelper;
    }

    public static BoxAccountHelper getBoxAccountHelper() {
        if (boxAccountHelper == null || boxAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            boxAccountHelper = new BoxAccountHelper(session.getBoxAccountDao());
        }
        return boxAccountHelper;
    }

    public static GoogleDriveAccountHelper getGoogleDriveAccountHelper() {
        if (googleAccountHelper == null || googleAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            googleAccountHelper = new GoogleDriveAccountHelper(session.getGoogleDriveAccountDao());
        }
        return googleAccountHelper;
    }

    public static GoogleDriveFileHelper getGoogleFileHelper() {
        if (googleFileHelper == null || googleFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            googleFileHelper = new GoogleDriveFileHelper(session.getGoogleDriveFileDao());
        }
        return googleFileHelper;
    }

    public static RecentOpenHelper getRecentOpenHelper() {
        if (recentOpenHelper == null || recentOpenHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            recentOpenHelper = new RecentOpenHelper(session.getRecentOpenDao());
        }
        return recentOpenHelper;
    }

    public static LabelHelper getLabelHelper() {
        if (labelHelper == null || labelHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            labelHelper = new LabelHelper(session.getLabelDao());
        }
        return labelHelper;
    }

    public static LabelFileHelper getLabelFileHelper() {
        if (labelFileHelper == null || labelFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            labelFileHelper = new LabelFileHelper(session.getLabelFileDao());
        }
        return labelFileHelper;
    }

    public static FavourateMusicHelper getFavourateMusicHelper() {
        if (favourateMusicHelper == null || favourateMusicHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            favourateMusicHelper = new FavourateMusicHelper(session.getFavourateMusicDao());
        }
        return favourateMusicHelper;
    }

    public static DownloadMusicHelper getDownloadMusicHelper() {
        if (downloadMusicHelper == null || downloadMusicHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            downloadMusicHelper = new DownloadMusicHelper(session.getDownloadMusicDao());
        }
        return downloadMusicHelper;
    }

    public static RecentMusicHelper getRecentMusicHelper() {
        if (recentMusicHelper == null || recentMusicHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            recentMusicHelper = new RecentMusicHelper(session.getRecentMusicDao());
        }
        return recentMusicHelper;
    }

    public static FtpAccountHelper getFtpAccountHelper() {
        if (ftpAccountHelper == null || ftpAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            ftpAccountHelper = new FtpAccountHelper(session.getFtpAccountDao());
        }
        return ftpAccountHelper;
    }

    public static FtpFileInfoHelper getFtpFileInfoHelper() {
        if (ftpFileInfoHelper == null || ftpFileInfoHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            ftpFileInfoHelper = new FtpFileInfoHelper(session.getFtpFileInfoDao());
        }
        return ftpFileInfoHelper;
    }

    public static OneDriveAccountHelper getOneDriveAccountHelper() {
        if (oneDriveAccountHelper == null || oneDriveAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            oneDriveAccountHelper = new OneDriveAccountHelper(session.getOneDriveAccountDao());
        }
        return oneDriveAccountHelper;
    }

    public static YanDexFileHelper getYanDexFileHelper() {
        if (yanDexFileHelper == null || yanDexFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            yanDexFileHelper = new YanDexFileHelper(session.getYandexFileDao());
        }
        return yanDexFileHelper;
    }

    public static YanDexAccountHelper getYanDexAccountHelper() {
        if (yanDexAccountHelper == null || yanDexAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            yanDexAccountHelper = new YanDexAccountHelper(session.getYandexAccountDao());
        }
        return yanDexAccountHelper;
    }

    public static OneDriveFileHelper getOneFileHelper() {
        if (oneDriveFileHelper == null || oneDriveFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            oneDriveFileHelper = new OneDriveFileHelper(session.getOneDriveFileDao());
        }
        return oneDriveFileHelper;
    }

    public static GCloudAccountHelper getGCloudAccountHelper() {
        if (gcloudAccountHelper == null || gcloudAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            gcloudAccountHelper = new GCloudAccountHelper(session.getGCloudAccountDao());
        }
        return gcloudAccountHelper;
    }

    public static GCloudFileHelper getGCloudFileHelper() {
        if (gcloudFileHelper == null || gcloudFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            gcloudFileHelper = new GCloudFileHelper(session.getGCloudFileDao());
        }
        return gcloudFileHelper;
    }

    public static WebDavHelper getWebDavFileHelper() {
        if (webDavFileHelper == null || webDavFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            webDavFileHelper = new WebDavHelper(session.getWebDavFileDao());
        }
        return webDavFileHelper;
    }

    public static WebDavAccountHelper getWebDavAccountHelper() {
        if (webDavAccountHelper == null || webDavAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            webDavAccountHelper = new WebDavAccountHelper(session.getWebDavAccountDao());
        }
        return webDavAccountHelper;
    }

    public static SugarSyncFileHelper getSugarSyncFileHelper() {
        if (sugarsyncFileHelper == null || sugarsyncFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            sugarsyncFileHelper = new SugarSyncFileHelper(session.getSugarsyncFileDao());
        }
        return sugarsyncFileHelper;
    }

    public static SugarSyncAccountHelper getSugarSyncAccountHelper() {
        if (sugarsyncAccountHelper == null || sugarsyncAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            sugarsyncAccountHelper = new SugarSyncAccountHelper(session.getSugarsyncAccountDao());
        }
        return sugarsyncAccountHelper;
    }

    public static FePrivateCloudAccountHelper getFePrivateCloudAccountHelper() {
        if (fePrivateCloudAccountHelper == null || fePrivateCloudAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            fePrivateCloudAccountHelper = new FePrivateCloudAccountHelper(session.getFePrivateCloudAccountDao());
        }
        return fePrivateCloudAccountHelper;
    }

    public static FePrivateCloudFileHelper getFePrivateCloudFileHelper() {
        if (fePrivateCloudFileHelper == null || fePrivateCloudFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            fePrivateCloudFileHelper = new FePrivateCloudFileHelper(session.getFePrivateCloudFileDao());
        }
        return fePrivateCloudFileHelper;
    }

    public static DownloadedFileHelper getDownloadedFileHelper() {
        if (downloadedFileHelper == null || downloadedFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            downloadedFileHelper = new DownloadedFileHelper(session.getDownloadedFileDao());
        }
        return downloadedFileHelper;
    }

    public static SyncInfoHelper getSyncInfoHelper() {
        if (syncInfoHelper == null || syncInfoHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            syncInfoHelper = new SyncInfoHelper(session.getSyncInfoDao());
        }
        return syncInfoHelper;
    }

    public static SyncSettingHelper getSyncSettingHelper() {
        if (syncSettingHelper == null || syncSettingHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            syncSettingHelper = new SyncSettingHelper(session.getSyncSettingDao());
        }
        return syncSettingHelper;
    }

    public static SyncHistoryHelper getSyncHistoryHelper() {
        if (syncHistoryHelper == null || syncHistoryHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            syncHistoryHelper = new SyncHistoryHelper(session.getSyncHistoryDao());
        }
        return syncHistoryHelper;
    }

    public static SinaStorageAccountHelper getSinaStorageAccountHelper() {
        if (sinaStorageAccountHelper == null || sinaStorageAccountHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            sinaStorageAccountHelper = new SinaStorageAccountHelper(session.getSinaStorageAccountDao());
        }
        return sinaStorageAccountHelper;
    }

    public static SinaStorageFileHelper getSinaStorageFileHelper() {
        if (sinaStorageFileHelper == null || sinaStorageFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            sinaStorageFileHelper = new SinaStorageFileHelper(session.getSinaStorageFileDao());
        }
        return sinaStorageFileHelper;
    }

    public static MixCloudFileHelper getMixCloudFileHelper() {
        if (mixCloudFileHelper == null || mixCloudFileHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            mixCloudFileHelper = new MixCloudFileHelper(session.getMixCloudFileDao());
        }
        return mixCloudFileHelper;
    }

    public static PartitionHelper getPartitionHelper() {
        if (partitionHelper == null || partitionHelper.isNeedReloadDB()) {
            session = DBCore.getDaoSession();
            partitionHelper = new PartitionHelper(session.getPartitionDao());
        }
        return partitionHelper;
    }

    public static void removeAllData() {
        if (ftpFileInfoHelper != null && !ftpFileInfoHelper.isNeedReloadDB()) {
            ftpFileInfoHelper.deleteAll();
        }

        if (fePrivateCloudFileHelper != null && !fePrivateCloudFileHelper.isNeedReloadDB()) {
            fePrivateCloudFileHelper.deleteAll();
        }
    }

    public static boolean enoughCloud4Secret() {
        List<BaseHelper> helperMap = getSecretCloudHelpers();
        long count = gcloudAccountHelper.count();
        for (BaseHelper helper : helperMap) {
            count += helper.count();
            if (count >= 3) {
                return true;
            }
        }

        return false;
    }

    public static List<BaseHelper> getSecretCloudHelpers() {
        return new ArrayList<BaseHelper>(){{
            add(dropboxAccountHelper);
            add(googleAccountHelper);
            add(oneDriveAccountHelper);
            add(sinaStorageAccountHelper);
            add(webDavAccountHelper);
        }};
    }

}