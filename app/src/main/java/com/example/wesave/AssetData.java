package com.example.wesave;

public class AssetData {

    String assetItem, assetDate, assetID, assetItemNdays, assetItemNyears;
    int assetAmount, assetYears;
    String assetNotes;

    public AssetData() {
    }

    public AssetData(String assetItem, String assetDate, String assetID, String assetItemNdays, String assetItemNyears, int assetAmount, int assetYears, String assetNotes) {
        this.assetItem = assetItem;
        this.assetDate = assetDate;
        this.assetID = assetID;
        this.assetItemNdays = assetItemNdays;
        this.assetItemNyears = assetItemNyears;
        this.assetAmount = assetAmount;
        this.assetYears = assetYears;
        this.assetNotes = assetNotes;
    }

    public String getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(String assetItem) {
        this.assetItem = assetItem;
    }

    public String getAssetDate() {
        return assetDate;
    }

    public void setAssetDate(String assetDate) {
        this.assetDate = assetDate;
    }

    public String getAssetID() {
        return assetID;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public String getAssetItemNdays() {
        return assetItemNdays;
    }

    public void setAssetItemNdays(String assetItemNdays) {
        this.assetItemNdays = assetItemNdays;
    }

    public String getAssetItemNyears() {
        return assetItemNyears;
    }

    public void setAssetItemNyears(String assetItemNyears) {
        this.assetItemNyears = assetItemNyears;
    }

    public int getAssetAmount() {
        return assetAmount;
    }

    public void setAssetAmount(int assetAmount) {
        this.assetAmount = assetAmount;
    }

    public int getAssetYears() {
        return assetYears;
    }

    public void setAssetYears(int assetYears) {
        this.assetYears = assetYears;
    }

    public String getAssetNotes() {
        return assetNotes;
    }

    public void setAssetNotes(String assetNotes) {
        this.assetNotes = assetNotes;
    }
}
