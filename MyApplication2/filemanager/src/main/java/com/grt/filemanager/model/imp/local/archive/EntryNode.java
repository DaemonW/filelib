package com.grt.filemanager.model.imp.local.archive;


import android.support.v4.util.ArrayMap;

import com.grt.filemanager.util.FileUtils;

import java.util.Map;

public class EntryNode {

    private String path;

    private boolean isFolder;
    private Object header;
    private ArrayMap<String, EntryNode> children;

    public EntryNode(String path, boolean isFolder, Object header) {
        this.path = path;
        this.isFolder = isFolder;
        this.header = header;
        this.children = new ArrayMap<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        if (path.endsWith("/")) {
            String tmpPath = path.substring(0, path.lastIndexOf("/"));
            return FileUtils.getFileName(tmpPath);
        } else {
            return FileUtils.getFileName(path);
        }
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public Object getHeader() {
        return header;
    }

    public void setHeader(Object header) {
        this.header = header;
    }

    public void addChild(String path, EntryNode entryNode) {
        children.put(path, entryNode);
    }

    public Map<String, EntryNode> getChildren() {
        return children;
    }
}
