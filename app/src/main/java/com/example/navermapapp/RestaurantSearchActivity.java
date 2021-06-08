package com.example.navermapapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import needle.Needle;
import needle.UiRelatedTask;

public class RestaurantSearchActivity extends FragmentActivity implements OnMapReadyCallback {

    private EditText mEditText1 ;
    private EditText mEditText2 ;
    private String insert_loc ;

    NaverMap mNaverMap;
    MapFragment mapFragment;

    Marker marker;
    FusedLocationProviderClient mFLPC; // 통합위치관리 제공자
    LocationCallback locationCallback;

    LatLng latLng = new LatLng(37.5796, 126.9770);

    private ArrayList<Restaurant> mRestList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        mEditText1 = findViewById(R.id.searchText1);
        mEditText2 = findViewById(R.id.searchText2);
        mRestList = new ArrayList<>();

        // id에 해당하는 fragment 가져오기
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // 지도 준비가 됬다고 알리는 이벤트 처리기 등록
        mapFragment.getMapAsync(this);  // -> onMapReady

    }

    // 검색해서 위치지정 / 화면드래그 가능 + 화면 클릭시 위치지정
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;
        mNaverMap.setSymbolScale(1f); //지도상에 보여지는 symbol scale (표준 크기)
        mNaverMap.setMapType(NaverMap.MapType.Basic);  // 지도 타입
        mNaverMap.moveCamera(CameraUpdate.zoomTo(15)); // 얼마만큼 확대 축소 할 것인지 ?

        mNaverMap.moveCamera(CameraUpdate.scrollTo(latLng)); //시작 지점
    }


    public void setMapPos(String insert_loc) {
        String a1 = mEditText1.getText().toString().trim();
        String a2 = mEditText2.getText().toString().trim();
        if(a1.isEmpty() || a2.isEmpty()) {
            Toast.makeText(getApplicationContext(), "전부 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        connectRestaurantsDB();
        Geocoder geocoder = new Geocoder(  this, Locale.KOREA);
        List<Address> list = null;

        try {
            list = geocoder.getFromLocationName(insert_loc, 5); //들어갈 부분 검색

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "주소 변환 오류", Toast.LENGTH_SHORT).show();
        }
        if(list == null || list.size() == 0) {
            Toast.makeText(getApplicationContext(),"해당되는 주소 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        final Address address = list.get(0);
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        latLng = new LatLng(lat, lng);

        marker = new Marker();
        marker.setPosition(latLng);
        marker.setMap(mNaverMap);
        marker.setCaptionText("검색한 위치");
        marker.setCaptionOffset(-215);
        marker.setCaptionTextSize(15);

        mNaverMap.moveCamera(CameraUpdate.scrollTo(latLng)); // 드래그로 이동 가능하도록 하기
        // 줌으로 보여기는 사이즈 조절하기

        getRestaurant(latLng);
    }

    // ======================== //
    // Public DB Setting        //
    // ======================== //
    public void connectRestaurantsDB() {
        // https://www.data.go.kr/data/15061948/openapi.do
        String temp_Url = "http://211.237.50.150:7080/openapi/599172167c8d647c6f00e44d681829dcde7e539d49e9b67f6250e8837fa71f6a/json/Grid_20200713000000000605_1/1/1000?RELAX_USE_YN=Y&RELAX_SI_NM=%EA%B2%BD%EA%B8%B0%EB%8F%84&RELAX_SIDO_NM=";
        temp_Url += mEditText2.getText().toString().trim();

        final String sUrl = temp_Url;
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override //db데이터 얻어오기
            protected String doWork() {
                URL url = null;
                HttpURLConnection con = null;
                try {
                    url = new URL(sUrl);
                    con = (HttpURLConnection)url.openConnection();
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
                JsonObject jsonObject = (JsonObject) JsonParser.parseString(s);
                JsonArray restaurants = (JsonArray)jsonObject.get("data");
                if(restaurants == null ) {
                    Toast.makeText(getApplicationContext(), "근처에 안심 음식점이 없거나 찾지못했습니다.", Toast.LENGTH_LONG).show();
                    return ;
                }
                for(int i =0; i < restaurants.size(); i++) {
                    JsonObject restaurant = (JsonObject)restaurants.get(i);
                    JsonElement jRestaurantAddress = restaurant.get("RELAX_ADD1");  // 주소1
                    JsonElement jRestaurantName = restaurant.get("RELAX_RSTRNT_NM"); // 가게이름
                    JsonElement jRestaurantCall = restaurant.get("RELAX_RSTRNT_TEL"); // 전화번호

                    Restaurant res_obj = new Restaurant();

                    // 이름,주소, 전화번호
                    res_obj.setAddress(jRestaurantAddress.getAsString()); // String address = jRestaurantAddress.getAsString();
                    res_obj.setName(jRestaurantName.getAsString()); // String name = jRestaurantName.getAsString();
                    res_obj.setCall(jRestaurantCall.getAsString()); // String call = jRestaurantCall.getAsString();

                    mRestList.add(res_obj);
                }
            }
        });
    }

    // 지도 위치 변경 시 불려지는 메소드
    public void getRestaurant(LatLng latLng) {
        double lat,lng ;

        Iterator<Restaurant> it = mRestList.iterator();
        while(it.hasNext()) {
            Restaurant res = it.next();
            lat = latLng.latitude - res.getLat();
            lng = latLng.longitude - res.getLng();

            // 코로나 안심 음식점이 아닌 조건
            if(res.getCheck() != "Y" ) { // + Date 값 비교
                // lat 365 ~ -270  / lng 350 -360  + 만약 결괏값이 30개 이상이면 lat/lng 범위 줄이기
                if((lat < 0.365 || lat > -0.270) || (lng < 0.350 || lng > -0.360)) {
                    marker = new Marker();
                    marker.setPosition(latLng);
                    marker.setMap(mNaverMap);
                }
            }
        }
    }

    // Marker 누르면 실행되는 메소드
    public void onClickMarker(View v) {
        Intent it = new Intent(getApplicationContext(), RestaurantInfoActivity.class);

        it.putExtra("name", insert_loc);
        it.putExtra("LatLng", latLng);

        setResult(RESULT_OK, it);
        finish();
    }


    public void onClickHome(View v) {
        finish();
    }

    public void onClickSearch(View v) {
        insert_loc = mEditText2.getText().toString().trim();

        setMapPos(insert_loc);
        mEditText2.setText("");

        // search버튼 누르면 지도에 내가 검색한 위치 뜨게하기
        // 처음에 지도는 그냥 기본 위치로 지정
    }
}