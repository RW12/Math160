package com.math160.math160;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MathMenu extends Activity implements View.OnClickListener {

    private Button[] buttons; //menu buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math_menu);

        buttons = new Button[5];
        buttons[0] = (Button) findViewById(R.id.addition);
        buttons[1] = (Button) findViewById(R.id.subtraction);
        buttons[2] = (Button) findViewById(R.id.multiplication);
        buttons[3] = (Button) findViewById(R.id.division);
        buttons[4] = (Button) findViewById(R.id.mix);

        for(int i = 0; i < 5; i++){
            buttons[i].setOnClickListener(this);
        }

        // Load ads
        AdView mmAdTop = (AdView) findViewById(R.id.mmAdTop);
        AdView mmAdBottom = (AdView) findViewById(R.id.mmAdBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        mmAdTop.loadAd(adRequest);
        mmAdBottom.loadAd(adRequest);
    }

    // Handle menu click: addition, subtraction, multiplication, division, or mix
    public void onClick(View v){
        switch(v.getId()){
            case R.id.addition:
                startNextActivity("Addition");
                break;
            case R.id.subtraction:
                startNextActivity("Subtraction");
                break;
            case R.id.multiplication:
                startNextActivity("Multiplication");
                break;
            case R.id.division:
                startNextActivity("Division");
                break;
            case R.id.mix:
                startNextActivity("Mix It!");
                break;
            default:
                break;
        }
    }

    private void startNextActivity(String s){
        //Game Menu intent
        Intent gamemenu = new Intent(this, GameMenu.class);
        gamemenu.putExtra("choice", s);
        startActivity(gamemenu);
        // Page transition
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}