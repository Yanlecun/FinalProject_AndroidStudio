package com.example.navermapapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RestaurantInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    // 1) 직접 코딩
    NaverMap mNaverMap;
    MapFragment mapFragment;
    Marker marker;
    LatLng latLng = new LatLng(37.5796, 126.9770);
    Restaurant res;
    String name ;
    String call;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        // Intent로 값들 가져오기 + textView text지정하기
        // 2) 출처 https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
        Bundle data = getIntent().getExtras();
        // 2) 출처 https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
        res = (Restaurant) data.getParcelable("restaurant");

        // 1) 직접 코딩
        name = res.getName();
        call = res.getCall();
        // 1) 직접 코딩
        TextView textViews[] = { findViewById(R.id.res_address), findViewById(R.id.res_name), findViewById(R.id.res_call), findViewById(R.id.res_gubun), findViewById(R.id.res_gubun_detail)};
        String str[] = {res.getAddress(), res.getName(), res.getCall(), res.getGubun(), res.getGubunDetail()};
        int index = 0;

        // 1) 직접 코딩
        for(TextView tv : textViews) {
            // 1) 직접 코딩
            tv.setText(str[index++]);
        }

        // id에 해당하는 fragment 가져오기
        // 1) 직접 코딩 (수업 중 사용 코드 응용)
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // 지도 준비가 됬다고 알리는 이벤트 처리기 등록
        // 1) 직접 코딩 (수업 중 사용 코드 응용)
        mapFragment.getMapAsync(this);  // -> onMapReady

    }

    // 검색해서 위치지정 / 화면드래그 가능 + 화면 클릭시 위치지정
    // 1) 직접 코딩 (수업 중 사용 코드 응용)
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        mNaverMap = naverMap;
        mNaverMap.setSymbolScale(1f);
        mNaverMap.setMapType(NaverMap.MapType.Basic);
        mNaverMap.moveCamera(CameraUpdate.zoomTo(15));

        mNaverMap.moveCamera(CameraUpdate.scrollTo(latLng));
        // 인자로 위치 넣어서 지도 위치 변경하기
        // 1) 직접 코딩
        setMapPos(name);
    }

    // 1) 직접 코딩 ( 수업 중 사용 코드 응용)
    public void setMapPos(String insert_loc) {
        // 1) 직접 코딩
        Geocoder geocoder = new Geocoder(  this, Locale.KOREA);
        System.out.println(insert_loc);
        List<Address> list = null;

        try {
            // 1) 직접 코딩
            list = geocoder.getFromLocationName(insert_loc, 5); //들어갈 부분 검색

        } catch (IOException e) {
            // 1) 직접 코딩
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "주소 변환 오류", Toast.LENGTH_SHORT).show();
        }
        // 1) 직접 코딩
        if(list == null || list.size() == 0) {
            Toast.makeText(getApplicationContext(),"해당되는 주소 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        // 1) 직접 코딩
        final Address address = list.get(0);
        double lat = address.getLatitude();
        double lng = address.getLongitude();
        latLng = new LatLng(lat, lng);

        // 1) 직접 코딩
        marker = new Marker();
        marker.setPosition(latLng);
        marker.setMap(mNaverMap);
        marker.setCaptionText(name);
        marker.setCaptionOffset(-215);
        marker.setCaptionTextSize(15);
        mNaverMap.moveCamera(CameraUpdate.scrollTo(latLng)); // 드래그로 이동 가능하도록 하기

    }

    // 1) 직접 코딩
    public void onClickHome(View v) {
        Intent it = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(it);
    }

    // 전화걸기 버튼 누르면 해당 안심식당에 전화걸기
    public void onClickCall(View v) {
        // 전화번호 replacing
        // 1) 직접 코딩
        call = call.replaceAll("-/gi","");

        // 1) 직접 코딩
        Intent it = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+call));
        startActivity(it);
    }
}