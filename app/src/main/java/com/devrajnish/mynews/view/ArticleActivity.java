package com.devrajnish.mynews.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.devrajnish.mynews.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ArticleActivity extends AppCompatActivity {

    private TextView title, time, description, source, link;
    private ImageView newsImage;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Intent intent = getIntent();
        HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_circular);
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        description = findViewById(R.id.description);
        source = findViewById(R.id.news_source);
        link = findViewById(R.id.link);
        newsImage = findViewById(R.id.imageView);

        String newsUrl = hashMap.get("Link");
        title.setText(hashMap.get("Title"));
        time.setText(getNiceDate(Objects.requireNonNull(hashMap.get("Time"))));
        description.setText(hashMap.get("Description"));
        source.setText(hashMap.get("NewsSource"));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        Glide.with(this)
                .load(hashMap.get("Image"))
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(newsImage);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                int colorInt = Color.parseColor("#0C54BE"); //color2
                builder.setToolbarColor(colorInt);
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(ArticleActivity.this, Uri.parse(newsUrl));
            }
        });
    }

    public String getNiceDate(String datetime) {
        String year = datetime.substring(0, 4);
        String month = datetime.substring(5, 7);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM", Locale.getDefault());
        int monthNum = Integer.parseInt(month);
        cal.set(Calendar.MONTH, monthNum);
        String monthName = month_date.format(cal.getTime());
        String date = datetime.substring(8, 10);
        String time = datetime.substring(13, 19);
        return year + " " + monthName + " " + date + " at " + time;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}