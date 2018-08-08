package com.peermountain.core.model.guarded;

import com.peermountain.common.model.DocumentID;

import java.util.ArrayList;

/**
 * Created by Galeen on 10/11/2017.
 */

public class Profile extends Contact{
    private ArrayList<DocumentID> documents = new ArrayList<>();
    private ArrayList<String> liveSelfie = new ArrayList<>();
    public ArrayList<DocumentID> getDocuments() {
        return documents;
    }

    public void setDocuments(ArrayList<DocumentID> documents) {
        this.documents = documents;
    }

    public ArrayList<String> getLiveSelfie() {
        return liveSelfie;
    }

    public void setLiveSelfie(ArrayList<String> liveSelfie) {
        this.liveSelfie = liveSelfie;
    }

    public boolean hasLiveSelfie(){
        return liveSelfie!=null && liveSelfie.size()>0;
    }
}
