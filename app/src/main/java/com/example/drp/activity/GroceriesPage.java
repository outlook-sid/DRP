package com.example.drp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.example.drp.R;
import com.example.drp.database.UserSessionManager;
import com.example.drp.helpers.UtilHelper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GroceriesPage extends AppCompatActivity {

    public static final String ITEM_NAME_RICE = "Rice";
    public static final String ITEM_NAME_WHEAT = "Wheat";
    public static final String ITEM_NAME_SUGAR = "Sugar";
    public static final String ITEM_NAME_KEROSENE = "Kerosene";

    private int itemCount;
    private String priceRice;
    private String selectedRice;
    private String priceWheat;
    private String selectedWheat;
    private String priceSugar;
    private String selectedSugar;
    private String priceKerosene;
    private String selectedKerosene;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries_page);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_dashboard_card_2));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        TextView textViewRiceSliderValue, textViewWheatSliderValue, textViewSugarSliderValue, textViewKeroseneSliderValue;

        textViewRiceSliderValue = findViewById(R.id.tv_rice_amount_in_unit);
        textViewWheatSliderValue = findViewById(R.id.tv_wheat_amount_in_unit);
        textViewSugarSliderValue = findViewById(R.id.tv_sugar_amount_in_unit);
        textViewKeroseneSliderValue = findViewById(R.id.tv_kerosene_amount_in_unit);

        TextView textViewRicePrice = findViewById(R.id.tv_rice_price_per_unit);
        TextView textViewWheatPrice = findViewById(R.id.tv_wheat_price_per_unit);
        TextView textViewSugarPrice = findViewById(R.id.tv_sugar_price_per_unit);
        TextView textViewKerosenePrice = findViewById(R.id.tv_kerosene_price_per_unit);

        final String selectedDate = UtilHelper.extract_year_month_date(Objects.requireNonNull(getIntent().getStringExtra("User_Selected_Date")));
        final String selectedTimeSlot = getIntent().getStringExtra("User_Selected_TimeSlot");
        priceRice = getIntent().getStringExtra("Price_of_Rice");
        String maxRice = getIntent().getStringExtra("Max_Rice_Per_Person");
        priceWheat = getIntent().getStringExtra("Price_of_Wheat");
        String maxWheat = getIntent().getStringExtra("Max_Wheat_Per_Person");
        priceSugar = getIntent().getStringExtra("Price_of_Sugar");
        String maxSugar = getIntent().getStringExtra("Max_Sugar_Per_Person");
        priceKerosene = getIntent().getStringExtra("Price_of_Kerosene");
        String maxKerosene = getIntent().getStringExtra("Max_Kerosene_Per_Person");
        String cardCount = getIntent().getStringExtra("Card_count");

        textViewRicePrice.setText(getString(R.string.grocery_price_in_inr, priceRice));
        textViewWheatPrice.setText(getString(R.string.grocery_price_in_inr, priceWheat));
        textViewSugarPrice.setText(getString(R.string.grocery_price_in_inr,priceSugar));
        textViewKerosenePrice.setText(getString(R.string.grocery_price_in_inr, priceKerosene));

        //__________________________ExtendedFloatingActionButton____________________________________
        final ExtendedFloatingActionButton extendedFloatingActionButton = findViewById(R.id.FAB_continue_btn);
        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_layout);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY + 10 && extendedFloatingActionButton.isExtended()) {
                extendedFloatingActionButton.shrink();
            }
            if (scrollY < oldScrollY - 10 && !extendedFloatingActionButton.isExtended()) {
                extendedFloatingActionButton.extend();
            }
            if (scrollY == 0) {
                extendedFloatingActionButton.extend();
            }
        });
        //__________________________________________________________________________________________


        //__________________________SLIDERS_________________________________________________________
        //......................Slider for Rice.....................................................
        Slider sliderRice = findViewById(R.id.slider_rice);
        assert maxRice != null;
        assert cardCount != null;
        float mr = (Float.parseFloat(maxRice) * (1.0F + Float.parseFloat(cardCount)));
        float sr = (float) ((mr - 0.0) / 20);
        sliderRice.setValueFrom(0F);
        sliderRice.setValueTo(mr);
        sliderRice.setStepSize(sr);
        sliderRice.setValue(0F);
        sliderRice.setLabelFormatter(value -> value + "Kg");

