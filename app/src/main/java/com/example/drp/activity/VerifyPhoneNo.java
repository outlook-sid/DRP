package com.example.drp.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.drp.R;
import com.example.drp.helpers.FieldHelper;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class VerifyPhoneNo extends AppCompatActivity {

    private TextView verifyPageTV1, verifyPageTV2;
    private ImageView step1Image;
    private TextInputLayout mobNoWrapper;
    private UtilHelper utilHelper;
    private String formattedPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_no);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_DeepBlue));

        mobNoWrapper = findViewById(R.id.verify_mobNo_wrapper);
        step1Image = findViewById(R.id.step_1_img);
        Button verifyButton = (Button) findViewById(R.id.verify_button1);
        verifyPageTV1 = findViewById(R.id.verify_no_Page_textView1);
        verifyPageTV2 = findViewById(R.id.verify_no_Page_textView2);

        verifyButton.setOnClickListener(v -> {

            utilHelper = new UtilHelper(VerifyPhoneNo.this);

            if (!UtilHelper.isConnected(VerifyPhoneNo.this)){
                showCustomDialog();
                return;
            }

            if (FieldHelper.validatePhoneNo(mobNoWrapper)) {

                utilHelper.showProgressBar();

                String _phoneNo = Objects.requireNonNull(mobNoWrapper.getEditText()).getText().toString();
                formattedPhoneNo = UtilHelper.formatPhoneNumber(_phoneNo);

                Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userPhone").equalTo(formattedPhoneNo);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            utilHelper.hideProgressBar();
                            Toast.makeText(VerifyPhoneNo.this, "This phone number is already registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(VerifyPhoneNo.this, CheckOTP.class);
                            intent.putExtra("PhoneNo", formattedPhoneNo);
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(VerifyPhoneNo.this,
                                    Pair.create(verifyPageTV1, "transition_Text1"),
                                    Pair.create(verifyPageTV2, "transition_Text2"),
                                    Pair.create(step1Image, "transition_progress_Image"));
                            utilHelper.hideProgressBar();
                            startActivity(intent, options.toBundle());
                            utilHelper=null;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(VerifyPhoneNo.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }


    private void showCustomDialog() {
        Dialog dialog = new Dialog(VerifyPhoneNo.this);
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