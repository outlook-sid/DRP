package com.example.drp.activity;

import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.drp.R;
import com.example.drp.ReminderBroadcast;
import com.example.drp.database.QRCodeSessionManager;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.TransactionInfoModel;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;


public class PaymentResultPage extends AppCompatActivity {

    private HashMap<String, String> qrDetails;
    private QRCodeSessionManager qrCodeSession;
    private ProgressBar progressBar;
    private CardView cardViewGoBack;
    private ImageView qrImage;
    private MaterialButton materialButtonExit;
    private DatabaseReference userDatabaseReference;
    private Query query;
    private Calendar calendarSelectedDate;
    private String stringToastMessage;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_PureWhite)); // status bar color
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); //dark text of status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); //stop screen capture

        TextView textViewTransactionDate, textViewTransactionTime, textViewUserID, textViewUserName, textViewSubtotal;

        qrImage = (ImageView) findViewById(R.id.QR_image);
        textViewTransactionDate = findViewById(R.id.tv_payment_result_tran_date);
        textViewTransactionTime = findViewById(R.id.tv_payment_result_tran_time);
        textViewUserID = findViewById(R.id.tv_payment_result_userID);
        textViewUserName = findViewById(R.id.tv_payment_result_userName);
        textViewSubtotal = findViewById(R.id.tv_payment_result_tran_subtotal);
        materialButtonExit = (MaterialButton) findViewById(R.id.mb_payment_result_exit);
        cardViewGoBack = (CardView) findViewById(R.id.mcv_payment_result_go_back);

        progressBar = findViewById(R.id.pb_card_payment_result);
        progressBar.setVisibility(View.GONE);

        UserSessionManager userSession = new UserSessionManager(PaymentResultPage.this);
        HashMap<String, String> userData = userSession.getUserDetailsFromSession();

        qrCodeSession = new QRCodeSessionManager(PaymentResultPage.this);
        if (qrCodeSession.checkIncompleteTransaction()) {
            Log.v("_____NOTE______", "logged in");
        } else Log.v("_____NOTE____", "not logged in");

        qrDetails = qrCodeSession.getQRCodeDetailsFromSession();

        String[] strings = Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SELECTED_DATE)).split("_");
        Log.v("_____selected_date_string____", strings[0] + " / " + strings[1] + " / " + strings[2]);
        int day_ofMonth = Integer.parseInt(strings[2]);
        int month = Integer.parseInt(strings[1]);
        int year = Integer.parseInt(strings[0]);
        Log.v("____select_date_int____", day_ofMonth + " / " + month + " / " + year);

        calendarSelectedDate = Calendar.getInstance();
        calendarSelectedDate.set(Calendar.YEAR, year);
        calendarSelectedDate.set(Calendar.MONTH, month - 1);
        calendarSelectedDate.set(Calendar.DAY_OF_MONTH, day_ofMonth);

        textViewTransactionDate.setText(UtilHelper.prettyDate(Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SELECTED_DATE))));
        textViewTransactionTime.setText(qrDetails.get(QRCodeSessionManager.KEY_SELECTED_TIME_SLOT));
        textViewSubtotal.setText(getString(R.string.grocery_price_in_inr, qrDetails.get(QRCodeSessionManager.KEY_PAYMENT_SUBTOTAL)));
        textViewUserID.setText(userData.get(UserSessionManager.KEY_PHONE));
        textViewUserName.setText(userData.get(UserSessionManager.KEY_NAME));

        stringToastMessage = getString(R.string.ration_claimed);

        String phoneNo = userData.get(UserSessionManager.KEY_PHONE);
        String transactionCount = userData.get(UserSessionManager.KEY_TRANSACTION_COUNT);
        String transactionDate = qrDetails.get(QRCodeSessionManager.KEY_TRANSACTION_DATE);
        String selectedDate = qrDetails.get(QRCodeSessionManager.KEY_SELECTED_DATE);
        String selectedTimeSlot = qrDetails.get(QRCodeSessionManager.KEY_SELECTED_TIME_SLOT);
        String paymentMethod = qrDetails.get(QRCodeSessionManager.KEY_PAYMENT_METHOD);
        String finalSubtotal = qrDetails.get(QRCodeSessionManager.KEY_PAYMENT_SUBTOTAL);
        String[] ricePriceAmt = Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_RICE_PRICE_AMT)).split("_");
        String subtotalRice = String.valueOf(Float.parseFloat(ricePriceAmt[0]) * Float.parseFloat(ricePriceAmt[1]));
        String[] wheatPriceAmt = Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_WHEAT_PRICE_AMT)).split("_");
        String subtotalWheat = String.valueOf(Float.parseFloat(wheatPriceAmt[0]) * Float.parseFloat(wheatPriceAmt[1]));
        String[] SugarPriceAmt = Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SUGAR_PRICE_AMT)).split("_");
        String subtotalSugar = String.valueOf(Float.parseFloat(SugarPriceAmt[0]) * Float.parseFloat(SugarPriceAmt[1]));
        String[] kerosenePriceAmt = Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_KEROSENE_PRICE_AMT)).split("_");
        String subtotalKerosene = String.valueOf(Float.parseFloat(kerosenePriceAmt[0]) * Float.parseFloat(kerosenePriceAmt[1]));

        assert phoneNo != null;
        userDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(phoneNo);
        query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("userPhone").equalTo(userData.get(UserSessionManager.KEY_PHONE));

        materialButtonExit.setOnClickListener(v -> {

            if (!UtilHelper.isConnected(PaymentResultPage.this)) {
                showOfflineDialog();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            materialButtonExit.setVisibility(View.GONE);
            cardViewGoBack.setClickable(false);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    assert transactionCount != null;
                    DataSnapshot snapshot = dataSnapshot.child(phoneNo);
                    DataSnapshot ref = snapshot.child("userIncompleteTransactions");
                    Log.v("___key__", " " + ref.getKey());
                    if (!ref.exists()) {
                        qrCodeSession.logoutUserFromSession();

                        Toast.makeText(PaymentResultPage.this, stringToastMessage, Toast.LENGTH_LONG).show();

                        TransactionInfoModel transactionInfo = new TransactionInfoModel(
                                "" + transactionDate,
                                "" + paymentMethod,
                                "" + selectedDate,
                                "" + selectedTimeSlot,
                                "" + subtotalRice,
                                "" + subtotalWheat,
                                "" + subtotalSugar,
                                "" + subtotalKerosene,
                                "" + finalSubtotal);
                        userDatabaseReference.child("userTransactions").child(transactionCount).setValue(transactionInfo).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.v("_____NOTE____", "ERROR in user_tran_info");
                                Toast.makeText(PaymentResultPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                        userDatabaseReference.child("userTransactionCount").setValue(String.valueOf(Integer.parseInt(transactionCount) + 1)).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.v("_____NOTE____", "ERROR in tran_count");
                                Toast.makeText(PaymentResultPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else setReminder();

                    exitToDashboard();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.v("___121___", error.getMessage());
                    Log.v("___key___", "went wrong");
                }
            });
        });

        cardViewGoBack.setOnClickListener(v -> {

            if (!UtilHelper.isConnected(PaymentResultPage.this)) {
                showOfflineDialog();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            materialButtonExit.setVisibility(View.GONE);
            cardViewGoBack.setClickable(false);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    assert transactionCount != null;
                    DataSnapshot snapshot = dataSnapshot.child(phoneNo);
                    DataSnapshot ref = snapshot.child("userIncompleteTransactions");
                    Log.v("___key__", " " + ref.getKey());
                    if (!ref.exists()) {
                        qrCodeSession.logoutUserFromSession();
                        Toast.makeText(PaymentResultPage.this, "Transaction is complete", Toast.LENGTH_SHORT).show();

                        TransactionInfoModel transactionInfo = new TransactionInfoModel(
                                "" + transactionDate,
                                "" + paymentMethod,
                                "" + selectedDate,
                                "" + selectedTimeSlot,
                                "" + subtotalRice,
                                "" + subtotalWheat,
                                "" + subtotalSugar,
                                "" + subtotalKerosene,
                                "" + finalSubtotal);
                        userDatabaseReference.child("userTransactions").child(transactionCount).setValue(transactionInfo).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.v("_____NOTE____", "ERROR in user_tran_info  ");
                                Toast.makeText(PaymentResultPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                        userDatabaseReference.child("userTransactionCount").setValue(String.valueOf(Integer.parseInt(transactionCount) + 1)).addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Log.v("_____NOTE____", "ERROR in tran_count");
                                Toast.makeText(PaymentResultPage.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    exitToDashboard();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.v("___121___", error.getMessage());
                    Log.v("___key___", "went wrong");
                }
            });
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        String inputText = qrDetails.get(QRCodeSessionManager.KEY_ENCODED_QR_CODE);
        Log.v("_____NOTE______", "qr code " + inputText);

        QRGEncoder qrgEncoder = new QRGEncoder(inputText, null, QRGContents.Type.TEXT, 768);
        Bitmap qrBitmap = qrgEncoder.getBitmap();


        //time slot pattern: "10:00 am - 10:20 am"
        int hr, min;
        try {
            String timeLimit = Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SELECTED_TIME_SLOT)).split(" - ")[1];
            String[] hr_min_ = timeLimit.split(" ");
            String[] hr_min = hr_min_[0].split(":");
            if (hr_min_[1].equals("pm")) hr = Integer.parseInt(hr_min[0]) + 12;
            else hr = Integer.parseInt(hr_min[0]);
            min = Integer.parseInt(hr_min[1]);

            calendarSelectedDate.set(Calendar.HOUR_OF_DAY, hr);
            calendarSelectedDate.set(Calendar.MINUTE, min);
        } catch (RuntimeException e) {
            calendarSelectedDate.set(Calendar.HOUR_OF_DAY, 16);
            calendarSelectedDate.set(Calendar.MINUTE, 30);
        }
        calendarSelectedDate.set(Calendar.SECOND, 0);

        TextView textViewSpecialMessage = findViewById(R.id.tv_payment_result_instruction);

        if (calendarSelectedDate.getTimeInMillis() < System.currentTimeMillis()) {

            Log.v("___NOTE___", "not in time...!!!");
            materialButtonExit.setVisibility(View.GONE);
            Bitmap myLogo = BitmapFactory.decodeResource(getResources(), R.drawable.png_red_cross);
            Bitmap mergedBitmap = mergeLogoToBitmaps(myLogo, qrBitmap, 4);
            qrImage.setImageBitmap(mergedBitmap);
            textViewSpecialMessage.setText(getString(R.string.bill_no_more_valid));
            stringToastMessage = getString(R.string.ration_cannot_be_claimed);
            textViewSpecialMessage.setTextColor((ContextCompat.getColor(this, R.color.color_Red)));
            userDatabaseReference.child("userIncompleteTransactions").setValue(null);
        }
        else {
            Bitmap myLogo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_png_app_logo);
            Bitmap mergedBitmap = mergeLogoToBitmaps(myLogo, qrBitmap, 10);
            qrImage.setImageBitmap(mergedBitmap);
        }

    }

    @Override
    public void onBackPressed() {

        Dialog dialog = new Dialog(PaymentResultPage.this);
        dialog.setContentView(R.layout.layout_dialog_exit);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        FloatingActionButton fabExit = (FloatingActionButton) dialog.findViewById(R.id.fab_exit_from_app);
        fabExit.setOnClickListener(vi -> {
            dialog.dismiss();
            getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //LoginPage.this.finish();
            finishAffinity();
            //System.exit(0);
        });

        dialog.show();

    }

    private void exitToDashboard() {

        progressBar.setVisibility(View.GONE);
        materialButtonExit.setClickable(true);
        cardViewGoBack.setClickable(true);

        Intent intent = new Intent(PaymentResultPage.this, UserDashboardPage.class);
        ConstraintLayout constraintLayout = findViewById(R.id.cl_payment_result);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PaymentResultPage.this, constraintLayout, "transition_qr_page");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent, options.toBundle());
    }

    private Bitmap mergeLogoToBitmaps(Bitmap logo, Bitmap qrImg, int scaleDown) {

        Bitmap combined = Bitmap.createBitmap(qrImg.getWidth(), qrImg.getHeight(), qrImg.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        canvas.drawBitmap(qrImg, new Matrix(), null);

        Bitmap resizeLogo = Bitmap.createScaledBitmap(logo, canvasWidth / scaleDown, canvasHeight / scaleDown, true);
        int centreX = (canvasWidth - resizeLogo.getWidth()) / 2;
        int centreY = (canvasHeight - resizeLogo.getHeight()) / 2;
        canvas.drawBitmap(resizeLogo, centreX, centreY, null);
        return combined;
    }

    private void setReminder() {

        Log.v("_____CurrentTimeInMillis____", "today in milis = " + System.currentTimeMillis());
        calendarSelectedDate.set(Calendar.HOUR_OF_DAY, 7);
        calendarSelectedDate.set(Calendar.MINUTE, 30);
        calendarSelectedDate.set(Calendar.SECOND, 0);

        String notificationTimeInstruction = " from " + Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SELECTED_TIME_SLOT)).split(" - ")[0]
                + " to " + Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SELECTED_TIME_SLOT)).split(" - ")[1];
        String notificationTitle = "Payment: " + qrDetails.get(QRCodeSessionManager.KEY_PAYMENT_METHOD) + " - ₹" + qrDetails.get(QRCodeSessionManager.KEY_PAYMENT_SUBTOTAL);
        String notificationBody = UtilHelper.prettyDate(Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SELECTED_DATE))) + notificationTimeInstruction;
        String notificationBigText = UtilHelper.prettyDate(Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SELECTED_DATE))) + notificationTimeInstruction + ", Items : \n"
                + "Rice : " + Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_RICE_PRICE_AMT)).split("_")[1] + "Kg\n"
                + "Wheat : " + Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_WHEAT_PRICE_AMT)).split("_")[1] + "Kg\n"
                + "Sugar : " + Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_SUGAR_PRICE_AMT)).split("_")[1] + "Kg\n"
                + "Kerosene : " + Objects.requireNonNull(qrDetails.get(QRCodeSessionManager.KEY_KEROSENE_PRICE_AMT)).split("_")[1] + "Liter";

        Log.v("___notification_big____", notificationBigText);

        Intent intentReminderBroadcast = new Intent(getApplicationContext(), ReminderBroadcast.class);
        intentReminderBroadcast.putExtra("title", notificationTitle);
        intentReminderBroadcast.putExtra("body", notificationBody);
        intentReminderBroadcast.putExtra("big", notificationBigText);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intentReminderBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendarSelectedDate.getTimeInMillis(), pendingIntent);
        }

    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(PaymentResultPage.this);
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
            Intent intent = new Intent(PaymentResultPage.this, LoginPage.class);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(PaymentResultPage.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            PaymentResultPage.this.finish();
        });

        dialog.show();
    }


}