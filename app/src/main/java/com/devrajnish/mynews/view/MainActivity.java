package com.devrajnish.mynews.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devrajnish.mynews.R;
import com.devrajnish.mynews.adapter.RecyclerAdapter;
import com.devrajnish.mynews.model.Article;
import com.devrajnish.mynews.viewmodel.ArticleViewModel;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.ItemClickListener{
    private List<Article> list;
    private ArticleViewModel articleViewModel;
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initRecycler();
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("MyNEWS");
        setSupportActionBar(toolbar);
    }

    private void initRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final TextView noResult = findViewById(R.id.error);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(list, this, this);
        recyclerView.setAdapter(recyclerAdapter);

        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        articleViewModel.getAllArticle().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null) {
                    list = articles;
                    recyclerAdapter.setArticleList(articles);
                    noResult.setVisibility(View.GONE);
                } else {
                    noResult.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        HashMap<String, String> map = new HashMap<>();
        map.put("NewsSource", article.getSource().getName());
        map.put("Image", article.getUrlToImage());
        map.put("Title", article.getTitle());
        map.put("Time", article.getPublishedAt());
        map.put("Description", article.getDescription());
        map.put("Link", article.getUrl());

        Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
        intent.putExtra("map", map);
        startActivity(intent);
    }
}