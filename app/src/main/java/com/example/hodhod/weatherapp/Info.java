package com.example.hodhod.weatherapp;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.widget.ImageView;

public class Info {

    private String degree;
    private String condition;
    private String localTime;

    public Info(String degree, String condition, String localTime) {
        this.degree = degree;
        this.condition = condition;
        this.localTime = localTime;
    }

    public String getDegree() {
        return degree;
    }

    public String getCondition() {
        return condition;
    }

    public String getLocalTime() {
        return localTime;
    }

}
