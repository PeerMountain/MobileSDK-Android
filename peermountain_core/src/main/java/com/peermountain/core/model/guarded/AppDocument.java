package com.peermountain.core.model.guarded;

import java.util.ArrayList;

/**
 * Created by Galeen on 10/23/17.
 */

public class AppDocument {
    private ArrayList<Document> documents = new ArrayList<>();
    private ArrayList<FileDocument> fileDocuments = new ArrayList<>();
    public  boolean isEmpty = false;

    public AppDocument(boolean isEmpty) {
        this.isEmpty = isEmpty;
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
}
