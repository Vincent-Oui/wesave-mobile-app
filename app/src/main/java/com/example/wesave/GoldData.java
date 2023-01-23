package com.example.wesave;

public class GoldData {

    private String date, base;

    public GoldData() {
    }

    public GoldData(String date, String base) {
        this.date = date;
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
