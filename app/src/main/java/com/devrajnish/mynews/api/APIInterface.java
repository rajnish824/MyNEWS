package com.devrajnish.mynews.api;

import com.devrajnish.mynews.model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface APIInterface {

    @GET
    Call<ResponseModel> getLatestNews(@Url String urlString);

    @GET
    Call<ResponseModel> getEverything(@Url String urlString);

    @GET
    Call<ResponseModel> getFromSource(@Url String urlString);
}
