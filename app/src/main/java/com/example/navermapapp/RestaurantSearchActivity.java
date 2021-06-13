package com.example.navermapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import needle.Needle;
import needle.UiRelatedTask;

public class RestaurantSearchActivity extends AppCompatActivity {

    // 1) 직접 코딩 - 변수설정
    private EditText mEditText ;
    private String insert_loc ;
    private String mLatestText;  // 최근 검색을 통해서 받아온 텍스트
    private ListView mListView ;
    private SimpleAdapter mSimpleAdapter;
    private ArrayList<HashMap<String,String>> mListData;
    // 인텐트 이동시 사용하는 array
    // 1) 직접 코딩
    private ArrayList<Restaurant> mListRest;

    // 검색 결과 여부
    // 1) 직접 코딩
    private boolean isHavingResult = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        // 1) 직접 코딩
        mListData = new ArrayList<>();
        mListRest = new ArrayList<>();
        mEditText = findViewById(R.id.searchText1);

        // 1) 직접 코딩 (수업 중 사용 코드 응용)
        mSimpleAdapter = new SimpleAdapter(this, mListData, R.layout.list_item_style
                ,new String[] {"name","address","gubun_detail"}, new int[] {R.id.text1, R.id.text2, R.id.text3});
        mListView = findViewById(R.id.list);
        mListView.setAdapter(mSimpleAdapter);

