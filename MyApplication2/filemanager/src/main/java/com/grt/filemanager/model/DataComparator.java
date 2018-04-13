package com.grt.filemanager.model;


import com.grt.filemanager.constant.SortConstant;

public class DataComparator implements java.util.Comparator<FileInfo> {

    private int mSortType;
    private int mSortOrder;

    public DataComparator(int sortType, int sortOrder) {
        this.mSortType = sortType;
        this.mSortOrder = sortOrder;
    }

    public void updateKeys(int sortType, int sortOrder) {
        this.mSortType = sortType;
        this.mSortOrder = sortOrder;
    }

    @Override
    public int compare(FileInfo lhs, FileInfo rhs) {
        if (lhs == null || rhs == null) {
            return 0;
        }

        int cr;
        if (rhs.isFolder() && (!lhs.isFolder())) {
            cr = 3;
        } else if ((!rhs.isFolder()) && lhs.isFolder()) {
            cr = -1;
        } else {

            if (mSortOrder == SortConstant.ASCENDING_ORDER) {
                cr = sortType(lhs, rhs);
            } else {
                cr = sortType(rhs, lhs);
            }
        }

        return cr;
    }

    private int sortType(FileInfo first, FileInfo second) {
        int result;

        switch (mSortType) {
            case SortConstant.TITLE_ORDER:
                result = first.getName().compareToIgnoreCase(second.getName());
                break;

            case SortConstant.SIZE_ORDER:
                result = Long.valueOf(first.getSize()).compareTo(second.getSize());
                break;

            case SortConstant.TIME_ORDER:
                result = Long.valueOf(first.getLastModified())
                        .compareTo(second.getLastModified());
                break;

            case SortConstant.TYPE_ORDER:
                String firstName = first.getName();
                String firstType = firstName.substring(firstName.lastIndexOf(".") + 1);
                String secondName = second.getName();
                String secondType = secondName.substring(secondName.lastIndexOf(".") + 1);
                result = firstType.compareTo(secondType);
                break;

            default:
                result = first.getName().compareToIgnoreCase(second.getName());
                break;
        }

        return result;
    }
}
