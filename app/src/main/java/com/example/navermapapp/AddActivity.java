package com.example.navermapapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mListData = new ArrayList<>();
        mSimpleAdapter = new SimpleAdapter(this, mListData, R.layout.my_list_item
                ,new String[] {"name","lat","lng"}, new int[] {R.id.text1, R.id.text2, R.id.text3});
        mListView = findViewById(R.id.list);
        mListView.setAdapter(mSimpleAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mISelectedItem = position;
                if(mListData.size() > 0) {
                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 지역명 검색해서 위도, 경도, 이름 값 받아오기
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

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", data.getStringExtra("name"));
        hashMap.put("id", String.valueOf(++id));

        hashMap.put("lat", String.valueOf(latLng.latitude));
        hashMap.put("lng", String.valueOf(latLng.longitude));

        mListData.add(hashMap);
        mSimpleAdapter.notifyDataSetChanged();
    }

    public void onClickDel(View v) {
        if(mISelectedItem == -1)
            Toast.makeText(getApplicationContext(), "항목을 선택해주세요", Toast.LENGTH_SHORT).show();

        // arraylist remove메소드로 삭제 + hashMap에서 id값 하나씩 땡기기
    }

    public void onClickComplete(View v) {
        Intent it = new Intent(getApplicationContext(), ListActivity.class);
        // 두 개 이상 선택 안 했을 시 완성버튼 x
        if(mListData.size() < 3) {
            Toast.makeText(getApplicationContext(), "최소 2개 이상 목적지를 추가해주세요", Toast.LENGTH_SHORT).show();
            return ;
        }

        // 위도, 경도, 이름, id값 담은 hashMap ArrayList 넘기기
        it.putExtra("data", mListData);
        setResult(RESULT_OK,it);
        finish();
    }

    public void onClickCancel(View v) {
        Intent it = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(it);
    }

}