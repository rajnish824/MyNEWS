package com.devrajnish.mynews.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devrajnish.mynews.R;
import com.devrajnish.mynews.adapter.RecyclerAdapter;
import com.devrajnish.mynews.location.AppLocationService;
import com.devrajnish.mynews.location.LocationAddress;
import com.devrajnish.mynews.model.Article;
import com.devrajnish.mynews.viewmodel.ArticleViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Created by Rajnish Sharma on 13 Dec 2020

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.ItemClickListener {
    //widgets
    private ProgressBar progressBar;
    private Spinner sortSpinner;
    private EditText Search;
    private TextView currentLocation;
    private TextView tvSort;
    private TextView Topic;
    private ExtendedFloatingActionButton floatingActionButton;
    ImageView noResult;

    //services
    AppLocationService appLocationService;

    //layouts
    private LinearLayout locationLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    //vars
    String strLocation;
    String[] strSort = {"newest", "popularity", "oldest"};
    private List<Article> list = new ArrayList<>();
    boolean spinnerTouched = false;
    String toSort;

    private ArticleViewModel articleViewModel;
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        floatingActionButton = findViewById(R.id.floating_action_button);
        Topic = findViewById(R.id.topic);
        tvSort = findViewById(R.id.tv_sort);
        noResult = findViewById(R.id.no_result);
        progressBar = findViewById(R.id.progress_circular);
        sortSpinner = findViewById(R.id.sort_spinner);
        Search = findViewById(R.id.search);
        locationLayout = findViewById(R.id.location_chooser);
        currentLocation = findViewById(R.id.location);

        loadingProgressBarVisible();
        initToolbar();
        getCurrentLocation();
        searchFunction();
        initRecycler();
        sortData();


        //Was Not able to implement these two functionality...
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Implemented only from current location", Toast.LENGTH_SHORT).show();
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Not yet Implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("MyNEWS");
        setSupportActionBar(toolbar);
    }

    private void initRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerAdapter = new RecyclerAdapter(list, this, this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.notifyDataSetChanged();
        getLatestData();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    floatingActionButton.shrink();
                } else {
                    floatingActionButton.extend();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLatestData();
                swipeRefreshLayout.setRefreshing(false);
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

    public void getLatestData() {
        list.clear();
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        articleViewModel.getMutableLiveData().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null) {
                    list = articles;
                    recyclerAdapter.setArticleList(articles);
                    noResult.setVisibility(View.GONE);
                } else {
                    noResult.setVisibility(View.VISIBLE);
                }
                loadingProgressBarGone();
            }
        });
    }

    public void getAllArticles() {
        list.clear();
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        articleViewModel.setSortBy(toSort);
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
                loadingProgressBarGone();
            }
        });
    }

    public void getSourceArticles(){
        list.clear();
        articleViewModel = ViewModelProviders.of(this).get(ArticleViewModel.class);
        articleViewModel.getSourceArticle().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null) {
                    list = articles;
                    recyclerAdapter.setArticleList(articles);
                    noResult.setVisibility(View.GONE);
                } else {
                    noResult.setVisibility(View.VISIBLE);
                }
                loadingProgressBarGone();
            }
        });
    }

    public void sortData(){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strSort);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    spinnerTouched = true;
                }
                return false;
            }
        });
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerTouched) {
                    toSort = sortSpinner.getSelectedItem().toString().trim();
                    getAllArticles();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void searchFunction() {
        Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    filter(s.toString());
                } else {
                    getLatestData();
                    widgetVisibilityVisible();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void filter(String text) {
        List<Article> filteredList = new ArrayList<>();
        for (Article article : list) {
            if (article.getTitle().toLowerCase().contains(text.toLowerCase())
                    || article.getSource().getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(article);
            }
        }
        recyclerAdapter.filterList(filteredList);
        widgetVisibilityGone();
    }

    public void getCurrentLocation() {
        appLocationService = new AppLocationService(
                this);
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (isOnline()) {
                                Location location = appLocationService
                                        .getLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                    LocationAddress locationAddress = new LocationAddress();
                                    LocationAddress.getAddressFromLocation(latitude, longitude, MainActivity.this, new GeocoderHandler());
                                } else {
                                    Toast.makeText(MainActivity.this, "Location Error, Try restarting app, Or type manually!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Error occurred! " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }).onSameThread()
                .check();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message message) {
            ArrayList<String> locationAddress;
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getStringArrayList("countryCode");
                currentLocation.setText(convertFirstLetterCaps(locationAddress.get(1)));
                strLocation = locationAddress.get(0);
                /*
                 * Sending the current location in view model
                 * the default country code which is "in" in viewModel will be
                 * replaced by this new location
                 */
                articleViewModel.setCountry(strLocation);
                appLocationService.stopLocation();
            } else {
                locationAddress = null;
            }
        }
    }

    static String convertFirstLetterCaps(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public void loadingProgressBarVisible() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void loadingProgressBarGone() {
        progressBar.setVisibility(View.GONE);
    }

    public void widgetVisibilityGone() {
        sortSpinner.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);
        tvSort.setVisibility(View.GONE);
        Topic.setVisibility(View.GONE);
    }

    public void widgetVisibilityVisible() {
        sortSpinner.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.VISIBLE);
        tvSort.setVisibility(View.VISIBLE);
        Topic.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appLocationService.stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appLocationService.stopLocation();
    }
}