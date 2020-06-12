package com.example.samarpan.weatherreporter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity
{
    EditText editText;
    TextView resultTextView;                          // referring to the output text view

    public void getWeather(View view)
    {

        DownloadTask task = new DownloadTask();
        try
        {

            //String encoder = URLEncoder.encode(editText.getText().toString(), "UTF-8");


            task.execute("https://openweathermap.org/data/2.5/weather?q=" + editText.getText().toString() + "&appid=439d4b804bc8187953eb36d2a8c26a02");


            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  // these two lines of code will pop the virtual
            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);            // keyboard down when the user clicks the button
        }catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather :( ", Toast.LENGTH_SHORT).show();

        }

    }



    public class DownloadTask extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... urls)
        {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try
            {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();


                while (data != -1)
                {
                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            }

            catch (Exception e)
            {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather :( ", Toast.LENGTH_SHORT).show();

                return "Download Failed";
            }

        }

        @Override
        protected void onPostExecute(String s)  // s actually takes in the result
        {
            super.onPostExecute(s);

            try
            {

                JSONObject jsonObject = new JSONObject(s);
                Log.i("Total ", s);


                String weatherInfo = jsonObject.getString("weather");  //weatherInfo is an object, that contains all the info and we wana break it up into an array
                Log.i("Weather Content", weatherInfo);

                String mainInfo = jsonObject.getString("main");
                Log.i("Main info ", mainInfo);





                String message = "";


         // DEALING WITH THE ONES OUTSIDE ARRAY

                String temp = jsonObject.getJSONObject("main").getString("temp");           // this is how we treat those outside an array
                String feels_like = jsonObject.getJSONObject("main").getString("feels_like"); // same as above
                String humidity = jsonObject.getJSONObject("main").getString("humidity");

                String windSpeed = jsonObject.getJSONObject("wind").getString("speed");
                // changing windSpeed to km/hr
                Double temporary = Double.parseDouble(windSpeed);
                temporary = temporary * 3.6;

                DecimalFormat df = new DecimalFormat("#.00");   // formatting temporary to 2 deci places
                String temporaryFormated = df.format(temporary);

                String realWindSpeed = temporaryFormated;



                message = message + "Temperature : " + temp + " deg C"+ "\r\n";
                message = message + "Feels Like : " + feels_like+ " deg C"+ "\r\n";
                message = message + "Humidity : " + humidity + " %"+ "\r\n";
                message = message + "Wind Speed : " + realWindSpeed + " km/h" + "\r\n";




        // DEALING WITH THE ONES INSIDE AN ARRAY

                JSONArray arr = new JSONArray(weatherInfo);

                for(int i = 0; i < arr.length(); i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");


                    Log.i("main", jsonPart.getString("main"));
                    Log.i("description", jsonPart.getString("description"));



                    if(!main.equals("") && !description.equals(""))
                    {
                        message += main + " : " + description + "\r\n";
                    }


                }




                if(!message.equals(""))
                {
                    resultTextView.setText(message);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Could not find weather :( ", Toast.LENGTH_SHORT).show();
                }





            } catch (Exception e)
            {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather :( ", Toast.LENGTH_SHORT).show();

            }

        }
    }








    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);


    }
}
