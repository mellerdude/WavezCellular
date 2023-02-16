package com.example.wavezcellular.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.wavezcellular.R;

public class SplashActivity extends AppCompatActivity {
    private ImageView logo;
    private LottieAnimationView lottie,lottie2;
    private final int ANIM_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        logo.setVisibility(View.INVISIBLE);
        lottie = findViewById(R.id.lottie);
        lottie2 = findViewById(R.id.lottie2);
        showViewSlideDown(logo);
    }

    public void showViewSlideDown(final View v) {
        v.setVisibility(View.VISIBLE);
        Path path = new Path();
        path.arcTo(0f, 0f, 400f, 500f, 270f, -180f, true);
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, View.X, View.Y, path);
        animator.setDuration(ANIM_DURATION);
        animator.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.cancel();
                replaceActivity();
            }
        }, 5000);
    }

    private void replaceActivity() {
        Intent intent = new Intent(this, MenuActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}