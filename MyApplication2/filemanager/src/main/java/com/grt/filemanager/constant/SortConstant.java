package com.grt.filemanager.constant;

public final class SortConstant {

    public static final int ASCENDING_ORDER = 1;
    public static final int DESCENDING_ORDER = 2;

    public static final int DO_NOT_SORT = -1;
    public static final int TITLE_ORDER = 1;
    public static final int TYPE_ORDER = 2;
    public static final int SIZE_ORDER = 3;
    public static final int TIME_ORDER = 4;

    private static Integer[] sortArray = new Integer[]{TITLE_ORDER, ASCENDING_ORDER};

    public static Integer[] getSortArray() {
        return sortArray;
    }
}
