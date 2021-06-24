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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.drp.R;
import com.example.drp.database.QRCodeSessionManager;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.QRCodeHelper;
import com.example.drp.helpers.QRCodeSessionModel;
import com.example.drp.helpers.TransactionCheckerModel;
import com.example.drp.helpers.TransactionInfoModel;
import com.example.drp.helpers.TransactionModel;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserInventoryPage extends AppCompatActivity {

    private MaterialButton materialButtonPayment, materialButtonOnlinePayment, materialButtonCashPayment;
    private DatabaseReference usersReferenceNode, transactionReferenceNode, transactionCheckerReferenceNode;
    private LinearLayout ll_paymentButton;
    private UserSessionManager userSessionManager;
    private QRCodeSessionManager qrCodeSession;
    private Boolean buttonState = false;
    private String priceRice, amtRice, priceWheat, amtWheat, priceSugar, amtSugar, priceKerosene, amtKerosene, selectedDate, selectedTimeSlot;
    private float subtotal = 0.0F;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_inventory_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_DeepBlue));

        TextView tv_headerSubtotal = findViewById(R.id.tv_inventory_subtotal);

        TextView tv_priceRice, tv_selectedRice, tv_totalPriceRice;
        TextView tv_priceWheat, tv_selectedWheat, tv_totalPriceWheat;
        TextView tv_priceSugar, tv_selectedSugar, tv_totalPriceSugar;
        TextView tv_priceKerosene, tv_selectedKerosene, tv_totalPriceKerosene;

        tv_priceRice = findViewById(R.id.tv_rice_price_per_unit_kart);
        tv_selectedRice = findViewById(R.id.tv_rice_amount_in_unit_kart);
        tv_totalPriceRice = findViewById(R.id.tv_rice_total_price_kart);
        tv_priceWheat = findViewById(R.id.tv_wheat_price_per_unit_kart);
        tv_selectedWheat = findViewById(R.id.tv_wheat_amount_in_unit_kart);
        tv_totalPriceWheat = findViewById(R.id.tv_wheat_total_price_kart);
        tv_priceSugar = findViewById(R.id.tv_sugar_price_per_unit_kart);
        tv_selectedSugar = findViewById(R.id.tv_sugar_amount_in_unit_kart);
        tv_totalPriceSugar = findViewById(R.id.tv_sugar_total_price_kart);
        tv_priceKerosene = findViewById(R.id.tv_kerosene_price_per_unit_kart);
        tv_selectedKerosene = findViewById(R.id.tv_kerosene_amount_in_unit_kart);
        tv_totalPriceKerosene = findViewById(R.id.tv_kerosene_total_price_kart);

        TextView textViewNotCollected = findViewById(R.id.tv_ration_not_collected);

        ConstraintLayout constraintLayoutRice = (ConstraintLayout) findViewById(R.id.cl_inventory_rice);
        ConstraintLayout constraintLayoutWheat = (ConstraintLayout) findViewById(R.id.cl_inventory_wheat);
        ConstraintLayout constraintLayoutSugar = (ConstraintLayout) findViewById(R.id.cl_inventory_sugar);
        ConstraintLayout constraintLayoutKerosene = (ConstraintLayout) findViewById(R.id.cl_inventory_kerosene);

        ll_paymentButton = (LinearLayout) findViewById(R.id.ll_inventory_layout2);

        materialButtonPayment = (MaterialButton) findViewById(R.id.mb_inventory_pay);
        materialButtonOnlinePayment = (MaterialButton) findViewById(R.id.mb_inventory_online_pay);
        materialButtonCashPayment = (MaterialButton) findViewById(R.id.mb_inventory_cash_pay);

        qrCodeSession = new QRCodeSessionManager(UserInventoryPage.this);

        if (qrCodeSession.checkIncompleteTransaction()) {
            materialButtonPayment.setVisibility(View.GONE);
            textViewNotCollected.setVisibility(View.VISIBLE);
        }

