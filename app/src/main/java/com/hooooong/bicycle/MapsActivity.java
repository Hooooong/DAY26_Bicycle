package com.hooooong.bicycle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.hooooong.bicycle.model.Bicycle;
import com.hooooong.bicycle.model.Row;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // 좌표 데이터를 저장하기 위한 저장소
    private List<Row> rowList;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        load();
    }

    private void load() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                return Remote.getData(Const.BICYCLE_URL);
            }

            @Override
            protected void onPostExecute(String result) {

                Gson gson = new Gson();
                Bicycle bicycle = gson.fromJson(result, Bicycle.class);
                rowList = Arrays.asList(bicycle.getGeoInfoBikeConvenientFacilitiesWGS().getRow());

                // Map 이 사용할 준비가 되었는지 비동기로 확인하는 작업
                mapFragment.getMapAsync(MapsActivity.this);
                // 사용할 준비가 되었으면
                // OnMapReadyCallback.onMapReady() 를 호출한다.
            }
        }.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng korea = new LatLng(37.524365, 126.977971);
        for (int i = 0; i < rowList.size(); i++) {
            Row row = rowList.get(i);
            LatLng sit = new LatLng(Double.parseDouble(row.getLAT()), Double.parseDouble(row.getLNG()));
            mMap.addMarker(new MarkerOptions().position(sit).title(row.getCLASS()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(korea, 10));
    }
}
