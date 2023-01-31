package com.rohya.weathermain;

public class weatherRvModel
{
    private String time;
    private String tempreture;
    private String icon;

    private int isday;

    public weatherRvModel(String time, String tempreture, String icon, int isday) {
        this.time = time;
        this.tempreture = tempreture;
        this.icon = icon;
        this.isday = isday;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTempreture() {
        return tempreture;
    }

    public void setTempreture(String tempreture) {
        this.tempreture = tempreture;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIsday() {
        return isday;
    }

    public void setIsday(int isday) {
        this.isday = isday;
    }
}
