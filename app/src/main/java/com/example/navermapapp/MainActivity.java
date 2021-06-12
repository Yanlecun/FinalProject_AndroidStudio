package com.example.navermapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 1) 직접 코딩
    public void onClickStart(View v) {
        Intent it = new Intent(getApplicationContext(), RestaurantSearchActivity.class);
        startActivity(it);
    }
}