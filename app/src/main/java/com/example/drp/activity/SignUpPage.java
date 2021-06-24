package com.example.drp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.drp.R;
import com.example.drp.helpers.FieldHelper;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

public class SignUpPage extends AppCompatActivity {

    private DatePicker birthDatePicker;
    private TextView headerGender_tv, headerDOB_tv, headerLocation_tv;
    private EditText userLocationDistrict_tf, userLocationMunicipality_tf, userLocationWardNo_tf;
    private TextInputLayout userName_tfw, userEmail_tfw, userRationID_tfw, userDealerName_tfw, userDealerID_tfw, userAccPassword_tfw, userAccPasswordRep_tfw;
    private RadioGroup userGender_rg;
    private RadioButton userGender_rb;
    private UtilHelper utilHelper;
    private String userName, userPhone, userEmail, userRationCardID, userGender, userDealerName,
            userDealerID, userAccPassword, userLocationState, userLocationDistrict, userLocationMunicipality, userLocationWardNo;
    private StringBuilder userDOB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_DeepBlue));

        userGender_rg = (RadioGroup) findViewById(R.id.gender_radio_grp);
        userName_tfw = findViewById(R.id.signUp_user_name_wrapper);
        userEmail_tfw = findViewById(R.id.signUp_email_address_wrapper);
        userRationID_tfw = findViewById(R.id.signUp_user_cardID_wrapper);
        userDealerName_tfw = findViewById(R.id.signUp_dealer_name_wrapper);
        userDealerID_tfw = findViewById(R.id.signUp_dealer_ID_wrapper);
        userAccPassword_tfw = findViewById(R.id.signUp_page_password_wrapper);
        userAccPasswordRep_tfw = findViewById(R.id.signUp_page_password_conf_wrapper);
        birthDatePicker = findViewById(R.id.birthDay_picker);
        userLocationDistrict_tf = findViewById(R.id.signUp_page_district);
        userLocationMunicipality_tf = findViewById(R.id.signUp_page_municipality);
        userLocationWardNo_tf = findViewById(R.id.signUp_page_wardNo);
        Button checkAgainButton = (Button) findViewById(R.id.check_again_button);
        headerGender_tv = findViewById(R.id.header_gender_tv);
        headerDOB_tv = findViewById(R.id.header_dob_tv);
        headerLocation_tv = findViewById(R.id.header_location);
        TextView _signUPPagePhoneNo_tv = findViewById(R.id.signUp_phoneNo_tv);

        userPhone = getIntent().getStringExtra("phoneNumber");
        _signUPPagePhoneNo_tv.setText(getString(R.string.signUp_phone_no, userPhone));
        userLocationState = "West Bengal";

        checkAgainButton.setOnClickListener(v -> {

            utilHelper = new UtilHelper(SignUpPage.this);

            if (!UtilHelper.isConnected(SignUpPage.this)) {
                showOfflineDialog();
                return;
            }

            if (!FieldHelper.validateFullName(userName_tfw) | !FieldHelper.validateEmail(userEmail_tfw) |
                    !FieldHelper.validatePassword(userAccPasswordRep_tfw) | !FieldHelper.validatePassword(userAccPassword_tfw) |
                    !FieldHelper.validateUserCardID(userRationID_tfw) | !FieldHelper.validateField(userDealerName_tfw) | !FieldHelper.validateField(userDealerID_tfw) |
                    !FieldHelper.validatePassword(userAccPassword_tfw, userAccPasswordRep_tfw) | !validateAge() | !validateGender() | !isLocationFieldsEmpty()) {
                return;
            }
            utilHelper.showProgressBar();
            String _cardNo = Objects.requireNonNull(userRationID_tfw.getEditText()).getText().toString();
            Query checkUser = FirebaseDatabase.getInstance().getReference("Ration_Cards").orderByChild("cardNo").equalTo(_cardNo);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(SignUpPage.this, "This Card is already registered", Toast.LENGTH_SHORT).show();
                        userRationID_tfw.setErrorEnabled(true);
                        userRationID_tfw.setError("This card already exists");
                        utilHelper.hideProgressBar();

                    } else {
                        //..............................................

                        final String newCardID = userRationID_tfw.getEditText().getText().toString();
                        Query masterRationCardNode = FirebaseDatabase.getInstance().getReference("Master_Ration_Cards").orderByChild("cardNo").equalTo(newCardID);
                        masterRationCardNode.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(newCardID).exists()) {

                                    userRationID_tfw.setErrorEnabled(false);
                                    userRationID_tfw.setError(null);

                                    userGender_rb = findViewById(userGender_rg.getCheckedRadioButtonId());
                                    userGender = userGender_rb.getText().toString().trim();

                                    userName = Objects.requireNonNull(userName_tfw.getEditText()).getText().toString();
                                    userEmail = Objects.requireNonNull(userEmail_tfw.getEditText()).getText().toString();
                                    userRationCardID = userRationID_tfw.getEditText().getText().toString();

                                    StringBuilder _dateBuilder = new StringBuilder();
                                    _dateBuilder.append(birthDatePicker.getDayOfMonth()).append("/");
                                    _dateBuilder.append(birthDatePicker.getMonth()).append("/");
                                    _dateBuilder.append(birthDatePicker.getYear()).append("");
                                    userDOB = _dateBuilder;

                                    userDealerName = Objects.requireNonNull(userDealerName_tfw.getEditText()).getText().toString();
                                    userDealerID = Objects.requireNonNull(userDealerID_tfw.getEditText()).getText().toString();
                                    userAccPassword = Objects.requireNonNull(userAccPassword_tfw.getEditText()).getText().toString();
                                    userLocationDistrict = userLocationDistrict_tf.getText().toString();
                                    userLocationMunicipality = userLocationMunicipality_tf.getText().toString();
                                    userLocationWardNo = userLocationWardNo_tf.getText().toString();

                                    String linkedPhone_toNewCard = snapshot.child(newCardID).child("userPhone").getValue(String.class);
                                    String newCardholderName = snapshot.child(newCardID).child("userName").getValue(String.class);

                                    Intent intent = new Intent(SignUpPage.this, CheckAccDetailsAgainPage.class);

                                    intent.putExtra("new_card", newCardID);
                                    intent.putExtra("new_cardholder_name", newCardholderName);
                                    intent.putExtra("linked_phone_number", linkedPhone_toNewCard);
                                    intent.putExtra("Name", userName);
                                    intent.putExtra("PhoneNumber", userPhone);
                                    intent.putExtra("AccPassword", userAccPassword);
                                    intent.putExtra("Email", userEmail);
                                    intent.putExtra("RationCard", userRationCardID);
                                    intent.putExtra("DOB", userDOB.toString());
                                    intent.putExtra("Gender", userGender);
                                    intent.putExtra("DealerName", userDealerName);
                                    intent.putExtra("DealerID", userDealerID);
                                    intent.putExtra("LocationState", userLocationState);
                                    intent.putExtra("LocationDistrict", userLocationDistrict);
                                    intent.putExtra("LocationMunicipality", userLocationMunicipality);
                                    intent.putExtra("LocationWardNo", userLocationWardNo);

                                    utilHelper.hideProgressBar();
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignUpPage.this, "Invalid ration card number", Toast.LENGTH_SHORT).show();
                                    utilHelper.hideProgressBar();
                                }
                                utilHelper = null;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SignUpPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    utilHelper.hideProgressBar();
                    utilHelper = null;
                }
            });

        });
    }

    //
    @Override
    public void onBackPressed() {

        Dialog dialog = new Dialog(SignUpPage.this);
        dialog.setContentView(R.layout.layout_dialog_exit);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        FloatingActionButton fabExit = (FloatingActionButton) dialog.findViewById(R.id.fab_exit_from_app);
        fabExit.setOnClickListener(vi -> {
            dialog.dismiss();
            getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SignUpPage.this.finish();
            //LoginPage.this.finish();
            //finishAffinity();
            //System.exit(0);
        });

        dialog.show();

    }

    private boolean isLocationFieldsEmpty() {
        String val;
        val = userLocationDistrict_tf.getText().toString();
        if (val.isEmpty()) {
            headerLocation_tv.setTextColor(ContextCompat.getColor(this,R.color.color_FadedBrown));
            return false;
        }
        val = userLocationMunicipality_tf.getText().toString();
        if (val.isEmpty()) {
            headerLocation_tv.setTextColor(ContextCompat.getColor(this,R.color.color_FadedBrown));
            return false;
        }
        val = userLocationWardNo_tf.getText().toString();
        if (val.isEmpty()) {
            headerLocation_tv.setTextColor(ContextCompat.getColor(this,R.color.color_FadedBrown));
            return false;
        }
        headerLocation_tv.setTextColor(ContextCompat.getColor(this,R.color.color_PureWhite));
        return true;
    }

    private boolean validateGender() {
        if (userGender_rg.getCheckedRadioButtonId() == -1) {
            headerGender_tv.setTextColor(ContextCompat.getColor(this,R.color.color_FadedBrown));
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            headerGender_tv.setTextColor(ContextCompat.getColor(this,R.color.color_PureWhite));
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userYear = birthDatePicker.getYear();
        int isAgeValid = currentYear - userYear;

        if (userYear == currentYear) {
            headerDOB_tv.setTextColor(ContextCompat.getColor(this,R.color.color_FadedBrown));
        }

        if (isAgeValid < 18) {
            Toast.makeText(this, "You are not eligible to apply", Toast.LENGTH_SHORT).show();
            headerDOB_tv.setTextColor(ContextCompat.getColor(this,R.color.color_FadedBrown));
            return false;
        } else
            headerDOB_tv.setTextColor(ContextCompat.getColor(this,R.color.color_PureWhite));
        return true;
    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(SignUpPage.this);
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
            Intent intent = new Intent(SignUpPage.this, LoginPage.class);
            startActivity(intent);
            //finish();
            //finishAffinity();
            //System.exit(0);
            SignUpPage.this.finish();
        });

        dialog.show();
    }

}