        // 1) 직접 코딩
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 1) 직접 코딩
                if(mListData.size() > 0) {
                    // 결괏값이 있을 경우
                    // 1) 직접 코딩
                    if(isHavingResult) {
                        // 1) 직접 코딩
                        Intent it = new Intent(getApplicationContext(), RestaurantInfoActivity.class);
                        // 1) 직접 코딩
                        Restaurant res = mListRest.get(position);

                        // 2) 출처 : https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
                        it.putExtra("restaurant",new Restaurant(res.getAddress(), res.getName(), res.getCall(), res.getGubun(), res.getGubunDetail()));
                        // 1) 직접 코딩
                        startActivity(it);
                    }
                }
            }
        });
        // 1) 직접 코딩
        Intent it = getIntent();
        mLatestText = it.getStringExtra("search_text");

        // 1) 직접 코딩
        if(mLatestText != null && !mLatestText.isEmpty()) {
            mEditText.setText(mLatestText);
            insert_loc = mLatestText;
            connectRestaurantsDB();
            mEditText.setText("");
            mListView.clearChoices();
            mListView.setSelection(0);
        }
    }


    // ======================== //
    // Public DB Setting        //
    // ======================== //
    // 1) 직접 코딩 (수업 중 사용 코드 응용)
    public void connectRestaurantsDB() {
        // https://www.data.go.kr/data/15061948/openapi.do
        // 1) 직접 코딩
        String temp_Url = "http://211.237.50.150:7080/openapi/599172167c8d647c6f00e44d681829dcde7e539d49e9b67f6250e8837fa71f6a/json/Grid_20200713000000000605_1/1/1000?RELAX_USE_YN=Y&RELAX_SI_NM=%EA%B2%BD%EA%B8%B0%EB%8F%84&RELAX_SIDO_NM=";
        // 1) 직접 코딩
        final String sUrl = temp_Url + mEditText.getText().toString().trim(); // 경기도 검색

        // 1) 직접 코딩 (수업 중 사용 코드 응용)
        Needle.onBackgroundThread().execute(new UiRelatedTask<String>() {
            @Override //db데이터 얻어오기
            protected String doWork() {
                URL url = null;
                HttpURLConnection con = null;

                // 1) 직접 코딩
                try {
                    url = new URL(sUrl);
                    con = (HttpURLConnection)url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 1) 직접 코딩 + 수업 중 사용 코드 응용
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
            // 1) 직접 코딩 (수업 중 사용 코드 응용)
            @Override
            protected void thenDoUiRelatedWork(String s) {
                // 1) 직접 코딩
                JsonObject jsonObject1 = (JsonObject)JsonParser.parseString(s);
                // 1) 직접 코딩
                JsonObject jsonObject2 = (JsonObject)jsonObject1.get("Grid_20200713000000000605_1");

                // 빈 칸으로 입력했을 경우확인 -> 잘못입력했을 경우확인
                // 1) 직접 코딩
                if(jsonObject2 == null || jsonObject2.get("totalCnt").toString().equals("0")) {
                    if(!mListData.isEmpty()) {
                        mListData.removeAll(mListData);
                    }

                    // 1) 직접 코딩
                    HashMap<String, String> hitem = new HashMap<>();
                    // 1) 직접 코딩
                    isHavingResult = false;
                    hitem.put("name", "검색 결과가 없습니다.   x_x");
                    hitem.put("address", "다시 입력해주세요");
                    hitem.put("gubun_detail", "");

                    // 1) 직접 코딩
                    mListData.add(hitem);
                    // 1) 직접 코딩
                    mSimpleAdapter.notifyDataSetChanged();
                    return ;
                }
                // 1) 직접 코딩
                JsonArray restaurants = (JsonArray)jsonObject2.get("row");

                // 1) 직접 코딩
                if(restaurants == null ) {
                    Toast.makeText(getApplicationContext(), "안심 식당이 없거나 찾지못했습니다.", Toast.LENGTH_LONG).show();
                    return ;
                }
                // 이전 검색 기록 지우기
                // 1) 직접 코딩
                if(!mListData.isEmpty()) {
                    mListData.removeAll(mListData);
                    mListRest.removeAll(mListRest);
                }
                for(int i =0; i < restaurants.size(); i++) {
                    // 1) 직접 코딩
                    JsonObject restaurant = (JsonObject)restaurants.get(i);
                    JsonElement jRestaurantAddress = restaurant.get("RELAX_ADD1");  // 주소1
                    JsonElement jRestaurantName = restaurant.get("RELAX_RSTRNT_NM"); // 가게이름
                    JsonElement jRestaurantCall = restaurant.get("RELAX_RSTRNT_TEL"); // 전화번호
                    JsonElement jRestaurantGubun = restaurant.get("RELAX_GUBUN");
                    JsonElement jRestaurantGubunDetail = restaurant.get("RELAX_GUBUN_DETAIL");

                    // 1) 직접 코딩
                    Restaurant res_obj = new Restaurant();

                    // 이름,주소, 전화번호
                    // 1) 직접 코딩
                    res_obj.setAddress(jRestaurantAddress.getAsString()); // String address = jRestaurantAddress.getAsString();
                    res_obj.setName(jRestaurantName.getAsString()); // String name = jRestaurantName.getAsString();
                    res_obj.setCall(jRestaurantCall.getAsString()); // String call = jRestaurantCall.getAsString();
                    res_obj.setGubun(jRestaurantGubun.getAsString()); // -
                    res_obj.setGubunDetail(jRestaurantGubunDetail.getAsString()); // -

                    // 1) 직접 코딩
                    HashMap<String, String> hitem = new HashMap<>();
                    hitem.put("name",res_obj.getName());
                    hitem.put("address", res_obj.getAddress());
                    hitem.put("gubun_detail", res_obj.getGubunDetail());

                    // 1) 직접 코딩
                    mListData.add(hitem);
                    mListRest.add(res_obj);
                    // 1) 직접 코딩
                    mSimpleAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    // 1) 직접 코딩
    public void onClickHome(View v) {
        Intent it = getIntent();
        it.putExtra("search_text", insert_loc);
        setResult(RESULT_OK,it);
        finish();
    }

    // 1) 직접 코딩
    public void onClickSearch(View v) {
        insert_loc = mEditText.getText().toString().trim();

        // 1) 직접 코딩
        connectRestaurantsDB();
        mEditText.setText("");
        // 1) 직접 코딩
        mListView.clearChoices();
        // 1) 직접 코딩
        mListView.setSelection(0);
    }
}