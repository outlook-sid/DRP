package com.example.drp.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.drp.R;
import com.example.drp.database.QRCodeSessionManager;
import com.example.drp.database.TransactionManager;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class NewTransactionPage extends AppCompatActivity {

    private TextView dateText, timeSlotText;
    private MaterialButton datePickerButton, timeSlotPickerButton, proceedButton;
    private UtilHelper utilHelper;
    private String priceRice, maxRice, priceWheat, maxWheat, priceSugar, maxSugar, priceKerosene, maxKerosene;
    private String dealerID, cardCount;
    private List<String> timeSlots;
    private UserSessionManager userSessionManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction_page);
        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_dashboard_card_2));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        dateText = findViewById(R.id.txt_date_trans);
        timeSlotText = findViewById(R.id.picked_time_slot_tv);
        datePickerButton = findViewById(R.id.date_select_button);
        timeSlotPickerButton = findViewById(R.id.time_slot_select_button);
        proceedButton = findViewById(R.id.date_time_confirm_button);

        TextView textViewTransactionMessage = findViewById(R.id.tv_new_trans_info);
        QRCodeSessionManager qrCodeSessionManager = new QRCodeSessionManager(NewTransactionPage.this);
        if (qrCodeSessionManager.checkIncompleteTransaction()) {
            textViewTransactionMessage.setText(getString(R.string.ration_not_collected_yet));
            datePickerButton.setVisibility(View.GONE);
        }

        userSessionManager = new UserSessionManager(NewTransactionPage.this);
        HashMap<String, String> userData = userSessionManager.getUserDetailsFromSession();

        // ----------------------Setting the  Material date picker for booking-------------------------
        long today = MaterialDatePicker.todayInUtcMilliseconds();
        MaterialDatePicker.Builder<Long> datePickerBuilder = MaterialDatePicker.Builder.datePicker();
        datePickerBuilder.setTitleText("Select Date");
        Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utc.add(Calendar.DAY_OF_MONTH, 0);
        long lowerBound = utc.getTimeInMillis();                                                 // disables date up to today
        utc.clear();
        utc.setTimeInMillis(today);
        utc.roll(Calendar.MONTH, 2);
        long upperBound = utc.getTimeInMillis();                                                 // sets the max bound to next 2 month
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setEnd(upperBound);
        constraintsBuilder.setValidator(DateValidatorPointForward.from(lowerBound));
        datePickerBuilder.setCalendarConstraints(constraintsBuilder.build());
        final MaterialDatePicker<Long> datePicker = datePickerBuilder.build();
        datePicker.setCancelable(false);
        //------------------------------------------------------------------------------------------

        datePickerButton.setOnClickListener(v -> {
            if (!UtilHelper.isConnected(NewTransactionPage.this)) {
                showOfflineDialog();
                return;
            }
            datePicker.show(getSupportFragmentManager(), "DATE");
            datePickerButton.setClickable(false);
            dateText.setVisibility(View.GONE);
            dateText.setText(null);
            timeSlotPickerButton.setVisibility(View.GONE);
            timeSlotText.setVisibility(View.GONE);
            timeSlotText.setText(null);
            proceedButton.setVisibility(View.GONE);
        });

        datePicker.addOnPositiveButtonClickListener(selection -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.clear();
            cal.setTimeInMillis(selection);
            int yyyy = cal.get(Calendar.YEAR);
            int mm = 1 + cal.get(Calendar.MONTH);
            int dd = cal.get(Calendar.DATE);
            String sdt = dtf.format(LocalDate.of(yyyy, mm, dd));
            dateText.setVisibility(View.VISIBLE);
            dateText.setText(UtilHelper.prettyDate(sdt));
            //dateText.setText(datePicker.getHeaderText());
            timeSlotPickerButton.setVisibility(View.VISIBLE);
            datePickerButton.setClickable(true);
        });
        datePicker.addOnNegativeButtonClickListener(v -> datePickerButton.setClickable(true));

        timeSlotPickerButton.setOnClickListener(v -> {
            utilHelper = new UtilHelper(NewTransactionPage.this);

            if (!UtilHelper.isConnected(NewTransactionPage.this)) {
                showOfflineDialog();
                return;
            }
            utilHelper.showProgressBar();

            String dealerID = userData.get(UserSessionManager.KEY_DEALER_ID);
            timeSlotPickerButton.setClickable(false);
            List<String> keysTimeSlots = new ArrayList<>();
            assert dealerID != null;
            Query transactionCheckQuery = FirebaseDatabase.getInstance().getReference("Transaction_Checker").child(dealerID);
            String bookDate = UtilHelper.extract_year_month_date(dateText.getText().toString());
            transactionCheckQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot snapshot = dataSnapshot.child(bookDate);
                    for (DataSnapshot s : snapshot.getChildren()) {
                        String key = s.getKey();
                        keysTimeSlots.add(key);
                    }
                    utilHelper.hideProgressBar();
                    showTimeSlotDialog(keysTimeSlots);
                    utilHelper = null;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

        proceedButton.setOnClickListener(v -> {

            utilHelper = new UtilHelper(NewTransactionPage.this);

            if (!UtilHelper.isConnected(NewTransactionPage.this)) {
                showOfflineDialog();
                return;
            }
            utilHelper.showProgressBar();
            //----------------------------------------------------------------------------------
            final String selectedDate = UtilHelper.extractYear_Month(Objects.requireNonNull(dateText.getText().toString()));

            dealerID = userData.get(UserSessionManager.KEY_DEALER_ID);
            cardCount = userData.get(UserSessionManager.KEY_LINKED_CARD_COUNT);

            Query supply = FirebaseDatabase.getInstance().getReference("Ration_Supplies").orderByChild("ID").equalTo(dealerID);
            supply.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.child(dealerID).exists()) {
                        Toast.makeText(NewTransactionPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        return;
                    }
                    if (!snapshot.child(dealerID).child("Month").child(selectedDate).exists()) {
                        Toast.makeText(NewTransactionPage.this, "This month's supply is not yet available.", Toast.LENGTH_SHORT).show();
                        utilHelper.hideProgressBar();
                        return;
                    }
                    DataSnapshot supplySnapshot = snapshot.child(dealerID).child("Month").child(selectedDate);
                    priceRice = supplySnapshot.child("Rice").child("Price").getValue(String.class);
                    maxRice = supplySnapshot.child("Rice").child("MaxPerHead").getValue(String.class);
                    priceWheat = supplySnapshot.child("Wheat").child("Price").getValue(String.class);
                    maxWheat = supplySnapshot.child("Wheat").child("MaxPerHead").getValue(String.class);
                    priceSugar = supplySnapshot.child("Sugar").child("Price").getValue(String.class);
                    maxSugar = supplySnapshot.child("Sugar").child("MaxPerHead").getValue(String.class);
                    priceKerosene = supplySnapshot.child("Kerosene").child("Price").getValue(String.class);
                    maxKerosene = supplySnapshot.child("Kerosene").child("MaxPerHead").getValue(String.class);

                    Intent intent = new Intent(NewTransactionPage.this, GroceriesPage.class);
                    intent.putExtra("User_Selected_Date", dateText.getText().toString());
                    intent.putExtra("User_Selected_TimeSlot", timeSlotText.getText().toString());
                    intent.putExtra("Price_of_Rice", priceRice);
                    intent.putExtra("Max_Rice_Per_Person", maxRice);
                    intent.putExtra("Price_of_Wheat", priceWheat);
                    intent.putExtra("Max_Wheat_Per_Person", maxWheat);
                    intent.putExtra("Price_of_Sugar", priceSugar);
                    intent.putExtra("Max_Sugar_Per_Person", maxSugar);
                    intent.putExtra("Price_of_Kerosene", priceKerosene);
                    intent.putExtra("Max_Kerosene_Per_Person", maxKerosene);
                    intent.putExtra("Dealer_ID", dealerID);
                    intent.putExtra("Card_count", cardCount);

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(NewTransactionPage.this, proceedButton, "transition_groceries_page");
                    startActivity(intent, options.toBundle());
                    utilHelper.hideProgressBar();
                    utilHelper = null;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            //----------------------------------------------------------------------------------

        });

    }

    @Override
    public void onBackPressed() {
        ConstraintLayout parentLayout = findViewById(R.id.new_transaction_parentmost);
        Intent intent = new Intent(NewTransactionPage.this, UserDashboardPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(NewTransactionPage.this, parentLayout, "transition_card_2");
        startActivity(intent, options.toBundle());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    private void showTimeSlotDialog(List<String> list) {
        timeSlots = new TransactionManager().getTimeSlotList(list);                                     //ListView for time slot selection
        ArrayAdapter<String> timeSlotsAdapter = new ArrayAdapter<>(this, R.layout.custom_list_layout_1, R.id.list_txt, timeSlots);

        Dialog dialog = new Dialog(NewTransactionPage.this);
        dialog.setContentView(R.layout.layout_dialog_time_slot);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        ListView timeSlotLV = dialog.findViewById(R.id.list_view_time_slot_picker);
        timeSlotLV.setAdapter(timeSlotsAdapter);
        timeSlotLV.setOnItemClickListener((parent, view, position, id) -> {
            //                Toast.makeText(NewTransactionPage.this,timeSlots.get(position), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            timeSlotText.setVisibility(View.VISIBLE);
            timeSlotText.setText(timeSlots.get(position));
            proceedButton.setVisibility(View.VISIBLE);
            timeSlotPickerButton.setClickable(true);
        });
        dialog.show();

    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(NewTransactionPage.this);
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
            Intent intent = new Intent(NewTransactionPage.this, LoginPage.class);
            startActivity(intent);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            NewTransactionPage.this.finish();
        });

        dialog.show();
    }
}