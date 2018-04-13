package com.grt.filemanager.orm.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "FAVOURATE_MUSIC".
 */
public class FavourateMusic {

    private Long id;
    private String name;
    private String path;
    private String album;
    private Long size;
    private Long lastModified;
    private String mimeType;
    private Boolean isFolder;
    private Long clickTime;
    private Long duration;

    public FavourateMusic() {
    }

    public FavourateMusic(Long id) {
        this.id = id;
    }

    public FavourateMusic(Long id, String name, String path, String album, Long size, Long lastModified, String mimeType, Boolean isFolder, Long clickTime, Long duration) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.album = album;
        this.size = size;
        this.lastModified = lastModified;
        this.mimeType = mimeType;
        this.isFolder = isFolder;
        this.clickTime = clickTime;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Boolean getIsFolder() {
        return isFolder;
    }

    public void setIsFolder(Boolean isFolder) {
        this.isFolder = isFolder;
    }

    public Long getClickTime() {
        return clickTime;
    }

    public void setClickTime(Long clickTime) {
        this.clickTime = clickTime;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

}
