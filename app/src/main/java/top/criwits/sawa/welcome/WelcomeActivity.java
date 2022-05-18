package top.criwits.sawa.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;

import top.criwits.sawa.R;
import top.criwits.sawa.config.Media;
import top.criwits.sawa.solo.SoloActivity;

public class WelcomeActivity extends AppCompatActivity {

    private int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_welcome, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.soloEasyMode:
                if (checked) {
                    difficulty = 0;
                    System.out.println("Easy");
                }
                break;
            case R.id.soloModerateMode:
                if (checked) {
                    difficulty = 1;
                    System.out.println("Moderate");
                }
                break;
            case R.id.soloHardMode:
                if (checked) {
                    difficulty = 2;
                    System.out.println("Hard");
                }
                break;
        }
    }

    public void audioChange(View view) {
        Media.music = ((Switch) view).isChecked();
    }



    public void startSoloGame(View view) {
        Intent intent = new Intent(this, SoloActivity.class);
        intent.putExtra("top.criwits.sawa.DIFFICULTY_INDEX", difficulty);
        startActivity(intent);
    }

}