package com.devrajnish.mynews.repository;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.devrajnish.mynews.R;
import com.devrajnish.mynews.api.APIInterface;
import com.devrajnish.mynews.api.ApiClient;
import com.devrajnish.mynews.model.Article;
import com.devrajnish.mynews.model.ResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleRepository {

    MutableLiveData<List<Article>> mutableLiveData;
    private Application application;


    public final static String BASE_URL = "https://newsapi.org/v2/";

    String apiKey = "b60165836b05432cb0721316b0a2c681";
    //String news_url = String.format("v2/top-headlines?country=us&category=%s&apiKey=%s", catName, apiKey);
    String news_url = "top-headlines?country=us&category=business&apiKey=" + apiKey;

    public ArticleRepository(Application application) {
        this.application = application;
    }


    public MutableLiveData<List<Article>> getMutableLiveData(){
        if (mutableLiveData == null){
            mutableLiveData = new MutableLiveData<>();
        }
        APIInterface apiInterface = ApiClient.getClient(BASE_URL).create(APIInterface.class);
        apiInterface.getLatestNews(news_url).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()){
                    ResponseModel responseModel = response.body();
                    List<Article> articles = responseModel.getArticles();
                    mutableLiveData.setValue(articles);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

            }
        });
        return mutableLiveData;
    }
}
