package com.math160.math160;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Random;


public class GamePlay extends Activity implements View.OnClickListener{

    private Button[] keypad;    // Holds number pad buttons 0 through 9
    private Button [] buttons;  // Holds pause, clear, delete, skip, and submit buttons (in that order)
    private EditText answerBox;
    private TextView topNumber, bottomNumber, symbol, score, timerDisplay;
    private int first, second, timerCount;
    private String [] choices, choicesAndStats;   // Math choice, game choice | math choice, game choice, number correct, time
    private int numCorrect, numAsked;
    private Intent i;

    // For fading activity at end of game
    private View v; // The view for this activity
    private Animation animation;

    // For the timer
    private Handler timerHandler;
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        // Keep keyboard from popping up - Method 1 (needed for lower APIs?)
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loadAds();
        initializeComponents();
        handleActions();
        generateQuestionAndUpdateScore();
        setTimer();
    }

    // Load ads
    private void loadAds(){
        AdView gpAdTop = (AdView) findViewById(R.id.gpAdTop);
        AdRequest adRequest = new AdRequest.Builder().build();
        gpAdTop.loadAd(adRequest);
    }

    // Start timer on create, and restart timer when back from pause
    @Override
    public void onResume(){
        super.onResume();
        startTimer();
    }

    // Stop timer when leaving activity
    @Override
    public void onPause(){
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    private void initializeComponents(){
        // Get choices from menus
        i = getIntent();
        Bundle b = i.getExtras();
        choices = b.getStringArray("choices");

        choicesAndStats = new String[5];
        choicesAndStats[0] = choices[0];
        choicesAndStats[1] = choices[1];

        keypad = new Button[10];
        keypad[0] = (Button) findViewById(R.id.zero);
        keypad[1] = (Button) findViewById(R.id.one);
        keypad[2] = (Button) findViewById(R.id.two);
        keypad[3] = (Button) findViewById(R.id.three);
        keypad[4] = (Button) findViewById(R.id.four);
        keypad[5] = (Button) findViewById(R.id.five);
        keypad[6] = (Button) findViewById(R.id.six);
        keypad[7] = (Button) findViewById(R.id.seven);
        keypad[8] = (Button) findViewById(R.id.eight);
        keypad[9] = (Button) findViewById(R.id.nine);

        buttons = new Button[5];
        buttons[0] = (Button) findViewById(R.id.pause);
        buttons[1] = (Button) findViewById(R.id.clear);
        buttons[2] = (Button) findViewById(R.id.delete);
        buttons[3] = (Button) findViewById(R.id.skip);
        buttons[4] = (Button) findViewById(R.id.submit);

        answerBox = (EditText) findViewById(R.id.answerBox);

        topNumber = (TextView) findViewById(R.id.topNumber);
        bottomNumber = (TextView) findViewById(R.id.bottomNumber);
        symbol = (TextView) findViewById(R.id.equationSymbol);

        score = (TextView) findViewById(R.id.score);
        numCorrect = 0;

        timerDisplay = (TextView) findViewById(R.id.time);
        timerHandler = new Handler();

        numAsked = 0;

        v = (View) findViewById(R.id.gamePlayView);
    }

    // Set timer depending on chosen game
    private void setTimer(){
        switch (choices[1]){
            case "MINUTE MANIA": // Start timer at 60 and count down
                timerDisplay.setText("60");
                timerCount = 59;
                timerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        timerDisplay.setText(String.valueOf(timerCount));
                        if(timerCount == 0){ // Stop timer and show end game screen once time is up (60s)
                            endGame();
                        }
                        timerCount--;
                        timerHandler.postDelayed(this, 1000); // Starts runnable again after 1000ms
                    }
                };
                break;
            case "DASH 100": // Start timer at 0 and count up
                timerDisplay.setText("00:00");
                timerCount = 1;
                timerRunnable = new Runnable() {
                    @Override
                    public void run() {
                        int min = timerCount/60;
                        int sec = timerCount%60;
                        timerDisplay.setText(String.format("%02d:%02d", min, sec));
                        timerCount++;
                        timerHandler.postDelayed(this, 1000); // Starts runnable again after 1000ms
                    }
                };
                break;
            case "MELLOW MATH": // Hide timer if Mellow Math chosen
                timerDisplay.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }

    // Bundle up stats for display on game end activity
    private void bundleStats(){
        choicesAndStats[2] = String.valueOf(numCorrect);
        choicesAndStats[3] = String.valueOf(timerCount);
    }

    // Starts timer
    private void startTimer(){
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    // Handle component actions
    private void handleActions(){
        // Make sure keyboard does not pop up if user selects inside answer box - Method 2
        answerBox.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent me) {
                v.onTouchEvent(me);
                InputMethodManager manager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager != null) {
                    manager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

        // Handle number pad inputs
        for(int i = 0; i < 10; i++){
            final String j = i + "";
            keypad[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Up to 4 digits can be entered in box
                    if(answerBox.getText().length()<4){
                        answerBox.getText().insert(answerBox.getSelectionStart(), j);
                    }
                }
            });
        }

        // Handle remaining buttons' actions
        for(int i = 0; i < 5; i++){
            buttons[i].setOnClickListener(this);
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.pause:
                i = new Intent(this, PauseGame.class);
                i.putExtra("choices", choices);
                startActivity(i);
                // Page transition
                overridePendingTransition(R.anim.in_from_top, R.anim.stay);
                break;
            case R.id.clear:
                answerBox.getText().clear();
                break;
            case R.id.delete:
                // Delete last digit in answer box text
                int l = answerBox.getText().length();
                if (l > 0){
                    answerBox.getText().delete(l-1, l);
                }
                break;
            case R.id.skip:
                generateQuestionAndUpdateScore();
                break;
            case R.id.submit:
                int answer;
                if(answerBox.getText().toString().equals("")) answer = 0;
                else answer = Integer.parseInt(answerBox.getText().toString());
                first = Integer.parseInt(topNumber.getText().toString());
                second = Integer.parseInt(bottomNumber.getText().toString());
                if(symbol.getText().toString().equals("+")){
                    if(answer == first + second){
                        numCorrect++;
                        generateQuestionAndUpdateScore();
                    }
                    else{
                        answerBox.setBackgroundColor(Color.parseColor("#FF8A80"));
                        answerBox.getText().clear();
                    }
                }else if(symbol.getText().toString().equals("\u2212")){// Subtraction
                    if(answer == first - second){
                        numCorrect++;
                        generateQuestionAndUpdateScore();
                    }
                    else{
                        answerBox.setBackgroundColor(Color.parseColor("#FF8A80"));
                        answerBox.getText().clear();
                    }
                }else if(symbol.getText().toString().equals("\u00D7")){// Multiplication
                    if(answer == first * second) {
                        numCorrect++;
                        generateQuestionAndUpdateScore();
                    }
                    else{
                        answerBox.setBackgroundColor(Color.parseColor("#FF8A80"));
                        answerBox.getText().clear();
                    }
                }else if(symbol.getText().toString().equals("\u00F7")){// Division
                    if(answer == first / second){
                        numCorrect++;
                        generateQuestionAndUpdateScore();
                    }
                    else{
                        answerBox.setBackgroundColor(Color.parseColor("#FF8A80"));
                        answerBox.getText().clear();
                    }
                }
                break;
            default:
                break;
        }
    }

    // Randomly generate questions based on user's selections and update score every time
    private void generateQuestionAndUpdateScore(){
        updateScore();
        answerBox.setBackgroundColor(Color.parseColor("#f1e2cc"));
        answerBox.getText().clear();
        Random r = new Random();
        setSymbol(r);
        setNumbers(r);
        numAsked++;
    }

    // Update score label based on game choice
    private void updateScore(){
        if (choices[1].equals("DASH 100")){
            score.setText("Score: " + numCorrect);
            if(numCorrect == 100){
                endGame();
            }
        }
        else if(choices[1].equals("MELLOW MATH")){
            score.setText("Score: " + numCorrect + "/" + numAsked);
            if(numAsked == 100){
                endGame();
            }
        }
        else score.setText("Score: " + numCorrect); // Don't include numAsked for Minute Mania - no penalty for skipping
    }

    // Start Game End Activity
    private void endGame(){
        i = new Intent(GamePlay.this, GameEnd.class);
        bundleStats();
        i.putExtra("choices and stats", choicesAndStats);
        finish(); // Don't put this activity in stack, so user cannot return on back
        startActivity(i);
    }

    // Set symbol based on math choice
    private void setSymbol(Random r){
        switch(choices[0]) {
            case "Addition":
                symbol.setText("+");
                break;
            case "Subtraction":
                symbol.setText("\u2212");
                break;
            case "Multiplication":
                symbol.setText("\u00D7");
                break;
            case "Division":
                symbol.setText("\u00F7");
                break;
            case "Mix It!":
                int i = r.nextInt(4) + 1;
                if (i == 1) {
                    symbol.setText("+");
                }else if (i == 2) {
                    symbol.setText("\u2212");
                }else if (i == 3) {
                    symbol.setText("\u00D7");
                }else {
                    symbol.setText("\u00F7");
                }
                break;
            default:
                break;
        }
    }

    // Set numbers for question based on math symbol so not too large (max total has 4 digits)
    private void setNumbers(Random r){
        String text1, text2;
        switch (symbol.getText().toString()){
            case "+": // Addition
                // Harder if Mellow Math was chosen
                if(choices[1].equals("MELLOW MATH")){
                    text1 = r.nextInt(500)+"";
                    text2 = r.nextInt(500)+"";
                }else{
                    text1 = r.nextInt(20)+"";
                    text2 = r.nextInt(20)+"";
                }
                topNumber.setText(text1);
                bottomNumber.setText(text2);
                break;
            case "\u2212": // Subtraction
                // Harder if Mellow Math was chosen
                if(choices[1].equals("MELLOW MATH")){
                    first = r.nextInt(500);
                    second = r.nextInt(500);
                    // Make sure answer is always positive
                    while(second > first){
                        second = r.nextInt(500);
                    }
                }else{
                    first = r.nextInt(30);
                    second = r.nextInt(30);
                    // Make sure answer is always positive
                    while(second > first){
                        second = r.nextInt(30);
                    }
                }
                text1 = first + "";
                text2 = second + "";
                topNumber.setText(text1);
                bottomNumber.setText(text2);
                break;
            case "\u00D7": // Multiplication
                first = r.nextInt(101);
                second = r.nextInt(99);
                // Allow second value to be less than or equal to 11 or equal to the first number (multiple of 5 if greater than 17
                // Harder if Mellow Math was chosen
                if(choices[1].equals("MELLOW MATH")){
                    while(true){
                        if(second <= 11) break;
                        else if(second == first && (second <= 16 || second%5 == 0)) break;
                        else second = r.nextInt(99);
                    }
                }else{
                    while(true){
                        first = r.nextInt(20);
                        second = r.nextInt(20);
                        if(second <= 10 && (first <= 11 || first == 20)) break;
                        else if(second == first && (second <= 16 || second == 20)) break;
                    }
                }
                text1 = first + "";
                text2 = second+"";
                topNumber.setText(text1);
                bottomNumber.setText(text2);
                break;
            case "\u00F7": // Division
                first = r.nextInt(300);
                // Make sure answer is always a whole number, second value is less than first, less than or equal to 11, or is a square root of first (multiple of 5 if greater than 17)
                // Harder if Mellow Math was chosen
                if(choices[1].equals("MELLOW MATH")){
                    while(true){
                        second = r.nextInt(300)+1; // Won't divide by 0
                        if(second <= 20 || (first == second * second && (second < 17 || second % 5 == 0))){
                            if (first % second == 0) break;
                        }
                    }
                }else{
                    while(true){
                        first = r.nextInt(100);
                        second = r.nextInt(100)+1; // Won't divide by 0
                        // Allow up to 11 multiples of the numbers 1 - 10
                        if(second <= 10 || first == second * second){
                            if (first % second == 0 && first / second <= 11) break;
                        }
                    }
                }
                text1 = first + "";
                text2 = second + "";
                topNumber.setText(text1);
                bottomNumber.setText(text2);
                break;
            default:
                break;
        }
    }

}
