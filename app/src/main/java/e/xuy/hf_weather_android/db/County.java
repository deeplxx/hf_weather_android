package e.xuy.hf_weather_android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by sumlo on 2017/11/20.
 *
 */

public class County extends DataSupport {

    private int id;
    private String countyName;
    private int cityId;
    private String weatherId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String name) {
        this.countyName = name;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
