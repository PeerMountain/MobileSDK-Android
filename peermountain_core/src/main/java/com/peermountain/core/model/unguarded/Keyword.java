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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Keyword)) return false;

        Keyword keyword = (Keyword) o;

        return value.equals(keyword.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
