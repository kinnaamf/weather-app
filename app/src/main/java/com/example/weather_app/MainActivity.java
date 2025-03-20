package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask<String, Void, String> {
        private String city;

        public getWeather(String city) {
            this.city = city;
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append('\n');
                }
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                show.setText("No weather data.");
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                JSONObject wind = jsonObject.getJSONObject("wind");
                double getTemp = Double.parseDouble(main.getString("temp"));
                double temperature = (int) Math.round(getTemp);
                String humidity = main.getString("humidity");
                String windVelocity = wind.getString("speed");

                show.setText("Temperatura in " + city + " acum!\n" +
                        "Temperatura aerului: " + temperature + "°C\n" +
                        "Umiditatea: " + humidity + "%\n" +
                        "Viteza vântului: " + windVelocity + " m/s");
            } catch (Exception e) {
                e.printStackTrace();
                show.setText("Error parsing data.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityName.getText().toString().trim();

                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter city name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String apiKey = "2533d8acadb078951a0a5158563e0398";
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric"; // Добавил &units=metric

                getWeather task = new getWeather(city);
                task.execute(url);
            }
        });
    }
}
