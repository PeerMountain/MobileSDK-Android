package com.peermountain.core.model.guarded;

import java.util.ArrayList;

/**
 * Created by Galeen on 10/11/2017.
 */

public class Profile extends Contact{
    private ArrayList<Document> documents = new ArrayList<>();
    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }
}
