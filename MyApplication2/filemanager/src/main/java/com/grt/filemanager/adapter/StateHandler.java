package com.grt.filemanager.adapter;

import android.text.TextUtils;
import android.util.SparseBooleanArray;

import com.grt.filemanager.model.FileInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StateHandler {

    private static StateHandler mStateHandler;

    private Map<Integer, Boolean> mSelectedItems;
    private SparseBooleanArray mItemsAnimShow;
    private Set<String> mSelectPath;

    private boolean mIsLongClickState;
    private boolean mIsCopyState;
    private boolean mIsUserClose;
    private String mSelectedParentPath;
    private long mSelectedFragmentId;

    private boolean mHasRecentOpen = false;
    private boolean isFirstEditor = true;

    private ComparatorByInt mComparator;

    private StateHandler() {
        mSelectPath = new HashSet<>();
        mSelectedItems = new LinkedHashMap<>();
        mItemsAnimShow = new SparseBooleanArray();
        mComparator = new ComparatorByInt();
    }

    public static StateHandler getInstance() {
        if (mStateHandler == null) {
            mStateHandler = new StateHandler();
        }
        return mStateHandler;
    }

    public void setIsFirstEditor(boolean isFirstEditor) {
        this.isFirstEditor = isFirstEditor;
    }

    public boolean isFirstEditor() {
        return isFirstEditor;
    }

    public boolean hasRecentOpen() {
        return mHasRecentOpen;
    }

    public void setHasRecentOpen(boolean hasRecentOpen) {
        this.mHasRecentOpen = hasRecentOpen;
    }

    public void setSelectedParentPath(String selectedParentPath) {
        mSelectedParentPath = selectedParentPath;
    }

    public String getSelectedParentPath() {
        return mSelectedParentPath;
    }

    public boolean getSelectState() {
        return mIsLongClickState;
    }

    public void setSelectState(boolean state) {
        mIsLongClickState = state;
    }

    public void setSelectedFragmentId(long fragmentId) {
        mSelectedFragmentId = fragmentId;
    }

    public long getSelectedFragmentId() {
        return mSelectedFragmentId;
    }

    public boolean isSelected(String path) {
        return mSelectPath.contains(path);
    }

    public boolean isIsUserClose() {
        return mIsUserClose;
    }

    public void setIsUserClose(boolean isUserClose) {
        this.mIsUserClose = isUserClose;
    }

    public boolean isNotSelectAndNotCopy() {
        return !mIsCopyState && !mIsLongClickState;
    }

    public boolean isSelectAndNotCopy() {
        return !mIsCopyState && mIsLongClickState;
    }

    public boolean isSelectOrCopy() {
        return mIsLongClickState || mIsCopyState;
    }

    public boolean checkSelectAndUserClose() {
        boolean result = isSelectAndNotCopy() && !mIsUserClose;
        mIsUserClose = false;
        return result;
    }

    //获取选中item总个数
    public int getSelectedItemCount() {
        return mSelectPath.size();
    }

    public boolean getIsCopyState() {
        return mIsCopyState;
    }

    public void setCopyState(boolean copyState) {
        mIsCopyState = copyState;
        if (!copyState) {
            mSelectedFragmentId = -1;
        }
    }

    public void putAnimShow(int pos) {
        mItemsAnimShow.put(pos, true);
    }

    public boolean getIsAnimShow(int pos) {
        return mItemsAnimShow.get(pos, false);
    }

    /**
     * 如果单击 设置为选中
     */
    public int selectItem(String path, int curSelectPos) {
        if (mSelectedItems.containsKey(curSelectPos)) {
            mSelectedItems.remove(curSelectPos);
            mItemsAnimShow.delete(curSelectPos);
            mSelectPath.remove(path);
        } else {
            mSelectedItems.put(curSelectPos, true);
            mSelectPath.add(path);
        }

        return curSelectPos;
    }

    /**
     * 再次长按 选中 上一次 到 这次长按区间所有文件 设置为选中
     */
    public int[] longClickAgain(DataAdapter dataAdapter, int curSelectPos) {
        int lastSelectPos = getLastSelectPos();

        if (!mSelectedItems.containsKey(curSelectPos)) {
            if (curSelectPos > lastSelectPos) {
                recordSelectItem(dataAdapter, lastSelectPos, curSelectPos);
            } else {
                recordSelectItem(dataAdapter, curSelectPos, lastSelectPos);
            }
        }

        return getRefreshPos(lastSelectPos, curSelectPos);
    }

    private void recordSelectItem(DataAdapter dataAdapter, int start, int count) {
        String path;
        for (int i = start; i <= count; i++) {
            path = dataAdapter.getPathByPosition(i);
            if (!TextUtils.isEmpty(path)) {
                mSelectedItems.put(i, true);
                mSelectPath.add(path);
            }
        }
    }

    public int[] getRefreshPos(int lastSelectPos, int curSelectPos) {
        int count;
        if (curSelectPos > lastSelectPos) {
            count = curSelectPos - lastSelectPos;
            return new int[]{lastSelectPos + 1, count};
        } else {
            count = lastSelectPos - curSelectPos;
            return new int[]{curSelectPos, count};
        }
    }

    /**
     * @return 获取上一次选中位置
     */
    public int getLastSelectPos() {
        int lastSelectPosition = -1;
        if (mSelectedItems.size() > 0) {
            Set<Integer> selectedPositions = mSelectedItems.keySet();
            for (int position : selectedPositions) {
                lastSelectPosition = position;
            }
        }

        return lastSelectPosition;
    }

    /**
     * 获取选中文件的位置集合.
     *
     * @return 选中文件的位置集合
     */
    public ArrayList<Integer> getSelectedItemList() {
        ArrayList<Integer> items = getSelectedItemListByOrder();
        Collections.sort(items, mComparator);
        return items;
    }

    /**
     * 返回按选中顺序排列的位置集合
     *
     * @return 选中文件的位置集合
     */
    public ArrayList<Integer> getSelectedItemListByOrder() {
        ArrayList<Integer> items = new ArrayList<>();

        Set<Integer> selectedSet = mSelectedItems.keySet();
        for (int i : selectedSet) {
            items.add(i);
        }

        return items;
    }

    public Map<Integer, Boolean> getSelectedArray() {
        return mSelectedItems;
    }

    public Set<String> getPathArray() {
        return mSelectPath;
    }

    public void resetState() {
        mSelectedItems.clear();
        mItemsAnimShow.clear();
        mSelectPath.clear();
        mIsLongClickState = false;
        mIsCopyState = false;
        mSelectedFragmentId = -1;
        mSelectedParentPath = null;
    }

    static class ComparatorByInt implements Comparator<Integer> {

        public int compare(Integer f1, Integer f2) {
            long diff = f1 - f2;
            if (diff > 0) {
                return 1;
            } else if (diff == 0) {
                return 0;
            } else {
                return -1;
            }
        }

    }

}
