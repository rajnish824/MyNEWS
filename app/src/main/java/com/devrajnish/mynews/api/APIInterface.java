package com.devrajnish.mynews.api;

import com.devrajnish.mynews.model.Article;
import com.devrajnish.mynews.model.ResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface APIInterface {


    @GET
    Call<ResponseModel> getLatestNews(@Url String urlString);

    @GET("everything")
    Call<List<Article>> getSearchResults(@Query("q") String query, @Query("sortBy") String sortBy,
                                         @Query("language") String language,
                                         @Query("b60165836b05432cb0721316b0a2c681") String apiKey);
}
