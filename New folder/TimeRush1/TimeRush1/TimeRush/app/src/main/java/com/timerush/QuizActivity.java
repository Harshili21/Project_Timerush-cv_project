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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestion, tvTimer, tvProgress, tvSubject, tvMotivation;
    private Button btnOption1, btnOption2, btnOption3, btnOption4, btnNext;
    private Button btnPrevious, btnUnvisited, btnExit;
    private ProgressBar progressBar, timerBar;
    private CardView cardQuestion;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int selectedOption = -1;
    private boolean[] visitedQuestions;
    private CountDownTimer countDownTimer;
    private static final int TIME_PER_QUESTION = 15; // seconds
    private long timeLeft;

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
        tvQuestion    = findViewById(R.id.tv_question);
        tvTimer       = findViewById(R.id.tv_timer);
        tvProgress    = findViewById(R.id.tv_progress);
        tvSubject     = findViewById(R.id.tv_subject);
        tvMotivation  = findViewById(R.id.tv_motivation);
        btnOption1    = findViewById(R.id.btn_option1);
        btnOption2    = findViewById(R.id.btn_option2);
        btnOption3    = findViewById(R.id.btn_option3);
        btnOption4    = findViewById(R.id.btn_option4);
        btnNext       = findViewById(R.id.btn_next);
        btnPrevious   = findViewById(R.id.btn_previous);
        btnUnvisited  = findViewById(R.id.btn_unvisited);
        btnExit       = findViewById(R.id.btn_exit);
        progressBar   = findViewById(R.id.progress_bar);
        timerBar      = findViewById(R.id.timer_bar);
        cardQuestion  = findViewById(R.id.card_question);

        btnNext.setOnClickListener(v -> moveToNextQuestion());
        btnPrevious.setOnClickListener(v -> moveToPreviousQuestion());
        btnUnvisited.setOnClickListener(v -> skipToUnvisitedQuestion());
        btnExit.setOnClickListener(v -> showExitConfirmation());

        View.OnClickListener optionClickListener = v -> {
            if (selectedOption == -1) {
                Button clickedBtn = (Button) v;
                if (v.getId() == R.id.btn_option1) selectedOption = 0;
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
            1, "Biology", "Basic"
        ));
        questions.add(new Question(
            "What is the value of \u03c0 (Pi) rounded to 2 decimal places?",
            new String[]{"3.12", "3.41", "3.14", "3.16"},
            2, "Math", "Basic"
        ));
        questions.add(new Question(
            "Which planet is known as the Red Planet?",
            new String[]{"Venus", "Jupiter", "Saturn", "Mars"},
            3, "Science", "Basic"
        ));
        questions.add(new Question(
            "What is the largest planet in our solar system?",
            new String[]{"Saturn", "Jupiter", "Neptune", "Uranus"},
            1, "Science", "Basic"
        ));
        questions.add(new Question(
            "How many phases does the Moon have?",
            new String[]{"4", "6", "8", "10"},
            2, "Science", "Basic"
        ));
        questions.add(new Question(
            "What causes seasons on Earth?",
            new String[]{"Distance from Sun", "Earth's tilt", "Moon's orbit", "Solar flares"},
            1, "Science", "Basic"
        ));
        questions.add(new Question(
            "What is the study of fossils called?",
            new String[]{"Archaeology", "Paleontology", "Geology", "Anthropology"},
            1, "Science", "Standard"
        ));
        questions.add(new Question(
            "Which element is most abundant in Earth's atmosphere?",
            new String[]{"Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen"},
            2, "Science", "Basic"
        ));
        questions.add(new Question(
            "What is the hottest planet in our solar system?",
            new String[]{"Mercury", "Venus", "Mars", "Jupiter"},
            1, "Science", "Standard"
        ));
        questions.add(new Question(
            "How many planets are in our solar system?",
            new String[]{"7", "8", "9", "10"},
            1, "Science", "Basic"
        ));
        questions.add(new Question(
            "What is the center of an atom called?",
            new String[]{"Proton", "Neutron", "Nucleus", "Electron"},
            2, "Science", "Basic"
        ));
        questions.add(new Question(
            "Which force keeps planets in orbit around the sun?",
            new String[]{"Magnetism", "Gravity", "Friction", "Nuclear force"},
            1, "Science", "Basic"
        ));
        questions.add(new Question(
            "What is the process by which plants make their own food?",
            new String[]{"Respiration", "Photosynthesis", "Digestion", "Circulation"},
            1, "Science", "Basic"
        ));
        questions.add(new Question(
            "Which layer of atmosphere protects us from harmful UV rays?",
            new String[]{"Troposphere", "Stratosphere", "Mesosphere", "Thermosphere"},
            1, "Science", "Standard"
        ));
        questions.add(new Question(
            "What is the chemical symbol for Gold?",
            new String[]{"Go", "Gd", "Au", "Ag"},
            2, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "What is the atomic number of Carbon?",
            new String[]{"4", "6", "8", "12"},
            1, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "What is the chemical formula for water?",
            new String[]{"CO2", "H2O", "O2", "N2"},
            1, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "Which element is known as the 'King of Elements'?",
            new String[]{"Gold", "Silver", "Platinum", "Mercury"},
            0, "Chemistry", "Standard"
        ));
        questions.add(new Question(
            "What is the pH value of pure water?",
            new String[]{"0", "7", "14", "1"},
            1, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "Which gas is produced when acid reacts with metal?",
            new String[]{"Oxygen", "Carbon Dioxide", "Hydrogen", "Nitrogen"},
            2, "Chemistry", "Standard"
        ));
        questions.add(new Question(
            "What is the chemical symbol for Silver?",
            new String[]{"Si", "Ag", "Sr", "Sn"},
            1, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "Which element has the chemical symbol 'O'?",
            new String[]{"Gold", "Oxygen", "Osmium", "Oganesson"},
            1, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "What is the chemical formula for carbon dioxide?",
            new String[]{"CO", "CO2", "C2O", "C2O2"},
            1, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "Which acid is found in vinegar?",
            new String[]{"Sulfuric acid", "Hydrochloric acid", "Acetic acid", "Nitric acid"},
            2, "Chemistry", "Standard"
        ));
        questions.add(new Question(
            "What is the lightest element in the periodic table?",
            new String[]{"Hydrogen", "Helium", "Lithium", "Carbon"},
            0, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "What is the chemical symbol for Iron?",
            new String[]{"I", "Ir", "Fe", "In"},
            2, "Chemistry", "Basic"
        ));
        questions.add(new Question(
            "Who wrote 'Romeo and Juliet'?",
            new String[]{"Charles Dickens", "William Shakespeare", "Mark Twain", "Jane Austen"},
            1, "English", "Basic"
        ));
        questions.add(new Question(
            "What is the past tense of 'go'?",
            new String[]{"goed", "went", "gone", "going"},
            1, "English", "Basic"
        ));
        questions.add(new Question(
            "Which word is a noun?",
            new String[]{"Run", "Quickly", "Beautiful", "Table"},
            3, "English", "Basic"
        ));
        questions.add(new Question(
            "What is the plural form of 'child'?",
            new String[]{"Childs", "Children", "Childes", "Childeren"},
            1, "English", "Basic"
        ));
        questions.add(new Question(
            "Which sentence is grammatically correct?",
            new String[]{"He don't like apples", "He doesn't like apples", "He doesn't likes apples", "He don't likes apples"},
            1, "English", "Basic"
        ));
        questions.add(new Question(
            "What is the synonym of 'happy'?",
            new String[]{"Sad", "Angry", "Joyful", "Tired"},
            2, "English", "Basic"
        ));
        questions.add(new Question(
            "Which word is an adjective?",
            new String[]{"Run", "Quickly", "Beautiful", "Table"},
            2, "English", "Basic"
        ));
        questions.add(new Question(
            "What is the antonym of 'hot'?",
            new String[]{"Warm", "Cold", "Cool", "Freezing"},
            1, "English", "Basic"
        ));
        questions.add(new Question(
            "Which sentence uses correct punctuation?",
            new String[]{"Hello how are you", "Hello, how are you", "Hello how are you,", "Hello, how, are you"},
            1, "English", "Standard"
        ));
        questions.add(new Question(
            "What is the past participle of 'write'?",
            new String[]{"Wrote", "Written", "Writing", "Writen"},
            1, "English", "Standard"
        ));
        questions.add(new Question(
            "Which word is a verb?",
            new String[]{"Book", "Quick", "Run", "Beautiful"},
            2, "English", "Basic"
        ));
        questions.add(new Question(
            "What is the correct spelling?",
            new String[]{"Recieve", "Receive", "Reseive", "Recive"},
            1, "English", "Standard"
        ));
        questions.add(new Question(
            "What is the square root of 144?",
            new String[]{"10", "11", "13", "12"},
            3, "Math", "Basic"
        ));
        questions.add(new Question(
            "What is 15% of 200?",
            new String[]{"25", "30", "35", "40"},
            1, "Math", "Basic"
        ));
        questions.add(new Question(
            "Solve for x: 2x + 5 = 15",
            new String[]{"5", "10", "7", "3"},
            0, "Math", "Standard"
        ));
        questions.add(new Question(
            "What is the area of a circle with radius 5? (Use pi = 3.14)",
            new String[]{"31.4", "78.5", "62.8", "94.2"},
            1, "Math", "Standard"
        ));
        questions.add(new Question(
            "What is the next number in the sequence: 2, 4, 8, 16, ?",
            new String[]{"24", "28", "32", "36"},
            2, "Math", "Basic"
        ));
        questions.add(new Question(
            "What is the value of 2³?",
            new String[]{"6", "8", "9", "12"},
            1, "Math", "Basic"
        ));
        questions.add(new Question(
            "What is 25% of 80?",
            new String[]{"15", "20", "25", "30"},
            1, "Math", "Basic"
        ));
        questions.add(new Question(
            "What is the perimeter of a square with side length 6?",
            new String[]{"12", "18", "24", "36"},
            2, "Math", "Basic"
        ));
        questions.add(new Question(
            "What is 3/4 as a decimal?",
            new String[]{"0.34", "0.75", "0.25", "0.80"},
            1, "Math", "Basic"
        ));
        questions.add(new Question(
            "What is the next prime number after 7?",
            new String[]{"9", "11", "13", "15"},
            1, "Math", "Standard"
        ));
        questions.add(new Question(
            "What is the sum of angles in a triangle?",
            new String[]{"90°", "180°", "270°", "360°"},
            1, "Math", "Basic"
        ));
        questions.add(new Question(
            "What is 7 × 8?",
            new String[]{"54", "56", "58", "64"},
            1, "Math", "Basic"
        ));
        questions.add(new Question(
            "Which gas do plants absorb during photosynthesis?",
            new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"},
            2, "Biology", "Basic"
        ));
        questions.add(new Question(
            "What is the largest organ in the human body?",
            new String[]{"Heart", "Brain", "Liver", "Skin"},
            3, "Biology", "Basic"
        ));
        questions.add(new Question(
            "How many bones are in the adult human body?",
            new String[]{"206", "208", "210", "212"},
            0, "Biology", "Standard"
        ));
        questions.add(new Question(
            "What is the basic unit of life?",
            new String[]{"Tissue", "Organ", "Cell", "Atom"},
            2, "Biology", "Basic"
        ));
        questions.add(new Question(
            "Which blood type is considered the universal donor?",
            new String[]{"A+", "B+", "AB+", "O-"},
            3, "Biology", "Standard"
        ));
        questions.add(new Question(
            "What is the function of red blood cells?",
            new String[]{"Fight infection", "Carry oxygen", "Digest food", "Produce hormones"},
            1, "Biology", "Basic"
        ));
        questions.add(new Question(
            "What is the largest muscle in the human body?",
            new String[]{"Heart", "Brain", "Gluteus maximus", "Tongue"},
            2, "Biology", "Standard"
        ));
        questions.add(new Question(
            "How many chambers are in the human heart?",
            new String[]{"2", "3", "4", "5"},
            2, "Biology", "Basic"
        ));
        questions.add(new Question(
            "What is the main function of the kidneys?",
            new String[]{"Digest food", "Filter blood", "Pump blood", "Store energy"},
            1, "Biology", "Basic"
        ));
        questions.add(new Question(
            "Which vitamin is produced when skin is exposed to sunlight?",
            new String[]{"Vitamin A", "Vitamin B", "Vitamin C", "Vitamin D"},
            3, "Biology", "Basic"
        ));
        questions.add(new Question(
            "What is the hardest substance in the human body?",
            new String[]{"Bone", "Enamel", "Skull", "Cartilage"},
            1, "Biology", "Basic"
        ));
        questions.add(new Question(
            "Which country has the largest population?",
            new String[]{"India", "China", "USA", "Indonesia"},
            1, "Geography", "Basic"
        ));
        questions.add(new Question(
            "What is the capital of France?",
            new String[]{"London", "Berlin", "Paris", "Madrid"},
            2, "Geography", "Basic"
        ));
        questions.add(new Question(
            "Which is the longest river in the world?",
            new String[]{"Amazon", "Nile", "Yangtze", "Mississippi"},
            1, "Geography", "Standard"
        ));
        questions.add(new Question(
            "What is the smallest country in the world?",
            new String[]{"Monaco", "Vatican City", "San Marino", "Liechtenstein"},
            1, "Geography", "Standard"
        ));
        questions.add(new Question(
            "Which desert is the largest in the world?",
            new String[]{"Sahara", "Arabian", "Gobi", "Antarctica"},
            3, "Geography", "Standard"
        ));
        questions.add(new Question(
            "What is the capital of Japan?",
            new String[]{"Seoul", "Beijing", "Tokyo", "Bangkok"},
            2, "Geography", "Basic"
        ));
        questions.add(new Question(
            "Which mountain range contains Mount Everest?",
            new String[]{"Andes", "Alps", "Himalayas", "Rocky Mountains"},
            2, "Geography", "Basic"
        ));
        questions.add(new Question(
            "What is the largest country by area?",
            new String[]{"Canada", "China", "Russia", "USA"},
            2, "Geography", "Basic"
        ));
        questions.add(new Question(
            "Which ocean is the smallest?",
            new String[]{"Atlantic", "Indian", "Arctic", "Southern"},
            2, "Geography", "Standard"
        ));
        questions.add(new Question(
            "What is the capital of Australia?",
            new String[]{"Sydney", "Melbourne", "Canberra", "Brisbane"},
            2, "Geography", "Standard"
        ));
        questions.add(new Question(
            "Which river flows through London?",
            new String[]{"Seine", "Thames", "Danube", "Rhine"},
            1, "Geography", "Basic"
        ));
        questions.add(new Question(
            "What is the speed of light (approx)?",
            new String[]{"3×10^8 m/s", "3×10^6 m/s", "3×10^5 m/s", "3×10^4 m/s"},
            0, "Physics", "Standard"
        ));
        questions.add(new Question(
            "What is Newton's First Law of Motion?",
            new String[]{"Every action has equal reaction", "F=ma", "Object at rest stays at rest", "Energy cannot be created"},
            2, "Physics", "Standard"
        ));
        questions.add(new Question(
            "What is the unit of force?",
            new String[]{"Joule", "Watt", "Newton", "Pascal"},
            2, "Physics", "Basic"
        ));
        questions.add(new Question(
            "What is the acceleration due to gravity on Earth?",
            new String[]{"8.9 m/s²", "9.8 m/s²", "10.8 m/s²", "11.8 m/s²"},
            1, "Physics", "Standard"
        ));
        questions.add(new Question(
            "Which type of wave is sound?",
            new String[]{"Electromagnetic", "Longitudinal", "Transverse", "Mechanical"},
            1, "Physics", "Standard"
        ));
        questions.add(new Question(
            "What is the formula for kinetic energy?",
            new String[]{"mv", "ma", "½mv²", "mgh"},
            2, "Physics", "Standard"
        ));
        questions.add(new Question(
            "Which device converts mechanical energy to electrical energy?",
            new String[]{"Motor", "Generator", "Transformer", "Battery"},
            1, "Physics", "Basic"
        ));
        questions.add(new Question(
            "What is the unit of electric current?",
            new String[]{"Volt", "Ampere", "Ohm", "Watt"},
            1, "Physics", "Basic"
        ));
        questions.add(new Question(
            "What is the law of reflection?",
            new String[]{"Angle of incidence equals angle of reflection", "Light travels in straight lines", "Light bends when passing through water", "Light speed is constant"},
            0, "Physics", "Standard"
        ));
        questions.add(new Question(
            "What is the unit of work or energy?",
            new String[]{"Newton", "Pascal", "Joule", "Coulomb"},
            2, "Physics", "Basic"
        ));
        questions.add(new Question(
            "Which type of energy is stored in a stretched rubber band?",
            new String[]{"Kinetic", "Potential", "Thermal", "Chemical"},
            1, "Physics", "Basic"
        ));
        questions.add(new Question(
            "What is the speed of sound in air (approx)?",
            new String[]{"34 m/s", "340 m/s", "3400 m/s", "34,000 m/s"},
            1, "Physics", "Standard"
        ));
        questions.add(new Question(
            "What is the process of heat transfer through direct contact called?",
            new String[]{"Convection", "Radiation", "Conduction", "Evaporation"},
            2, "Physics", "Basic"
        ));
        
        String category = getIntent().getStringExtra("CATEGORY");
        String difficulty = getIntent().getStringExtra("DIFFICULTY");
        
        // Filter by category first
        if (category != null && !category.equals("All Subjects")) {
            List<Question> filtered = new ArrayList<>();
            for (Question q : questions) {
                if (q.getSubject().equalsIgnoreCase(category)) {
                    filtered.add(q);
                }
            }
            questions = filtered;
        }
        
        // Then filter by difficulty
        if (difficulty != null) {
            List<Question> difficultyFiltered = new ArrayList<>();
            for (Question q : questions) {
                if (q.getDifficulty().equalsIgnoreCase(difficulty)) {
                    difficultyFiltered.add(q);
                }
            }
            questions = difficultyFiltered;
        }
        
        // Initialize visited questions array
        visitedQuestions = new boolean[questions.size()];
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            goToResult();
            return;
        }

        selectedOption = -1;
        resetOptionButtons();

        Question q = questions.get(currentQuestionIndex);
        
        // Mark current question as visited
        visitedQuestions[currentQuestionIndex] = true;

        // Animate question card
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

        // Update progress bar
        progressBar.setMax(questions.size());
        progressBar.setProgress(currentQuestionIndex + 1);

        btnNext.setVisibility(View.GONE);
        
        // Update Previous button visibility
        if (currentQuestionIndex > 0) {
            btnPrevious.setVisibility(View.VISIBLE);
        } else {
            btnPrevious.setVisibility(View.GONE);
        }

        startTimer();
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

                // Change timer color as time runs out
                if (seconds <= 5) {
                    tvTimer.setTextColor(Color.parseColor("#FF4757"));
                    timerBar.getProgressDrawable().setColorFilter(
                        Color.parseColor("#FF4757"),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    );
                } else {
                    tvTimer.setTextColor(Color.parseColor("#2ED573"));
                    timerBar.getProgressDrawable().setColorFilter(
                        Color.parseColor("#2ED573"),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    );
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("0");
                highlightAnswer(-1); // time's up - show correct answer
                btnNext.setVisibility(View.VISIBLE);
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
            }
        }

        if (selected == correct) score++;
        btnNext.setVisibility(View.VISIBLE);
    }

    private void resetOptionButtons() {
        Button[] buttons = {btnOption1, btnOption2, btnOption3, btnOption4};
        for (Button btn : buttons) {
            btn.setEnabled(true);
            btn.setBackgroundResource(R.drawable.btn_option);
            btn.setTextColor(Color.parseColor("#1A1A2E"));
        }
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex >= questions.size()) {
            goToResult();
        } else {
            displayQuestion();
        }
    }

    private void moveToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion();
        }
    }

    private void skipToUnvisitedQuestion() {
        for (int i = 0; i < visitedQuestions.length; i++) {
            if (!visitedQuestions[i]) {
                currentQuestionIndex = i;
                displayQuestion();
                return;
            }
        }
        // All questions visited, go to first question
        currentQuestionIndex = 0;
        displayQuestion();
    }

    private void showExitConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit Quiz")
            .setMessage("Are you sure you want to exit the quiz? Your progress will be lost.")
            .setPositiveButton("Exit", (dialog, which) -> {
                if (countDownTimer != null) countDownTimer.cancel();
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void goToResult() {
        if (countDownTimer != null) countDownTimer.cancel();
        Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
        intent.putExtra("SCORE", score);
        intent.putExtra("TOTAL", questions.size());
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
