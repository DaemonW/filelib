package com.grt.filemanager.model;

/**
 * Created by LDC on 2018/3/14.
 */

public class CreateInfo {
    public FileInfo file;
    public int result;

    public CreateInfo(FileInfo file, int result) {
        this.file = file;
        this.result = result;
    }
}
