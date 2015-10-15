package com.math160.math160;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class PauseGame extends Activity implements View.OnClickListener{

    private Button [] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause_game);

        loadAds();
        initializeComponents();
        handleClicks();
    }

    // Load ads
    private void loadAds(){
        AdView pgAdTop = (AdView) findViewById(R.id.pgAdTop);
        AdView pgAdBottom = (AdView) findViewById(R.id.pgAdBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        pgAdTop.loadAd(adRequest);
        pgAdBottom.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.out_to_top);
    }

    private void initializeComponents(){
        buttons = new Button[3];
        buttons[0] = (Button) findViewById(R.id.resume);
        buttons[1] = (Button) findViewById(R.id.newGame);
        buttons[2] = (Button) findViewById(R.id.mainMenu);

    }

    private void handleClicks(){
        for(int i = 0; i < 3; i++){
            buttons[i].setOnClickListener(this);
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.resume: // Return to game
                finish();
                overridePendingTransition(R.anim.stay, R.anim.out_to_top);
                break;
            case R.id.newGame: // Double check with user before starting new game
                showPopup("new game");
                break;
            case R.id.mainMenu: // Double check with user before quitting
                showPopup("main menu");
                break;
            default:
                break;

        }
    }

    private void showPopup(String string){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final String s = string;
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE: // Yes
                        if(s.equals("new game")){
                            // Get choices from menus then pass to new start game activity
                            Intent i = getIntent();
                            Bundle b = i.getExtras();
                            Intent intent = new Intent(PauseGame.this, StartGame.class);
                            intent.putExtra("choices", b.getStringArray("choices"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            // Page transition
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                        }
                        else if (s.equals("main menu")){
                            Intent intent = new Intent(PauseGame.this, MathMenu.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            // Page transition
                            overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE: // No
                        dialog.cancel();
                        break;
                }
            }
        };
        dialogBuilder.setMessage("All current data will be lost. Are you sure you want to continue?").setNegativeButton("NO", dialogListener).setPositiveButton("YES", dialogListener).show();
    }

}