/*
        slider1.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                // Responds to when slider's touch event is being started
            }
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // Responds to when slider's touch event is being stopped
            }
        });
*/

        sliderRice.addOnChangeListener((slider, value, fromUser) -> {
            // Responds to when slider's value is changed
            textViewRiceSliderValue.setText(getString(R.string.grocery_qty_in_Kg, value));

        });

        //......................Slider for Wheat....................................................
        Slider sliderWheat = findViewById(R.id.slider_wheat);
        assert maxWheat != null;
        float mw = (Float.parseFloat(maxWheat) * (1.0F + Float.parseFloat(cardCount)));
        float sw = (float) ((mw - 0.0) / 20);
        sliderWheat.setValueFrom(0F);
        sliderWheat.setValueTo(mw);
        sliderWheat.setStepSize(sw);
        sliderWheat.setValue(0F);
        sliderWheat.setLabelFormatter(value -> value + "Kg");
        sliderWheat.addOnChangeListener((slider, value, fromUser) -> {
            // Responds to when slider's value is changed
            textViewWheatSliderValue.setText(getString(R.string.grocery_qty_in_Kg, value));

        });

        //......................Slider for Sugar....................................................
        Slider sliderSugar = findViewById(R.id.slider_sugar);
        assert maxSugar != null;
        float ms = (Float.parseFloat(maxSugar) * (1.0F + Float.parseFloat(cardCount)));
        float ss = (float) ((ms - 0.0) / 20);
        sliderSugar.setValueFrom(0F);
        sliderSugar.setValueTo(ms);
        sliderSugar.setStepSize(ss);
        sliderSugar.setValue(0F);
        sliderSugar.setLabelFormatter(value -> value + "Kg");
        sliderSugar.addOnChangeListener((slider, value, fromUser) -> {
            // Responds to when slider's value is changed
            textViewSugarSliderValue.setText(getString(R.string.grocery_qty_in_Kg, value));

        });

        //......................Slider for Kerosene.................................................
        Slider sliderKerosene = findViewById(R.id.slider_Kerosene);
        assert maxKerosene != null;
        float mk = (Float.parseFloat(maxKerosene) * (1.0F + Float.parseFloat(cardCount)));
        float sk = (float) ((mk - 0.0) / 20);
        sliderKerosene.setValueFrom(0F);
        sliderKerosene.setValueTo(mk);
        sliderKerosene.setStepSize(sk);
        sliderKerosene.setValue(0F);
        sliderKerosene.setLabelFormatter(value -> value + "Liter");
        sliderKerosene.addOnChangeListener((slider, value, fromUser) -> {
            // Responds to when slider's value is changed
            textViewKeroseneSliderValue.setText(getString(R.string.grocery_qty_in_liters, value));

        });
        //__________________________________________________________________________________________


        extendedFloatingActionButton.setOnClickListener(v -> {

            if (!UtilHelper.isConnected(GroceriesPage.this)) {
                showOfflineDialog();
                return;
            }

            itemCount = 0;
            selectedRice = "0.0";
            selectedWheat = "0.0";
            selectedSugar = "0.0";
            selectedKerosene = "0.0";

            HashMap<String, Boolean> hmItems = new HashMap<>(4);

            hmItems.put(ITEM_NAME_RICE, isRequired(textViewRiceSliderValue));
            if (isRequired(textViewRiceSliderValue)) {
                selectedRice = parseIntsAndFloats(textViewRiceSliderValue.getText().toString());
                itemCount++;
            }
            hmItems.put(ITEM_NAME_WHEAT, isRequired(textViewWheatSliderValue));
            if (isRequired(textViewWheatSliderValue)) {
                selectedWheat = parseIntsAndFloats(textViewWheatSliderValue.getText().toString());
                itemCount++;
            }
            hmItems.put(ITEM_NAME_SUGAR, isRequired(textViewSugarSliderValue));
            if (isRequired(textViewSugarSliderValue)) {
                selectedSugar = parseIntsAndFloats(textViewSugarSliderValue.getText().toString());
                itemCount++;
            }
            hmItems.put(ITEM_NAME_KEROSENE, isRequired(textViewKeroseneSliderValue));
            if (isRequired(textViewKeroseneSliderValue)) {
                selectedKerosene = parseIntsAndFloats(textViewKeroseneSliderValue.getText().toString());
                itemCount++;
            }

            if (itemCount == 0) {
                Toast.makeText(GroceriesPage.this, "You haven't selected any item to buy", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(GroceriesPage.this, UserInventoryPage.class);
            intent.putExtra("Price_Rice", priceRice);
            intent.putExtra("Selected_Rice", selectedRice);
            intent.putExtra("Price_Wheat", priceWheat);
            intent.putExtra("Selected_Wheat", selectedWheat);
            intent.putExtra("Price_Sugar", priceSugar);
            intent.putExtra("Selected_Sugar", selectedSugar);
            intent.putExtra("Price_Kerosene", priceKerosene);
            intent.putExtra("Selected_Kerosene", selectedKerosene);
            intent.putExtra("Items_Chosen", hmItems);
            intent.putExtra("Item_Count", itemCount);
            intent.putExtra("Selected_Date", selectedDate);
            intent.putExtra("Selected_TimeSlot", selectedTimeSlot);
            startActivity(intent);
        });
    }

    private boolean isRequired(TextView textView) {
        String s = textView.getText().toString();
        return !(s.equals("0.0 Kg") | s.equals("0.0 Liter") | s.equals("not required"));
    }

    private void showOfflineDialog() {
        Dialog dialog = new Dialog(GroceriesPage.this);
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
            Intent intent = new Intent(GroceriesPage.this, LoginPage.class);
            startActivity(intent);
            UserSessionManager userSessionManager = new UserSessionManager(GroceriesPage.this);
            userSessionManager.logoutUserFromSession();
            //finish();
            //finishAffinity();
            //System.exit(0);
            GroceriesPage.this.finish();
        });

        dialog.show();
    }

    private String parseIntsAndFloats(String raw) {
        ArrayList<String> listBuffer = new ArrayList<>();
        Pattern p = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher m = p.matcher(raw);
        while (m.find()) {
            listBuffer.add(m.group());
        }
        return listBuffer.get(0);
    }


}