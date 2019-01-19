package com.example.hodhod.weatherapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.internal.http.RequestLine;

public class MainActivity extends AppCompatActivity {

    private static final String URL_FORECAST = "https://api.openweathermap.org/data/2.5/forecast?";
    private static final String URL_CURRENT = "https://api.openweathermap.org/data/2.5/weather?";
    private static final String URL_CURRENT_CITY = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String URL_FORECAST_CITY = "https://api.openweathermap.org/data/2.5/forecast?q=";
    private String cityName;
    private String latAndLon;
    private static final String APP_ID = "&units=metric&mode=json&appid=17bf81d20aeeefcb99f759ab837c84a9";

    private static final long MIN_TIME = 5000;
    private static final float MIN_DISTANCE = 1000;

    private static final int REQUEST_CODE = 123;

    private static final String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;
    // location manager will start or stop location request
    private LocationManager mLocationManager;
    //location listener will be notified if the location changed
    private LocationListener mLocationListener;

    private TextView currentDegree;
    private TextView country;
    private TextView region;
    private TextView currentCondition;
    private ImageButton imageButtonRefresh;
    private ImageButton buttonChangeCity;
    private List<Info> mInfoList;

    private StringRequest stringRequestForecast;
    private StringRequest mStringRequestCurrent;
    private StringRequest mStringRequestCityNameCurrent;
    private StringRequest mStringRequestCityNameForecast;


    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;

