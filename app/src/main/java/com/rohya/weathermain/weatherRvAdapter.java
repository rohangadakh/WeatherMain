package com.rohya.weathermain;

import static java.lang.Integer.parseInt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class weatherRvAdapter extends RecyclerView.Adapter<weatherRvAdapter.ViewHolder>
{

    private Context context;
    private ArrayList<weatherRvModel> weatherRvModelArrayList;

    public weatherRvAdapter(Context context, ArrayList<weatherRvModel> weatherRvModelArrayList) {
        this.context = context;
        this.weatherRvModelArrayList = weatherRvModelArrayList;
    }

    @NonNull
    @Override
    public weatherRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull weatherRvAdapter.ViewHolder holder, int position) {

        weatherRvModel model = weatherRvModelArrayList.get(position);
//      holder.img_upt.setImageIcon();
        holder.txt_temp.setText(model.getTempreture() + " °C");

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try{
            Date t = input.parse(model.getTime());
            holder.txt_time.setText(output.format(t));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String code = model.getIcon();

        int isDay = model.getIsday();

        if (isDay==1)
        {
            switch (code) {

                case "1000":
                    holder.img_upt.setImageResource(R.drawable.sunny_1000);
                    break;
                case "1003":
                case "1006":
                case "1009":
                    holder.img_upt.setImageResource(R.drawable.cloudy_1006);
                    break;
                case "1030":
                case "1135":
                case "1147":
                    holder.img_upt.setImageResource(R.drawable.fog_day_1135);
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
                    holder.img_upt.setImageResource(R.drawable.rain_1063);
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
                    holder.img_upt.setImageResource(R.drawable.snow_1204);
                    break;
                case "1192":
                case "1195":
                case "1198":
                case "1201":
                    holder.img_upt.setImageResource(R.drawable.heavy_rain_1192);
                    break;
                case "1087":
                case "1264":
                case "1273":
                case "1243":
                case "1246":
                case "1276":
                    holder.img_upt.setImageResource(R.drawable.chance_of_storm);
                    break;
            }
        } else {
            // It is currently nighttime
            Log.d("Time2", "It's nighttime.");

            switch (code) {

                case "1000":
                    holder.img_upt.setImageResource(R.drawable.moon_1000);
                    break;
                case "1003":
                case "1006":
                case "1009":
                    holder.img_upt.setImageResource(R.drawable.night_cloud_1006);
                    break;
                case "1030":
                case "1135":
                case "1147":
                    holder.img_upt.setImageResource(R.drawable.fog_night_1135);
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
                    holder.img_upt.setImageResource(R.drawable.rain_1063);
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
                    holder.img_upt.setImageResource(R.drawable.snow_1204);
                    break;
                case "1192":
                case "1195":
                case "1198":
                case "1201":
                    holder.img_upt.setImageResource(R.drawable.heavy_rain_1192);
                    break;
                case "1087":
                case "1264":
                case "1273":
                case "1243":
                case "1246":
                case "1276":
                    holder.img_upt.setImageResource(R.drawable.chance_of_storm);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return weatherRvModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_time, txt_temp, txt_icon;
        private ImageView img_upt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_time = itemView.findViewById(R.id.txt_time);
            txt_temp = itemView.findViewById(R.id.txt_temp);
            txt_icon = itemView.findViewById(R.id.txt_icon);
            img_upt = itemView.findViewById(R.id.img_upt);
        }
    }
}
