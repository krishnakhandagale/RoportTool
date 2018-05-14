package com.electivechaos.claimsadjuster.ui;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.electivechaos.claimsadjuster.R;

/**
 * Created by krishna on 11/25/17.
 */

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_activity_layout);

        ImageView animatedView = findViewById(R.id.animated);
        Drawable animation = animatedView.getDrawable();
        if (animation instanceof Animatable) {
            ((Animatable) animation).start();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this,MainTabsActivity.class);
                startActivity(i);
                finish();
            }
        }, 4000);
    }
}
