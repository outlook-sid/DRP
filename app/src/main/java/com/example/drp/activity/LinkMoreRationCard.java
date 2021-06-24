package com.example.drp.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.drp.R;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.FieldHelper;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LinkMoreRationCard extends AppCompatActivity {

    private final int MAX_CARD_COUNT = 4;
    private int cardCount;
    private TextInputLayout[] cardID_etl = new TextInputLayout[MAX_CARD_COUNT];
    private String[] cardsArray = new String[MAX_CARD_COUNT];
    private ConstraintLayout superLayout;
    private UtilHelper utilHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_more_ration_card);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_dashboard_card_1));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TextInputLayout cardID_etl0 = findViewById(R.id.link_card_card0_wrapper);

        superLayout = findViewById(R.id.link_more_ration_card_super_c_layout);
        TextView messageText = findViewById(R.id.message_text_view);
        ImageButton next_btn = (ImageButton) findViewById(R.id.link_card_next_btn);

        cardID_etl[0] = findViewById(R.id.link_card_card1_wrapper);
        cardID_etl[1] = findViewById(R.id.link_card_card2_wrapper);
        cardID_etl[2] = findViewById(R.id.link_card_card3_wrapper);
        cardID_etl[3] = findViewById(R.id.link_card_card4_wrapper);


        UserSessionManager userSessionManager = new UserSessionManager(LinkMoreRationCard.this);
        HashMap<String, String> userData = userSessionManager.getUserDetailsFromSession();
        List<String> alreadyLinkedCardList = userSessionManager.getUserLinkedCardsList();
        String linkedCardCount = userData.get(UserSessionManager.KEY_LINKED_CARD_COUNT);

        Objects.requireNonNull(cardID_etl0.getEditText()).setText(userData.get(UserSessionManager.KEY_RATION_CARD));
        cardID_etl0.getEditText().setKeyListener(null);                          // to make the edit text un-editable
        cardID_etl0.setEndIconMode(TextInputLayout.END_ICON_NONE);

        assert linkedCardCount != null;
        cardCount = Integer.parseInt(linkedCardCount);
        if (cardCount == 0) {
            messageText.setText(getString(R.string.one_ration_card_found));
        } else
            messageText.setText(getString(R.string.linkRationCard_multiple_ration_card_found,Integer.parseInt(linkedCardCount) + 1));

        for (int k = 0; k < cardCount; k++) {
            cardID_etl[k].setVisibility(View.VISIBLE);
            cardsArray[k] = alreadyLinkedCardList.get(k);
            Objects.requireNonNull(cardID_etl[k].getEditText()).setText(alreadyLinkedCardList.get(k));
            Objects.requireNonNull(cardID_etl[k].getEditText()).setKeyListener(null);                          // to make the edit text un-editable
            cardID_etl[k].setEndIconMode(TextInputLayout.END_ICON_NONE);                                       //      "               "
        }

        if (Integer.parseInt(linkedCardCount) == MAX_CARD_COUNT) {
            Toast.makeText(LinkMoreRationCard.this, "No more ration cards can be added", Toast.LENGTH_SHORT).show();
            next_btn.setVisibility(View.GONE);
        } else cardID_etl[cardCount].setVisibility(View.VISIBLE);


        next_btn.setOnClickListener(v -> {

            utilHelper = new UtilHelper(LinkMoreRationCard.this);

            if (!UtilHelper.isConnected(LinkMoreRationCard.this)) {
                showOfflineDialog();
                return;
            }

            if (cardCount == MAX_CARD_COUNT) {
                return;
            }
            utilHelper.showProgressBar();

            if (FieldHelper.validateUserCardID(cardID_etl[cardCount]) && FieldHelper.isRepeatingTextInputEditText(cardID_etl, cardCount)) {

                final String newCardID = Objects.requireNonNull(cardID_etl[cardCount].getEditText()).getText().toString();
                Query rationCardNode = FirebaseDatabase.getInstance().getReference("Ration_Cards").orderByChild("cardNo").equalTo(newCardID);
                rationCardNode.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(newCardID).exists()) {
                            cardID_etl[cardCount].setErrorEnabled(true);
                            cardID_etl[cardCount].setError("already linked to another account");
                            utilHelper.hideProgressBar();
                            return;
                        }

                        Query masterRationCardNode = FirebaseDatabase.getInstance().getReference("Master_Ration_Cards").orderByChild("cardNo").equalTo(newCardID);
                        masterRationCardNode.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child(newCardID).exists()) {
                                    String linkedPhone_toNewCard = snapshot.child(newCardID).child("userPhone").getValue(String.class);
                                    String cardholderName = snapshot.child(newCardID).child("userName").getValue(String.class);
                                    Intent intent = new Intent(LinkMoreRationCard.this, VerifyRationCard.class);
                                    intent.putExtra("new_User_SignUp", false);
                                    intent.putExtra("card_count", String.valueOf(cardCount));
                                    intent.putExtra("card_array", cardsArray);
                                    intent.putExtra("new_card", newCardID);
                                    intent.putExtra("linked_phone_number", linkedPhone_toNewCard);
                                    intent.putExtra("new_cardholder_name", cardholderName);
                                    Log.v("______NOTE______ ", "holder name  " + cardholderName);
                                    startActivity(intent);
                                    utilHelper.hideProgressBar();
                                    utilHelper = null;
                                    finish();
                                } else {
                                    Toast.makeText(LinkMoreRationCard.this, "Invalid ration card number", Toast.LENGTH_SHORT).show();
                                    utilHelper.hideProgressBar();
                                    utilHelper = null;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            } else {
                utilHelper.hideProgressBar();
                utilHelper = null;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LinkMoreRationCard.this, UserDashboardPage.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LinkMoreRationCard.this, superLayout, "transition_card_1");
        startActivity(intent, options.toBundle());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(LinkMoreRationCard.this);
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
            Intent intent = new Intent(LinkMoreRationCard.this, LoginPage.class);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(LinkMoreRationCard.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            LinkMoreRationCard.this.finish();
        });

        dialog.show();
    }
}
