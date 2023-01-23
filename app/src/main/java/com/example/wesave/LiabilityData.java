package com.example.wesave;

public class LiabilityData {

    String liabilityItem, liabilityDate, liabilityID, liabilityItemNdays, liabilityItemNyears;
    int liabilityAmount, liabilityYears;
    String liabilityNotes;

    public LiabilityData() {
    }

    public LiabilityData(String liabilityItem, String liabilityDate, String liabilityID, String liabilityItemNdays, String liabilityItemNyears, int liabilityAmount, int liabilityYears, String liabilityNotes) {
        this.liabilityItem = liabilityItem;
        this.liabilityDate = liabilityDate;
        this.liabilityID = liabilityID;
        this.liabilityItemNdays = liabilityItemNdays;
        this.liabilityItemNyears = liabilityItemNyears;
        this.liabilityAmount = liabilityAmount;
        this.liabilityYears = liabilityYears;
        this.liabilityNotes = liabilityNotes;
    }

    public String getLiabilityItem() {
        return liabilityItem;
    }

    public void setLiabilityItem(String liabilityItem) {
        this.liabilityItem = liabilityItem;
    }

    public String getLiabilityDate() {
        return liabilityDate;
    }

    public void setLiabilityDate(String liabilityDate) {
        this.liabilityDate = liabilityDate;
    }

    public String getLiabilityID() {
        return liabilityID;
    }

    public void setLiabilityID(String liabilityID) {
        this.liabilityID = liabilityID;
    }

    public String getLiabilityItemNdays() {
        return liabilityItemNdays;
    }

    public void setLiabilityItemNdays(String liabilityItemNdays) {
        this.liabilityItemNdays = liabilityItemNdays;
    }

    public String getLiabilityItemNyears() {
        return liabilityItemNyears;
    }

    public void setLiabilityItemNyears(String liabilityItemNyears) {
        this.liabilityItemNyears = liabilityItemNyears;
    }

    public int getLiabilityAmount() {
        return liabilityAmount;
    }

    public void setLiabilityAmount(int liabilityAmount) {
        this.liabilityAmount = liabilityAmount;
    }

    public int getLiabilityYears() {
        return liabilityYears;
    }

    public void setLiabilityYears(int liabilityYears) {
        this.liabilityYears = liabilityYears;
    }

    public String getLiabilityNotes() {
        return liabilityNotes;
    }

    public void setLiabilityNotes(String liabilityNotes) {
        this.liabilityNotes = liabilityNotes;
    }
}
