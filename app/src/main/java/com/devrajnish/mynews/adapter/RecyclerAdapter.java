package com.devrajnish.mynews.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.devrajnish.mynews.R;
import com.devrajnish.mynews.model.Article;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Article> list;
    private Context context;
    private ItemClickListener clickListener;

    public RecyclerAdapter(List<Article> list, Context context, ItemClickListener clickListener) {
        this.list = list;
        this.context = context;
        this.clickListener = clickListener;
    }

    public void setArticleList(List<Article> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.article_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapter.ViewHolder holder, int position) {
        Article article = list.get(position);
        holder.articleSource.setText(article.getSource().getName());
        holder.articleTitle.setText(article.getTitle());
        holder.articleTime.setText(getNiceDate(position));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        Glide.with(context)
                .load(article.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onArticleClick(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (this.list!=null){
            return this.list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView articleSource, articleTitle, articleTime;
        private ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            articleSource = itemView.findViewById(R.id.article_source);
            imageView = itemView.findViewById(R.id.article_image);
            articleTime = itemView.findViewById(R.id.article_time);
            articleTitle = itemView.findViewById(R.id.article_title);
            progressBar = itemView.findViewById(R.id.progress_circular);
        }
    }

    public String getNiceDate(int position){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        String dateStr = list.get(position).getPublishedAt();
        Date date = null;
        try {
            date = inputFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return  (String) DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);
    }

    public interface ItemClickListener {
        public void onArticleClick(Article article);
    }

    public void filterList(List<Article> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}

