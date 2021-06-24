package com.example.drp.helpers;

import java.io.Serializable;

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class UserModel implements Serializable {

    private int linkedCardCount = 0;
    private int transactionCount = 0;
    private String[] linkedCards;

    private String userName;
    private String userPhone;
    private String userAccPassword;
    private String userEmail;
    private String userDOB;
    private String userGender;
    private String userRationCardID;
    private String userLocationState;
    private String userLocationDistrict;
    private String userLocationMunicipality;
    private String userLocationWardNo;
    private String userDealerName;
    private String userDealerID;
    private String userLinkedCardCount;
    private String userTransactionCount;

    public UserModel() {

    }

    public UserModel(String userName, String userPhone, String userAccPassword, String userEmail,
                     String userDOB, String userGender, String userRationCardID, String userLocationState,
                     String userLocationDistrict, String userLocationMunicipality, String userLocationWardNo,
                     String userDealerName, String userDealerID, String userLinkedCardCount, String userTransactionCount) {

        this.userName = userName;
        this.userPhone = userPhone;
        this.userAccPassword = userAccPassword;
        this.userEmail = userEmail;
        this.userDOB = userDOB;
        this.userGender = userGender;
        this.userRationCardID = userRationCardID;
        this.userLocationState = userLocationState;
        this.userLocationDistrict = userLocationDistrict;
        this.userLocationMunicipality = userLocationMunicipality;
        this.userLocationWardNo = userLocationWardNo;
        this.userDealerName = userDealerName;
        this.userDealerID = userDealerID;
        this.userLinkedCardCount = userLinkedCardCount;
        this.userTransactionCount = userTransactionCount;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserDOB() {
        return userDOB;
    }

    public void setUserDOB(String userDOB) {
        this.userDOB = userDOB;
    }

    public String getUserRationCardID() {
        return userRationCardID;
    }

    public String getUserLinkedCardCount() {
        return userLinkedCardCount;
    }

    public void setUserLinkedCardCount(String userLinkedCardCount) {
        this.userLinkedCardCount = userLinkedCardCount;
    }

    public void setUserRationCardID(String userRationCardID) {
        this.userRationCardID = userRationCardID;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserDealerName() {
        return userDealerName;
    }

    public void setUserDealerName(String userDealerName) {
        this.userDealerName = userDealerName;
    }

    public String getUserDealerID() {
        return userDealerID;
    }

    public void setUserDealerID(String userDealerID) {
        this.userDealerID = userDealerID;
    }

    public String getUserAccPassword() {
        return userAccPassword;
    }

    public void setUserAccPassword(String userAccPassword) {
        this.userAccPassword = userAccPassword;
    }

    public String getUserLocationState() {
        return userLocationState;
    }

    public void setUserLocationState(String userLocationState) {
        this.userLocationState = userLocationState;
    }

    public String getUserLocationDistrict() {
        return userLocationDistrict;
    }

    public void setUserLocationDistrict(String userLocationDistrict) {
        this.userLocationDistrict = userLocationDistrict;
    }

    public String getUserLocationMunicipality() {
        return userLocationMunicipality;
    }

    public void setUserLocationMunicipality(String userLocationMunicipality) {
        this.userLocationMunicipality = userLocationMunicipality;
    }

    public String getUserLocationWardNo() {
        return userLocationWardNo;
    }

    public void setUserLocationWardNo(String userLocationWardNo) {
        this.userLocationWardNo = userLocationWardNo;
    }

    public String getUserTransactionCount() {
        return userTransactionCount;
    }

    public void setUserTransactionCount(String userTransactionCount) {
        this.userTransactionCount = userTransactionCount;
    }

    public String[] getLinkedCards() {
        return linkedCards;
    }

    public void linkCard(String cardNo, int cardIndex, int cardCount) {
        if (linkedCardCount == 0){
            linkedCards = new String[cardCount];
        }
        linkedCards[linkedCardCount] = cardNo;
        linkedCardCount = linkedCardCount + 1;
        userLinkedCardCount = String.valueOf(linkedCardCount);
    }

}
