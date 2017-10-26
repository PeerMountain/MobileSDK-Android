package com.peermountain.core.model.guarded;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Galeen on 10/23/17.
 */

public class AppDocument {
    private String id;
    private ArrayList<DocumentID> documents = new ArrayList<>();
    private ArrayList<FileDocument> fileDocuments = new ArrayList<>();
    private int res;
    private String title;
    private   boolean isEmpty = false;

    public AppDocument(boolean isEmpty) {
        this();
        this.isEmpty = isEmpty;
    }

    public AppDocument(int res, String title) {
        this();
        this.res = res;
        this.title = title;
    }
    public AppDocument(String title) {
        this();
        this.title = title;
    }

    public AppDocument() {
        id = UUID.randomUUID().toString();
    }

    public boolean isIdentityDocument(){
        return documents!=null && documents.size()>0 && documents.get(0)!=null;
    }
    public ArrayList<DocumentID> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<DocumentID> documents) {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppDocument)) return false;

        AppDocument that = (AppDocument) o;

        if (res != that.res) return false;
        if (isEmpty != that.isEmpty) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (documents != null ? !documents.equals(that.documents) : that.documents != null) return false;
        if (fileDocuments != null ? !fileDocuments.equals(that.fileDocuments) : that.fileDocuments != null)
            return false;
        return title != null ? title.equals(that.title) : that.title == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (documents != null ? documents.hashCode() : 0);
        result = 31 * result + (fileDocuments != null ? fileDocuments.hashCode() : 0);
        result = 31 * result + res;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (isEmpty ? 1 : 0);
        return result;
    }
}
