package com.example.nutrieye;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseApp;

public class SplashScreen extends AppCompatActivity {

    ImageView logo, appName, splashImg, tagLine;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initializeViews();

        animateViews();
    }

    private void initializeViews() {
        logo = findViewById(R.id.Logo);
        appName = findViewById(R.id.app_name);
        splashImg = findViewById(R.id.img);
        tagLine = findViewById(R.id.tag_line);
        lottieAnimationView = findViewById(R.id.lottie);
    }

    private void animateViews() {
        animateView(splashImg, -2600);
        animateView(logo, 2200);
        animateView(appName, 2200);
        animateView(tagLine, 2200);
        animateView(lottieAnimationView, 2200);
    }

    private void animateView(ImageView view, float translationY) {
        view.animate()
                .translationY(translationY)
                .setDuration(800)
                .setStartDelay(2000)
                .setListener(new SplashScreenAnimatorListenerAdapter(this));
    }

    private class SplashScreenAnimatorListenerAdapter extends AnimatorListenerAdapter {

        private SplashScreen activity;

        public SplashScreenAnimatorListenerAdapter(SplashScreen activity) {
            this.activity = activity;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            startLoginScreen();
        }

        private void startLoginScreen() {
            Intent intent = new Intent(activity, LoginScreen.class);
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
