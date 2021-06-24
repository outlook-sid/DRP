package com.example.drp.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.drp.R;

public class SplashScreen extends AppCompatActivity {

    private ImageView logoImage;
    private TextView startupText_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logoImage = findViewById(R.id.logo_image);
        ImageView startupTopImage = findViewById(R.id.startup_top_image_1);
        startupText_1 = findViewById(R.id.start_screen_text_1);
        TextView startupText_2 = findViewById(R.id.start_screen_text_2);

        Animation startAnimation1 = AnimationUtils.loadAnimation(this, R.anim.startup_top_animation);
        Animation startAnimation2 = AnimationUtils.loadAnimation(this, R.anim.startup_middle_animation);
        Animation startAnimation3 = AnimationUtils.loadAnimation(this, R.anim.startup_bottom_annimation);

        startupTopImage.setAnimation(startAnimation1);
        logoImage.setAnimation(startAnimation2);
        startupText_1.setAnimation(startAnimation3);
        startupText_2.setAnimation(startAnimation2);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, LoginPage.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
                    Pair.create(logoImage, "transition_Image"),
                    Pair.create(startupText_1, "transition_Text"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent, options.toBundle());
            //finishAffinity();
        },2000);
    }
}