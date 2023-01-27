package com.rohya.weathermain;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView txt_result, txt_time, txt_date, txt_day;

    private static final int REQUEST_LOCATION = 1;

    ImageView img_update;

    Button btn_read;

    TextToSpeech tts;

    SwipeRefreshLayout swipeRefreshLayout;

    double latitude, longitude;
    LocationManager locationManager;

    private static final int SPEECH_REQUEST_CODE = 0;

    private final String apiKey = "2d345f5d7dd14460a9474250232301";

    DecimalFormat df = new DecimalFormat("#.##");

    LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lottieAnimationView = findViewById(R.id.lott_weather);

        txt_result = findViewById(R.id.txt_result);
        txt_time = findViewById(R.id.txt_time);
        txt_date = findViewById(R.id.txt_date);
        txt_day = findViewById(R.id.txt_day);

        img_update = findViewById(R.id.img_update);

        swipeRefreshLayout = findViewById(R.id.refresh);

        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance().format(calendar.getTime());
        txt_date.setText(date);
        String day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        txt_day.setText(day);

        lottieAnimationView.setRepeatCount(-1);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherDetails();

                swipeRefreshLayout.setRefreshing(false);
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
//            Toast.makeText(this, latitude + "  " + longitude, Toast.LENGTH_SHORT).show();
        }

            String url = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + latitude + "," + longitude + "&days=1";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // parse the response and update UI
                            Log.d("response", response);

                            String output = "";
                            try {

                                JSONObject jsonResponse = new JSONObject(response);

                                JSONObject jsonObjectLocation = jsonResponse.getJSONObject("location");
                                String cityName = jsonObjectLocation.getString("name");
                                String region = jsonObjectLocation.getString("region");
                                String country = jsonObjectLocation.getString("country");

                                JSONObject jsonObjectCurrent = jsonResponse.getJSONObject("current");
                                Double temp = jsonObjectCurrent.getDouble("temp_c");
                                Double feelsLike = jsonObjectCurrent.getDouble("feelslike_c");
                                JSONObject jsonObjectCurrentCondition = jsonObjectCurrent.getJSONObject("condition");
                                String text = jsonObjectCurrentCondition.getString("text");
                                String icon = jsonObjectCurrentCondition.getString("icon");
                                String code = jsonObjectCurrentCondition.getString("code");

                                JSONObject jsonObjectForecast = jsonResponse.getJSONObject("forecast");
                                JSONArray jsonArray = jsonObjectForecast.getJSONArray("forecastday");
                                JSONObject jsonObjectForecastday = jsonArray.getJSONObject(0);

                                JSONObject jsonObjectDay = jsonObjectForecastday.getJSONObject("day");
                                Double minTemp = jsonObjectDay.getDouble("mintemp_c");
                                Double maxTemp = jsonObjectDay.getDouble("maxtemp_c");
                                String visibility = jsonObjectDay.getString("avgvis_km");
                                String willItRain = jsonObjectDay.getString("daily_will_it_rain");
                                String chanceOfRain = jsonObjectDay.getString("daily_chance_of_rain");
                                String willItSnow = jsonObjectDay.getString("daily_will_it_snow");
                                String chanceOfSnow = jsonObjectDay.getString("daily_chance_of_snow");
                                String avghumidity = jsonObjectDay.getString("avghumidity");
                                JSONObject jsonObjectCondition = jsonObjectDay.getJSONObject("condition");
                                String textDay = jsonObjectCondition.getString("text");
                                String iconDay = jsonObjectCondition.getString("icon");

                                JSONObject jsonObjectAstro = jsonObjectForecastday.getJSONObject("astro");
                                String sunrise = jsonObjectAstro.getString("sunrise");
                                String sunset = jsonObjectAstro.getString("sunset");

                                Log.d("TIME", "onResponse: "+ sunrise + " " + sunset);

                                output += "Current weather - " + cityName + ", " + region + " (" + country + ")"
                                        + "\nTemperature : " + df.format(temp) + " °C"
                                        + "\nFeels Like : " + df.format(feelsLike) + " °C"
                                        + "\nDescription : " + text
                                        + "\nMinimum Temperature : " + df.format(minTemp) + " °C"
                                        + "\nMaximum Temperature : " + df.format(maxTemp) + " °C"
                                        + "\nHumidity : " + avghumidity + "%"
                                        + "\nVisibility : " + visibility + " km"
                                        + "\nWill it Rain : " + willItRain
                                        + "\nChances of Rain : " + chanceOfRain + "%"
                                        + "\nWill it Snow : " + willItSnow
                                        + "\nChances of Snow : " + chanceOfSnow + "%";

                                txt_result.setText(output);

                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
                                Date sunriseDate = sdf.parse(sunrise);
                                Date sunsetDate = sdf.parse(sunset);

                                LocalTime sunriseTime = LocalTime.of(sunriseDate.getHours(), sunriseDate.getMinutes());
                                LocalTime sunsetTime = LocalTime.of(sunsetDate.getHours(), sunsetDate.getMinutes());
                                LocalDateTime now = LocalDateTime.now();
                                if (now.toLocalTime().isAfter(sunriseTime) && now.toLocalTime().isBefore(sunsetTime)) {    // It is currently daytime

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
                                } else {
                                    // It is currently nighttime

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
                                }

                            } catch (JSONException | ParseException e) {
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

}
