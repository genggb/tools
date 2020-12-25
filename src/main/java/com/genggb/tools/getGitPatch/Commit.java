package com.genggb.tools.getGitPatch;

import java.util.List;

public class Commit {

    private String id;
    private String msg;
    private List<String> updateFileList;

    private List<String> newFileList;

    private List<String> delFileList;
    private String author;
    //yyyy-MM-dd HH:mm:ss;
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getUpdateFileList() {
        return updateFileList;
    }

    public void setUpdateFileList(List<String> updateFileList) {
        this.updateFileList = updateFileList;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getNewFileList() {
        return newFileList;
    }

    public void setNewFileList(List<String> newFileList) {
        this.newFileList = newFileList;
    }

    public List<String> getDelFileList() {
        return delFileList;
    }

    public void setDelFileList(List<String> delFileList) {
        this.delFileList = delFileList;
    }
}
