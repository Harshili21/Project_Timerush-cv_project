package com.timerush;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 10);

        TextView tvScore        = findViewById(R.id.tv_score);
        TextView tvMessage      = findViewById(R.id.tv_message);
        TextView tvSubMessage   = findViewById(R.id.tv_sub_message);
        TextView tvEmoji        = findViewById(R.id.tv_emoji);
        Button   btnPlayAgain   = findViewById(R.id.btn_play_again);
        Button   btnHome        = findViewById(R.id.btn_home);
        View     scoreCard      = findViewById(R.id.score_card);

        tvScore.setText(score + "/" + total);

        // Set message based on score
        int percentage = (score * 100) / total;
        if (percentage >= 80) {
            tvMessage.setText("Excellent! 🏆");
            tvSubMessage.setText("You're a Quiz Master!");
            tvEmoji.setText("🌟");
        } else if (percentage >= 60) {
            tvMessage.setText("Good Job! 👏");
            tvSubMessage.setText("Keep pushing forward!");
            tvEmoji.setText("😊");
        } else if (percentage >= 40) {
            tvMessage.setText("Not Bad! 💪");
            tvSubMessage.setText("Practice makes perfect!");
            tvEmoji.setText("📚");
        } else {
            tvMessage.setText("Try Again! 🔄");
            tvSubMessage.setText("Every expert was once a beginner.");
            tvEmoji.setText("💡");
        }

        // Animate score card
        Animation bounceIn = AnimationUtils.loadAnimation(this, R.anim.bounce_in);
        scoreCard.startAnimation(bounceIn);

        Animation fadeInUp = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        fadeInUp.setStartOffset(400);
        btnPlayAgain.startAnimation(fadeInUp);

        Animation fadeInUp2 = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        fadeInUp2.setStartOffset(600);
        btnHome.startAnimation(fadeInUp2);

        btnPlayAgain.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, QuizActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });
    }
}
