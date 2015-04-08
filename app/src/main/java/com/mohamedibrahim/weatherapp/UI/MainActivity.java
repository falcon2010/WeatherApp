package com.mohamedibrahim.weatherapp.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mohamedibrahim.weatherapp.R;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;

import java.util.List;

import Adapter.CityAdapter;
import Manager.WeatherManager;
import Utils.WeatherIconMapper;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements WeatherManager.IWeatherUtils {


    @InjectView(R.id.my_toolbar)
    Toolbar toolbar;

    private ListView cityListView;

    private WeatherManager weatherManager;

    // Widget
    @InjectView(R.id.temp)
    TextView tempView;
    @InjectView(R.id.weather_icon)
    ImageView weatherIcon;
    @InjectView(R.id.pressure)
    TextView pressView;
    @InjectView(R.id.hum)
    TextView humView;
    @InjectView(R.id.wind)
    TextView windView;


    //https://github.com/survivingwithandroid/Surviving-with-android/blob/master/MaterialWeather/app/src/main/java/com/survivingwithandroid/materialweather/WeatherActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        weatherManager = weatherManager.getInstance(this, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Dialog d = createDialog();
            d.show();

        }
        return super.onOptionsItemSelected(item);
    }


    /*
    create dialgo for show the list of countries
     */
    private Dialog createDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.select_city_dialog, null);
        builder.setView(v);
        EditText et = (EditText) v.findViewById(R.id.ptnEdit);
        cityListView = (ListView) v.findViewById(R.id.cityList);
        cityListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        cityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final City selectedCity = (City) parent.getItemAtPosition(position);
                weatherManager.setCurrentCity(selectedCity);

            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                weatherManager.searchCity(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // set positive Button data
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (weatherManager.getCurrentCity() != null) {
                    toolbar.setTitle(weatherManager.getCurrentCity().getName() + " , " + weatherManager.getCurrentCity().getCountry());
                    weatherManager.getWeather();

                }

            }
        });

        // set negative Button data
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        return builder.create();
    }

    /**
     * set the toolbar color based on weather data
     */

    private void setToolbarColor(float temp) {
        int color = -1;

        if (temp < -10)
            color = getResources().getColor(R.color.primary_indigo);
        else if (temp >= -10 && temp <= -5)
            color = getResources().getColor(R.color.primary_blue);
        else if (temp > -5 && temp < 5)
            color = getResources().getColor(R.color.primary_light_blue);
        else if (temp >= 5 && temp < 10)
            color = getResources().getColor(R.color.primary_teal);
        else if (temp >= 10 && temp < 15)
            color = getResources().getColor(R.color.primary_light_green);
        else if (temp >= 15 && temp < 20)
            color = getResources().getColor(R.color.primary_green);
        else if (temp >= 20 && temp < 25)
            color = getResources().getColor(R.color.primary_lime);
        else if (temp >= 25 && temp < 28)
            color = getResources().getColor(R.color.primary_yellow);
        else if (temp >= 28 && temp < 32)
            color = getResources().getColor(R.color.primary_amber);
        else if (temp >= 32 && temp < 35)
            color = getResources().getColor(R.color.primary_orange);
        else if (temp >= 35)
            color = getResources().getColor(R.color.primary_red);

        toolbar.setBackgroundColor(color);

    }

    @Override
    public void onCompleteSearchCities(List<City> cities) {

        CityAdapter ca = new CityAdapter(this, cities);
        cityListView.setAdapter(ca);
    }

    @Override
    public void onCompleteCurrentWeather(CurrentWeather currentWeather) {

        // We have the current weather now
        // Update subtitle toolbar
        toolbar.setSubtitle(currentWeather.weather.currentCondition.getDescr());
        tempView.setText(String.format("%.0f", currentWeather.weather.temperature.getTemp()));
        pressView.setText(String.valueOf(currentWeather.weather.currentCondition.getPressure()));
        windView.setText(String.valueOf(currentWeather.weather.wind.getSpeed()));
        humView.setText(String.valueOf(currentWeather.weather.currentCondition.getHumidity()));
        weatherIcon.setImageResource(WeatherIconMapper.getWeatherResource(currentWeather.weather.currentCondition.getIcon(), currentWeather.weather.currentCondition.getWeatherId()));

        setToolbarColor(currentWeather.weather.temperature.getTemp());

    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
