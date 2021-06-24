package com.example.drp.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.braintreepayments.cardform.view.CardForm;
import com.ebanx.swipebtn.SwipeButton;
import com.example.drp.R;
import com.example.drp.database.QRCodeSessionManager;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.QRCodeSessionModel;
import com.example.drp.helpers.TransactionCheckerModel;
import com.example.drp.helpers.TransactionModel;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

public class OnlinePayment extends AppCompatActivity {

    private SwipeButton swipeButton;
    private DatabaseReference usersReferenceNode, transactionReferenceNode, transactionCheckerReferenceNode;
    private UserSessionManager userSessionManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_payment);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_BluishGreen));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TextView tvSubtotal = findViewById(R.id.tv_online_payment_subtotal_txt);
        swipeButton = findViewById(R.id.online_payment_swipe_button);

        CardForm cardForm = findViewById(R.id.online_payment_card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .setup(OnlinePayment.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        cardForm.getExpirationDateEditText().setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);


        String _ricePrice = getIntent().getStringExtra("Rice_Price");
        String _riceAmt = getIntent().getStringExtra("Rice_Amt");
        //String _riceSubtotal = getIntent().getStringExtra("Rice_Subtotal");

        String _wheatPrice = getIntent().getStringExtra("Wheat_Price");
        String _wheatAmt = getIntent().getStringExtra("Wheat_Amt");
        //String _wheatSubtotal = getIntent().getStringExtra("Wheat_Subtotal");

        String _sugarPrice = getIntent().getStringExtra("Sugar_Price");
        String _sugarAmt = getIntent().getStringExtra("Sugar_Amt");
        //String _sugarSubtotal = getIntent().getStringExtra("Sugar_Subtotal");

        String _kerosenePrice = getIntent().getStringExtra("Kerosene_Price");
        String _keroseneAmt = getIntent().getStringExtra("Kerosene_Amt");
        //String _keroseneSubtotal = getIntent().getStringExtra("Kerosene_Subtotal");

        String rawSubtotal = getIntent().getStringExtra("Subtotal_Payment");
        String transactionID = getIntent().getStringExtra("Transaction_ID");
        String selectedDate = getIntent().getStringExtra("Selected_Date");
        String selectedTime = getIntent().getStringExtra("Selected_Time");

        Log.i("_____CHECK_____", "tran ID" + transactionID);

        swipeButton.setVisibility(View.GONE);

        String subtotal = "₹ " + rawSubtotal;
        tvSubtotal.setText(subtotal);

        cardForm.setOnCardFormValidListener(b -> {
            if (b) {
                if (!UtilHelper.isConnected(OnlinePayment.this)) {
                    showOfflineDialog();
                    return;
                }
//                Toast.makeText(OnlinePayment.this, "VALID CARD", Toast.LENGTH_SHORT).show();
                swipeButton.setVisibility(View.VISIBLE);
            }
        });

/*
        swipeButton.setOnStateChangeListener(active -> {
            Toast.makeText(OnlinePayment.this, "State: " + active, Toast.LENGTH_SHORT).show();
        });
*/

        swipeButton.setOnActiveListener(() -> {
            if (!UtilHelper.isConnected(OnlinePayment.this)) {
                showOfflineDialog();
                return;
            }
//            String cardNumber = cardForm.getCardNumber();
//            String expirationMonth = cardForm.getExpirationMonth();
//            String expirationYear = cardForm.getExpirationYear();
//            String cvv = cardForm.getCvv();

            if (cardForm.isValid()) {

                Drawable drawableIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_check, null);
                swipeButton.setEnabledDrawable(drawableIcon);

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
                LocalDateTime now = LocalDateTime.now();
                String date = now.format(dtf);

                UtilHelper utilHelper = new UtilHelper(OnlinePayment.this);
                utilHelper.showProgressBar();

                userSessionManager = new UserSessionManager(OnlinePayment.this);
                HashMap<String, String> userData = userSessionManager.getUserDetailsFromSession();
                String phoneNo = userData.get(UserSessionManager.KEY_PHONE);
                String transactionCount = userData.get(UserSessionManager.KEY_TRANSACTION_COUNT);


                transactionReferenceNode = FirebaseDatabase.getInstance().getReference("Transactions");
                transactionCheckerReferenceNode = FirebaseDatabase.getInstance().getReference("Transaction_Checker");
                usersReferenceNode = FirebaseDatabase.getInstance().getReference("Users");

                assert phoneNo != null;
                assert transactionCount != null;

                String dealerID = userData.get(UserSessionManager.KEY_DEALER_ID);
                TransactionModel transaction = new TransactionModel(
                        "" + userData.get(UserSessionManager.KEY_NAME),
                        "" + userData.get(UserSessionManager.KEY_PHONE),
                        "" + dealerID,
                        "" + userData.get(UserSessionManager.KEY_LINKED_CARD_COUNT),
                        "" + transactionID,
                        "" + date,
                        "online",
                        "" + selectedDate,
                        "" + selectedTime,
                        "" + _ricePrice,
                        "" + _riceAmt,
                        "" + _wheatPrice,
                        "" + _wheatAmt,
                        "" + _sugarPrice,
                        "" + _sugarAmt,
                        "" + _kerosenePrice,
                        "" + _keroseneAmt,
                        "" + rawSubtotal);

                //UPLOAD
                assert transactionID != null;
                transactionReferenceNode.child(transactionID).setValue(transaction).addOnCompleteListener(task -> {

                });

                TransactionCheckerModel transactionChecker = new TransactionCheckerModel(
                        "" + selectedDate,
                        "" + selectedTime,
                        "" + transactionID,
                        "" + date);


                //UPLOAD
                assert selectedDate != null;
                assert selectedTime != null;
                assert dealerID != null;
                transactionCheckerReferenceNode.child(dealerID).child(selectedDate).child(selectedTime).setValue(transactionChecker).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(OnlinePayment.this, "ERROR in tran_check", Toast.LENGTH_SHORT).show();
                    }
                });

                QRCodeSessionModel qrCodeSessionModel = new QRCodeSessionModel(
                        "" + transactionID,
                        "" + date,
                        "" + selectedDate,
                        "" + selectedTime,
                        "Online",
                        "" + rawSubtotal,
                        _ricePrice + "_" + _riceAmt,
                        _wheatPrice + "_" + _wheatAmt,
                        _sugarPrice + "_" + _sugarAmt,
                        _kerosenePrice + "_" + _keroseneAmt);

                usersReferenceNode.child(phoneNo).child("userIncompleteTransactions").setValue(qrCodeSessionModel).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.v("_____NOTE____", "ERROR in user_incomplete_tran  ");
                        Toast.makeText(OnlinePayment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });

                QRCodeSessionManager qrCodeSession = new QRCodeSessionManager(OnlinePayment.this);
                qrCodeSession.createQRCodeSession(
                        "" + transactionID,
                        "" + date,
                        "" + selectedDate,
                        "" + selectedTime,
                        "Online",
                        "" + rawSubtotal,
                        _ricePrice + "_" + _riceAmt,
                        _wheatPrice + "_" + _wheatAmt,
                        _sugarPrice + "_" + _sugarAmt,
                        _kerosenePrice + "_" + _keroseneAmt);


                utilHelper.hideProgressBar();

                Toast.makeText(OnlinePayment.this, "date and time-slot successfully booked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OnlinePayment.this, PaymentResultPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(OnlinePayment.this, swipeButton, "transition_qr_page");
                startActivity(intent, options.toBundle());
            } else {
                Drawable drawableIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_close, null);
                swipeButton.setEnabledDrawable(drawableIcon);
                Toast.makeText(OnlinePayment.this, "Please complete the form!", Toast.LENGTH_SHORT).show();
            }
        });

        CardView materialCardViewOtherPaymentMethodUPI = (CardView) findViewById(R.id.mcv_online_payment_upi);
        materialCardViewOtherPaymentMethodUPI.setOnClickListener(v -> {
            View view = (View)findViewById(R.id.cl_online_payment_page);
            Snackbar.make(view, "UPI payment is not available yet", Snackbar.LENGTH_LONG)
                    .setAction("OK", view1 -> {

                    })
                    .setActionTextColor(ContextCompat.getColor(this,R.color.color_LitePurple))
                    .show();

        });

        CardView materialCardViewOtherPaymentMethodNB = (CardView) findViewById(R.id.mcv_online_payment_net_banking);
        materialCardViewOtherPaymentMethodNB.setOnClickListener(v -> {
            View view = (View)findViewById(R.id.cl_online_payment_page);
            Snackbar.make(view, "Net Banking is not available yet", Snackbar.LENGTH_LONG)
                    .setAction("OK", view1 -> {

                    })
                    .setActionTextColor(ContextCompat.getColor(this,R.color.color_LitePurple))
                    .show();

        });

    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(OnlinePayment.this);
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
            Intent intent = new Intent(OnlinePayment.this, LoginPage.class);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(OnlinePayment.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            OnlinePayment.this.finish();
        });

        dialog.show();
    }
}