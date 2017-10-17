package com.hooooong.bicycle;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Android Hong on 2017-10-17.
 */

public class Remote {
    public static String getData(String urlString) {
        int runCount = 0;
        boolean runFlag = true;
        StringBuilder result = new StringBuilder();


        while (runFlag) {

            try {
                // Network 처리
                // 1. URL 객체 선언 ( 웹 주소를 가지고 생성 )
                URL url = new URL(urlString);
                // 2. URL 객체에서 서버 연결을 해준다
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // 3. Connection 방식을 선언 ( Default : GET )
                urlConnection.setRequestMethod("GET");

                // 통신이 성공적인지 체크
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // 4. 연결되어 있는 Stream 을 통해서 Data 를 가져온다.
                    // 여기서부터는 File 에서 Data 를 가져오는 방식과 동일
                    InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader br = new BufferedReader(isr);

                    String temp = "'";
                    while ((temp = br.readLine()) != null) {
                        result.append(temp).append("\n");
                    }

                    // 5. 연결 Stream 을 닫는다.
                    br.close();
                    isr.close();
                } else {
                    Log.e("ServerError", urlConnection.getResponseCode() + " , " + urlConnection.getResponseMessage());
                }
                urlConnection.disconnect();
                runFlag = false;
            } catch (Exception e) {
                Log.e("Error", e.toString());
                e.printStackTrace();

                runFlag = true;
                runCount++;

                if(runCount == 3){
                    runFlag = false;
                    return Const.CONNECTION_ERROR;
                }
            }
        }

        return result.toString();
    }
}
