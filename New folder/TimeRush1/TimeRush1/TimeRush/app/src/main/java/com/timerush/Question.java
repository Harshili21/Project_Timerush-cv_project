package com.timerush;

public class Question {
    private String questionText;
    private String[] options;
    private int correctAnswerIndex;
    private String subject;
    private String difficulty;

    public Question(String questionText, String[] options, int correctAnswerIndex, String subject, String difficulty) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.subject = subject;
        this.difficulty = difficulty;
    }

    public String getQuestionText() { return questionText; }
    public String[] getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public String getSubject() { return subject; }
    public String getDifficulty() { return difficulty; }
}
