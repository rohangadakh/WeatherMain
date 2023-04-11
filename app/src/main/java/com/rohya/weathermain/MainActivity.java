package com.rohya.weathermain;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView txt_result, txt_time, txt_date, txt_day;

    private static final int REQUEST_LOCATION = 1;

    private ArrayList<weatherRvModel> weatherRvModelArrayList;

    private weatherRvAdapter weatherRvAdapter;

    EditText cityName;

    String code;
    Double temp = 0.0;
    String chance_of_rain;

    RecyclerView weatherRV;

    private TextToSpeech mTTS;

    ImageView img_update;

    int isday = 1;

    SwipeRefreshLayout swipeRefreshLayout;

    double latitude, longitude;

    LocationManager locationManager;

    private final String apiKey = "50bd684bcac746099f0122454230604";

    DecimalFormat df = new DecimalFormat("#.##");

    LottieAnimationView lottieAnimationView;

       public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public String getChance_of_rain() {
        return chance_of_rain;
    }

    public void setChance_of_rain(String chance_of_rain) {
        this.chance_of_rain = chance_of_rain;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lottieAnimationView = findViewById(R.id.lott_weather);

        txt_result = findViewById(R.id.txt_result);
        txt_time = findViewById(R.id.txt_time);

        img_update = findViewById(R.id.img_update);

        swipeRefreshLayout = findViewById(R.id.refresh);

        cityName = findViewById(R.id.ed_city);

        weatherRvModelArrayList = new ArrayList<>();
        weatherRvAdapter = new weatherRvAdapter(this, weatherRvModelArrayList);
        weatherRV = findViewById(R.id.idRvWeather);
        weatherRV.setAdapter(weatherRvAdapter);

        lottieAnimationView.setRepeatCount(-1);

        getWeatherDetails();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherDetails();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

            mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        int result = mTTS.setLanguage(Locale.getDefault());
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "Language not supported");
                        }
                    } else {
                        Log.e("TTS", "Initialization failed");
                    }
                }
            });

    }
    public void getWeatherDetails() {

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Check gps is enable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            //Write Function To enable gps
            OnGPS();
        } else {

            //GPS is already On then
            getLocation();

            String url;
            String cn = cityName.getText().toString();
            if(!cn.equals(""))
            {
                url = "http://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + cn + "&days=1&aqi=no&alerts=no";
            }
            else
            {
                url = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + latitude + "," + longitude + "&days=1";
            }

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Log.d("response", response);

                            String output = "";
                            try {

                                JSONObject jsonResponse = new JSONObject(response);

                                JSONObject jsonObjectLocation = jsonResponse.getJSONObject("location");
                                String cityName = jsonObjectLocation.getString("name");
                                String region = jsonObjectLocation.getString("region");
                                String country = jsonObjectLocation.getString("country");

                                JSONObject jsonObjectCurrent = jsonResponse.getJSONObject("current");
                                temp = jsonObjectCurrent.getDouble("temp_c");
                                Double feelsLike = jsonObjectCurrent.getDouble("feelslike_c");
                                JSONObject jsonObjectCurrentCondition = jsonObjectCurrent.getJSONObject("condition");
                                String text = jsonObjectCurrentCondition.getString("text");
                                code = jsonObjectCurrentCondition.getString("code");

//                              String icon = jsonObjectCurrentCondition.getString("icon");

                                JSONObject jsonObjectForecast = jsonResponse.getJSONObject("forecast");
                                JSONArray jsonArray = jsonObjectForecast.getJSONArray("forecastday");
                                JSONObject jsonObjectForecastday = jsonArray.getJSONObject(0);

                                JSONObject jsonObjectDay = jsonObjectForecastday.getJSONObject("day");
                                Double minTemp = jsonObjectDay.getDouble("mintemp_c");
                                Double maxTemp = jsonObjectDay.getDouble("maxtemp_c");
                                String visibility = jsonObjectDay.getString("avgvis_km");
                                String willItRain = jsonObjectDay.getString("daily_will_it_rain");
                                chance_of_rain = jsonObjectDay.getString("daily_chance_of_rain");
                                String willItSnow = jsonObjectDay.getString("daily_will_it_snow");
                                String chanceOfSnow = jsonObjectDay.getString("daily_chance_of_snow");
                                String avghumidity = jsonObjectDay.getString("avghumidity");
                                JSONObject jsonObjectCondition = jsonObjectDay.getJSONObject("condition");

                                JSONObject jsonObjectAstro = jsonObjectForecastday.getJSONObject("astro");
                                String sunrise = jsonObjectAstro.getString("sunrise");
                                String sunset = jsonObjectAstro.getString("sunset");

                                output += "Current weather - " + cityName + ", " + region + " (" + country + ")"
                                        + "\nTemperature : " + df.format(temp) + " °C"
                                        + ", Feels Like : " + df.format(feelsLike) + " °C"
                                        + "\nDescription : " + text + ", Code : " + code
                                        + "\nMinimum Temperature : " + df.format(minTemp) + " °C"
                                        + "\nMaximum Temperature : " + df.format(maxTemp) + " °C"
                                        + "\nHumidity : " + avghumidity + "%"
                                        + "\nVisibility : " + visibility + " km"
                                        + "\nChances of Rain : " + chance_of_rain + "%"
                                        + "\nChances of Snow : " + chanceOfSnow + "%";

                                txt_result.setText(output);

                                String speaktext = "Current weather of " + cityName + ", of region " + region + ", " + country + ". Temperature is " + df.format(temp) + "°C, and it feels like " +  df.format(feelsLike) + "°C. Minimum Temperature is " + df.format(minTemp) + "°C, Maximum Temperature is " + df.format(maxTemp) + "°C. Humidity is " + avghumidity + "%. Visibility is " + visibility + "%. Chances of rain " + chance_of_rain + "%. Chances of snow " + chanceOfSnow + "%. This is all about the current Temperature and the today's weather. Have Great Day !!! ";
                                speak(speaktext);

                                weatherRvModelArrayList.clear();

                                try {
                                    JSONArray jsonObjectHour = jsonObjectForecastday.getJSONArray("hour");

                                    for (int i = 0; i < jsonObjectHour.length(); i++) {
                                        JSONObject hour = jsonObjectHour.getJSONObject(i);
                                        String time = hour.getString("time");
                                        String temp_c = hour.getString("temp_c");
                                        double temp_f = hour.getDouble("temp_f");
                                        isday = hour.getInt("is_day");
                                        JSONObject condition = hour.getJSONObject("condition");
                                        String txt = condition.getString("text");
                                        String icn = condition.getString("icon");
                                        String cod = condition.getString("code");

                                        Log.d("Hour", "onResponse: "+ time + "_" + temp_c + "_" + temp_f + "_"+ isday + "_" + txt  + "_" + icn  + "_" + cod );
                                        weatherRvModelArrayList.add(new weatherRvModel(time, temp_c, cod, isday));
                                    }

                                    weatherRvAdapter.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Calendar calendar = Calendar.getInstance();
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                                if (isday==1)
                                {
                                    // It's daytime
                                    Log.d("Time", "It's daytime.");

                                    switch (code) {

                                        case "1000":
                                            img_update.setImageResource(R.drawable.sunny_1000);
                                            break;
                                        case "1003":
                                        case "1006":
                                        case "1009":
                                            img_update.setImageResource(R.drawable.cloudy_1006);
                                            break;
                                        case "1030":
                                        case "1135":
                                        case "1147":
                                            img_update.setImageResource(R.drawable.fog_day_1135);
                                            break;
                                        case "1063":
                                        case "1240":
                                        case "1150":
                                        case "1153":
                                        case "1168":
                                        case "1171":
                                        case "1180":
                                        case "1183":
                                        case "1186":
                                        case "1189":
                                            img_update.setImageResource(R.drawable.rain_1063);
                                            break;
                                        case "1066":
                                        case "1069":
                                        case "1072":
                                        case "1114":
                                        case "1117":
                                        case "1204":
                                        case "1207":
                                        case "1210":
                                        case "1213":
                                        case "1216":
                                        case "1219":
                                        case "1222":
                                        case "1225":
                                        case "1237":
                                        case "1249":
                                        case "1252":
                                        case "1255":
                                        case "1258":
                                        case "1261":
                                            img_update.setImageResource(R.drawable.snow_1204);
                                            break;
                                        case "1192":
                                        case "1195":
                                        case "1198":
                                        case "1201":
                                            img_update.setImageResource(R.drawable.heavy_rain_1192);
                                            break;
                                        case "1087":
                                        case "1264":
                                        case "1273":
                                        case "1243":
                                        case "1246":
                                        case "1276":
                                            img_update.setImageResource(R.drawable.chance_of_storm);
                                            break;
                                    }
                                } else {
                                    // It is currently nighttime
                                    switch (code) {

                                        case "1000":
                                            img_update.setImageResource(R.drawable.moon_1000);
                                            break;
                                        case "1003":
                                        case "1006":
                                        case "1009":
                                            img_update.setImageResource(R.drawable.night_cloud_1006);
                                            break;
                                        case "1030":
                                        case "1135":
                                        case "1147":
                                            img_update.setImageResource(R.drawable.fog_night_1135);
                                            break;
                                        case "1063":
                                        case "1240":
                                        case "1150":
                                        case "1153":
                                        case "1168":
                                        case "1171":
                                        case "1180":
                                        case "1183":
                                        case "1186":
                                        case "1189":
                                            img_update.setImageResource(R.drawable.rain_1063);
                                            break;
                                        case "1066":
                                        case "1069":
                                        case "1072":
                                        case "1114":
                                        case "1117":
                                        case "1204":
                                        case "1207":
                                        case "1210":
                                        case "1213":
                                        case "1216":
                                        case "1219":
                                        case "1222":
                                        case "1225":
                                        case "1237":
                                        case "1249":
                                        case "1252":
                                        case "1255":
                                        case "1258":
                                        case "1261":
                                            img_update.setImageResource(R.drawable.snow_1204);
                                            break;
                                        case "1192":
                                        case "1195":
                                        case "1198":
                                        case "1201":
                                            img_update.setImageResource(R.drawable.heavy_rain_1192);
                                            break;
                                        case "1087":
                                        case "1264":
                                        case "1273":
                                        case "1243":
                                        case "1246":
                                        case "1276":
                                            img_update.setImageResource(R.drawable.chance_of_storm);
                                            break;
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new

                    DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }
    private void getLocation() {

        //Check Permissions again
        if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps !=null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();

                latitude= (lat);
                longitude= (longi);

            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();

                latitude= (lat);
                longitude= (longi);

            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();

                latitude= (lat);
                longitude= (longi);
            }
        }
    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                txt_result.setText("Please enable GPS");
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
    private void speak(String text)
    {
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

}