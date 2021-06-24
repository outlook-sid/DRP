package com.example.drp.helpers;

public class TransactionCheckerModel {

    /**
     * Firebase Database Node name: "Transaction_Checker"
     */

    private String transactionBookedDate;
    private String transactionBookedTime;
    private String transactionID;
    private String transactionDate;

    public TransactionCheckerModel(String transactionBookedDate, String transactionBookedTime, String transactionID, String transactionDate) {
        this.transactionBookedDate = transactionBookedDate;
        this.transactionBookedTime = transactionBookedTime;
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
    }

    public String getTransactionBookedDate() {
        return transactionBookedDate;
    }

    public void setTransactionBookedDate(String transactionBookedDate) {
        this.transactionBookedDate = transactionBookedDate;
    }

    public String getTransactionBookedTime() {
        return transactionBookedTime;
    }

    public void setTransactionBookedTime(String transactionBookedTime) {
        this.transactionBookedTime = transactionBookedTime;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
