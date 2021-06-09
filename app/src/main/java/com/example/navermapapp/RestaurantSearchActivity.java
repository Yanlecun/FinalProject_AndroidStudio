package com.example.navermapapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import needle.Needle;
import needle.UiRelatedTask;

public class RestaurantSearchActivity extends AppCompatActivity {

    private EditText mEditText ;
    private String insert_loc ;

    private ListView mListView ;
    private SimpleAdapter mSimpleAdapter;
    private ArrayList<HashMap<String,String>> mListData;
    private int mISelectedItem = -1;

    // 인텐트 이동시 사용하는 array
    private ArrayList<Restaurant> mListRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        mListData = new ArrayList<>();
        mListRest = new ArrayList<>();

        mEditText = findViewById(R.id.searchText1);

        mSimpleAdapter = new SimpleAdapter(this, mListData, R.layout.my_list_item
                ,new String[] {"name","address","gubun_detail"}, new int[] {R.id.text1, R.id.text2, R.id.text3});
        mListView = findViewById(R.id.list);
        mListView.setAdapter(mSimpleAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mISelectedItem = position;
                if(mListData.size() > 0) {
                    if(mISelectedItem >-1) {
                        Intent it = new Intent(getApplicationContext(), RestaurantInfoActivity.class);
                        Restaurant res = mListRest.get(mISelectedItem);

                        it.putExtra("restaurant",new Restaurant(res.getAddress(), res.getName(), res.getCall(), res.getGubun(), res.getGubunDetail()));

                        startActivityForResult(it,200);
                    }
                }
            }
        });

    }


    // ======================== //
    // Public DB Setting        //
    // ======================== //
    public void connectRestaurantsDB() {
        // https://www.data.go.kr/data/15061948/openapi.do
        String temp_Url = "http://211.237.50.150:7080/openapi/599172167c8d647c6f00e44d681829dcde7e539d49e9b67f6250e8837fa71f6a/json/Grid_20200713000000000605_1/1/1000?RELAX_USE_YN=Y&RELAX_SI_NM=%EA%B2%BD%EA%B8%B0%EB%8F%84&RELAX_SIDO_NM=";
        final String sUrl = temp_Url + mEditText.getText().toString().trim(); // 경기도 검색
        //Toast.makeText(getApplicationContext(),sUrl,Toast.LENGTH_SHORT).show();

        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override //db데이터 얻어오기
            protected String doWork() {
                URL url = null;
                HttpURLConnection con = null;

                try {
                    url = new URL(sUrl);
                    con = (HttpURLConnection)url.openConnection();

                    System.out.println(url);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try (
                        InputStream inputStream = con.getInputStream();
                        BufferedInputStream bufStream = new BufferedInputStream(inputStream);
                        InputStreamReader inputStreamReader = new InputStreamReader(bufStream,"utf-8");
                        BufferedReader bufReader = new BufferedReader(inputStreamReader);

                ){
                    StringBuffer sBuf   = new StringBuffer();
                    String sLine = "";
                    while((sLine = bufReader.readLine()) !=null ) {
                        sBuf.append(sLine);
                    }
                    con.disconnect();
                    return sBuf.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            //데이터 Json형식으로 파싱 + StringBuffer로 얻은 문자열 여기로 이동
            @Override
            protected void thenDoUiRelatedWork(String s) {

                if(s == null) {return ;}
                JsonObject jsonObject1 = (JsonObject)JsonParser.parseString(s);
                JsonObject jsonObject2 = (JsonObject)jsonObject1.get("Grid_20200713000000000605_1");
                JsonArray restaurants = (JsonArray)jsonObject2.get("row");

//                JsonObject jo = (JsonObject)restaurants.get(0);
//                JsonElement je = jo.get("RELAX_ADD1");
//                String test = je.getAsString();
//                System.out.println(test);

                if(restaurants == null ) {
                    Toast.makeText(getApplicationContext(), "근처에 안심 음식점이 없거나 찾지못했습니다.", Toast.LENGTH_LONG).show();
                    return ;

                }
                // 이전 검색 기록 지우기
                if(!mListData.isEmpty()) {
                    mListData.removeAll(mListData);
                    mListRest.removeAll(mListRest);
                }
                for(int i =0; i < restaurants.size(); i++) {
                    JsonObject restaurant = (JsonObject)restaurants.get(i);
                    JsonElement jRestaurantAddress = restaurant.get("RELAX_ADD1");  // 주소1
                    JsonElement jRestaurantName = restaurant.get("RELAX_RSTRNT_NM"); // 가게이름
                    JsonElement jRestaurantCall = restaurant.get("RELAX_RSTRNT_TEL"); // 전화번호

                    JsonElement jRestaurantGubun = restaurant.get("RELAX_GUBUN");
                    JsonElement jRestaurantGubunDetail = restaurant.get("RELAX_GUBUN_DETAIL");
                    Restaurant res_obj = new Restaurant();

                    // 이름,주소, 전화번호
                    res_obj.setAddress(jRestaurantAddress.getAsString()); // String address = jRestaurantAddress.getAsString();
                    res_obj.setName(jRestaurantName.getAsString()); // String name = jRestaurantName.getAsString();
                    res_obj.setCall(jRestaurantCall.getAsString()); // String call = jRestaurantCall.getAsString();
                    res_obj.setGubun(jRestaurantGubun.getAsString()); // -
                    res_obj.setGubunDetail(jRestaurantGubunDetail.getAsString()); // -

                    HashMap<String, String> hitem = new HashMap<>();
                    hitem.put("name",res_obj.getName());
                    hitem.put("address", res_obj.getAddress());
                    hitem.put("gubun_detail", res_obj.getGubunDetail());


                    mListData.add(hitem);
                    mListRest.add(res_obj);
                    mSimpleAdapter.notifyDataSetChanged();
                }
            }
        });
    }





    public void onClickHome(View v) {
        finish();
    }

    public void onClickSearch(View v) {
        insert_loc = mEditText.getText().toString().trim();

        connectRestaurantsDB();
        mEditText.setText("");

        // search버튼 누르면 지도에 내가 검색한 위치 뜨게하기
        // 처음에 지도는 그냥 기본 위치로 지정
    }
}