    public static Activity finishThis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //to setup recyclerView
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);

        mInfoList = new ArrayList<>();

        finishThis = this;

        currentDegree = findViewById(R.id.current_temp_textView);
        country = findViewById(R.id.country_textView);
        region = findViewById(R.id.region_textView);
        currentCondition = findViewById(R.id.current_weather_condition);
        imageButtonRefresh = findViewById(R.id.image_button_refresh);
        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequestForecast);
                requestQueue.add(mStringRequestCurrent);

                Toast.makeText(getApplicationContext(), "refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        buttonChangeCity = findViewById(R.id.changeCity_button);
        buttonChangeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeCityIntent = new Intent(MainActivity.this,ByCity.class);
                startActivity(changeCityIntent);
            }
        });

        Intent cityNameIntent = getIntent();
        cityName = cityNameIntent.getStringExtra("cityName");

        if(cityName != null){
            loadNewCityInfo();
            imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(mStringRequestCityNameCurrent);
                    requestQueue.add(mStringRequestCityNameForecast);

                    Toast.makeText(getApplicationContext(), "refreshed", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            loadInfo();
        }

        Log.d("testWebsite current" , URL_CURRENT + latAndLon + APP_ID);
        Log.d("testWebsite forecast" , URL_FORECAST + latAndLon + APP_ID);
    }

    private void loadInfo() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
            return;
        }

        mLocationListener = new MyLocationListener();
        mLocationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

        Location location = mLocationManager.getLastKnownLocation(LOCATION_PROVIDER);
        String longitude = String.valueOf(location.getLongitude());
        String latitude = String.valueOf(location.getLatitude());

        Log.d("currentLocation", "longitude is: " + longitude + " latitude is: " + latitude);

        latAndLon = "lat=" + latitude + "&lon=" + longitude;

        Log.d("forecastTemp", URL_FORECAST+ latAndLon + APP_ID);

        stringRequestForecast = new StringRequest(Request.Method.GET,
                URL_FORECAST + latAndLon + APP_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.getJSONArray("list");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObjectListCompare = jsonArray.getJSONObject(i + 1);
                                //time and date used to compare date
                                String timeAndDateCompare = jsonObjectListCompare.getString("dt_txt");

                                JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                //time and date
                                String timeAndDate = jsonObjectList.getString("dt_txt");

                                //unix time to be printed
                                String timeAndDateUnix = jsonObjectList.getString("dt");
                                long unixTimeAndDate = Long.parseLong(timeAndDateUnix);
                                String time = timeConverter(unixTimeAndDate);

                                //2018-12-02 forecast
                                String trimmedTimeAndDate = timeAndDate.substring(0, 10);

                                // the first element in the array
                                JSONObject jsonObjectListFirst = jsonArray.getJSONObject(0);
                                //current time and date
                                String timeAndDateCurrent = jsonObjectListFirst.getString("dt_txt");
                                //2018-12-02 current
                                String trimmedTimeAndDateCurrent = timeAndDateCurrent.substring(0, 10);
                                JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("main");

                                // temp
                                String temp = jsonObjectTemp.getString("temp");

                                String roundedTemp = roundTemp(temp);

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectDesc = jsonArrayWeather.getJSONObject(0);
                                // description
                                String weatherDesc = jsonObjectDesc.getString("description");

                                if (trimmedTimeAndDate.equals(trimmedTimeAndDateCurrent)) {

                                    Info info = new Info(roundedTemp, weatherDesc,time);
                                    mInfoList.add(info);

                                    mAdapter = new MyAdapter(mInfoList);
                                    mRecyclerView.setAdapter(mAdapter);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "no internet connection", Toast.LENGTH_LONG).show();

                    }
                }

        );


        mStringRequestCurrent = new StringRequest(Request.Method.GET,
                URL_CURRENT + latAndLon + APP_ID
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("currentLocation", "onResponseMethod");

                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    JSONObject jsonObjectTempCurrent = jsonObject1.getJSONObject("main");
                    JSONArray jsonArrayDescCurrent = jsonObject1.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArrayDescCurrent.getJSONObject(0);
                    JSONObject jsonObjectCountry = jsonObject1.getJSONObject("sys");

                    String tempCurrent = jsonObjectTempCurrent.getString("temp");

                    String roundedTemp = roundTemp(tempCurrent);

                    // current temp
                    currentDegree.setText(roundedTemp);
                    //current condition
                    currentCondition.setText(jsonObjectWeather.getString("description"));
                    // current region
                    region.setText(jsonObject1.getString("name"));
                    //current country
                    country.setText(jsonObjectCountry.getString("country"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "no internet connection", Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequestForecast);
        requestQueue.add(mStringRequestCurrent);
    }

    private void loadNewCityInfo(){

        Log.d("newCity", "loadNewCityInfo is called" );

            mStringRequestCityNameCurrent = new StringRequest(Request.Method.GET,
                    URL_CURRENT_CITY + cityName + APP_ID
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("currentLocation", "onResponseMethod");

                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        JSONObject jsonObjectTempCurrent = jsonObject1.getJSONObject("main");
                        JSONArray jsonArrayDescCurrent = jsonObject1.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArrayDescCurrent.getJSONObject(0);
                        JSONObject jsonObjectCountry = jsonObject1.getJSONObject("sys");

                        String tempCurrent = jsonObjectTempCurrent.getString("temp");

                        String roundedTemp = roundTemp(tempCurrent);

                        // current temp
                        currentDegree.setText(roundedTemp);
                        //current condition
                        currentCondition.setText(jsonObjectWeather.getString("description"));
                        // current region
                        region.setText(jsonObject1.getString("name"));
                        //current country
                        country.setText(jsonObjectCountry.getString("country"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "wrong city name", Toast.LENGTH_LONG).show();
                        }
                    });

        mStringRequestCityNameForecast = new StringRequest(Request.Method.GET,
                URL_FORECAST_CITY + cityName + APP_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.getJSONArray("list");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObjectListCompare = jsonArray.getJSONObject(i + 1);
                                //time and date used to compare date
                                String timeAndDateCompare = jsonObjectListCompare.getString("dt_txt");

                                JSONObject jsonObjectList = jsonArray.getJSONObject(i);
                                //time and date
                                String timeAndDate = jsonObjectList.getString("dt_txt");

                                //unix time to be printed
                                String timeAndDateUnix = jsonObjectList.getString("dt");
                                long unixTimeAndDate = Long.parseLong(timeAndDateUnix);
                                String time = timeConverter(unixTimeAndDate);

                                //2018-12-02 forecast
                                String trimmedTimeAndDate = timeAndDate.substring(0, 10);

                                // the first element in the array
                                JSONObject jsonObjectListFirst = jsonArray.getJSONObject(0);
                                //current time and date
                                String timeAndDateCurrent = jsonObjectListFirst.getString("dt_txt");
                                //2018-12-02 current
                                String trimmedTimeAndDateCurrent = timeAndDateCurrent.substring(0, 10);
                                JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("main");

                                // temp
                                String temp = jsonObjectTemp.getString("temp");

                                String roundedTemp = roundTemp(temp);

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectDesc = jsonArrayWeather.getJSONObject(0);
                                // description
                                String weatherDesc = jsonObjectDesc.getString("description");
                                if (trimmedTimeAndDate.equals(trimmedTimeAndDateCurrent)) {

                                    Info info = new Info(roundedTemp, weatherDesc,time);
                                    mInfoList.add(info);

                                    mAdapter = new MyAdapter(mInfoList);
                                    mRecyclerView.setAdapter(mAdapter);

                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), "wrong city name", Toast.LENGTH_LONG).show();

                    }
                }

        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(mStringRequestCityNameCurrent);
        requestQueue.add(mStringRequestCityNameForecast);

    }

    public String timeConverter(long unixTime) {
        // convert seconds to milliseconds
        Date date = new Date(unixTime * 1000L);
        // the format of your date
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm aaa");
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public String roundTemp (String temp){

        double tempDouble = Double.valueOf(temp);

        long tempLong = Math.round(tempDouble);

        String tempString = String.valueOf(tempLong);
        return tempString;

    }

}
