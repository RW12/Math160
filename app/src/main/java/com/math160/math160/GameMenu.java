package com.math160.math160;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class GameMenu extends Activity {

    //Menu buttons
    private Button [] buttons;

    //Strings to store choice from previous menu
    private String [] choices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        loadAds();

        choices = new String[2];

        //Get choice from previous menu
        Intent i = getIntent();
        choices[0] = i.getStringExtra("choice");

        //Get buttons and handle clicks
        buttons = new Button[3];
        buttons[0] = (Button) findViewById(R.id.minutemania);
        buttons[1] = (Button) findViewById(R.id.dash100);
        buttons[2] = (Button) findViewById(R.id.mellowmath);

        handleButtonClicks();
    }

    // Load ads
    private void loadAds(){
        AdView gmAdTop = (AdView) findViewById(R.id.gmAdTop);
        AdView gmAdBottom = (AdView) findViewById(R.id.gmAdBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        gmAdTop.loadAd(adRequest);
        gmAdBottom.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    //Handle menu button clicks
    public void handleButtonClicks(){
        for(int i = 0 ; i<3; i++){
            final int j = i;
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (j) {
                        case 0:
                            choices[1] = "MINUTE MANIA";
                            startNextActivity();
                            break;
                        case 1:
                            choices[1] = "DASH 100";
                            startNextActivity();
                            break;
                        case 2:
                            choices[1] = "MELLOW MATH";
                            startNextActivity();
                            break;
                        default:
                            break;
                    }
                }
            });}
    }

    private void startNextActivity(){
        //Start Game intent
        Intent startGame = new Intent(GameMenu.this, StartGame.class);
        startGame.putExtra("choices", choices);
        startActivity(startGame);
        // Page transition
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
