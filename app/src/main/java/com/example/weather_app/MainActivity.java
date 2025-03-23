package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    Button search;
    TextView temperature, humidity, windSpeed, pressure;
    ImageView tempIcon, humidityIcon, windIcon, pressureIcon;
    String url;

    class GetWeather extends AsyncTask<String, Void, String> {
        private String city;

        public GetWeather(String city) {
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
                temperature.setText("No weather data.");
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                JSONObject wind = jsonObject.getJSONObject("wind");
                double getTemp = main.getDouble("temp");
                int roundedTemp = (int) Math.round(getTemp);
                String humidityValue = main.getString("humidity");
                String windVelocity = wind.getString("speed");
                String pressureValue = main.getString("pressure");

                temperature.setText(roundedTemp + "°C");
                humidity.setText(humidityValue + "%");
                windSpeed.setText(windVelocity + " m/s");
                pressure.setText(pressureValue + " hPa");

                findViewById(R.id.weather_info).setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
                temperature.setText("Error parsing data.");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        temperature = findViewById(R.id.temperature);
        humidity = findViewById(R.id.humidity);
        windSpeed = findViewById(R.id.wind_speed);
        pressure = findViewById(R.id.pressure);
        tempIcon = findViewById(R.id.temp_icon);
        humidityIcon = findViewById(R.id.humidity_icon);
        windIcon = findViewById(R.id.wind_icon);
        pressureIcon = findViewById(R.id.pressure_icon);

        tempIcon.setImageResource(R.drawable.ic_temperature);
        humidityIcon.setImageResource(R.drawable.ic_humidity);
        windIcon.setImageResource(R.drawable.ic_wind);
        pressureIcon.setImageResource(R.drawable.ic_pressure);

        findViewById(R.id.weather_info).setVisibility(View.GONE);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityName.getText().toString().trim();

                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter city name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String apiKey = "2533d8acadb078951a0a5158563e0398";
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";
                new GetWeather(city).execute(url);
            }
        });
    }
}
