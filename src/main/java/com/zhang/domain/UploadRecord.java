package com.zhang.domain;

public class UploadRecord {
    String name;//文件名
    String uuidName;//唯一文件名
    String uploadDate;//上传日期
    String size;//文件大小

    public UploadRecord() {
    }

    public UploadRecord(String name, String uuidName, String uplaodDate, String size) {
        this.name = name;
        this.uuidName = uuidName;
        this.uploadDate = uplaodDate;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuidName() {
        return uuidName;
    }

    public void setUuidName(String uuidName) {
        this.uuidName = uuidName;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
