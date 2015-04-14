package com.meyerlaurent.cactv;

import android.graphics.Bitmap;
import android.net.Uri;
import android.text.SpannableStringBuilder;

/**
 * Created by laurentmeyer on 11/04/15.
 */
public class People {
    private SpannableStringBuilder name;
    private SpannableStringBuilder data;
    private Bitmap picture;

    public People(SpannableStringBuilder name, SpannableStringBuilder data, Bitmap picture) {
        this.name = name;
        this.data = data;
        this.picture = picture;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }


    public SpannableStringBuilder getName() {
        return name;
    }

    public void setName(SpannableStringBuilder name) {
        this.name = name;
    }

    public SpannableStringBuilder getData() {
        return data;
    }

    public void setData(SpannableStringBuilder data) {
        this.data = data;
    }

    protected People(SpannableStringBuilder name, SpannableStringBuilder data) {
        this(name,data,null);
    }
}