# Google Map( JSON Data 사용 )

### 설명
____________________________________________________

![Bicycle](https://github.com/Hooooong/DAY26_Bicycle/blob/master/image/Bicycle.gif)

- JSON Parsing 예제 ( 서울시 공공데이터 사용 )
- Google Map 을 사용하여 Marker 출력하기

### KeyPoint
____________________________________________________

- Json

  - JSON 데이터를 Parsing 하기 위해서는 변수 명이 JSON 데이터 이름과 동일해야 한다.

  ```json
  [
    {
    "id":1,
    "name":"이흥기",
    "hobby":["게임", "음악듣기", "영화감상"],
    "friends":[
                { "name":"김철",
                  "phone":"010-0000-0000"
                },
                {
                  "name":"임수빈",
                  "phone":"010-1111-2222"
                }
              ]
    }
  ]
  ```

  ```java
  class JsonClass{
  // 최상위 json class 이름은 임의로 지정해야 한다.
    int id;
    String name;
    String hobby[];
    Friends friends[];
  }

  class Friends{
    String name;
    String phone;
  }
  ```

  - Json 형식을 POJO Class로 변환하는 Web Site : [sodhanalibrary](http://pojo.sodhanalibrary.com/)

  - 참조 : [JSON](https://github.com/Hooooong/DAY25_JSONData), [서울 열린데이터 광장](http://data.seoul.go.kr/index.jsp)

- Google Map 사용

  - google map API 를 사용하여 지도를 보여줄 수 있다.

  - [Google API](https://console.developers.google.com) 에서 api key 를 발급받아 사용한다.

  - xml 설정

  ```XML
  <!-- Google Map 의 Fragment 를 new 해서 넣는 코드 -->
  <!-- android:name="com.google.android.gms.maps.SupportMapFragment" -->

  <fragment xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:map="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:id="@+id/map"
      android:name="com.google.android.gms.maps.SupportMapFragment"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:context="com.hooooong.bicycle.MapsActivity" />
  ```

  - java 설정

  ```java
  // 0. OnMapReadyCallback 를 구현하거나, implements 를 하여 onMapReady() 를 Override 한다.
  public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // 1. SupportMapFragment 를 사용하여 mapFragment 를 준비한다.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // 2. Map 이 사용할 준비가 되었는지 비동기로 확인하는 작업
        mapFragment.getMapAsync(MapsActivity.this);
        // 사용할 준비가 되었으면
        // 3. OnMapReadyCallback.onMapReady() 를 호출한다.
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // 현재 지도에 중심점이 되는 좌표를 설정한다.
        LatLng korea = new LatLng(37.524365, 126.977971);

        // 현재 지도에 Marker 를 출력한다.
        // position : 좌표
        // title : Marker 를 눌렀을 때 출력하는 Text
        mMap.addMarker(new MarkerOptions().position(korea).title("KOREA"));

        // 현재 지도에서의 Camera(View) 를 움직이는 메소드
        // CameraUpdateFactory.newLatLngZoom(new LatLng(123, 123), 10) : 좌표와 ZOOM Level 을
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(korea, 10));
    }
  }
  ```

### Code Review
____________________________________________________

- MapActivity.java

  - Google Map 에 Marker 를 출력하는 클래스

  - 서울 공공 데이터(JSON)를 가져와 Parsing 하여 좌표를 얻어온다.

  ```java
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
  ```

- Remote.java

  - HttpURLConnection 을 통하여 JSONString 을 가져오는 클래스

  ```java
  public class Remote {
      public static String getData(String urlString) {
          // 반복 횟수
          int runCount = 0;
          // 정상적으로 돌아갔는지에 대한 구분값
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
  ```

- activity_maps.XML

  ```XML
  <!-- Google Map 의 Fragment 를 new 해서 넣는 코드 -->
  <!-- android:name="com.google.android.gms.maps.SupportMapFragment" -->

  <fragment xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:map="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      android:id="@+id/map"
      android:name="com.google.android.gms.maps.SupportMapFragment"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      tools:context="com.hooooong.bicycle.MapsActivity" />
  ```
