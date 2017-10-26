package com.peermountain.core.model.guarded;

import java.util.ArrayList;

/**
 * Created by Galeen on 10/11/2017.
 */

public class Profile extends Contact{
    private ArrayList<DocumentID> documents = new ArrayList<>();
    public ArrayList<DocumentID> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<DocumentID> documents) {
        this.documents = documents;
    }
}
