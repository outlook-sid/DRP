package com.example.drp.helpers;

public class QRCodeSessionModel {

    private String encodedQRCode;
    private String transactionDate;
    private String selectedDate;
    private String selectedTimeSlot;
    private String paymentMethod;
    private String paymentSubtotal;
    private String ricePriceAmount;
    private String wheatPriceAmount;
    private String sugarPriceAmount;
    private String kerosenePriceAmount;

    public QRCodeSessionModel( ) {
    }

    public QRCodeSessionModel(String encodedQRCode, String transactionDate,
                              String selectedDate, String selectedTimeSlot,
                              String paymentMethod, String paymentSubtotal,
                              String ricePriceAmount, String wheatPriceAmount,
                              String sugarPriceAmount, String kerosenePriceAmount) {
        this.encodedQRCode = encodedQRCode;
        this.transactionDate = transactionDate;
        this.selectedDate = selectedDate;
        this.selectedTimeSlot = selectedTimeSlot;
        this.paymentMethod = paymentMethod;
        this.paymentSubtotal = paymentSubtotal;
        this.ricePriceAmount = ricePriceAmount;
        this.wheatPriceAmount = wheatPriceAmount;
        this.sugarPriceAmount = sugarPriceAmount;
        this.kerosenePriceAmount = kerosenePriceAmount;
    }

    public String getEncodedQRCode() {
        return encodedQRCode;
    }

    public void setEncodedQRCode(String encodedQRCode) {
        this.encodedQRCode = encodedQRCode;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedTimeSlot() {
        return selectedTimeSlot;
    }

    public void setSelectedTimeSlot(String selectedTimeSlot) {
        this.selectedTimeSlot = selectedTimeSlot;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentSubtotal() {
        return paymentSubtotal;
    }

    public void setPaymentSubtotal(String paymentSubtotal) {
        this.paymentSubtotal = paymentSubtotal;
    }

    public String getRicePriceAmount() {
        return ricePriceAmount;
    }

    public void setRicePriceAmount(String ricePriceAmount) {
        this.ricePriceAmount = ricePriceAmount;
    }

    public String getWheatPriceAmount() {
        return wheatPriceAmount;
    }

    public void setWheatPriceAmount(String wheatPriceAmount) {
        this.wheatPriceAmount = wheatPriceAmount;
    }

    public String getSugarPriceAmount() {
        return sugarPriceAmount;
    }

    public void setSugarPriceAmount(String sugarPriceAmount) {
        this.sugarPriceAmount = sugarPriceAmount;
    }

    public String getKerosenePriceAmount() {
        return kerosenePriceAmount;
    }

    public void setKerosenePriceAmount(String kerosenePriceAmount) {
        this.kerosenePriceAmount = kerosenePriceAmount;
    }
}
