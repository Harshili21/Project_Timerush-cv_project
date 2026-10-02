package com.timerush;

public class Question {
    private String questionText;
    private String[] options;
    private int correctAnswerIndex;
    private String subject;

    public Question(String questionText, String[] options, int correctAnswerIndex, String subject) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.subject = subject;
    }

    public String getQuestionText() { return questionText; }
    public String[] getOptions() { return options; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public String getSubject() { return subject; }
}
