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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drp.R;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.TransactionInfoAdapter;
import com.example.drp.helpers.TransactionInfoModel;
import com.example.drp.helpers.UtilHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TransactionHistoryPage extends AppCompatActivity {

    private List<TransactionInfoModel> transactionInfoModelList;
    private RecyclerView recyclerViewTransactionHistory;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_page);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.color_dashboard_card_3));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        progressBar = (ProgressBar)findViewById(R.id.pb_tran_hist);

        ImageView imageViewRefresh = (ImageView) findViewById(R.id.iv_refresh_tran_history);


        recyclerViewTransactionHistory = (RecyclerView) findViewById(R.id.rv_transaction_history);
        recyclerViewTransactionHistory.setHasFixedSize(false);
        recyclerViewTransactionHistory.setLayoutManager(new LinearLayoutManager(this));

        transactionInfoModelList = new ArrayList<>();

        imageViewRefresh.setOnClickListener(v -> {

            imageViewRefresh.animate().rotation(imageViewRefresh.getRotation() + 360).start();

            if (!UtilHelper.isConnected(TransactionHistoryPage.this)) {
                showOfflineDialog();
                return;
            }
            loadTransactionHistoryRecyclerView();
        });

        //utilHelper.showProgressBar();


        //...................................FOR TESTING ONLY................................................
/*      transactionInfoModelList.add(new TransactionInfoModel(
                "------",
                "-----",
                "-- -- ----",
                "--:-- pm - --:-- pm",
                "₹ --.--",
                "₹ --.--",
                "₹ --.--",
                "₹ --.--",
                "₹ --.--"
        ));
        transactionInfoModelList.add(new TransactionInfoModel(
                "08 May 2021",
                "Cash",
                "11 May 2021",
                "12:40 pm - 01:00 pm",
                "₹ 21.20",
                "₹ 11.20",
                "₹ 20.10",
                "₹ 77.20",
                "₹ 271.20"
        ));*/
        //..............................REMOVE...............................................................


    }

    @Override
    public void onBackPressed() {
        ConstraintLayout parentLayout = (ConstraintLayout) findViewById(R.id.constraint_layout_transaction_history);
        Intent intent = new Intent(TransactionHistoryPage.this, UserDashboardPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(TransactionHistoryPage.this, parentLayout, "transition_card_3");
        startActivity(intent, options.toBundle());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        TransactionHistoryPage.this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!UtilHelper.isConnected(TransactionHistoryPage.this)) {
            showOfflineDialog();
        }else loadTransactionHistoryRecyclerView();
    }

    private void loadTransactionHistoryRecyclerView() {
        progressBar.setVisibility(View.VISIBLE);

        UserSessionManager userSessionManager = new UserSessionManager(TransactionHistoryPage.this);
        HashMap<String, String> userDetails = userSessionManager.getUserDetailsFromSession();
        String userPhoneNo = userDetails.get(UserSessionManager.KEY_PHONE);

        Query userRef = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userPhone").equalTo(userPhoneNo);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                assert userPhoneNo != null;
                DataSnapshot tranInfoSnapshot = snapshot.child(userPhoneNo).child("userTransactions");
                if (tranInfoSnapshot.exists()) {
                    transactionInfoModelList.clear();
                    for (DataSnapshot s : tranInfoSnapshot.getChildren()) {
                        TransactionInfoModel transactionInfoModel;
                        transactionInfoModel = s.getValue(TransactionInfoModel.class);

                        //Log.i("_______NOTE______", "Method : " + transactionInfoModel.getTransactionPaymentMethod());

                        transactionInfoModelList.add(transactionInfoModel);

                    }
                    Collections.reverse(transactionInfoModelList);
                    TransactionInfoAdapter transactionInfoAdapter = new TransactionInfoAdapter(TransactionHistoryPage.this, transactionInfoModelList);
                    recyclerViewTransactionHistory.setAdapter(transactionInfoAdapter);

                } else {
                    Toast.makeText(TransactionHistoryPage.this, "No records found", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(TransactionHistoryPage.this);
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
            Intent intent = new Intent(TransactionHistoryPage.this, LoginPage.class);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(TransactionHistoryPage.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            TransactionHistoryPage.this.finish();
        });

        dialog.show();
    }
}