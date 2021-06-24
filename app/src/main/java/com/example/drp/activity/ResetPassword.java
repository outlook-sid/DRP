package com.example.drp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.drp.R;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.FieldHelper;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {

    private TextInputLayout password_tl, confirmPassword_tl;
    private String actualPhoneNumber;

    private UtilHelper utilHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_DarkBlue));

        password_tl = findViewById(R.id.resetPassword_page_password_wrapper);
        confirmPassword_tl = findViewById(R.id.resetPassword_page_password_conf_wrapper);
        Button confirmBtn = findViewById(R.id.set_new_password_button);

        utilHelper = new UtilHelper(ResetPassword.this);

        actualPhoneNumber = getIntent().getStringExtra("phoneNumber");

        confirmBtn.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(ResetPassword.this)){
                showOfflineDialog();
                return;
            }
            if (!FieldHelper.validatePassword(password_tl) | !FieldHelper.validatePassword(confirmPassword_tl) |
                    !FieldHelper.validatePassword(password_tl,confirmPassword_tl)) {
                return;
            }

            utilHelper.showProgressBar();

            password_tl.setErrorEnabled(false);
            password_tl.setError(null);
            confirmPassword_tl.setErrorEnabled(false);
            confirmPassword_tl.setError(null);

            String _newPassword = Objects.requireNonNull(password_tl.getEditText()).getText().toString().trim();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(actualPhoneNumber).child("userAccPassword").setValue(_newPassword);

            Toast.makeText(ResetPassword.this,"Password has been changed",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ResetPassword.this, PasswordChangedPage.class);
            utilHelper.hideProgressBar();
            startActivity(intent);
            finish();
        });


    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
        builder.setMessage("You haven't changed your password yet")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }



    private void showOfflineDialog() {
        Dialog dialog = new Dialog(ResetPassword.this);
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
            Intent intent = new Intent(ResetPassword.this, LoginPage.class);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(ResetPassword.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            ResetPassword.this.finish();
        });

        dialog.show();
    }



}