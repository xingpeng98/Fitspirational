package com.testapp.fitspirational.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.testapp.fitspirational.Models.Article;
import com.testapp.fitspirational.FeatureActivities.Fitness.WebView;

import java.util.ArrayList;

public class FitnessArticlesRecyclerViewAdapter extends RecyclerView.Adapter<FitnessArticlesRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Article> articles;
    private Context context;
    private int layout, img, text;

    public FitnessArticlesRecyclerViewAdapter(ArrayList<Article> articles, Context context, int layout, int img, int text) {
        this.articles = articles;
        this.context = context;
        this.layout = layout;
        this.img = img;
        this.text = text;
    }

    @NonNull
    @Override
    public FitnessArticlesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessArticlesRecyclerViewAdapter.ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.textView.setText(article.getTitle());
        Picasso.get().load(article.getImgUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    //ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(img);
            textView = itemView.findViewById(text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String url = articles.get(position).getArticleUrl();

            context.startActivity(new Intent(context, WebView.class).putExtra("url", url));
        }
    }
}
