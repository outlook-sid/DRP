package com.example.drp.helpers;

import java.io.Serializable;

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class RationCardModel implements Serializable {

    private String cardNo;
    private String userName;
    private String userPhone;
    private String dealerID;
    private String linkedAccount;

    public RationCardModel() {
    }

    public RationCardModel(String cardNo, String userName, String userPhone, String dealerID, String linkedAccount) {
        this.cardNo = cardNo;
        this.userName = userName;
        this.userPhone = userPhone;
        this.dealerID = dealerID;
        this.linkedAccount = linkedAccount;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getDealerID() {
        return dealerID;
    }

    public void setDealerID(String dealerID) {
        this.dealerID = dealerID;
    }

    public String getLinkedAccount() {
        return linkedAccount;
    }

    public void setLinkedAccount(String linkedAccount) {
        this.linkedAccount = linkedAccount;
    }
}
