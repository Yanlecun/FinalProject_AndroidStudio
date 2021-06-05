package com.example.navermapapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class AddActivity extends AppCompatActivity {
    private ListView mListView ;
    private SimpleAdapter mSimpleAdapter;
    private ArrayList<HashMap<String,String>> mListData;
    private int mISelectedItem = -1;

    Intent it_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        it_list = getIntent();
    }

    // 주소만 받아오자
    public void onClickAdd(View v) {
        Intent it = new Intent(getApplicationContext(), SearchActivity.class);
        startActivityForResult(it, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != 200 || resultCode != RESULT_OK) {
            Toast.makeText(getApplicationContext(),"추가하기 실패", Toast.LENGTH_SHORT).show();
            return ;
        }
        LatLng latLng = data.getExtras().getParcelable("LatLng");


    }


    public void onClickDel(View v) {

    }

    public void onClickComplete(View v) {
        Intent it = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(it);
    }

    public void onClickCancel(View v) {
        Intent it = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(it);
    }

}