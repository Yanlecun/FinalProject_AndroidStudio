package com.example.navermapapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RestaurantSearchActivity extends FragmentActivity implements OnMapReadyCallback {

    private EditText mEditText ;
    private String insert_loc ;

    NaverMap mNaverMap;
    MapFragment mapFragment;

    Marker marker;
    FusedLocationProviderClient mFLPC; // 통합위치관리 제공자
    LocationCallback locationCallback;

    LatLng latLng = new LatLng(37.5796, 126.9770);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        mEditText = findViewById(R.id.searchText);

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

        //latLng = new LatLng(37.5796, 126.9770);
//        marker = new Marker();
//        marker.setMap(mNaverMap);
//        marker.setPosition(latLng);
//        marker.setCaptionText("검색한 위치");
        mNaverMap.moveCamera(CameraUpdate.scrollTo(latLng)); //시작 지점



 //       mFLPC = LocationServices.getFusedLocationProviderClient(this);

//        locationCallback = new LocationCallback() {  // 위치 변경 시 마다 불려지는 메소드
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                if (locationResult == null) {
//                    return;
//                }
//                List<Location> list = locationResult.getLocations(); // 현재 받은 result 저장
//                if (list == null || list.size() == 0) {
//                    return;
//                }
//                Location location = list.get(0);
//                latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                if (marker == null) {
//                    marker = new Marker();
//                }
//                marker.setMap(mNaverMap);
//                marker.setPosition(latLng);
//                marker.setCaptionText("검색한 위치");
//
//                mNaverMap.moveCamera(CameraUpdate.scrollTo(latLng));
//            }
//        };

        // 위치추적 권한 설정 코드
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
//                    , Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//            return;
//        }

        // ==============================================//

        //getLocation();
        //setMap("경복궁");

    }

//    // 권한 허용시 실행되는 메소드
//    @SuppressLint("MissingPermission")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode != 100) {return;}
//        if(grantResults.length >1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//            // 위치 추적 관련 코드
//            getLocation();
//        }
//    }

//    @SuppressLint("MissingPermission")
//    void getLocation() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        //locationRequest.setInterval(5 * 1000); // 5초 간격으로 정보 가져오기
//
//        mFLPC.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(mFLPC != null)
//            mFLPC.removeLocationUpdates(locationCallback); //종료시 콜백메소드종료
//    }

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


    public void onClickComplete(View v) {
        Intent it = new Intent(getApplicationContext(), RestaurantInfoActivity.class);

        it.putExtra("name", insert_loc);
        it.putExtra("LatLng", latLng);

        setResult(RESULT_OK, it);
        finish();
    }


    public void onClickCancel(View v) {
        Toast.makeText(getApplicationContext(), latLng.toString(), Toast.LENGTH_SHORT).show();
//        Intent it = new Intent(getApplicationContext(), AddActivity.class);
//        startActivity(it);
    }

    public void onClickSearch(View v) {
        insert_loc = mEditText.getText().toString().trim();

        setMapPos(insert_loc);
        mEditText.setText("");
        // search버튼 누르면 지도에 내가 검색한 위치 뜨게하기
        // 처음에 지도는 그냥 기본 위치로 지정
    }
}