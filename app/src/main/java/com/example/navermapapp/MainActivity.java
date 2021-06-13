package com.example.navermapapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView mTv ;
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1) 직접코딩 - 수업 중 사용 코드 응용
        mTv = findViewById(R.id.safetyText);
        mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                adb.setTitle("코로나 안심식당 선정 조건");
                adb.setMessage(R.string.main_text_safety_alert);
                adb.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb.show();
            }
        });
    }

    // 1) 직접 코딩
    public void onClickStart(View v) {
        Intent it = new Intent(getApplicationContext(), RestaurantSearchActivity.class);
        System.out.println(searchText+ "hi2");
        it.putExtra("search_text", searchText);
        startActivityForResult(it,200);
    }

    // Info에서 "홈으로" -> "뒤로가기"
    // 1) 직접 코딩
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) return;
        searchText = data.getStringExtra("search_text");
    }
}