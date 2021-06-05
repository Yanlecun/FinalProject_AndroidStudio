package com.example.navermapapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {
    private ListView mListView ;
    private SimpleAdapter mSimpleAdapter;
    private ArrayList<HashMap<String,String>> mListData;
    private int mISelectedItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListData = new ArrayList<>();
        mSimpleAdapter = new SimpleAdapter(this, mListData, R.layout.my_list_item
                ,new String[] {"pos_start","pos_destination","time"}, new int[] {R.id.text1, R.id.text2, R.id.text3});

        mListView = findViewById(R.id.list);
        mListView.setAdapter(mSimpleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mISelectedItem = position;
            }
        });
    }

    // 위도,경도,
    public void onClickAdd(View v) {
        Intent it = new Intent(getApplicationContext(), AddActivity.class);
        startActivityForResult(it,200);
    }

    public void onClickDel(View v) {
        if(mISelectedItem == -1)
            Toast.makeText(getApplicationContext(), "항목을 선택하거나 추가해주세요", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != 200)
            return;

    }
}