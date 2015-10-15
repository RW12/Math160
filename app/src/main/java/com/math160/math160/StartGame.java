package com.math160.math160;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class StartGame extends Activity {

    private String[] choices; // stores user's math and game mode choices, respectively
    private TextView gameChoice, mathChoice, best, description;
    private Button start; //start button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        loadAds();
        setTextViews();
        startGame();
    }

    // Load ads
    private void loadAds(){
        AdView sgAdTop = (AdView) findViewById(R.id.sgAdTop);
        AdView sgAdBottom = (AdView) findViewById(R.id.sgAdBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        sgAdTop.loadAd(adRequest);
        sgAdBottom.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    //Show game mode and high score statistics
    private void setTextViews() {
        //Get choices from previous menus
        Intent i = getIntent();
        Bundle b = i.getExtras();
        choices = b.getStringArray("choices");

        gameChoice = (TextView) findViewById(R.id.gameChoice);
        mathChoice = (TextView) findViewById(R.id.mathChoice);

        best = (TextView) findViewById(R.id.best);

        description = (TextView) findViewById(R.id.description);

        // Get high score for display
        SharedPreferences prefs = this.getSharedPreferences("Math160HighScore", Context.MODE_PRIVATE);
        int currentBest = prefs.getInt(choices[0] + choices[1], 0); //0 is default if no other value is found

        gameChoice.setText(choices[1]);

        //Set TextView colors and best score TextView based on choices
        //Game Choice
        switch (choices[1]) {
            case "MINUTE MANIA":
                gameChoice.setTextColor(Color.parseColor("#AB47BC"));
                best.setText("High Score: " + currentBest);
                description.setTextColor(Color.parseColor("#AB47BC"));
                description.setText("Answer as many questions as you can in 60 seconds. Let's go!");
                break;
            case "DASH 100":
                // First best time shown will be 00:00
                gameChoice.setTextColor(Color.parseColor("#0097A7"));
                int min = currentBest / 60;
                int sec = currentBest % 60;
                best.setText("Best Time: " + String.format("%02d:%02d", min, sec));
                description.setTextColor(Color.parseColor("#0097A7"));
                description.setText("Correctly answer 100 questions as fast as you can. Are you ready?");
                break;
            case "MELLOW MATH":
                gameChoice.setTextColor(Color.parseColor("#E91E63"));
                best.setText("High Score: " + currentBest);
                description.setTextColor(Color.parseColor("#E91E63"));
                description.setText("100 slightly harder questions, but don't worry. It's not timed!");
                break;
        }

        //Math Choice
        switch (choices[0]) {
            case "Addition":
                mathChoice.setText("+");
                mathChoice.setTextColor(Color.parseColor("#2196F3"));
                break;
            case "Subtraction":
                mathChoice.setText("\u2212");
                mathChoice.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Multiplication":
                mathChoice.setText("\u00D7");
                mathChoice.setTextColor(Color.parseColor("#F44336"));
                break;
            case "Division":
                mathChoice.setText("\u00F7");
                mathChoice.setTextColor(Color.parseColor("#FF9800"));
                break;
            case "Mix It!":
                mathChoice.setText("+\u2212\u00D7\u00F7");
                mathChoice.setTextColor(Color.parseColor("#9E9E9E"));
                break;
        }


    }

    public void startGame() {

        start = (Button) findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gamePlay = new Intent(StartGame.this, GamePlay.class);
                gamePlay.putExtra("choices", choices);
                startActivity(gamePlay);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });
    }
}