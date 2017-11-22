package e.xuy.hf_weather_android.gson;

import java.util.List;

/**
 * Created by sumlo on 2017/11/21.
 *
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    public List<Forecast> daily_forecast;
}
