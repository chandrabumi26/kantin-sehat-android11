package com.kantinsehat.milenial;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String Base_URL="https://bikinlanding.com/kantinsehat/upload/";
//    private static final String Base_URL="http://192.168.100.12/kantinconnect/";
//    private static final String Base_URL="https://ubfuneral.000webhostapp.com/";
    private static Retrofit retrofit;

    public static Retrofit getApiClient(){
        if(retrofit == null){
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder().baseUrl(Base_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        }

        return  retrofit;
    }
}
