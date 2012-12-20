package com.cdburrows.android.roguelike;

import com.cburrows.android.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity {

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        // New Game
        TextView newGame = (TextView) findViewById(R.id.tvwNewgame);
        newGame.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, RoguelikeActivity.class);
                startActivity(intent);
            }
            
        });
        
        // New Game
        TextView options = (TextView) findViewById(R.id.tvwOptions);
        options.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
            
        });
        
        // Quit
        TextView quit = (TextView) findViewById(R.id.tvwQuit);
        quit.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                quit();
            }
            
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }
    
    private void quit() {
        //Toast.makeText(this, "Quit!", Toast.LENGTH_SHORT).show();
            MainMenuActivity.this.finish();
    }

    
}
