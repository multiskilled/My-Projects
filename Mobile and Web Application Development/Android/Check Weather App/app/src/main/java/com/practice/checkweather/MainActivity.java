package com.practice.checkweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
//create a object of the data model class
    private CurrentWeather currentWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey="3b465dca47msh92da6f92cd487d6p1ae8ddjsnd3b5683ca40b";
        String forecast="https://aerisweather1.p.rapidapi.com/sunmoon/ankara,tr";

//method to check to see if network is available
        if(isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://aerisweather1.p.rapidapi.com/sunmoon/ankara,tr")
                    .get()
                    .addHeader("X-RapidAPI-Key", "3b465dca47msh92da6f92cd487d6p1ae8ddjsnd3b5683ca40b")
                    .addHeader("X-RapidAPI-Host", "aerisweather1.p.rapidapi.com")
                    .build();

            Call call = client.newCall(request);
            //Asynchronous method
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //all the exceptions being handled in one place
                    try {
                        // Response response = call.execute();//synchronous method
                        String jsonData=response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                                    //get current details if response is successful
                            currentWeather=getCurrentDetails(jsonData);
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught", e);
                    } catch (JSONException e){
                        Log.e(TAG, "Json exception caught", e);
                    }
                }
            });

        }
        //see order of log statements
        Log.e(TAG,"Main UI code is running");
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast=new JSONObject(jsonData);

        String timeZone=forecast.getString("timezone");
        Log.i(TAG,"From Json: "+timeZone);

        JSONObject currently=forecast.getJSONObject("currently");

        CurrentWeather currentWeather=new CurrentWeather();

        //set all the values from the data model
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        //hardcode location, later you can create a search option for users
        currentWeather.setLocationLabel("CA");
        currentWeather.setPrecipChange(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timeZone);


        //checking the formatted time
        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //instantiate network info object
        NetworkInfo networkInfo=manager.getActiveNetworkInfo();

        boolean isAvailable=false;

        if(networkInfo!=null && networkInfo.isConnected()){
            isAvailable=true;
        }else{
            Toast.makeText(this, "Sorry! Network is unavailable", Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {

        AlertDialogFragment dialog=new AlertDialogFragment();
        dialog.show(getSupportFragmentManager(), "error_dialog");
    }
}