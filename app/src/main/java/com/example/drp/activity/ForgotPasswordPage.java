package com.example.drp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.drp.R;
import com.example.drp.helpers.FieldHelper;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ForgotPasswordPage extends AppCompatActivity {

    private View parentLayout;
    private TextInputLayout registeredPhoneNo;
    private PinView enteredOTP_pv;
    private TextView instructionTxt_tv_1, instructionTxt_tv_2;
    private Button sendOTPBtn_1, verifyOTPBtn_2;
    private ImageButton backToLoginPage_btn;
    private ProgressBar progressBar;
    private String formattedPhoneNumber, UserEnteredOTP;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_DarkBlue));

        firebaseAuth = FirebaseAuth.getInstance();

        parentLayout = (View)findViewById(R.id.forgotPassword_page_layout);
        progressBar = findViewById(R.id.forgotPassword_progressBar);
        registeredPhoneNo = findViewById(R.id.verify_mobNo_wrapper);
        enteredOTP_pv = (PinView)findViewById(R.id.verify_otp_pin);
        instructionTxt_tv_1 = findViewById(R.id.forgotPassword_Page_textView2_1);
        instructionTxt_tv_2 = findViewById(R.id.forgotPassword_Page_textView2_2);
        sendOTPBtn_1 = (Button)findViewById(R.id.sendOTP_button_1);
        verifyOTPBtn_2 = (Button)findViewById(R.id.verifyOTP_button_2);
        backToLoginPage_btn = (ImageButton)findViewById(R.id.back_toLogin_page_btn);

        progressBar.setVisibility(View.GONE);
        enteredOTP_pv.setVisibility(View.GONE);
        instructionTxt_tv_2.setVisibility(View.GONE);
        verifyOTPBtn_2.setVisibility(View.GONE);

        backToLoginPage_btn.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(),"Password was not changed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ForgotPasswordPage.this, LoginPage.class);
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        });

        sendOTPBtn_1.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(ForgotPasswordPage.this)){
                showCustomDialog();
                return;
            }
            if (!FieldHelper.validatePhoneNo(registeredPhoneNo)){
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            String _pickedPhoneNo = Objects.requireNonNull(registeredPhoneNo.getEditText()).getText().toString().trim();
            formattedPhoneNumber = UtilHelper.formatPhoneNumber(_pickedPhoneNo);

            Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userPhone").equalTo(formattedPhoneNumber);
            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        registeredPhoneNo.setError(null);
                        registeredPhoneNo.setErrorEnabled(false);

                        instructionTxt_tv_1.setVisibility(View.GONE);
                        instructionTxt_tv_2.setVisibility(View.VISIBLE);
                        registeredPhoneNo.setVisibility(View.GONE);
                        enteredOTP_pv.setVisibility(View.VISIBLE);
                        sendOTPBtn_1.setVisibility(View.GONE);
                        verifyOTPBtn_2.setVisibility(View.VISIBLE);
                        backToLoginPage_btn.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                        sendVerificationCodeToUser(formattedPhoneNumber);

                    } else {
                        registeredPhoneNo.setError("Invalid phone number");
                        Toast.makeText(ForgotPasswordPage.this, "This phone number is not registered", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ForgotPasswordPage.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }

    private void sendVerificationCodeToUser(String formattedPhoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(formattedPhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(ForgotPasswordPage.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            UserEnteredOTP = s;
            Toast.makeText(getApplicationContext(),"code sent", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                enteredOTP_pv.setText(code);
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(ForgotPasswordPage.this, "not verified "+ UserEnteredOTP, Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(UserEnteredOTP, code);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(ForgotPasswordPage.this, task -> {

            if (task.isSuccessful()) {

                Toast.makeText(ForgotPasswordPage.this, "Provide new password", Toast.LENGTH_SHORT).show();

                //Perform required action here to either let the user sign In or do something required
                Intent intent = new Intent(ForgotPasswordPage.this, ResetPassword.class);
                intent.putExtra("phoneNumber", formattedPhoneNumber);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordPage.this, parentLayout, "OverallLayout_Transition");

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent, options.toBundle());
                finish();

            } else {
                Log.e("______NOTE_____", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
            }

        });

        verifyOTPBtn_2.setOnClickListener(v -> {
            String userCode = Objects.requireNonNull(enteredOTP_pv.getText()).toString();

            if (userCode.isEmpty() || userCode.length() < 6) {
                Toast.makeText(ForgotPasswordPage.this,"Wrong OTP...", Toast.LENGTH_SHORT).show();
                enteredOTP_pv.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(userCode);

        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Password was not changed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ForgotPasswordPage.this, LoginPage.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }


    private void showCustomDialog() {
        Dialog dialog = new Dialog(ForgotPasswordPage.this);
        dialog.setContentView(R.layout.layout_dialog_offline);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        Button positiveButton = dialog.findViewById(R.id.dialog_positive_btn);
        Button negativeButton = dialog.findViewById(R.id.dialog_negative_btn);
        positiveButton.setOnClickListener(vi -> {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            dialog.dismiss();
        });
        negativeButton.setOnClickListener(vi -> {
            dialog.dismiss();
            this.finish();
        });

        dialog.show();
    }


}