package com.devrajnish.mynews.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devrajnish.mynews.api.APIInterface;
import com.devrajnish.mynews.api.ApiClient;
import com.devrajnish.mynews.model.Article;
import com.devrajnish.mynews.model.ResponseModel;
import com.devrajnish.mynews.view.MainActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleViewModel extends ViewModel {
    MutableLiveData<List<Article>> latestArticles;
    MutableLiveData<List<Article>> allArticle;
    MutableLiveData<List<Article>> sourceArticle;

    public final static String BASE_URL = "https://newsapi.org/v2/";
    String apiKey = "81080f0b9ee94f36be68d431b249293d";

    //default values
    String country = "in";
    String sources = "";
    String sortBy = "";
    String query = "breaking-news";
    int page = 1;

    public void setPage(int _page) {
        page = _page;
    }

    public void setCountry(String _country) {
        this.country = _country;
    }

    public void setQuery(String _query) {
        query = _query;
    }

    public void setSortBy(String _sortBy) {
        sortBy = _sortBy;
    }

    public void setSources(String _sources){
        sources = _sources;
    }

    String top_headline = "top-headlines?country=" + country + "&apiKey=" + apiKey;

    String all_article = "everything?q=" + query + "&sortBy=" + sortBy + "&apiKey=" + apiKey;

    String source_article = "everything?domains=" + sources + "&apiKey=" + apiKey;

    public MutableLiveData<List<Article>> getMutableLiveData() {
        if (latestArticles == null) {
            latestArticles = new MutableLiveData<>();
        }
        APIInterface apiInterface = ApiClient.getClient(BASE_URL).create(APIInterface.class);
        apiInterface.getLatestNews(top_headline).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    ResponseModel responseModel = response.body();
                    List<Article> articles = responseModel.getArticles();
                    latestArticles.setValue(articles);
                } else {
                    //Problem in fetching data due to some reason
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                //problem from API
            }
        });
        return latestArticles;
    }

    public MutableLiveData<List<Article>> getAllArticle() {
        if (allArticle == null) {
            allArticle = new MutableLiveData<>();
        }
        APIInterface apiInterface = ApiClient.getClient(BASE_URL).create(APIInterface.class);
        apiInterface.getEverything(all_article).enqueue(new Callback<ResponseModel>() {
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

    public MutableLiveData<List<Article>> getSourceArticle(){
        if (sourceArticle == null){
            sourceArticle = new MutableLiveData<>();
        }
        APIInterface apiInterface = ApiClient.getClient(BASE_URL).create(APIInterface.class);
        apiInterface.getFromSource(source_article).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()){
                    ResponseModel responseModel = response.body();
                    List<Article> articles = responseModel.getArticles();
                    sourceArticle.setValue(articles);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {

            }
        });
        return sourceArticle;
    }
}
