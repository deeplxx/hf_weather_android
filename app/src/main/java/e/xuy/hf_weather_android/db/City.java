package e.xuy.hf_weather_android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by sumlo on 2017/11/20.
 *
 */

public class City extends DataSupport {
    private int id;  // TODO：id应该是DataSupport内部处理的，只需要定义出来就好
    private int provinceId;
    private String cityName;
    private int cityCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String name) {
        this.cityName = name;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int code) {
        this.provinceId = code;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int code) {
        this.cityCode = code;
    }
}
