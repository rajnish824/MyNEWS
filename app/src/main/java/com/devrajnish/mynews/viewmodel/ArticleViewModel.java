package com.devrajnish.mynews.viewmodel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.devrajnish.mynews.model.Article;
import com.devrajnish.mynews.repository.ArticleRepository;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {

    ArticleRepository articleRepository;

    public ArticleViewModel(@NonNull Application application) {
        super(application);
        articleRepository = new ArticleRepository(application);
    }

    public LiveData<List<Article>> getAllArticle() {
        return articleRepository.getMutableLiveData();
    }

}
