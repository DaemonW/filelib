package com.grt.filemanager.orm.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "APP".
 */
public class App {

    private Long id;
    private String displayName;
    private String packageName;
    private String path;
    private Long size;
    private Long lastModified;
    private Integer versionCode;
    private Integer installedVersionCode;
    private String versionName;
    private Boolean isSystem;
    private Boolean isInstalled;
    private Boolean isPackage;

    public App() {
    }

    public App(Long id) {
        this.id = id;
    }

    public App(Long id, String displayName, String packageName, String path, Long size, Long lastModified, Integer versionCode, Integer installedVersionCode, String versionName, Boolean isSystem, Boolean isInstalled, Boolean isPackage) {
        this.id = id;
        this.displayName = displayName;
        this.packageName = packageName;
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
        this.versionCode = versionCode;
        this.installedVersionCode = installedVersionCode;
        this.versionName = versionName;
        this.isSystem = isSystem;
        this.isInstalled = isInstalled;
        this.isPackage = isPackage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public Integer getInstalledVersionCode() {
        return installedVersionCode;
    }

    public void setInstalledVersionCode(Integer installedVersionCode) {
        this.installedVersionCode = installedVersionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Boolean getIsInstalled() {
        return isInstalled;
    }

    public void setIsInstalled(Boolean isInstalled) {
        this.isInstalled = isInstalled;
    }

    public Boolean getIsPackage() {
        return isPackage;
    }

    public void setIsPackage(Boolean isPackage) {
        this.isPackage = isPackage;
    }

}
