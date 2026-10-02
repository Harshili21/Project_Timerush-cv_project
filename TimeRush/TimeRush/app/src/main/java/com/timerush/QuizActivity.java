package com.timerush;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion, tvTimer, tvProgress, tvSubject, tvMotivation;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;
    private Button btnPrev, btnSkipUnvisited, btnExit;
    private ProgressBar progressBar, timerBar;
    private CardView cardQuestion;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int selectedOption = -1;
    private CountDownTimer countDownTimer;
    private static final int TIME_PER_QUESTION = 15;
    private long timeLeft;

    // Track state per question
    private boolean[] visited;   // true = answered or timed out
    private int[] userAnswers;   // selected option index, -1 = timed out/skipped

    private static final String[] MOTIVATIONAL_TEXTS = {
        "Keep going! 🔥", "You're on fire! ⚡", "Almost there! 💪",
        "Stay focused! 🎯", "Great job! ⭐", "Don't stop now! 🚀"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        initViews();
        loadQuestions();
        displayQuestion();
    }

    private void initViews() {
        tvQuestion       = findViewById(R.id.tv_question);
        tvTimer          = findViewById(R.id.tv_timer);
        tvProgress       = findViewById(R.id.tv_progress);
        tvSubject        = findViewById(R.id.tv_subject);
        tvMotivation     = findViewById(R.id.tv_motivation);
        btnOption1       = findViewById(R.id.btn_option1);
        btnOption2       = findViewById(R.id.btn_option2);
        btnOption3       = findViewById(R.id.btn_option3);
        btnOption4       = findViewById(R.id.btn_option4);

        btnPrev          = findViewById(R.id.btn_prev);
        btnSkipUnvisited = findViewById(R.id.btn_skip_unvisited);
        btnExit          = findViewById(R.id.btn_exit_quiz);
        progressBar      = findViewById(R.id.progress_bar);
        timerBar         = findViewById(R.id.timer_bar);
        cardQuestion     = findViewById(R.id.card_question);        // PREV: go to previous question
        btnPrev.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                if (countDownTimer != null) countDownTimer.cancel();
                currentQuestionIndex--;
                displayQuestion();
            }
        });

        // SKIP UNVISITED: jump to next unanswered question
        btnSkipUnvisited.setOnClickListener(v -> {
            int next = -1;
            // Search from current+1 to end
            for (int i = currentQuestionIndex + 1; i < questions.size(); i++) {
                if (!visited[i]) { next = i; break; }
            }
            // Wrap around from beginning
            if (next == -1) {
                for (int i = 0; i < currentQuestionIndex; i++) {
                    if (!visited[i]) { next = i; break; }
                }
            }
            if (next == -1) {
                Toast.makeText(this, "All questions answered!", Toast.LENGTH_SHORT).show();
                goToResult();
            } else {
                if (countDownTimer != null) countDownTimer.cancel();
                currentQuestionIndex = next;
                displayQuestion();
            }
        });

        // EXIT: confirmation dialog
        btnExit.setOnClickListener(v -> showExitDialog());

        // Option click listeners
        View.OnClickListener optionClickListener = v -> {
            if (selectedOption == -1 && !visited[currentQuestionIndex]) {
                if (v.getId() == R.id.btn_option1)      selectedOption = 0;
                else if (v.getId() == R.id.btn_option2) selectedOption = 1;
                else if (v.getId() == R.id.btn_option3) selectedOption = 2;
                else if (v.getId() == R.id.btn_option4) selectedOption = 3;
                highlightAnswer(selectedOption);
            }
        };

        btnOption1.setOnClickListener(optionClickListener);
        btnOption2.setOnClickListener(optionClickListener);
        btnOption3.setOnClickListener(optionClickListener);
        btnOption4.setOnClickListener(optionClickListener);
    }

    private void loadQuestions() {
        questions = new ArrayList<>();

        questions.add(new Question(
            "What is the powerhouse of the cell?",
            new String[]{"Nucleus", "Mitochondria", "Ribosome", "Chloroplast"},
            1, "Biology"
        ));
        questions.add(new Question(
            "What is the value of π (Pi) rounded to 2 decimal places?",
            new String[]{"3.12", "3.41", "3.14", "3.16"},
            2, "Math"
        ));
        questions.add(new Question(
            "Which planet is known as the Red Planet?",
            new String[]{"Venus", "Jupiter", "Saturn", "Mars"},
            3, "Science"
        ));
        questions.add(new Question(
            "What is the chemical symbol for Gold?",
            new String[]{"Go", "Gd", "Au", "Ag"},
            2, "Chemistry"
        ));
        questions.add(new Question(
            "Who wrote 'Romeo and Juliet'?",
            new String[]{"Charles Dickens", "William Shakespeare", "Mark Twain", "Jane Austen"},
            1, "English"
        ));
        questions.add(new Question(
            "What is the square root of 144?",
            new String[]{"10", "11", "13", "12"},
            3, "Math"
        ));
        questions.add(new Question(
            "Which gas do plants absorb during photosynthesis?",
            new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"},
            2, "Biology"
        ));
        questions.add(new Question(
            "What is the speed of light (approx)?",
            new String[]{"3×10⁸ m/s", "3×10⁶ m/s", "3×10⁵ m/s", "3×10⁴ m/s"},
            0, "Physics"
        ));
        questions.add(new Question(
            "How many continents are there on Earth?",
            new String[]{"5", "6", "8", "7"},
            3, "Geography"
        ));
        questions.add(new Question(
            "What is the largest ocean on Earth?",
            new String[]{"Atlantic", "Indian", "Pacific", "Arctic"},
            2, "Geography"
        ));

        // Shuffle for variety
        Collections.shuffle(questions);

        // Filter by category
        String category = getIntent().getStringExtra("CATEGORY");
        if (category != null && !category.equals("All Subjects")) {
            List<Question> filtered = new ArrayList<>();
            for (Question q : questions) {
                if (q.getSubject().equalsIgnoreCase(category)) filtered.add(q);
            }
            questions = filtered;
        }

        // Initialize state tracking arrays
        visited     = new boolean[questions.size()];
        userAnswers = new int[questions.size()];
        Arrays.fill(userAnswers, -2); // -2 = not yet visited
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            goToResult();
            return;
        }

        Question q = questions.get(currentQuestionIndex);

        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        cardQuestion.startAnimation(slideIn);

        tvQuestion.setText(q.getQuestionText());
        tvSubject.setText(q.getSubject());
        tvProgress.setText("Question " + (currentQuestionIndex + 1) + "/" + questions.size());
        tvMotivation.setText(MOTIVATIONAL_TEXTS[currentQuestionIndex % MOTIVATIONAL_TEXTS.length]);

        String[] options = q.getOptions();
        btnOption1.setText(options[0]);
        btnOption2.setText(options[1]);
        btnOption3.setText(options[2]);
        btnOption4.setText(options[3]);

        progressBar.setMax(questions.size());
        progressBar.setProgress(currentQuestionIndex + 1);

        // PREV button: enabled only if not on the first question
        btnPrev.setEnabled(currentQuestionIndex > 0);
        btnPrev.setAlpha(currentQuestionIndex > 0 ? 1.0f : 0.4f);

        if (visited[currentQuestionIndex]) {
            // Already answered — restore previous state, no timer
            selectedOption = userAnswers[currentQuestionIndex];
            restoreAnswerState(selectedOption);
        } else {
            // Fresh question — enable options and start timer
            selectedOption = -1;
            resetOptionButtons();
            startTimer();
        }
    }

    private void restoreAnswerState(int selected) {
        if (countDownTimer != null) countDownTimer.cancel();
        tvTimer.setText("–");
        timerBar.setProgress(0);

        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
        int correct = questions.get(currentQuestionIndex).getCorrectAnswerIndex();
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(false);
            if (i == correct) {
                buttons[i].setBackgroundResource(R.drawable.btn_correct);
                buttons[i].setTextColor(Color.WHITE);
            } else if (i == selected && selected != correct) {
                buttons[i].setBackgroundResource(R.drawable.btn_wrong);
                buttons[i].setTextColor(Color.WHITE);
            } else {
                buttons[i].setBackgroundResource(R.drawable.btn_option_disabled);
                buttons[i].setTextColor(Color.parseColor("#8899CC"));
            }
        }
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timerBar.setMax(TIME_PER_QUESTION * 1000);

        countDownTimer = new CountDownTimer(TIME_PER_QUESTION * 1000L, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.valueOf(seconds));
                timerBar.setProgress((int) millisUntilFinished);
                if (seconds <= 5) {
                    tvTimer.setTextColor(Color.parseColor("#FF4757"));
                    timerBar.getProgressDrawable().setColorFilter(
                        Color.parseColor("#FF4757"), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    tvTimer.setTextColor(Color.parseColor("#2ED573"));
                    timerBar.getProgressDrawable().setColorFilter(
                        Color.parseColor("#2ED573"), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0");
                visited[currentQuestionIndex] = true;
                userAnswers[currentQuestionIndex] = -1;
                highlightAnswer(-1);
            }
        }.start();
    }

    private void highlightAnswer(int selected) {
        if (countDownTimer != null) countDownTimer.cancel();

        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
        int correct = questions.get(currentQuestionIndex).getCorrectAnswerIndex();

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setEnabled(false);
            if (i == correct) {
                buttons[i].setBackgroundResource(R.drawable.btn_correct);
                buttons[i].setTextColor(Color.WHITE);
            } else if (i == selected && selected != correct) {
                buttons[i].setBackgroundResource(R.drawable.btn_wrong);
                buttons[i].setTextColor(Color.WHITE);
            } else {
                buttons[i].setBackgroundResource(R.drawable.btn_option_disabled);
                buttons[i].setTextColor(Color.parseColor("#8899CC"));
            }
        }

        // Auto-advance after 2 seconds
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) moveToNextQuestion();
        }, 2000);
    }

    private void resetOptionButtons() {
        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
        for (Button btn : buttons) {
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.btn_option);
            btn.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    private void showExitDialog() {
        if (countDownTimer != null) countDownTimer.cancel();
        new AlertDialog.Builder(this)
            .setTitle("Exit Quiz?")
            .setMessage("Your current progress will be submitted as your score.")
            .setPositiveButton("Exit", (d, w) -> goToResult())
            .setNegativeButton("Continue", (d, w) -> {
                if (!visited[currentQuestionIndex]) startTimer();
            })
            .setCancelable(false)
            .show();
    }

    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (userAnswers[i] == questions.get(i).getCorrectAnswerIndex()) score++;
        }
        return score;
    }

    private void moveToNextQuestion() {
        // Find the next unvisited question after current
        for (int i = currentQuestionIndex + 1; i < questions.size(); i++) {
            if (!visited[i]) {
                currentQuestionIndex = i;
                displayQuestion();
                return;
            }
        }
        // All remaining are visited — check if any unvisited exist before current
        for (int i = 0; i < currentQuestionIndex; i++) {
            if (!visited[i]) {
                currentQuestionIndex = i;
                displayQuestion();
                return;
            }
        }
        // All done
        goToResult();
    }

    private void goToResult() {
        if (countDownTimer != null) countDownTimer.cancel();
        int score = calculateScore();
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questions.size());
        intent.putExtra("CATEGORY", getIntent().getStringExtra("CATEGORY"));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
