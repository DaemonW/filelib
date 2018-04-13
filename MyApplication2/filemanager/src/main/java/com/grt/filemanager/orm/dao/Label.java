package com.grt.filemanager.orm.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "LABEL".
 */
public class Label {

    private Long id;
    private String labelType;
    private Integer labelColor;
    private Long date_modified;
    private int count;
    private String path;
    private String name;
    private String mimeType;

    private boolean isFolder;

    public Label() {
    }

    public Label(Long id) {
        this.id = id;
    }

    public Label(Long id, String labelType, Integer labelColor, Long date_modified) {
        this.id = id;
        this.labelType = labelType;
        this.labelColor = labelColor;
        this.date_modified = date_modified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabelType() {
        return labelType;
    }

    public void setLabelType(String labelType) {
        this.labelType = labelType;
    }

    public Integer getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(Integer labelColor) {
        this.labelColor = labelColor;
    }

    public Long getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(Long date_modified) {
        this.date_modified = date_modified;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public String getTabName() {
        return name;
    }

    public void setTabName(String name) {
        this.name = name;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
