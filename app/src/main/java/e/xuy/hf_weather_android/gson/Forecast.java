package e.xuy.hf_weather_android.gson;

/**
 * Created by sumlo on 2017/11/21.
 *
 */

public class Forecast {

    public String date;

    public Cond cond;

    public Tmp tmp;

    public class Cond {
        public String txt_d;
    }

    public class Tmp {
        public String max;
        public String min;
    }
}
