package com.example.navermapapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RestaurantInfoActivity extends FragmentActivity implements OnMapReadyCallback {
    NaverMap mNaverMap;
    MapFragment mapFragment;

    Marker marker;
    LatLng latLng = new LatLng(37.5796, 126.9770);

    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        // Intent로 값들 가져오기 + textView text지정하기
        Intent it = getIntent();


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
        setMapPos("경복궁"); //사용자 클릭 장소
    }

    public void setMapPos(String insert_loc) {
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

    }

    // Marker 누르면 실행되는 메소드
//    public void onClickMarker(View v) {
//        Intent it = new Intent(getApplicationContext(), RestaurantInfoActivity.class);
//
//        it.putExtra("name", insert_loc);
//        it.putExtra("LatLng", latLng);
//
//        setResult(RESULT_OK, it);
//        finish();
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode != 200 || resultCode != RESULT_OK) {
//            Toast.makeText(getApplicationContext(),"추가하기 실패", Toast.LENGTH_SHORT).show();
//            return ;
//        }
//        LatLng latLng = data.getExtras().getParcelable("LatLng");
//
//        HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("name", data.getStringExtra("name"));
//        hashMap.put("id", String.valueOf(++id));
//
//        hashMap.put("lat", String.valueOf(latLng.latitude));
//        hashMap.put("lng", String.valueOf(latLng.longitude));
//
//        mListData.add(hashMap);
//        mSimpleAdapter.notifyDataSetChanged();
    }

    public void onClickHome(View v) {
        Intent it = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(it);
    }

    // 뒤로가기 눌러도 검색한 설정 유지
    public void onClickBack(View v) {
        Intent it = new Intent(getApplicationContext(), RestaurantSearchActivity.class);
        startActivityForResult(it, 200);
    }

//    public void onClickDel(View v) {
//        if(mISelectedItem == -1)
//            Toast.makeText(getApplicationContext(), "항목을 선택해주세요", Toast.LENGTH_SHORT).show();
//
//        // arraylist remove메소드로 삭제 + hashMap에서 id값 하나씩 땡기기
//    }

//    public void onClickComplete(View v) {
//        Intent it = new Intent(getApplicationContext(), ListActivity.class);
//        if(mListData.size() < 3) {
//            Toast.makeText(getApplicationContext(), "최소 2개 이상 목적지를 추가해주세요", Toast.LENGTH_SHORT).show();
//            return ;
//        }
//
//        // 위도, 경도, 이름, id값 담은 hashMap ArrayList 넘기기
//        it.putExtra("data", mListData);
//        setResult(RESULT_OK,it);
//        finish();
//    }

    // 지도 위치 변경 시 불려지는 메소드
//    public void getRestaurant(LatLng latLng) {
//        double lat,lng ;
//
//        Iterator<Restaurant> it = mRestList.iterator();
//        while(it.hasNext()) {
//            Restaurant res = it.next();
//            lat = latLng.latitude - res.getLat();
//            lng = latLng.longitude - res.getLng();
//
//            // 코로나 안심 음식점이 아닌 조건
//            if(res.getCheck() != "Y" ) { // + Date 값 비교
//                // lat 365 ~ -270  / lng 350 -360  + 만약 결괏값이 30개 이상이면 lat/lng 범위 줄이기
//                if((lat < 0.365 || lat > -0.270) || (lng < 0.350 || lng > -0.360)) {
//                    marker = new Marker();
//                    marker.setPosition(latLng);
//                    marker.setMap(mNaverMap);
//                }
//            }
//        }
//    }


}