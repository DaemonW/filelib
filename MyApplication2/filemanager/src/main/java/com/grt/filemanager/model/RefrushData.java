package com.grt.filemanager.model;

/**
 * Created by LDC on 2018/3/20.
 * EventBus 回传刷新列表
 */

public class RefrushData {
    private int dataId;
    private DataModel data;
    private String path;//创建文件路径
    private int type;
    private int result;

    public void setData(DataModel data) {
        this.data = data;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public DataModel getData() {
        return data;
    }

    public int getDataId() {
        return dataId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}

