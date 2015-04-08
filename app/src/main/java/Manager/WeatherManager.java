package Manager;

import android.content.Context;

import com.survivingwithandroid.weather.lib.WeatherClient;
import com.survivingwithandroid.weather.lib.WeatherConfig;
import com.survivingwithandroid.weather.lib.exception.WeatherLibException;
import com.survivingwithandroid.weather.lib.model.City;
import com.survivingwithandroid.weather.lib.model.CurrentWeather;
import com.survivingwithandroid.weather.lib.provider.openweathermap.OpenweathermapProviderType;
import com.survivingwithandroid.weather.lib.request.WeatherRequest;

import java.util.List;

/**
 * Created by Lenovo on 07/04/2015.
 * Represent all operations on the weather
 */
public class WeatherManager {

    private static WeatherManager instance;
    private WeatherClient weatherclient;
    private City currentCity;
    private Context context;
    private IWeatherUtils listener;


    public void setListener(IWeatherUtils listener) {
        this.listener = listener;
    }

    public IWeatherUtils getListener() {
        return listener;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public void setCurrentCity(City currentCity) {
        this.currentCity = currentCity;
    }

    public City getCurrentCity() {
        return currentCity;
    }

    private WeatherManager(Context context, IWeatherUtils listener) {

        this.context = context;
        this.listener = listener;

        WeatherClient.ClientBuilder builder = new WeatherClient.ClientBuilder();
        WeatherConfig config = new WeatherConfig();
        config.unitSystem = WeatherConfig.UNIT_SYSTEM.M;
        config.lang = "en"; // If you want to use english
        config.maxResult = 5; // Max number of cities retrieved
        config.numDays = 6; // Max num of days in the forecast

        try {
            weatherclient = builder.attach(context)
                    .provider(new OpenweathermapProviderType())
                    .httpClient(com.survivingwithandroid.weather.lib.client.volley.WeatherClientDefault.class)
                    .config(config)
                    .build();
        } catch (Throwable t) {
            // we will handle it later
        }

    }


    public void getWeather() {


        weatherclient.getCurrentCondition(new WeatherRequest(currentCity.getId()), new WeatherClient.WeatherEventListener() {
            @Override
            public void onWeatherRetrieved(CurrentWeather w) {

                if (listener != null) {
                    listener.onCompleteCurrentWeather(w);
                }

            }

            @Override
            public void onWeatherError(WeatherLibException e) {

                if (listener != null) {
                    if (e != null)
                        listener.onFailure(e.getMessage());
                    else
                        listener.onFailure("Error happened while processing");
                }
            }

            @Override
            public void onConnectionError(Throwable throwable) {
                if (listener != null) {
                    if (throwable != null)
                        listener.onFailure(throwable.getMessage());
                    else
                        listener.onFailure("Error happened while processing");
                }
            }
        });


    }


    public void searchCity(String cityName) {

        if (cityName.length() > 0) {

            weatherclient.searchCity(cityName, new WeatherClient.CityEventListener() {
                @Override
                public void onCityListRetrieved(List<City> cities) {

                    if (cities != null && cities.size() > 0) {
                        if (listener != null) {
                            listener.onCompleteSearchCities(cities);

                        }
                    }


                }

                @Override
                public void onWeatherError(WeatherLibException e) {

                    if (listener != null) {
                        if (e != null && e.getMessage().length() > 0) {
                            listener.onFailure(e.getMessage());

                        } else {
                            listener.onFailure("Error happened while processing your request");
                        }

                    }
                }

                @Override
                public void onConnectionError(Throwable e) {
                    if (listener != null) {
                        if (e != null && e.getMessage().length() > 0) {
                            listener.onFailure(e.getMessage());

                        } else {
                            listener.onFailure("Error happened while processing your request");
                        }

                    }
                }
            });

        } else {

            if (listener != null) {
                listener.onFailure("City name must be more than 3 letters");
            }
        }


    }


    public static WeatherManager getInstance(Context context, IWeatherUtils listener) {
        if (instance == null) {
            synchronized (WeatherManager.class) {
                if (instance == null) {
                    instance = new WeatherManager(context, listener);
                }
            }
        } else {

            instance.setContext(context);
            instance.setListener(listener);

        }
        return instance;

    }


    public static interface IWeatherUtils {

        public void onCompleteSearchCities(List<City> cities);

        public void onCompleteCurrentWeather(CurrentWeather CurrentWeather);

        public void onFailure(String message);

    }

}
