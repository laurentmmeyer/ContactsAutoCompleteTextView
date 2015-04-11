package com.meyerlaurent.cactv;

import android.text.SpannableStringBuilder;

/**
 * Created by laurentmeyer on 11/04/15.
 */
public class People {
    private SpannableStringBuilder name;
    private SpannableStringBuilder number;

    public SpannableStringBuilder getName() {
        return name;
    }

    public void setName(SpannableStringBuilder name) {
        this.name = name;
    }

    public SpannableStringBuilder getNumber() {
        return number;
    }

    public void setNumber(SpannableStringBuilder number) {
        this.number = number;
    }

    protected People(SpannableStringBuilder name, SpannableStringBuilder number) {
        this.name = name;
        this.number = number;
    }
}