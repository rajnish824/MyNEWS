package com.devrajnish.mynews.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devrajnish.mynews.api.APIInterface;
import com.devrajnish.mynews.api.ApiClient;
import com.devrajnish.mynews.model.Article;
import com.devrajnish.mynews.model.ResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleViewModel extends ViewModel {
    MutableLiveData<List<Article>> allArticle;

    public final static String BASE_URL = "https://newsapi.org/v2/";

    String apiKey = "b60165836b05432cb0721316b0a2c681";
    String country = "in";
    String category = "business";
    String sortBy = "oldest";

    public void setCountry(String _country){
        country = _country;
    }

    public void setCategory(String _category){
        category = _category;
    }

    public void setSortBy(String _sortBy){
        sortBy = _sortBy;
    }

    String news_url = "top-headlines?country=" + country + "&category=" + category + "&sortBy=" + sortBy + "&apiKey=" + apiKey;

    public MutableLiveData<List<Article>> getMutableLiveData() {
        if (allArticle == null) {
            allArticle = new MutableLiveData<>();
        }
        APIInterface apiInterface = ApiClient.getClient(BASE_URL).create(APIInterface.class);
        apiInterface.getLatestNews(news_url).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    ResponseModel responseModel = response.body();
                    List<Article> articles = responseModel.getArticles();
                    allArticle.setValue(articles);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

            }
        });
        return allArticle;
    }
}
