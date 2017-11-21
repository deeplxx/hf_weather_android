package e.xuy.hf_weather_android.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by sumlo on 2017/11/21.
 *
 * 网络工具
 */

public class HttpUtil {

    public static void sendOKHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
