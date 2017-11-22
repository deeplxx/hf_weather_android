package e.xuy.hf_weather_android;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import e.xuy.hf_weather_android.gson.Weather;
import e.xuy.hf_weather_android.util.HttpUtil;
import e.xuy.hf_weather_android.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressWarnings("ALL")
public class AutoUpdateService extends Service {

    private final String TAG = "AutoUpdateService";

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: execute");

        // 逻辑操作应当放在子线程中,因为这些是耗时的，对定时任务的准确性有一定影响
        updateWeather();
        updateBingPic();

        // 这个定时任务就是每过一段时间执行一次pi
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000;  // 毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;  // 开机至今的毫秒数+延迟毫秒数
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
                String weatherString = prefs.getString("weather", null);
                if (weatherString != null) {
                    Weather weather = Utility.handleWeatherResponse(weatherString);
                    //noinspection ConstantConditions
                    String weatherId = weather.basic.weatherId;

                    String weatherUrl = "http://guolin.tech/api/weather?cityid="
                            + weatherId + "&key=89e7532489534ee5a582891a8c5e40fd";
                    HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseText = response.body().string();
//                Log.d(TAG, "onResponse: responseText: " + responseText);
                            final Weather weather = Utility.handleWeatherResponse(responseText);
                            if (weather != null && "ok".equals(weather.status)) {
                                @SuppressLint("CommitPrefEdits")
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                        AutoUpdateService.this
                                ).edit();
                                editor.putString("weather", responseText);
                                editor.apply();
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateBingPic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String requestBingpic = "http://guolin.tech/api/bing_pic";
                HttpUtil.sendOKHttpRequest(requestBingpic, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String bingpic_str = response.body().string();
                        @SuppressLint("CommitPrefEdits")
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this)
                                .edit();
                        editor.putString("bing_pic", bingpic_str);
                        editor.apply();
                    }
                });
            }
        });

    }
}
