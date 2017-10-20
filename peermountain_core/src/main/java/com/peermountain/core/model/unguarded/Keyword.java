package com.peermountain.core.model.unguarded;

/**
 * Created by Galeen on 10/20/2017.
 */

public class Keyword {
    private String value;
    private boolean selected;

    public Keyword(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
