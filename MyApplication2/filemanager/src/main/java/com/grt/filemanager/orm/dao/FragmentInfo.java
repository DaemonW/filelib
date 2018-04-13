package com.grt.filemanager.orm.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "FRAGMENT_INFO".
 */
public class FragmentInfo {

    private Integer dataId;
    private Integer accountId;
    private Long fragmentId;
    private String fragmentTitle;
    private String accountTitle;
    private String currentPath;
    private String stack;
    private Integer sortType;
    private Integer sortOrder;
    private Integer rankMode;

    public FragmentInfo() {
    }

    public FragmentInfo(Integer dataId, Integer accountId, Long fragmentId, String fragmentTitle, String accountTitle, String currentPath, String stack, Integer sortType, Integer sortOrder, Integer rankMode) {
        this.dataId = dataId;
        this.accountId = accountId;
        this.fragmentId = fragmentId;
        this.fragmentTitle = fragmentTitle;
        this.accountTitle = accountTitle;
        this.currentPath = currentPath;
        this.stack = stack;
        this.sortType = sortType;
        this.sortOrder = sortOrder;
        this.rankMode = rankMode;
    }

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Long getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(Long fragmentId) {
        this.fragmentId = fragmentId;
    }

    public String getFragmentTitle() {
        return fragmentTitle;
    }

    public void setFragmentTitle(String fragmentTitle) {
        this.fragmentTitle = fragmentTitle;
    }

    public String getAccountTitle() {
        return accountTitle;
    }

    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getRankMode() {
        return rankMode;
    }

    public void setRankMode(Integer rankMode) {
        this.rankMode = rankMode;
    }

}
