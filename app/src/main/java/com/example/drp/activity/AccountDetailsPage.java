package com.example.drp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.util.Log;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drp.R;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.RationCardInfoAdapter;
import com.example.drp.helpers.RationCardModel;
import com.example.drp.helpers.UtilHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AccountDetailsPage extends AppCompatActivity {

    private RecyclerView recyclerViewRationCardDetails;
    private List<RationCardModel> rationCardModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details_page);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_dashboard_card_4));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        ImageView imageViewRefresh = findViewById(R.id.iv_refresh_ration_cards);

        recyclerViewRationCardDetails = (RecyclerView)findViewById(R.id.rv_acc_details_ration_cards);
        recyclerViewRationCardDetails.setHasFixedSize(false);

        TextView textViewAccDetailsName = findViewById(R.id.acc_details_text_1);
        TextView textViewAccDetailsPhone = findViewById(R.id.acc_details_phone_tv);
        TextView textViewAccDetailsEmail = findViewById(R.id.acc_details_email_tv);
        TextView textViewState = findViewById(R.id.tv_acc_details_state);
        TextView textViewDistrict = findViewById(R.id.tv_acc_details_district);
        TextView textViewMunicipality = findViewById(R.id.tv_acc_details_municipality);
        TextView textViewWard = findViewById(R.id.tv_acc_details_ward);

        UserSessionManager userSessionManager = new UserSessionManager(AccountDetailsPage.this);
        HashMap<String, String > userDetails = userSessionManager.getUserDetailsFromSession();

        textViewAccDetailsName.setText(userDetails.get(UserSessionManager.KEY_NAME));
        textViewAccDetailsPhone.setText(userDetails.get(UserSessionManager.KEY_PHONE));
        textViewAccDetailsEmail.setText(userDetails.get(UserSessionManager.KEY_EMAIL));
        textViewState.setText(userDetails.get(UserSessionManager.KEY_LOCATION1));
        textViewDistrict.setText(userDetails.get(UserSessionManager.KEY_LOCATION2));
        textViewMunicipality.setText(userDetails.get(UserSessionManager.KEY_LOCATION3));
        textViewWard.setText(userDetails.get(UserSessionManager.KEY_LOCATION4));


        imageViewRefresh.setOnClickListener( v -> {

            imageViewRefresh.animate().rotation(imageViewRefresh.getRotation() + 360).start();

            String phoneNumber = userDetails.get(UserSessionManager.KEY_PHONE);
            rationCardModelList = new ArrayList<>();

            if (!UtilHelper.isConnected(AccountDetailsPage.this)) {
                showOfflineDialog();
                return;
            }
            Query query = FirebaseDatabase.getInstance().getReference("Ration_Cards").orderByChild("linkedAccount").equalTo(phoneNumber);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        imageViewRefresh.setVisibility(View.GONE);
                        for (DataSnapshot s : snapshot.getChildren()) {
                            RationCardModel rationCard = s.getValue(RationCardModel.class);
                            assert rationCard != null;
                            Log.v("_____NOTE_____", "holder name "+rationCard.getUserName());
                            rationCardModelList.add(rationCard);
                        }
                        RationCardInfoAdapter rationCardInfoAdapter = new RationCardInfoAdapter(AccountDetailsPage.this, rationCardModelList);
                        recyclerViewRationCardDetails.setAdapter(rationCardInfoAdapter);
                    }
                    else {
                        Toast.makeText(AccountDetailsPage.this, "No details available...", Toast.LENGTH_SHORT).show();
                        imageViewRefresh.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AccountDetailsPage.this, "No details available...", Toast.LENGTH_SHORT).show();
                    imageViewRefresh.setVisibility(View.VISIBLE);
                    Log.v("____NOTE____", error.getMessage());
                }
            });

        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        ConstraintLayout parentLayout = (ConstraintLayout) findViewById(R.id.constraint_layout_acc_details);
        Intent intent = new Intent(AccountDetailsPage.this, UserDashboardPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AccountDetailsPage.this, parentLayout, "transition_card_4");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent, options.toBundle());
    }


    private void showOfflineDialog() {
        Dialog dialog = new Dialog(AccountDetailsPage.this);
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
            Intent intent = new Intent(AccountDetailsPage.this, LoginPage.class);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(AccountDetailsPage.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            AccountDetailsPage.this.finish();
        });

        dialog.show();
    }


}