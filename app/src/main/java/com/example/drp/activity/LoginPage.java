package com.example.drp.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;

import com.example.drp.R;
import com.example.drp.database.LoginSessionManager;
import com.example.drp.database.QRCodeSessionManager;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.FieldHelper;
import com.example.drp.helpers.QRCodeSessionModel;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LoginPage extends AppCompatActivity {

    private TextInputLayout loginPageUserID_w, loginPageAccPassword_w;
    private TextInputEditText loginPageUserID, loginPageAccPassword;
    private Button goBtn, verifyNoBtn;
    private CheckBox rememberMeToggleCb;
    private String uIDPhoneNo, userEnteredPassword;
    private LoginSessionManager rememberMeSessionManager;
    private TextView forgotPasswordTvBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_GreenGrey));

        loginPageUserID_w = findViewById(R.id.login_ID_wrapper);
        loginPageAccPassword_w = findViewById(R.id.login_password_wrapper);
        loginPageUserID = findViewById(R.id.login_ID);
        loginPageAccPassword = findViewById(R.id.login_password);
        forgotPasswordTvBtn = findViewById(R.id.forgot_password_tvbtn);
        goBtn = (Button) findViewById(R.id.login_button);
        verifyNoBtn = (Button) findViewById(R.id.verify_phone_no_page_button);
        rememberMeToggleCb = (CheckBox) findViewById(R.id.remember_me_toggle);

        rememberMeSessionManager = new LoginSessionManager(LoginPage.this);
        if (rememberMeSessionManager.checkLogin()) {
            rememberMeToggleCb.setChecked(true);
            HashMap<String, String> rememberMeDetails = rememberMeSessionManager.getRememberMeDetailsFromSession();
            loginPageUserID.setText(rememberMeDetails.get(LoginSessionManager.KEY_PHONE));
            loginPageAccPassword.setText(rememberMeDetails.get(LoginSessionManager.KEY_PASSWORD));
        }

        goBtn.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(LoginPage.this)) {
                showOfflineDialog();
                return;
            }

            if (!FieldHelper.validatePhoneNo(loginPageUserID_w) | !FieldHelper.validateField(loginPageAccPassword_w)) {
                return;
            }
            final UtilHelper utilHelper = new UtilHelper(LoginPage.this);
            utilHelper.showProgressBar();

            userEnteredPassword = Objects.requireNonNull(loginPageAccPassword.getText()).toString();
            String _PhoneNo = Objects.requireNonNull(loginPageUserID.getText()).toString();
            uIDPhoneNo = UtilHelper.formatPhoneNumber(_PhoneNo);
            Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userPhone").equalTo(uIDPhoneNo);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        loginPageUserID_w.setError(null);
                        loginPageUserID_w.setErrorEnabled(false);
                        String actualPassword = snapshot.child(uIDPhoneNo).child("userAccPassword").getValue(String.class);

                        if (userEnteredPassword.equals(actualPassword)) {
                            loginPageAccPassword_w.setError(null);
                            loginPageAccPassword_w.setErrorEnabled(false);

                            DataSnapshot ref = snapshot.child(uIDPhoneNo);

                            if (rememberMeToggleCb.isChecked()) {
                                rememberMeSessionManager = new LoginSessionManager(LoginPage.this);
                                rememberMeSessionManager.createRememberMeSession(uIDPhoneNo, userEnteredPassword);
                            } else rememberMeSessionManager.logoutUserFromSession();


//                            UserModel userObject = new UserModel();
//                            userObject.setUserPhone(ref.child("userPhone").getValue(String.class));

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

                            UserSessionManager userSessionManager = new UserSessionManager(LoginPage.this);
                            userSessionManager.createDashboardSession(
                                    ""+_name,
                                    ""+_email,
                                    ""+_phone,
                                    ""+ _password,
                                    ""+_dob,
                                    ""+ _gender,
                                    ""+_cardNo,
                                    ""+_location1,
                                    ""+ _location2,
                                    ""+_location3,
                                    ""+_location4,
                                    ""+ _dealerName,
                                    ""+_dealerID,
                                    ""+_linkedCardCount,
                                    ""+_transactionCount,
                                    linkedCardSet);

                            DataSnapshot refTransactionSession = ref.child("userIncompleteTransactions");
                            if (refTransactionSession.exists()) {
                                QRCodeSessionModel qrCodeSessionModel = refTransactionSession.getValue(QRCodeSessionModel.class);
                                QRCodeSessionManager qrCodeSessionManager = new QRCodeSessionManager(LoginPage.this);
                                assert qrCodeSessionModel != null;
                                qrCodeSessionManager.createQRCodeSession(
                                        ""+qrCodeSessionModel.getEncodedQRCode(),
                                        ""+qrCodeSessionModel.getTransactionDate(),
                                        ""+qrCodeSessionModel.getSelectedDate(),
                                        ""+qrCodeSessionModel.getSelectedTimeSlot(),
                                        ""+qrCodeSessionModel.getPaymentMethod(),
                                        ""+qrCodeSessionModel.getPaymentSubtotal(),
                                        ""+qrCodeSessionModel.getRicePriceAmount(),
                                        ""+qrCodeSessionModel.getWheatPriceAmount(),
                                        ""+qrCodeSessionModel.getSugarPriceAmount(),
                                        ""+qrCodeSessionModel.getKerosenePriceAmount());
                            }else Log.v("_____NOTE____","node :userIncompleteTransactions not found...");


                            Intent intent = new Intent(LoginPage.this, UserDashboardPage.class);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginPage.this, goBtn, "transition_go_Button");
                            startActivity(intent, options.toBundle());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            utilHelper.hideProgressBar();
                        } else {
                            utilHelper.hideProgressBar();
                            verifyNoBtn.setClickable(true);
                            forgotPasswordTvBtn.setClickable(true);
                            goBtn.setClickable(true);
                            Toast.makeText(LoginPage.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        utilHelper.hideProgressBar();
                        verifyNoBtn.setClickable(true);
                        forgotPasswordTvBtn.setClickable(true);
                        goBtn.setClickable(true);
                        Toast.makeText(LoginPage.this, "No such User found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        });

        verifyNoBtn.setOnClickListener(v -> {

            if (!UtilHelper.isConnected(LoginPage.this)) {
                showOfflineDialog();
                return;
            }

            Intent intent = new Intent(LoginPage.this, VerifyPhoneNo.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginPage.this, verifyNoBtn, "transition_signUp_Button");
            startActivity(intent, options.toBundle());
        });

        forgotPasswordTvBtn.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(LoginPage.this)) {
                showOfflineDialog();
                return;
            }
            Intent intent = new Intent(LoginPage.this, ForgotPasswordPage.class);
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        });

    }

    @Override
    public void onBackPressed() {

        Dialog dialog = new Dialog(LoginPage.this);
        dialog.setContentView(R.layout.layout_dialog_exit);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        FloatingActionButton fabExit = (FloatingActionButton)dialog.findViewById(R.id.fab_exit_from_app);
        fabExit.setOnClickListener(vi -> {
            dialog.dismiss();
            getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //LoginPage.this.finish();
            finishAffinity();
            //System.exit(0);
        });

        dialog.show();

    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(LoginPage.this);
        dialog.setContentView(R.layout.layout_dialog_offline);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        Button positiveButton = dialog.findViewById(R.id.dialog_positive_btn);
        Button negativeButton = dialog.findViewById(R.id.dialog_negative_btn);
        negativeButton.setText(getString(R.string.exit));
        positiveButton.setOnClickListener(vi -> {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(vi -> {
            dialog.dismiss();
            getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //LoginPage.this.finish();
            finishAffinity();
            //this.finish();
        });

        dialog.show();
    }

}