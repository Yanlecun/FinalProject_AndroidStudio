package com.example.navermapapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.icu.text.Edits;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import needle.Needle;
import needle.UiRelatedTask;

public class RestaurantSearchActivity extends FragmentActivity implements OnMapReadyCallback {

    private EditText mEditText ;
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

        mEditText = findViewById(R.id.searchText);
        mRestList = new ArrayList<>();

        // id에 해당하는 fragment 가져오기
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // 지도 준비가 됬다고 알리는 이벤트 처리기 등록
        mapFragment.getMapAsync(this);  // -> onMapReady
 //       connectRestaurantsDB();
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
        };

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

//    }

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

    // ======================== //
    // Public DB Setting        //
    // ======================== //
    private void connectRestaurantsDB() {
        // https://www.data.go.kr/data/15061948/openapi.do
        final String sUrl = "https://data.mafra.go.kr/opendata/data/indexOpenDataDetail.do?data_id=20200713000000001391&service_ty=&filter_ty=G&sort_id=regist_dt&s_data_nm=&cl_code=&instt_id=201410120001&shareYn=";
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
                    JsonElement jRestaurantAddress = restaurant.get("RELAX_ADD1");
                    JsonElement jRestaurantName = restaurant.get("RELAX_RSTRNT_NM");
                    JsonElement jRestaurantCall = restaurant.get("RELAX_RSTRNT_TEL");

                    // 안심식당 여부
                    JsonElement jRestaurantSafetyCheck = restaurant.get("RELAX_USE_YN");

                    // 안심식당 지정 시작-종료일
                    JsonElement jRestaurantDayStart = restaurant.get("RELAX_RSTRNT_REG_DT");
                    JsonElement jRestaurantDayEnd = restaurant.get("RELAX_RSTRNT_CNCL_DT");

                    // =================================변수에 저장 ====================== //
                    // 안심식당 위치 위도/경도
                    JsonElement jLat = restaurant.get("LA");
                    JsonElement jLng = restaurant.get("LO");

                    Restaurant res_obj = new Restaurant();
                    // 이름,주소, 전화번호
                    res_obj.setAddress(jRestaurantAddress.getAsString()); // String address = jRestaurantAddress.getAsString();
                    res_obj.setName(jRestaurantName.getAsString()); // String name = jRestaurantName.getAsString();
                    res_obj.setCall(jRestaurantCall.getAsString()); // String call = jRestaurantCall.getAsString();
                    // 안심 음식점 여부
                    res_obj.setCheck(jRestaurantSafetyCheck.getAsString()); //String check = jRestaurantSafetyCheck.getAsString();
                    // 안심 음식점 지정 시작/종료
                    res_obj.setDayStart(jRestaurantDayStart.getAsString()); // String dayStart = jRestaurantDayStart.getAsString();
                    res_obj.setDayEnd(jRestaurantDayEnd.getAsString()); // String dayEnd = jRestaurantDayEnd.getAsString();
                    // 위치
                    res_obj.setLat(jLat.getAsDouble()); //double lat = jLat.getAsDouble();
                    res_obj.setLng(jLng.getAsDouble()); // double lng = jLng.getAsDouble();

                    mRestList.add(res_obj);
                }
            }
        });
    }

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
                if((lat < 0.365 || lat > -0.270) || lng < 0.350 || lng > -0.360) {

                }
            }
        }
    }

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
        insert_loc = mEditText.getText().toString().trim();

        setMapPos(insert_loc);
        mEditText.setText("");

        // search버튼 누르면 지도에 내가 검색한 위치 뜨게하기
        // 처음에 지도는 그냥 기본 위치로 지정
    }
}