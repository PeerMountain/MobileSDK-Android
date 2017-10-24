package com.peermountain.core.model.guarded;

import java.util.ArrayList;

/**
 * Created by Galeen on 10/23/17.
 */

public class AppDocument {
    private ArrayList<Document> documents = new ArrayList<>();
    private ArrayList<FileDocument> fileDocuments = new ArrayList<>();
    private int res;
    private String title;
    private   boolean isEmpty = false;

    public AppDocument(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public AppDocument(int res, String title) {
        this.res = res;
        this.title = title;
    }

    public AppDocument() {
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }

    public ArrayList<FileDocument> getFileDocuments() {
        return fileDocuments;
    }

    public void setFileDocuments(ArrayList<FileDocument> fileDocuments) {
        this.fileDocuments = fileDocuments;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
