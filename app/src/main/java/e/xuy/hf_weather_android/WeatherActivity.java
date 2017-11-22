package e.xuy.hf_weather_android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import e.xuy.hf_weather_android.gson.Forecast;
import e.xuy.hf_weather_android.gson.Weather;
import e.xuy.hf_weather_android.util.HttpUtil;
import e.xuy.hf_weather_android.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@SuppressWarnings("NullableProblems")
public class WeatherActivity extends AppCompatActivity {

    private final String TAG = "WeatherActicity";

    // 各控件定义
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfo;
    private LinearLayout forcastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPic;

    // 滑动菜单
    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefreshLayout; // public是为了滑动菜单的逻辑

    public String weatherId;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 初始化控件
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfo = findViewById(R.id.weather_info_text);
        forcastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPic = findViewById(R.id.bing_pic_img);

        // 滑动菜单
        drawerLayout = findViewById(R.id.drawer_layout);
        Button navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);  // 打开滑动菜单
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {  // 有缓存时直接解析
            Weather weather = Utility.handleWeatherResponse(weatherString);
            //noinspection ConstantConditions
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {  // 无缓存时向服务器查询
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);  // 内部也调用了show
        }

        String bingpic_str = prefs.getString("bing_pic", null);
        if (bingpic_str != null) {
            Glide.with(this).load(bingpic_str).into(bingPic);
        } else {
            loadBingPic();
        }

        // 下拉刷新
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);  // 内部也调用了show
            }
        });
        // 下拉菜单背景融合(还需修改weather.xml来避免重叠)
        if (Build.VERSION.SDK_INT >= 21) {
            View decorview = getWindow().getDecorView();
            decorview.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        // 加入后台定时服务
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }


    private void loadBingPic() {
        String requestBingpic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOKHttpRequest(requestBingpic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Toast.makeText(WeatherActivity.this, "加载bing图片失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                @SuppressWarnings("ConstantConditions") final String bingpic_str = response.body().string();
                @SuppressLint("CommitPrefEdits")
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                        .edit();
                editor.putString("bing_pic", bingpic_str);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingpic_str).into(bingPic);
                    }
                });
            }
        });
    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="
                + weatherId + "&key=89e7532489534ee5a582891a8c5e40fd";
        HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败(查询失败)", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                @SuppressWarnings("ConstantConditions") final String responseText = response.body().string();
//                Log.d(TAG, "onResponse: responseText: " + responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            @SuppressLint("CommitPrefEdits")
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    WeatherActivity.this
                            ).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            Log.d(TAG, "run: " + weatherId);
                            showWeatherInfo(weather);
                        }  else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败(无此数据)", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);  // 请求结束调用此方法表示刷新结束，隐藏刷新进度条
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String weatherInfotxt = weather.now.cond.txt;
        String degree = weather.now.tmperate + "°C";
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfo.setText(weatherInfotxt);

        forcastLayout.removeAllViews();
        for (Forecast f: weather.daily_forecast) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forcastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(f.date);
            infoText.setText(f.cond.txt_d);
            maxText.setText(f.tmp.max);
            minText.setText(f.tmp.min);
            forcastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comf.txt;
        String carWash = "洗车指数：" + weather.suggestion.cw.txt;
        String sport = "运动指数：" + weather.suggestion.sport.txt;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
