package com.example.drp.helpers;

public class TransactionInfoModel {

    /**
     * Firebase Database Node name: "Users.<int>userTransactionCount</int>userTransactions"
     */

    private String transactionDate;
    private String transactionPaymentMethod;
    private String transactionBookedDate;
    private String transactionBookedTime;
    private String transactionRiceSubtotal;
    private String transactionWheatSubtotal;
    private String transactionSugarSubtotal;
    private String transactionKeroseneSubtotal;
    private String transactionSubtotal;

    public TransactionInfoModel(){

    }

    public TransactionInfoModel(String transactionDate, String transactionPaymentMethod,
                                String transactionBookedDate, String transactionBookedTime,
                                String transactionRiceSubtotal, String transactionWheatSubtotal,
                                String transactionSugarSubtotal, String transactionKeroseneSubtotal,
                                String transactionSubtotal) {
        this.transactionDate = transactionDate;
        this.transactionPaymentMethod = transactionPaymentMethod;
        this.transactionBookedDate = transactionBookedDate;
        this.transactionBookedTime = transactionBookedTime;
        this.transactionRiceSubtotal = transactionRiceSubtotal;
        this.transactionWheatSubtotal = transactionWheatSubtotal;
        this.transactionSugarSubtotal = transactionSugarSubtotal;
        this.transactionKeroseneSubtotal = transactionKeroseneSubtotal;
        this.transactionSubtotal = transactionSubtotal;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionPaymentMethod() {
        return transactionPaymentMethod;
    }

    public void setTransactionPaymentMethod(String transactionPaymentMethod) {
        this.transactionPaymentMethod = transactionPaymentMethod;
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

    public String getTransactionRiceSubtotal() {
        return transactionRiceSubtotal;
    }

    public void setTransactionRiceSubtotal(String transactionRiceSubtotal) {
        this.transactionRiceSubtotal = transactionRiceSubtotal;
    }

    public String getTransactionWheatSubtotal() {
        return transactionWheatSubtotal;
    }

    public void setTransactionWheatSubtotal(String transactionWheatSubtotal) {
        this.transactionWheatSubtotal = transactionWheatSubtotal;
    }

    public String getTransactionSugarSubtotal() {
        return transactionSugarSubtotal;
    }

    public void setTransactionSugarSubtotal(String transactionSugarSubtotal) {
        this.transactionSugarSubtotal = transactionSugarSubtotal;
    }

    public String getTransactionKeroseneSubtotal() {
        return transactionKeroseneSubtotal;
    }

    public void setTransactionKeroseneSubtotal(String transactionKeroseneSubtotal) {
        this.transactionKeroseneSubtotal = transactionKeroseneSubtotal;
    }

    public String getTransactionSubtotal() {
        return transactionSubtotal;
    }

    public void setTransactionSubtotal(String transactionSubtotal) {
        this.transactionSubtotal = transactionSubtotal;
    }
}
