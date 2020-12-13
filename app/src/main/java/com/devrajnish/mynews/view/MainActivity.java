package com.devrajnish.mynews.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
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

//Created by Rajnish Sharma on 13 Dec 2020

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.ItemClickListener {
    private List<Article> list;
    private ArticleViewModel articleViewModel;
    RecyclerAdapter recyclerAdapter;
    private ProgressBar progressBar;
    private Spinner sortSpinner;

    String[] strSort = {"Newest", "popularity", "Oldest"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_circular);
        sortSpinner = findViewById(R.id.sort_spinner);
        loadingProgressBarVisible();
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
        final ImageView noResult = findViewById(R.id.no_result);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(list, this, this);
        recyclerView.setAdapter(recyclerAdapter);

        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strSort);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortBy = sortSpinner.getSelectedItem().toString().trim();
                Toast.makeText(MainActivity.this, sortBy, Toast.LENGTH_SHORT).show();
                articleViewModel.setSortBy(sortBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        articleViewModel.getMutableLiveData().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null) {
                    list = articles;
                    recyclerAdapter.notifyDataSetChanged();
                    recyclerAdapter.setArticleList(articles);
                    noResult.setVisibility(View.GONE);
                } else {
                    noResult.setVisibility(View.VISIBLE);
                }
                loadingProgressBarGone();
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

    public void loadingProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void loadingProgressBarGone() {
        progressBar.setVisibility(View.GONE);
    }
}