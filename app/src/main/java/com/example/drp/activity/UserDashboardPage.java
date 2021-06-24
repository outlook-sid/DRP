package com.example.drp.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.drp.R;
import com.example.drp.database.QRCodeSessionManager;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Objects;

public class UserDashboardPage extends AppCompatActivity {

    private CardView cardView_1, cardView_2, cardView_3, cardView_4;
    private MaterialButton materialButtonShowQR;
    private  UserSessionManager userSessionManager;
    private QRCodeSessionManager qrCodeSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_BluishGrey));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        cardView_1 = (CardView) findViewById(R.id.card_1);
        cardView_2 = (CardView) findViewById(R.id.card_2);
        cardView_3 = (CardView) findViewById(R.id.card_3);
        cardView_4 = (CardView) findViewById(R.id.card_4);

        Toolbar toolbar = findViewById(R.id.tool_bar_1);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        //toolbar.setNavigationIcon(R.drawable.ic_baseline_dashboard_);
        toolbar.setTitle(R.string.startup_text_1);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.color_DeepBlack));
        toolbar.setSubtitle("");
        toolbar.setNavigationOnClickListener(v -> Toast.makeText(UserDashboardPage.this, "Not done yet...", Toast.LENGTH_SHORT).show());

        materialButtonShowQR = (MaterialButton) findViewById(R.id.mb_show_qr_code);

        ImageView logoutImg = findViewById(R.id.img_logout_btn);
        TextView logoutTxt = findViewById(R.id.txt_log_out);
        TextView phoneNumber_tv = findViewById(R.id.user_phone_number);
        TextView userName_tv = findViewById(R.id.header_user_name);
        TextView rationCardNumber_tv = findViewById(R.id.ration_card_number);

        userSessionManager = new UserSessionManager(UserDashboardPage.this);
        HashMap<String, String> _userData = userSessionManager.getUserDetailsFromSession();
        String _phone = _userData.get(UserSessionManager.KEY_PHONE);
        String _name = _userData.get(UserSessionManager.KEY_NAME);
        String _cardNo = _userData.get(UserSessionManager.KEY_RATION_CARD);

        qrCodeSessionManager = new QRCodeSessionManager(UserDashboardPage.this);

        phoneNumber_tv.setText(_phone);
        userName_tv.setText(_name);
        String _cardCount = _userData.get(UserSessionManager.KEY_LINKED_CARD_COUNT);
        assert _cardCount != null;
        if (_cardCount.equals("0")) {
            rationCardNumber_tv.setText(_cardNo);
        } else rationCardNumber_tv.setText(getString(R.string.userDashboard_cardID_plus, _cardNo,_cardCount));


        logoutImg.setOnClickListener(v -> {
            logoutUserFromApp();
            Intent intent = new Intent(UserDashboardPage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });
        logoutTxt.setOnClickListener(v -> {
            logoutUserFromApp();
            Intent intent = new Intent(UserDashboardPage.this, LoginPage.class);
            startActivity(intent);
            finish();
        });


        cardView_1.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardPage.this, LinkMoreRationCard.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserDashboardPage.this, cardView_1, "transition_card_1");
            startActivity(intent, options.toBundle());

        });

        cardView_2.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardPage.this, NewTransactionPage.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserDashboardPage.this, cardView_2, "transition_card_2");
            startActivity(intent, options.toBundle());

        });

        cardView_3.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardPage.this, TransactionHistoryPage.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserDashboardPage.this, cardView_3, "transition_card_3");
            startActivity(intent, options.toBundle());

        });

        cardView_4.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardPage.this, AccountDetailsPage.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserDashboardPage.this, cardView_4, "transition_card_4");
            startActivity(intent, options.toBundle());

        });


        if (qrCodeSessionManager.checkIncompleteTransaction()) {
            materialButtonShowQR.setVisibility(View.VISIBLE);
            materialButtonShowQR.setOnClickListener(v -> {
                if (!UtilHelper.isConnected(UserDashboardPage.this)) {
                    showOfflineDialog();
                    return;
                }
                Intent intent = new Intent(UserDashboardPage.this, PaymentResultPage.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserDashboardPage.this, materialButtonShowQR, "transition_qr_page");
                startActivity(intent, options.toBundle());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            });
        } else materialButtonShowQR.setVisibility(View.GONE);


    }

    @Override
    public void onBackPressed() {
        Dialog dialog = new Dialog(UserDashboardPage.this);
        dialog.setContentView(R.layout.layout_dialog_exit);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        FloatingActionButton fabExit = (FloatingActionButton) dialog.findViewById(R.id.fab_exit_from_app);
        fabExit.setOnClickListener(vi -> {
            dialog.dismiss();
//            getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            logoutUserFromApp();
            finishAffinity();
            //System.exit(0);
        });

        dialog.show();
    }

    private void logoutUserFromApp() {
        userSessionManager.logoutUserFromSession();
        qrCodeSessionManager.logoutUserFromSession();
    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(UserDashboardPage.this);
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
            logoutUserFromApp();
            //UserDashboardPage.this.finish();
            //finish();
        });

        dialog.show();
    }

}