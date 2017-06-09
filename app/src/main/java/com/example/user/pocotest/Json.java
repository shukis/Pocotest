package com.example.user.pocotest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Json {
    private final String LOG = "OKHTTP3";

    public Request createRequest(JSONObject actualData, String url) {
        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody postData = RequestBody.create(JSON, actualData.toString());
        Log.d(LOG, "RequestBody created");
        return new Request.Builder()
                .url(url)
                .post(postData)
                .build();
    }

    public String getResponse(Request request) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.d(LOG, "Request done, got the response");
            return response.body().string();
        } catch (IOException e) {
            Log.d(LOG, "Response IOException");
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject createJsonObject(String activity, User user) throws JSONException {
        JSONObject actualData = new JSONObject();
        switch (activity) {
            case "LoginActivity":
                actualData.put("email", user.getEmail());
                actualData.put("password", user.getPassword());
                break;
            case "RegistrationActivity":
                actualData.put("email", user.getEmail());
                actualData.put("password", user.getPassword());
                actualData.put("country", user.getCountry());
                actualData.put("city", user.getCity());
                actualData.put("postal_code", user.getPostalCode());
                break;
        }
        return actualData;
    }
}
