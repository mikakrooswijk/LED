package com.example.mikakrooswijk.led;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.*;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mikakrooswijk.led.domain.Temperature;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView temp, avgTemp,maxTemp;
    final String url = "http://94.208.5.119:3030/api/temp/room";
    final String urlAVg = "http://94.208.5.119:3030/api/temp/room/avgtoday";
    final String urlMax = "http://94.208.5.119:3030/api/temp/room/maxtoday";
    final String urlAll = "http://94.208.5.119:3030/api/temp/room/alltoday";
    private ProgressBar tempbar;
    private ArrayList<Temperature> tempdataArray = new ArrayList<>();
    GraphView graph;
    Button refresh;


    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle("temperature today");



        maxTemp = (TextView) findViewById(R.id.MaxTempText);

        tempbar = (ProgressBar) findViewById(R.id.progressBar);
        tempbar.setMax(40);

        temp = findViewById(R.id.temp);
        avgTemp = findViewById(R.id.AvgTempText);

        refresh();

        refresh = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                refresh.setClickable(false);
                refresh();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void refresh(){

        refreshAvgToday();
        refreshMaxToday();
        refreshDayGraph();

        SharedPreferences preferences = this.getApplication().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final String token = preferences.getString("token", "");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        temp.setTextColor(Color.BLACK);
        temp.setTextSize(70);
        temp.setText("Loading...");
        tempbar.setProgress(tempbar.getMax());
        tempbar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#4ddbff")));

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resTemp = response.getString("temp");
                            temp.setText(resTemp + " °C");
                            Double tempDub = Double.parseDouble(resTemp);
                            tempbar.setProgress(tempDub.intValue());

                            if(tempDub < 21){
                                tempbar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
                            }else if(tempDub > 23){
                                tempbar.setProgressTintList(ColorStateList.valueOf(Color.RED));
                            }else{
                                tempbar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("FF8BC34A")));
                            }


                            Float tempNum = Float.parseFloat(resTemp);
                            if(tempNum > 23){
                                temp.setTextColor(Color.RED);
                            }else if(tempNum < 21){
                                temp.setTextColor(Color.BLUE);
                            }else{
                                temp.setTextColor(Color.GREEN);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            temp.setTextSize(12);
                            temp.setText(e.toString());
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);

                        }
                        refresh.setClickable(true);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        temp.setTextSize(12);
                        temp.setText(error.toString());
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token", token);

                return params;
            }
        };

        requestQueue.add(jsObjRequest);

    }

    public void refreshAvgToday(){
        SharedPreferences preferences = this.getApplication().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final String token = preferences.getString("token", "");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        avgTemp.setTextColor(Color.BLACK);
        avgTemp.setText("Loading...");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlAVg, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resTemp = response.getString("avg(temp)");
                            DecimalFormat df = new DecimalFormat("0.0");
                            String formate = df.format(Double.parseDouble(resTemp));
                            avgTemp.setText(formate + " °C");


                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        temp.setTextSize(12);
                        temp.setText(error.toString());
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token", token);

                return params;
            }
        };

        requestQueue.add(jsObjRequest);

    }

    public void refreshMaxToday(){
        SharedPreferences preferences = this.getApplication().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final String token = preferences.getString("token", "");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        maxTemp.setTextColor(Color.BLACK);
        maxTemp.setText("Loading...");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlMax, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String resTemp = response.getString("max(temp)");
                            DecimalFormat df = new DecimalFormat("0.0");
                            String formate = df.format(Double.parseDouble(resTemp));
                            maxTemp.setText(formate + " °C");


                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        temp.setTextSize(12);
                        temp.setText(error.toString());
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token", token);

                return params;
            }
        };

        requestQueue.add(jsObjRequest);

    }

    public void refreshDayGraph(){

        graph.removeAllSeries();

        SharedPreferences preferences = this.getApplication().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final String token = preferences.getString("token", "");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());


        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                (Request.Method.GET, urlAll, null, new Response.Listener<JSONArray>() {


                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            tempdataArray = new ArrayList<Temperature>();
                            for(int i = 0; i < response.length(); i++){
                                Temperature temp = new Temperature(
                                        Double.parseDouble(response.getJSONObject(i).getString("temp")),
                                        response.getJSONObject(i).getString("date"),
                                        response.getJSONObject(i).getString("time")
                                );
                                tempdataArray.add(temp);
                            }
                            int size = tempdataArray.size();
                            DataPoint[] values = new DataPoint[size];
                            for(int i = 0; i < size; i++){
                                DateFormat format = new SimpleDateFormat("yyy-MM-dd");
                                Date date = format.parse(tempdataArray.get(i).getDate());
                                Log.i("Date", date.toString());
                                DataPoint d = new DataPoint(i, tempdataArray.get(i).getTemperature());
                                values[i] = d;
                            }

                            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(values);

                            Paint paint = new Paint();
                            paint.setStrokeWidth(5);
                            paint.setColor(Color.parseColor("#FF8BC34A"));
                            series.setCustomPaint(paint);

                            graph.removeAllSeries();
                            graph.addSeries(series);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("token", token);

                return params;
            }
        };

        requestQueue.add(jsObjRequest);

    }


}
