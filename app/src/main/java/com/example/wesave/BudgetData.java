package com.example.wesave;

public class BudgetData {

    String budgetItem, budgetDate, budgetID,budgetItemNdays, budgetItemNweeks, budgetItemNmonths;
    int budgetAmount, budgetMonth, budgetWeek;
    String budgetNotes;

    public BudgetData() {
    }

    public BudgetData(String budgetItem, String budgetDate, String budgetID, String budgetNotes, String budgetItemNdays, String budgetItemNweeks, String budgetItemNmonths, int budgetAmount, int budgetMonth, int budgetWeek) {
        this.budgetItem = budgetItem;
        this.budgetDate = budgetDate;
        this.budgetID = budgetID;
        this.budgetNotes = budgetNotes;
        this.budgetItemNdays = budgetItemNdays;
        this.budgetItemNweeks = budgetItemNweeks;
        this.budgetItemNmonths = budgetItemNmonths;
        this.budgetAmount = budgetAmount;
        this.budgetMonth = budgetMonth;
        this.budgetWeek = budgetWeek;
    }

    public String getBudgetItem() {
        return budgetItem;
    }

    public void setBudgetItem(String budgetItem) {
        this.budgetItem = budgetItem;
    }

    public String getBudgetDate() {
        return budgetDate;
    }

    public void setBudgetDate(String budgetDate) {
        this.budgetDate = budgetDate;
    }

    public String getBudgetID() {
        return budgetID;
    }

    public void setBudgetID(String budgetID) {
        this.budgetID = budgetID;
    }

    public String getBudgetNotes() {
        return budgetNotes;
    }

    public void setBudgetNotes(String budgetNotes) {
        this.budgetNotes = budgetNotes;
    }

    public String getBudgetItemNdays() {
        return budgetItemNdays;
    }

    public void setBudgetItemNdays(String budgetItemNdays) {
        this.budgetItemNdays = budgetItemNdays;
    }

    public String getBudgetItemNweeks() {
        return budgetItemNweeks;
    }

    public void setBudgetItemNweeks(String budgetItemNweeks) {
        this.budgetItemNweeks = budgetItemNweeks;
    }

    public String getBudgetItemNmonths() {
        return budgetItemNmonths;
    }

    public void setBudgetItemNmonths(String budgetItemNmonths) {
        this.budgetItemNmonths = budgetItemNmonths;
    }

    public int getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(int budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public int getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(int budgetMonth) {
        this.budgetMonth = budgetMonth;
    }

    public int getBudgetWeek() {
        return budgetWeek;
    }

    public void setBudgetWeek(int budgetWeek) {
        this.budgetWeek = budgetWeek;
    }
}
