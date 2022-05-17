package top.criwits.sawa.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import top.criwits.sawa.R;
import top.criwits.sawa.config.LoadConfig;
import top.criwits.sawa.solo.SoloActivity;
import top.criwits.sawa.solo.SoloGameView;

public class WelcomeActivity extends AppCompatActivity {

    private int difficulty = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_audio_switch:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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

    public void startSoloGame(View view) {

        Intent intent = new Intent(this, SoloActivity.class);
        intent.putExtra("top.criwits.sawa.DIFFICULTY_INDEX", difficulty);
        startActivity(intent);
    }

}