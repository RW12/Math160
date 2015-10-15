package com.math160.math160;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class GameEnd extends Activity implements View.OnClickListener{

    private String [] choices, choicesAndStats;   // Math choice, game choice | math choice, game choice, number correct, time
    private TextView endText, score, newBest;
    private Button playAgain, mainMenu;
    private Handler blinkerHandler;
    private Runnable blinkerRunnable;
    private int blinkerCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        // Page transition - fade
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        loadAds();
        initializeComponents();
        compareScoresAndSetText();
        handleButtonClicks();
    }

    // Load ads
    private void loadAds(){
        AdView geAdTop = (AdView) findViewById(R.id.geAdTop);
        AdView geAdBottom = (AdView) findViewById(R.id.geAdBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        geAdTop.loadAd(adRequest);
        geAdBottom.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void onPause(){ // Stop blinker counter when user leaves page
        super.onPause();
        blinkerHandler.removeCallbacks(blinkerRunnable);
    }

    private void initializeComponents(){
        // Get choices from menus then pass to new start game activity
        Intent i = getIntent();
        Bundle b = i.getExtras();
        choicesAndStats = b.getStringArray("choices and stats");

        choices = new String[2];
        choices[0] = choicesAndStats[0];
        choices[1] = choicesAndStats[1];

        endText = (TextView) findViewById(R.id.endText);
        score = (TextView) findViewById(R.id.score);
        newBest = (TextView) findViewById(R.id.newBest);

        playAgain = (Button) findViewById(R.id.playAgain);
        mainMenu = (Button) findViewById(R.id.mainMenu);

        blinkerCounter = 0;
        blinkerHandler = new Handler();
        blinkerRunnable = new Runnable() {
            @Override
            public void run() {
                if(blinkerCounter % 2 == 0){ // Show label on even counts
                    newBest.setVisibility(View.VISIBLE);
                }
                else{
                    newBest.setVisibility(View.INVISIBLE);
                }
                blinkerCounter++;
                blinkerHandler.postDelayed(this, 700); // Starts runnable again after 700ms
            }
        };
    }

    // Compares current score with stored high score. If improved, update saved high score
    // Update labels accordingly
    private void compareScoresAndSetText(){
        // Hide "New Best!" label initially
        newBest.setVisibility(View.INVISIBLE);

        // Scores are stored with keys in the format "Math choiceGAME CHOICE"
        // Get high score for comparison
        SharedPreferences prefs = this.getSharedPreferences("Math160HighScore", Context.MODE_PRIVATE);
        int currentBest;

        // For setting new best score
        SharedPreferences.Editor bestScoreEditor = prefs.edit();

        switch (choicesAndStats[1]){
            case "MINUTE MANIA":
                score.setText("Score: " + choicesAndStats[2]);

                currentBest = prefs.getInt(choicesAndStats[0] + choicesAndStats[1], 0); //0 is default if no other value is found

                // Update best score and show "New Best!" label if applicable
                if(Integer.parseInt(choicesAndStats[2]) > currentBest){
                    flashNewBestLabel();
                    bestScoreEditor.putInt(choicesAndStats[0] + choicesAndStats[1], Integer.parseInt(choicesAndStats[2]));
                    bestScoreEditor.apply();
                }
                break;
            case "DASH 100":
                endText.setText("YOU MADE IT!");
                int min = Integer.parseInt(choicesAndStats[3])/60;
                int sec = Integer.parseInt(choicesAndStats[3])%60;
                score.setText("Time: " + String.format("%02d:%02d", min, sec));

                currentBest = prefs.getInt(choicesAndStats[0] + choicesAndStats[1], 86400); //86400 (~24hrs) is default if no other value is found

                // Update best score and show "New Best!" label if applicable
                if(Integer.parseInt(choicesAndStats[3]) < currentBest){ // Want the lower number for faster time
                    flashNewBestLabel();
                    bestScoreEditor.putInt(choicesAndStats[0] + choicesAndStats[1], Integer.parseInt(choicesAndStats[3]));
                    bestScoreEditor.apply();
                }
                break;
            case "MELLOW MATH":
                endText.setText("THAT'S IT!");
                score.setText("Score: " + choicesAndStats[2]);

                currentBest = prefs.getInt(choicesAndStats[0] + choicesAndStats[1], 0); //0 is default if no other value is found

                // Update best score and show "New Best!" label if applicable
                if(Integer.parseInt(choicesAndStats[2]) > currentBest){
                    flashNewBestLabel();
                    bestScoreEditor.putInt(choicesAndStats[0] + choicesAndStats[1], Integer.parseInt(choicesAndStats[2]));
                    bestScoreEditor.apply();
                }
                break;
            default:
                break;
        }
    }

    private void flashNewBestLabel(){
        blinkerHandler.postDelayed(blinkerRunnable, 0); // Starts runnable immediately
    }

    private void handleButtonClicks(){
        playAgain.setOnClickListener(this);
        mainMenu.setOnClickListener(this);
    }

    public void onClick(View v){
        Intent i;
        switch (v.getId()){
            case R.id.playAgain:
                i = new Intent(GameEnd.this, StartGame.class);
                i.putExtra("choices", choices);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // Page transition
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;
            case R.id.mainMenu:
                i = new Intent(GameEnd.this, MathMenu.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // Page transition
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;
            default:
                break;
        }
    }
}
