package com.timerush;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView titleText = findViewById(R.id.tv_title);
        TextView subtitleText = findViewById(R.id.tv_subtitle);
        Button startButton = findViewById(R.id.btn_start);
        View logoIcon = findViewById(R.id.iv_logo);

        // Animate logo
        Animation fadeInDown = AnimationUtils.loadAnimation(this, R.anim.fade_in_down);
        logoIcon.startAnimation(fadeInDown);

        // Animate title
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn.setStartOffset(200);
        titleText.startAnimation(fadeIn);

        // Animate subtitle
        Animation fadeInUp = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        fadeInUp.setStartOffset(400);
        subtitleText.startAnimation(fadeInUp);

        // Animate button
        Animation fadeInUp2 = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        fadeInUp2.setStartOffset(600);
        startButton.startAnimation(fadeInUp2);

        startButton.setOnClickListener(v -> {
            String[] difficulties = {"Basic", "Standard"};
            new android.app.AlertDialog.Builder(MainActivity.this)
                .setTitle("Choose Difficulty Level")
                .setItems(difficulties, (difficultyDialog, difficultyIndex) -> {
                    String selectedDifficulty = difficulties[difficultyIndex];
                    
                    String[] categories = {"All Subjects", "Biology", "Math", "Science", "Chemistry", "English", "Physics", "Geography"};
                    new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle("Choose Subject")
                        .setItems(categories, (categoryDialog, which) -> {
                            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                            intent.putExtra("CATEGORY", categories[which]);
                            intent.putExtra("DIFFICULTY", selectedDifficulty);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        })
                        .show();
                })
                .show();
        });
    }
}
