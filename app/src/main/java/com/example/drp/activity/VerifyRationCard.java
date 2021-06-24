package com.example.drp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.chaos.view.PinView;
import com.example.drp.R;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.RationCardModel;
import com.example.drp.helpers.UserModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class VerifyRationCard extends AppCompatActivity {

    private UserSessionManager userSessionManager;
    private PinView otpPin;
    private ProgressBar progressBar;
    private String newCardID, cardCount;
    private String phone, userName, state, district, municipality, wardNo, dealerId, cardholderName;
    private String  phoneNo, accountPassword, emailAddress, gender, dob, cardNo, linkedPhone, dealerName;
    private DatabaseReference cardReferenceNode, usersReferenceNode;
    private String sUserEnteredOTP;
    private FirebaseAuth firebaseAuth;
    private int no_ofCardsAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_ration_card);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_DeepBlue));

        TextView cardID = (TextView) findViewById(R.id.verify_ration_instruction_txt2);
        otpPin = (PinView) findViewById(R.id.verify_ration_card_pin);
        MaterialButton verifyBtn = (MaterialButton) findViewById(R.id.verify_ration_card_button);
        progressBar = (ProgressBar) findViewById(R.id.verify_ration_card_progress_bar);
        ImageView exit = findViewById(R.id.cross_btn);

        cardReferenceNode = FirebaseDatabase.getInstance().getReference("Ration_Cards");
        usersReferenceNode = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        newCardID = getIntent().getStringExtra("new_card");
        cardCount = getIntent().getStringExtra("card_count");
        linkedPhone = getIntent().getStringExtra("linked_phone_number");
        cardholderName = getIntent().getStringExtra("new_cardholder_name");

        cardID.setText(newCardID);
        no_ofCardsAdded = Integer.parseInt(cardCount);

        Log.v("_____NOTE_____", "linked phone:  "+linkedPhone);
        sendVerificationCodeToUser(linkedPhone);

        verifyBtn.setOnClickListener(v -> {
            String userCode = Objects.requireNonNull(otpPin.getText()).toString();

            if (userCode.isEmpty() || userCode.length() < 6) {
                Toast.makeText(VerifyRationCard.this, "Wrong OTP...", Toast.LENGTH_SHORT).show();
                otpPin.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(userCode);

        });

        exit.setOnClickListener(v -> {
            Dialog dialog = new Dialog(VerifyRationCard.this);
            dialog.setContentView(R.layout.layout_dialog_exit);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(true);
            FloatingActionButton fabExit = (FloatingActionButton)dialog.findViewById(R.id.fab_exit_from_app);
            fabExit.setOnClickListener(vi -> {
                dialog.dismiss();
                getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                VerifyRationCard.this.finish();
                //LoginPage.this.finish();
                //finishAffinity();
                //System.exit(0);
            });

            dialog.show();
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(VerifyRationCard.this, "navigating back is restricted", Toast.LENGTH_LONG).show();
    }

    void sendVerificationCodeToUser(String sUserEnteredPhoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(sUserEnteredPhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(VerifyRationCard.this)   // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            sUserEnteredOTP = s;
            Toast.makeText(getApplicationContext(), "code sent", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                otpPin.setText(code);
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
            Toast.makeText(getApplicationContext(), "verification complete", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            Log.v("_____NOTE_____", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(VerifyRationCard.this, "not verified " + sUserEnteredOTP, Toast.LENGTH_LONG).show();
        }
    };


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sUserEnteredOTP, code);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(VerifyRationCard.this, task -> {
            if (task.isSuccessful()) {
                //Perform required action here to either let the user sign In or do something required

                if (getIntent().getBooleanExtra("new_User_SignUp", false)) {
                    //  DURING CREATING NEW ACCOUNT

                    userName = getIntent().getStringExtra("Name");
                    phoneNo = getIntent().getStringExtra("PhoneNumber");
                    accountPassword = getIntent().getStringExtra("AccPassword");
                    emailAddress = getIntent().getStringExtra("Email");
                    gender = getIntent().getStringExtra("Gender");
                    dob = getIntent().getStringExtra("DOB");
                    cardNo = getIntent().getStringExtra("RationCard");
                    state = getIntent().getStringExtra("LocationState");
                    district = getIntent().getStringExtra("LocationDistrict");
                    municipality = getIntent().getStringExtra("LocationMunicipality");
                    wardNo = getIntent().getStringExtra("LocationWardNo");
                    dealerName = getIntent().getStringExtra("DealerName");
                    dealerId = getIntent().getStringExtra("DealerID");



                    UserModel userModel = new UserModel(userName, phoneNo, accountPassword, emailAddress,
                            dob, gender, cardNo, state, district, municipality, wardNo, dealerName, dealerId, "0", "0");
                    usersReferenceNode.child(userModel.getUserPhone()).setValue(userModel).addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Toast.makeText(VerifyRationCard.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            super.onBackPressed();
                        }
                    });


                    RationCardModel rationCardModel = new RationCardModel(cardNo, cardholderName, linkedPhone, dealerId, phoneNo);
                    cardReferenceNode.child(rationCardModel.getCardNo()).setValue(rationCardModel).addOnCompleteListener(task12 -> {
                        if (!task12.isSuccessful()) {
                            Toast.makeText(VerifyRationCard.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });



                    Intent intent = new Intent(VerifyRationCard.this, LoginPage.class);

                    startActivity(intent);
                    finish();
                }
                else {
                    // DURING LINKING NEW RATION CARDS TO AN ACCOUNT
                    //String[] linkedCardArray = getIntent().getStringArrayExtra("card_array");
                    userSessionManager = new UserSessionManager(VerifyRationCard.this);
                    HashMap<String, String> userData = userSessionManager.getUserDetailsFromSession();
                    //List<String> alreadyLinkedCardList = userSessionManager.getUserLinkedCardsList();
                    phone = userData.get(UserSessionManager.KEY_PHONE);
                    userName = userData.get(UserSessionManager.KEY_NAME);
                    state = userData.get(UserSessionManager.KEY_LOCATION1);
                    district = userData.get(UserSessionManager.KEY_LOCATION2);
                    municipality = userData.get(UserSessionManager.KEY_LOCATION3);
                    wardNo = userData.get(UserSessionManager.KEY_LOCATION4);
                    dealerId = userData.get(UserSessionManager.KEY_DEALER_ID);


                    Toast.makeText(VerifyRationCard.this, "Ration Card has been added successfully!", Toast.LENGTH_SHORT).show();
                    RationCardModel rationCardModel = new RationCardModel(newCardID, cardholderName, linkedPhone, dealerId, phone);
                    cardReferenceNode.child(rationCardModel.getCardNo()).setValue(rationCardModel).addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Toast.makeText(VerifyRationCard.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                    usersReferenceNode.child(phone).child("linkedCards").child(cardCount).setValue(newCardID);
                    usersReferenceNode.child(phone).child("userLinkedCardCount").setValue(String.valueOf(no_ofCardsAdded + 1));

                    //.........LOGOUT FROM SHARED PREFERENCE & RE-LOGIN TO UPDATE SharedPreferences........
                    userSessionManager.logoutUserFromSession();
                    Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userPhone").equalTo(phone);
                    checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot ref = snapshot.child(phone);

                            String _phone = ref.child("userPhone").getValue(String.class);
                            String _password = ref.child("userAccPassword").getValue(String.class);
                            String _email = ref.child("userEmail").getValue(String.class);
                            String _name = ref.child("userName").getValue(String.class);
                            String _cardNo = ref.child("userRationCardID").getValue(String.class);
                            String _dob = ref.child("userDOB").getValue(String.class);
                            String _gender = ref.child("userGender").getValue(String.class);
                            String _location1 = ref.child("userLocationState").getValue(String.class);
                            String _location2 = ref.child("userLocationDistrict").getValue(String.class);
                            String _location3 = ref.child("userLocationMunicipality").getValue(String.class);
                            String _location4 = ref.child("userLocationWardNo").getValue(String.class);
                            String _dealerName = ref.child("userDealerName").getValue(String.class);
                            String _dealerID = ref.child("userDealerID").getValue(String.class);
                            String _linkedCardCount = ref.child("userLinkedCardCount").getValue(String.class);
                            String _transactionCount = ref.child("userTransactionCount").getValue(String.class);

                            Set<String> linkedCardSet = new HashSet<>();
                            try {
                                assert _linkedCardCount != null;
                                for (int i = 0; i < Integer.parseInt(_linkedCardCount); i++) {
                                    String val = ref.child("linkedCards").child(String.valueOf(i)).getValue(String.class);
                                    linkedCardSet.add(val);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            userSessionManager = new UserSessionManager(VerifyRationCard.this);
                            userSessionManager.createDashboardSession(_name, _email, _phone,
                                    _password, _dob, _gender, _cardNo, _location1, _location2,
                                    _location3, _location4, _dealerName, _dealerID, _linkedCardCount, _transactionCount, linkedCardSet);

                            Intent intent = new Intent(VerifyRationCard.this, LinkMoreRationCard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(VerifyRationCard.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            } else {
                Toast.makeText(VerifyRationCard.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

}