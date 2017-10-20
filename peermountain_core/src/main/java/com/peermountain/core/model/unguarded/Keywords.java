package com.peermountain.core.model.unguarded;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Galeen on 10/20/2017.
 */

public class Keywords {
    private ArrayList<Keyword> keywords;

    public Keywords() {
    }

    public Keywords(Set<Keyword> keywords) {
        this.keywords = new ArrayList<>(keywords);
    }

    public ArrayList<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<Keyword> keywords) {
        this.keywords = keywords;
    }
}
