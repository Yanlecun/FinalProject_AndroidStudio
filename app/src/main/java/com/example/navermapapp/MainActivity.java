package com.example.navermapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClickStart(View v) {
        Intent it = new Intent(getApplicationContext(), AddActivity.class);
        startActivity(it);
    }

    public void onClickList(View v) {
        Intent it = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(it);
    }
}