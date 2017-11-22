package e.xuy.hf_weather_android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sumlo on 2017/11/21.
 *
 * json数据
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
