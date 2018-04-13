package com.grt.filemanager.model;

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProgressData implements Serializable {

    private int mOperationId;
    private String mCurName;
    private String mDstFolderPath;
    private FileInfo mDstFolder;
    private String mSrcFolderPath;
    private long mTime;
    private long mSize;
    private long mProgressMax;
    private AtomicLong mProgressValue;
    private AtomicLong mLastProgressValue;
    private int mAllCount;
    private AtomicInteger mCurNumber;
    private int mSelectedCount;
    private AtomicInteger mSpendTime;

    private int mDesUniqueType = UNIQUE_TYPE_PATH; //标识文件唯一的类型:路径，Id
    private int mSrcUniqueType = UNIQUE_TYPE_PATH;

    public static final int UNIQUE_TYPE_PATH = 1;
    public static final int UNIQUE_TYPE_ID = 2;

    private int mProgressType; //

    public static final int PROGRESS_TYPE_COUNT = 1;
    public static final int PROGRESS_TYPE_SIZE = 2;

    private int mOperationType; //文件操作类型：复制，移动

    public static final int COPY = 1;
    public static final int MOVE = 2;
    public static final int COMPRESS = 3;
    public static final int EXTRACT = 4;
    public static final int BACKUP = 5;
    public static final int DELETE = 6;
    public static final int DELETE_TO_RECYCLE = 7;
    public static final int RESTORE_FROM_RECYCLE = 8;
    public static final int TO_PDF = 9;
    public static final int SHREDDER = 10;
    public static final int MOVE_TO_SAFEBOX = 11;
    public static final int BACKUP_TO_GCLOUD = 12;

    private long mDesFragmentId;
    private long mSrcFragmentId;
    private int mDesDataId;
    private int mSrcDataId;
    private int mDesAccountId;
    private int mSrcAccountId;

    private List<Integer> mSelectedPosition;
    private List<FileInfo> mSelectedList;
    private List<FileInfo> mNewConflictList;
    private List<FileInfo> mOldConflictList;
    private List<FileInfo> mFailedList;
    private List<FileInfo> mSuccessList;
    private int mConflictResolve;

    private Timer mTimer;
    private long mSpeed;

    private int mOperationStatus = STATUS_PROCESSING;//0:preparing; 1:fail; 2:copy_successful
    private int mOperationResult = 2;//failed

    public static final int STATUS_PROCESSING = 0;
    public static final int STATUS_FAIL = 1;
    public static final int STATUS_SUCCESSFUL = 2;
    public static final int STATUS_CANCEL = 3;
    public static final int STATUS_UPLOADING = 4;
    public static final int STATUS_CONVERTING = 5;

    private String mFilename;
    private String mPassword;
    private String mCharset;
    private boolean mIsArchiveFile;
    private boolean mShowDialog = true;
    private boolean mIsRename = false;
    private boolean mReSelectedDes = false;
    private Bundle mArgs;
    private int mCurShowView = STATUS_SHOW_DIALOG;
    private boolean mRefreshView = false;

    public static final int STATUS_SHOW_NOTIFY = 1;
    public static final int STATUS_SHOW_DIALOG = 2;

    public ProgressData() {
        this.mSpendTime = new AtomicInteger(0);
        this.mProgressValue = new AtomicLong(0);
        this.mLastProgressValue = new AtomicLong(0);
        this.mCurNumber = new AtomicInteger(0);
    }

    public int getOperationId() {
        return mOperationId;
    }

    public void setOperationId(int mOperationId) {
        this.mOperationId = mOperationId;
    }

    public String getCurName() {
        return mCurName;
    }

    public void setCurName(String mCurName) {
        this.mCurName = mCurName;
    }

    public String getDstFolderPath() {
        return mDstFolderPath;
    }

    public void setDstFolderPath(String dstFolderPath) {
        this.mDstFolderPath = dstFolderPath;
    }

    public FileInfo getDstFolder() {
        return mDstFolder;
    }

    public void setDstFolder(FileInfo dstFolder) {
        this.mDstFolder = dstFolder;
    }

    public String getSrcFolderPath() {
        return mSrcFolderPath;
    }

    public void setSrcFolderPath(String srcFolderPath) {
        this.mSrcFolderPath = srcFolderPath;
    }

    public long getProgressMax() {
        return mProgressMax;
    }

    public void setProgressMax(long mProgressMax) {
        this.mProgressMax = mProgressMax;
    }

    public void addProgressMax(long value) {
        this.mProgressMax += value;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        this.mSize = size;
    }

    public long getProgressValue() {
        return mProgressValue.longValue();
    }

    public void setProgressValue(long progressValue) {
        this.mProgressValue.set(progressValue);
    }

    public void addProgressValue(long progressValue) {
        this.mProgressValue.addAndGet(progressValue);
    }

    public long getLastProgressValue() {
        return mLastProgressValue.longValue();
    }

    public void setLastProgressValue(long lastProgressValue) {
        this.mLastProgressValue.addAndGet(lastProgressValue);
    }

    public int getAllCount() {
        return mAllCount;
    }

    public void setAllCount(int allCount) {
        this.mAllCount = allCount;
    }

    public void addAllCount(int count) {
        this.mAllCount += count;
    }

    public int getCurNumber() {
        return mCurNumber.intValue();
    }

    public void setCurNumber(int curNumber) {
        this.mCurNumber.set(curNumber);
    }

    public void addCurNumber() {
        this.mCurNumber.incrementAndGet();
    }

    public void addMultipleNumbers(int count) {
        this.mCurNumber.addAndGet(count);
    }

    public int getSelectedCount() {
        return mSelectedCount;
    }

    public void setSelectedCount(int selectedCount) {
        this.mSelectedCount = selectedCount;
    }

    public int getOperationStatus() {
        return mOperationStatus;
    }

    public void setOperationStatus(int operationStatus) {
        this.mOperationStatus = operationStatus;
    }

    public int getOperationResult() {
        return mOperationResult;
    }

    public void setOperationResult(int operationResult) {
        this.mOperationResult = operationResult;
    }

    public int getSpendTime() {
        return mSpendTime.get();
    }

    public void setSpendTime(int spendTime) {
        this.mSpendTime.set(spendTime);
    }

    public void addSpendTime() {
        this.mSpendTime.incrementAndGet();
    }

    public int getDesUniqueType() {
        return mDesUniqueType;
    }

    public void setDesUniqueType(int uniqueType) {
        this.mDesUniqueType = uniqueType;
    }

    public int getSrcUniqueType() {
        return mSrcUniqueType;
    }

    public void setSrcUniqueType(int uniqueType) {
        this.mSrcUniqueType = uniqueType;
    }

    public int getProgressType() {
        return mProgressType;
    }

    public void setProgressType(int progressType) {
        this.mProgressType = progressType;
    }

    public int getOperationType() {
        return mOperationType;
    }

    public void setOperationType(int operationType) {
        this.mOperationType = operationType;
    }

    public long getDesFragmentId() {
        return mDesFragmentId;
    }

    public void setDesFragmentId(long desFragmentId) {
        this.mDesFragmentId = desFragmentId;
    }

    public long getSrcFragmentId() {
        return mSrcFragmentId;
    }

    public void setSrcFragmentId(long srcFragmentId) {
        this.mSrcFragmentId = srcFragmentId;
    }

    public int getDesDataId() {
        return mDesDataId;
    }

    public void setDesDataId(int desDataId) {
        this.mDesDataId = desDataId;
    }

    public int getSrcDataId() {
        return mSrcDataId;
    }

    public void setSrcDataId(int srcDataId) {
        this.mSrcDataId = srcDataId;
    }

    public int getDesAccountId() {
        return mDesAccountId;
    }

    public void setDesAccountId(int desAccountId) {
        this.mDesAccountId = desAccountId;
    }

    public int getSrcAccountId() {
        return mSrcAccountId;
    }

    public void setSrcAccountId(int srcAccountId) {
        this.mSrcAccountId = srcAccountId;
    }

    public List<Integer> getSelectedPosition() {
        return mSelectedPosition;
    }

    public void setSelectedPosition(List<Integer> selectedPosition) {
        this.mSelectedPosition = selectedPosition;
    }

    public List<FileInfo> getSelectedList() {
        return mSelectedList;
    }

    public void setSelectedList(List<FileInfo> fileList) {
        this.mSelectedList = fileList;
    }

    public List<FileInfo> getNewConflictList() {
        return mNewConflictList;
    }

    public void setNewConflictList(ArrayList<FileInfo> newConflictList) {
        this.mNewConflictList = newConflictList;
    }

    public List<FileInfo> getOldConflictList() {
        return mOldConflictList;
    }

    public void setOldConflictList(ArrayList<FileInfo> oldConflictList) {
        this.mOldConflictList = oldConflictList;
    }

    public List<FileInfo> getFailedList() {
        return mFailedList;
    }

    public void addFailed(List<FileInfo> failedList) {
        if (mFailedList == null) {
            mFailedList = new ArrayList<>();
        }
        mFailedList.addAll(failedList);
    }

    public void addFailed(FileInfo failedFile) {
        if (mFailedList == null) {
            mFailedList = new ArrayList<>();
        }
        mFailedList.add(failedFile);
    }

    public void addSuccess(FileInfo file) {
        if (mSuccessList == null) {
            mSuccessList = new ArrayList<>();
        }
        mSuccessList.add(file);
    }

    public List<FileInfo> getSuccessList() {
        return mSuccessList;
    }

    public int getConflictResolve() {
        return mConflictResolve;
    }

    public void setConflictResolve(int conflictResolve) {
        this.mConflictResolve = conflictResolve;
    }

    public Timer getTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        return mTimer;
    }

    public long getSpeed() {
        return mSpeed;
    }

    public void setSpeed(long mSpeed) {
        this.mSpeed = mSpeed;
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String filename) {
        this.mFilename = filename;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public String getCharset() {
        return mCharset;
    }

    public void setCharset(String charset) {
        this.mCharset = charset;
    }

    public boolean isIsArchiveFile() {
        return mIsArchiveFile;
    }

    public void setIsArchiveFile(boolean isArchiveFile) {
        this.mIsArchiveFile = isArchiveFile;
    }

    public boolean isShowDialog() {
        return mShowDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.mShowDialog = showDialog;
    }

    public boolean isRename() {
        return mIsRename;
    }

    public void setIsRename(boolean isRename) {
        this.mIsRename = isRename;
    }

    public boolean isReSelectedDes() {
        return mReSelectedDes;
    }

    public void setReSelectedDes(boolean reSelectedDes) {
        this.mReSelectedDes = reSelectedDes;
    }

    public Bundle getArgs() {
        return mArgs;
    }

    public void setArgs(Bundle args) {
        this.mArgs = args;
    }

    public int getCurShowView() {
        return mCurShowView;
    }

    public void setCurShowView(int curShowView) {
        this.mCurShowView = curShowView;
    }

    public boolean isRefreshView() {
        return mRefreshView;
    }

    public void setRefreshView(boolean refreshView) {
        this.mRefreshView = refreshView;
    }
}
