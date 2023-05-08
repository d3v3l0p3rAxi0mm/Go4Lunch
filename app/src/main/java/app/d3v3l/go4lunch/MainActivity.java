package app.d3v3l.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import app.d3v3l.go4lunch.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.ActivityMainButtonSignInWithEmmail.setOnClickListener(v -> {
            Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
            MainActivity.this.startActivity(myIntent);
        });

    }


}