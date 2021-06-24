package com.example.drp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.drp.R;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class CheckAccDetailsAgainPage extends AppCompatActivity {

    private ProgressBar progressBar1;
    private FloatingActionButton previousPage_btn;
    private TextView goToPreviousPage_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkaccdetailsagain_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_DeepBlack));

        progressBar1 = findViewById(R.id.progress_bar_1);
        TextView userName_tv = findViewById(R.id.user_name_tv);
        TextView phoneNumber_tv = findViewById(R.id.phone_number_tv);
        TextView emailAddress_tv = findViewById(R.id.email_address_tv);
        TextView rationCardID_tv = findViewById(R.id.ration_card_id_tv);
        TextView gender_tv = findViewById(R.id.user_gender_tv);
        TextView dateOfBirth_tv = findViewById(R.id.date_of_birth_tv);
        TextView stateName_tv = findViewById(R.id.state_name_tv);
        TextView districtName_tv = findViewById(R.id.district_name_tv);
        TextView villageMunicipalityName_tv = findViewById(R.id.village_municipality_name_tv);
        TextView wardNumber_tv = findViewById(R.id.ward_number_tv);
        TextView dealerName_tv = findViewById(R.id.dealer_name_tv);
        TextView dealerID_tv = findViewById(R.id.dealer_id_tv);
        goToPreviousPage_tv = findViewById(R.id.tv_goto_previousPage);

        previousPage_btn = (FloatingActionButton) findViewById(R.id.fab_goBack_button);
        MaterialButton createAcc_btn = (MaterialButton) findViewById(R.id.mb_createAccount);

        String userName = getIntent().getStringExtra("Name");
        String phoneNo = getIntent().getStringExtra("PhoneNumber");
        String emailAddress = getIntent().getStringExtra("Email");
        String gender = getIntent().getStringExtra("Gender");
        String dob = getIntent().getStringExtra("DOB");
        String cardNo = getIntent().getStringExtra("RationCard");
        String state = getIntent().getStringExtra("LocationState");
        String district = getIntent().getStringExtra("LocationDistrict");
        String municipality = getIntent().getStringExtra("LocationMunicipality");
        String wardNo = getIntent().getStringExtra("LocationWardNo");
        String dealerName = getIntent().getStringExtra("DealerName");
        String dealerId = getIntent().getStringExtra("DealerID");

        userName_tv.setText(userName);
        phoneNumber_tv.setText(phoneNo);
        emailAddress_tv.setText(emailAddress);
        rationCardID_tv.setText(cardNo);
        gender_tv.setText(gender);
        dateOfBirth_tv.setText(dob);
        stateName_tv.setText(state);
        districtName_tv.setText(district);
        villageMunicipalityName_tv.setText(municipality);
        wardNumber_tv.setText(wardNo);
        dealerName_tv.setText(dealerName);
        dealerID_tv.setText(dealerId);
        progressBar1.setVisibility(View.GONE);

        createAcc_btn.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(CheckAccDetailsAgainPage.this)) {
                showOfflineDialog();
                return;
            }

            previousPage_btn.setVisibility(View.GONE);
            progressBar1.setVisibility(View.VISIBLE);
            previousPage_btn.setClickable(false);
            goToPreviousPage_tv.setText(getString(R.string.plz_wait));



            Intent intent = new Intent(CheckAccDetailsAgainPage.this, VerifyRationCard.class);
            intent.putExtra("new_User_SignUp", true);
            intent.putExtra("card_count", String.valueOf(0));
            intent.putExtra("new_card", getIntent().getStringExtra("RationCard"));
            intent.putExtra("linked_phone_number", getIntent().getStringExtra("linked_phone_number"));
            intent.putExtra("new_cardholder_name", getIntent().getStringExtra("new_cardholder_name"));

            intent.putExtra("Name", getIntent().getStringExtra("Name"));
            intent.putExtra("PhoneNumber", getIntent().getStringExtra("PhoneNumber"));
            intent.putExtra("AccPassword", getIntent().getStringExtra("AccPassword"));
            intent.putExtra("Email", getIntent().getStringExtra("Email"));
            intent.putExtra("RationCard", getIntent().getStringExtra("RationCard"));
            intent.putExtra("DOB", getIntent().getStringExtra("DOB"));
            intent.putExtra("Gender", getIntent().getStringExtra("Gender"));
            intent.putExtra("DealerName", getIntent().getStringExtra("DealerName"));
            intent.putExtra("DealerID", getIntent().getStringExtra("DealerID"));
            intent.putExtra("LocationState", getIntent().getStringExtra("LocationState"));
            intent.putExtra("LocationDistrict", getIntent().getStringExtra("LocationDistrict"));
            intent.putExtra("LocationMunicipality", getIntent().getStringExtra("LocationMunicipality"));
            intent.putExtra("LocationWardNo", getIntent().getStringExtra("LocationWardNo"));


            new Handler().postDelayed(() -> {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }, 1000);
        });

        previousPage_btn.setOnClickListener(v -> onBackPressed());

        goToPreviousPage_tv.setOnClickListener(v -> onBackPressed());
    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(CheckAccDetailsAgainPage.this);
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
            Intent intent = new Intent(CheckAccDetailsAgainPage.this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(CheckAccDetailsAgainPage.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);

        });

        dialog.show();
    }

}