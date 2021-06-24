package com.example.drp.helpers;

public class TransactionModel {

    /**
     * Firebase Database Node name: "Transactions"
     */

    private String accountName;
    private String accountID;
    private String dealerID;
    private String accountCardCount;
    private String transactionID;
    private String transactionDate;
    private String transactionPaymentMethod;
    private String transactionBookedDate;
    private String transactionBookedTime;
    private String transactionRicePrice;
    private String transactionRiceAmt;
    private String transactionWheatPrice;
    private String transactionWheatAmt;
    private String transactionSugarPrice;
    private String transactionSugarAmt;
    private String transactionKerosenePrice;
    private String transactionKeroseneAmt;
    private String transactionSubtotal;

    public TransactionModel(){

    }

    public TransactionModel(String accountName, String accountID, String dealerID,
                            String accountCardCount, String transactionID, String transactionDate,
                            String transactionPaymentMethod, String transactionBookedDate,
                            String transactionBookedTime, String transactionRicePrice,
                            String transactionRiceAmt, String transactionWheatPrice,
                            String transactionWheatAmt, String transactionSugarPrice,
                            String transactionSugarAmt, String transactionKerosenePrice,
                            String transactionKeroseneAmt, String transactionSubtotal) {
        this.accountName = accountName;
        this.accountID = accountID;
        this.dealerID = dealerID;
        this.accountCardCount = accountCardCount;
        this.transactionID = transactionID;
        this.transactionDate = transactionDate;
        this.transactionPaymentMethod = transactionPaymentMethod;
        this.transactionBookedDate = transactionBookedDate;
        this.transactionBookedTime = transactionBookedTime;
        this.transactionRicePrice = transactionRicePrice;
        this.transactionRiceAmt = transactionRiceAmt;
        this.transactionWheatPrice = transactionWheatPrice;
        this.transactionWheatAmt = transactionWheatAmt;
        this.transactionSugarPrice = transactionSugarPrice;
        this.transactionSugarAmt = transactionSugarAmt;
        this.transactionKerosenePrice = transactionKerosenePrice;
        this.transactionKeroseneAmt = transactionKeroseneAmt;
        this.transactionSubtotal = transactionSubtotal;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getDealerID() {
        return dealerID;
    }

    public void setDealerID(String dealerID) {
        this.dealerID = dealerID;
    }

    public String getAccountCardCount() {
        return accountCardCount;
    }

    public void setAccountCardCount(String accountCardCount) {
        this.accountCardCount = accountCardCount;
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

    public String getTransactionRicePrice() {
        return transactionRicePrice;
    }

    public void setTransactionRicePrice(String transactionRicePrice) {
        this.transactionRicePrice = transactionRicePrice;
    }

    public String getTransactionRiceAmt() {
        return transactionRiceAmt;
    }

    public void setTransactionRiceAmt(String transactionRiceAmt) {
        this.transactionRiceAmt = transactionRiceAmt;
    }

    public String getTransactionWheatPrice() {
        return transactionWheatPrice;
    }

    public void setTransactionWheatPrice(String transactionWheatPrice) {
        this.transactionWheatPrice = transactionWheatPrice;
    }

    public String getTransactionWheatAmt() {
        return transactionWheatAmt;
    }

    public void setTransactionWheatAmt(String transactionWheatAmt) {
        this.transactionWheatAmt = transactionWheatAmt;
    }

    public String getTransactionSugarPrice() {
        return transactionSugarPrice;
    }

    public void setTransactionSugarPrice(String transactionSugarPrice) {
        this.transactionSugarPrice = transactionSugarPrice;
    }

    public String getTransactionSugarAmt() {
        return transactionSugarAmt;
    }

    public void setTransactionSugarAmt(String transactionSugarAmt) {
        this.transactionSugarAmt = transactionSugarAmt;
    }

    public String getTransactionKerosenePrice() {
        return transactionKerosenePrice;
    }

    public void setTransactionKerosenePrice(String transactionKerosenePrice) {
        this.transactionKerosenePrice = transactionKerosenePrice;
    }

    public String getTransactionKeroseneAmt() {
        return transactionKeroseneAmt;
    }

    public void setTransactionKeroseneAmt(String transactionKeroseneAmt) {
        this.transactionKeroseneAmt = transactionKeroseneAmt;
    }

    public String getTransactionSubtotal() {
        return transactionSubtotal;
    }

    public void setTransactionSubtotal(String transactionSubtotal) {
        this.transactionSubtotal = transactionSubtotal;
    }


}
