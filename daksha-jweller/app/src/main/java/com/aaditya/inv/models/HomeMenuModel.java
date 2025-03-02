package com.aaditya.inv.models;

import android.graphics.drawable.Drawable;

public class HomeMenuModel {

    private String heading;
    private String description;

    private Drawable icon;

    public HomeMenuModel(String heading, String description, Drawable icon) {
        this.heading = heading;
        this.description = description;
        this.icon = icon;
    }

    public HomeMenuModel(String heading, String description) {
        this.heading = heading;
        this.description = description;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