//       textViewNotCollected.setOnClickListener(v -> {                                              //  ************remove this***********
//            qrCodeSession.logoutUserFromSession();
//        });


        constraintLayoutRice.setVisibility(View.GONE);
        constraintLayoutWheat.setVisibility(View.GONE);
        constraintLayoutSugar.setVisibility(View.GONE);
        constraintLayoutKerosene.setVisibility(View.GONE);


        priceRice = getIntent().getStringExtra("Price_Rice");
        amtRice = getIntent().getStringExtra("Selected_Rice");
        priceWheat = getIntent().getStringExtra("Price_Wheat");
        amtWheat = getIntent().getStringExtra("Selected_Wheat");
        priceSugar = getIntent().getStringExtra("Price_Sugar");
        amtSugar = getIntent().getStringExtra("Selected_Sugar");
        priceKerosene = getIntent().getStringExtra("Price_Kerosene");
        amtKerosene = getIntent().getStringExtra("Selected_Kerosene");
        selectedDate = getIntent().getStringExtra("Selected_Date");
        selectedTimeSlot = getIntent().getStringExtra("Selected_TimeSlot");

        HashMap<String, Boolean> selectedItems = (HashMap<String, Boolean>) getIntent().getSerializableExtra("Items_Chosen");

        userSessionManager = new UserSessionManager(UserInventoryPage.this);
        HashMap<String, String> userData = userSessionManager.getUserDetailsFromSession();


        float subtotalRice = Float.parseFloat(priceRice) * Float.parseFloat(amtRice);
        if ((boolean) selectedItems.get(GroceriesPage.ITEM_NAME_RICE)) {
            constraintLayoutRice.setVisibility(View.VISIBLE);
            tv_priceRice.setText(getString(R.string.grocery_price_in_inr, priceRice));
            tv_selectedRice.setText(getString(R.string.grocery_qty_in_Kg, Float.parseFloat(amtRice)));
            tv_totalPriceRice.setText(getString(R.string.grocery_price_in_inr, formatFloat_to2Decimal(subtotalRice)));
            subtotal = subtotal + subtotalRice;
        }

        float subtotalWheat = Float.parseFloat(priceWheat) * Float.parseFloat(amtWheat);
        if ((boolean) selectedItems.get(GroceriesPage.ITEM_NAME_WHEAT)) {
            constraintLayoutWheat.setVisibility(View.VISIBLE);
            tv_priceWheat.setText(getString(R.string.grocery_price_in_inr, priceWheat));
            tv_selectedWheat.setText(getString(R.string.grocery_qty_in_Kg, Float.parseFloat(amtWheat)));
            tv_totalPriceWheat.setText(getString(R.string.grocery_price_in_inr, formatFloat_to2Decimal(subtotalWheat)));
            subtotal = subtotal + subtotalWheat;
        }

        float subtotalSugar = Float.parseFloat(priceSugar) * Float.parseFloat(amtSugar);
        if ((boolean) selectedItems.get(GroceriesPage.ITEM_NAME_SUGAR)) {
            constraintLayoutSugar.setVisibility(View.VISIBLE);
            tv_priceSugar.setText(getString(R.string.grocery_price_in_inr, priceSugar));
            tv_selectedSugar.setText(getString(R.string.grocery_qty_in_Kg, Float.parseFloat(amtSugar)));
            tv_totalPriceSugar.setText(getString(R.string.grocery_price_in_inr, formatFloat_to2Decimal(subtotalSugar)));
            subtotal = subtotal + subtotalSugar;
        }

        float subtotalKerosene = Float.parseFloat(priceKerosene) * Float.parseFloat(amtKerosene);
        if ((boolean) selectedItems.get(GroceriesPage.ITEM_NAME_KEROSENE)) {
            constraintLayoutKerosene.setVisibility(View.VISIBLE);
            tv_priceKerosene.setText(getString(R.string.grocery_price_in_inr, priceKerosene));
            tv_selectedKerosene.setText(getString(R.string.grocery_qty_in_liters, Float.parseFloat(amtKerosene)));
            tv_totalPriceKerosene.setText(getString(R.string.grocery_price_in_inr, formatFloat_to2Decimal(subtotalKerosene)));
            subtotal = subtotal + subtotalKerosene;
        }

        int itemCount = getIntent().getIntExtra("Item_Count", 0);
        if (itemCount == 1) {
            tv_headerSubtotal.setText(getString(R.string.userInventor_subtotal, itemCount, "item", formatFloat_to2Decimal(subtotal)));
        } else {
            tv_headerSubtotal.setText(getString(R.string.userInventor_subtotal, itemCount, "items", formatFloat_to2Decimal(subtotal)));
        }


        String accID = userData.get(UserSessionManager.KEY_PHONE);

        assert accID != null;

        //Toast.makeText(UserInventoryPage.this, "code "+selectedTimeSlot, Toast.LENGTH_LONG).show();


        materialButtonPayment.setOnClickListener(v -> {
            if (buttonState) {
                ll_paymentButton.setVisibility(View.GONE);
                Drawable drawableIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_arrow_down, null);
                materialButtonPayment.setIcon(drawableIcon);
                buttonState = false;
            } else {
                ll_paymentButton.setVisibility(View.VISIBLE);
                Drawable drawableIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_arrow_up, null);
                materialButtonPayment.setIcon(drawableIcon);

                buttonState = true;
            }
        });

        String finalSubtotal = formatFloat_to2Decimal(subtotal);


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(dtf);

        Log.i("_____CHECK_____", date + "\n" +
                "Selected " + selectedDate + "_" + selectedTimeSlot + "\n" +
                "r " + priceRice + " X " + amtRice + " = " + subtotalRice + "\n" +
                "w " + priceWheat + " X " + amtWheat + " = " + subtotalWheat + "\n" +
                "s " + priceSugar + " X " + amtSugar + " = " + subtotalSugar + "\n" +
                "k " + priceKerosene + " X " + amtKerosene + " = " + subtotalKerosene + "\n" +
                finalSubtotal);


        materialButtonOnlinePayment.setOnClickListener(v -> {
            // _________Online Payment_________

            if (!UtilHelper.isConnected(UserInventoryPage.this)) {
                showOfflineDialog();
                return;
            }
            String _accKey = userData.get(UserSessionManager.KEY_PASSWORD);
            String _transactionID = QRCodeHelper.encodeQRCode(accID.substring(3), selectedDate, _accKey, "Online_Payment");

            Intent intent = new Intent(UserInventoryPage.this, OnlinePayment.class);
            intent.putExtra("Transaction_ID", _transactionID);
            intent.putExtra("Selected_Date", selectedDate);
            intent.putExtra("Selected_Time", selectedTimeSlot);
            intent.putExtra("Subtotal_Payment", finalSubtotal);
            //price
            intent.putExtra("Rice_Price", priceRice);
            intent.putExtra("Wheat_Price", priceWheat);
            intent.putExtra("Sugar_Price", priceSugar);
            intent.putExtra("Kerosene_Price", priceKerosene);
            //amount
            intent.putExtra("Rice_Amt", amtRice);
            intent.putExtra("Wheat_Amt", amtWheat);
            intent.putExtra("Sugar_Amt", amtSugar);
            intent.putExtra("Kerosene_Amt", amtKerosene);
            //subtotal
            intent.putExtra("Rice_Subtotal", String.valueOf(subtotalRice));
            intent.putExtra("Wheat_Subtotal", String.valueOf(subtotalWheat));
            intent.putExtra("Sugar_Subtotal", String.valueOf(subtotalSugar));
            intent.putExtra("Kerosene_Subtotal", String.valueOf(subtotalKerosene));
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserInventoryPage.this, materialButtonOnlinePayment, "transition_online_payment");
            startActivity(intent, options.toBundle());

        });

        materialButtonCashPayment.setOnClickListener(v -> {
            // ________Cash payment__________

            if (!UtilHelper.isConnected(UserInventoryPage.this)) {
                showOfflineDialog();
                return;
            }

            usersReferenceNode = FirebaseDatabase.getInstance().getReference("Users");
            transactionReferenceNode = FirebaseDatabase.getInstance().getReference("Transactions");
            transactionCheckerReferenceNode = FirebaseDatabase.getInstance().getReference("Transaction_Checker");

            String _accKey = userData.get(UserSessionManager.KEY_PASSWORD);
            String transactionID = QRCodeHelper.encodeQRCode(accID.substring(3), selectedDate, _accKey, "Cash_Payment");

            UtilHelper utilHelper = new UtilHelper(UserInventoryPage.this);
            utilHelper.showProgressBar();


            String phoneNo = userData.get(UserSessionManager.KEY_PHONE);
            String transactionCount = userData.get(UserSessionManager.KEY_TRANSACTION_COUNT);

            TransactionInfoModel transactionInfo = new TransactionInfoModel(
                    "" + date,
                    "cash",
                    "" + selectedDate,
                    "" + selectedTimeSlot,
                    "" + subtotalRice,
                    "" + subtotalWheat,
                    "" + subtotalSugar,
                    "" + subtotalKerosene,
                    "" + finalSubtotal);

            assert phoneNo != null;
            assert transactionCount != null;

            Log.i("_____CHECK_____", date + " " + selectedDate + selectedTimeSlot + "\nr " + priceRice + " X " + amtRice + " = " + subtotalRice + "\n" +
                    "w " + priceWheat + " X " + amtWheat + " = " + subtotalWheat + "\n" +
                    "s " + priceSugar + " X " + amtSugar + " = " + subtotalSugar + "\n" +
                    "k " + priceKerosene + " X " + amtKerosene + " = " + subtotalKerosene + "\n" +
                    finalSubtotal + "\n" + transactionID);


            String dealerID = userData.get(UserSessionManager.KEY_DEALER_ID);
            TransactionModel transaction = new TransactionModel(
                    "" + userData.get(UserSessionManager.KEY_NAME),
                    "" + userData.get(UserSessionManager.KEY_PHONE),
                    "" + dealerID,
                    "" + userData.get(UserSessionManager.KEY_LINKED_CARD_COUNT),
                    "" + transactionID,
                    "" + date,
                    "cash",
                    "" + selectedDate,
                    "" + selectedTimeSlot,
                    "" + priceRice,
                    "" + amtRice,
                    "" + priceWheat,
                    "" + amtWheat,
                    "" + priceSugar,
                    "" + amtSugar,
                    "" + priceKerosene,
                    "" + amtKerosene,
                    "" + finalSubtotal);

            transactionReferenceNode.child(transactionID).setValue(transaction).addOnCompleteListener(task -> {                                        //UPLOAD
                if (!task.isSuccessful()) {
                    Toast.makeText(UserInventoryPage.this, "ERROR in tran", Toast.LENGTH_SHORT).show();
                }
            });

            Log.i("_____CHECK_____", "line - 346");

            TransactionCheckerModel transactionChecker = new TransactionCheckerModel(
                    "" + selectedDate,
                    "" + selectedTimeSlot,
                    "" + transactionID,
                    "" + date);
            assert dealerID != null;
            transactionCheckerReferenceNode.child(dealerID).child(selectedDate).child(selectedTimeSlot).setValue(transactionChecker).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(UserInventoryPage.this, "ERROR in tran_check", Toast.LENGTH_SHORT).show();
                }
            });                     //UPLOAD
            Log.i("_____CHECK_____", "line - 353");


            QRCodeSessionModel qrCodeSessionModel = new QRCodeSessionModel(
                    "" + transactionID,
                    "" + date,
                    "" + selectedDate,
                    "" + selectedTimeSlot,
                    "Cash",
                    "" + finalSubtotal,
                    priceRice + "_" + amtRice,
                    priceWheat + "_" + amtWheat,
                    priceSugar + "_" + amtSugar,
                    priceKerosene + "_" + amtKerosene);

            usersReferenceNode.child(phoneNo).child("userIncompleteTransactions").setValue(qrCodeSessionModel).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.v("_____NOTE____", "ERROR in user_incomplete_tran  ");
                    Toast.makeText(UserInventoryPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

            Log.v("_____NOTE____", "inside else line 425");
            qrCodeSession.createQRCodeSession(
                    "" + transactionID,
                    "" + date,
                    "" + selectedDate,
                    "" + selectedTimeSlot,
                    "Cash",
                    "" + finalSubtotal,
                    priceRice + "_" + amtRice,
                    priceWheat + "_" + amtWheat,
                    priceSugar + "_" + amtSugar,
                    priceKerosene + "_" + amtKerosene);


            Toast.makeText(UserInventoryPage.this, "date and time-slot successfully booked", Toast.LENGTH_SHORT).show();
            utilHelper.hideProgressBar();

            Intent intent = new Intent(UserInventoryPage.this, PaymentResultPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(UserInventoryPage.this, materialButtonCashPayment, "transition_qr_page");
            startActivity(intent, options.toBundle());
        });

    }


    private String formatFloat_to2Decimal(float f) {
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.DOWN);
        return df.format(f);
    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(UserInventoryPage.this);
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
            Intent intent = new Intent(UserInventoryPage.this, LoginPage.class);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(UserInventoryPage.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            UserInventoryPage.this.finish();
        });

        dialog.show();
    }

}