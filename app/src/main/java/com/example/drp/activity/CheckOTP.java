package com.example.drp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.drp.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CheckOTP extends AppCompatActivity {

    private String sUserEnteredOTP, sUserEnteredPhoneNumber;
    private PinView otpPin;
    private ProgressBar progressBar1;
    private TextView verifyOtpPageTV4;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_otp);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_DeepBlue));


        verifyOtpPageTV4 = findViewById(R.id.verify_otp_Page_textView4);
        otpPin = findViewById(R.id.verify_otp_pin);
        progressBar1 = findViewById(R.id.progress_bar_1);
        //
        Button verifyOTPBtn = findViewById(R.id.verify_otp_button);
        firebaseAuth = FirebaseAuth.getInstance();

        progressBar1.setVisibility(View.GONE);

        sUserEnteredPhoneNumber = getIntent().getStringExtra("PhoneNo");
//        Toast.makeText(CheckOTP.this, sUserEnteredPhoneNumber,Toast.LENGTH_LONG).show();

        verifyOTPBtn.setOnClickListener(v -> {
            String userCode = Objects.requireNonNull(otpPin.getText()).toString();

            if (userCode.isEmpty() || userCode.length() < 6) {
                Toast.makeText(CheckOTP.this, "Wrong OTP...", Toast.LENGTH_SHORT).show();
                otpPin.requestFocus();
                return;
            }
            progressBar1.setVisibility(View.VISIBLE);
            verifyCode(userCode);

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyOtpPageTV4.setText(getString(R.string.checkOTP_otp_send_to_no, sUserEnteredPhoneNumber));

        sendVerificationCodeToUser(sUserEnteredPhoneNumber);
    }

    void sendVerificationCodeToUser(String sUserEnteredPhoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(sUserEnteredPhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(CheckOTP.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)              // OnVerificationStateChangedCallbacks
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
                progressBar1.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
            Toast.makeText(getApplicationContext(), "verification complete", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(CheckOTP.this, "not verified " + sUserEnteredOTP, Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sUserEnteredOTP, code);
        signInTheUserByCredentials(credential);
    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        ImageView step2Image = findViewById(R.id.step_2_img);
        TextView verifyOtpPageTV1 = findViewById(R.id.verify_otp_Page_textView1);
        TextView verifyOtpPageTV2 = findViewById(R.id.verify_otp_Page_textView2);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(CheckOTP.this, task -> {

            if (task.isSuccessful()) {

                Toast.makeText(CheckOTP.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();

                //Perform required action here to either let the user sign In or do something required
                Intent intent = new Intent(CheckOTP.this, SignUpPage.class);
                intent.putExtra("phoneNumber", sUserEnteredPhoneNumber);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CheckOTP.this,
                        Pair.create(verifyOtpPageTV1, "transition_Text1"),
                        Pair.create(verifyOtpPageTV2, "transition_Text2"),
                        Pair.create(step2Image, "transition_progress_Image"));

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent, options.toBundle());

            } else {
                Toast.makeText(CheckOTP.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }

}


//project-539692040813