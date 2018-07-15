package com.markiantorno.weathertestapp.services;

import com.google.gson.Gson;
import com.markiantorno.weathertestapp.BuildConfig;
import com.markiantorno.weathertestapp.RestServiceMockUtils;
import com.markiantorno.weathertestapp.objects.weather.WeatherStatus;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrentWeatherServiceTest {

    private final String WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY;
    private final String WEATHER_API_BASE_URL = BuildConfig.OPEN_WEATHER_URL;
    private final String CITY_NAME = "Toronto";

    private final String DESIRED_URL = "data/2.5/weather?APPID=80a620659f0dd2cfb9be452d5e9dc34e&q=Toronto";

    private String mWeatherStatusRawJson = null;
    private WeatherStatus mWeatherStatus = null;

    private MockWebServer mServer;
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;
    private CurrentWeatherService mCurrentWeatherService;

    @Before
    public void setUp() throws Exception {
        Gson gson = new Gson();

        mWeatherStatusRawJson = RestServiceMockUtils.getStringFromFile(this.getClass().getClassLoader(), "weather_status.json");
        mWeatherStatus = gson.fromJson(mWeatherStatusRawJson, WeatherStatus.class);

        mServer = new MockWebServer();
        mServer.start();

        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                String endpoint = request.getPath().substring(1);
                String methodHTTP = request.getMethod();
                switch (endpoint) {
                    case (DESIRED_URL):
                        if ("GET".equals(methodHTTP)) {
                            return new MockResponse()
                                    .setResponseCode(HttpURLConnection.HTTP_OK)
                                    .addHeader("Content-Type", "application/json; charset=utf-8")
                                    .setBody(mWeatherStatusRawJson);
                        } else {
                            return new MockResponse().setResponseCode(HttpURLConnection.HTTP_FORBIDDEN);
                        }
                    default:
                        return new MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND);
                }
            }
        };

        mServer.setDispatcher(dispatcher);
        HttpUrl baseUrl = mServer.url("");

        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(RestServiceMockUtils.CONNECTION_TIMEOUT_SHORT, TimeUnit.SECONDS)
                .connectTimeout(RestServiceMockUtils.CONNECTION_TIMEOUT_SHORT, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl.toString())
                .addConverterFactory(GsonConverterFactory.create())
                .client(mOkHttpClient)
                .build();

        mCurrentWeatherService = mRetrofit.create(CurrentWeatherService.class);
    }

    @After
    public void tearDown() throws Exception {
        mServer.shutdown();
    }

    @Test
    public void fetchWeatherByCityId() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);

        Call<WeatherStatus> call = mCurrentWeatherService.getWeatherForCity(WEATHER_API_KEY, CITY_NAME);
        call.enqueue(new Callback<WeatherStatus>() {
            @Override
            public void onResponse(Call<WeatherStatus> call, retrofit2.Response<WeatherStatus> response) {
                if (response.isSuccessful()) {
                    WeatherStatus body = response.body();
                    Assert.assertNotNull(body);
                    System.out.println(body.toString());
                    latch.countDown();
                } else {
                    TestCase.fail("fetchMealContextsById !isSuccessful : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherStatus> call, Throwable t) {
                TestCase.fail("fetchMealContextsById onFailure : " + t.getMessage());
            }
        });

        Assert.assertTrue(latch.await(RestServiceMockUtils.CONNECTION_TIMEOUT_MED*100000, TimeUnit.SECONDS));
    }

}