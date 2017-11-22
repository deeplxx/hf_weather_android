package e.xuy.hf_weather_android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sumlo on 2017/11/21.
 *
 */

public class Now {

    @SerializedName("tmp")
    public String tmperate;

    public Cond cond;

    public class Cond {

        public String txt;
    }
}
