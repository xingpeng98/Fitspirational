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
import com.testapp.fitspirational.R;

import java.util.ArrayList;

public class DietAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LAYOUT_ONE = 1;
    private static final int LAYOUT_TWO = 2;

    private ArrayList<Article> articles;
    private Context context;
    private int layout1, l1_title, l1_body, l1_image;
    private int layout2, l2_title, l2_body;
    private Article l1_article;

    public DietAdapter(ArrayList<Article> articles, Context context, int[] layout1, int[] layout2, Article l1_article) {
        this.articles = articles;
        this.context = context;
        this.layout1 = layout1[0];
        this.l1_image = layout1[1];
        this.l1_title = layout1[2];
        this.l1_body = layout1[3];
        this.layout2 = layout2[0];
        this.l2_title = layout2[1];
        this.l2_body = layout2[2];
        this.l1_article = l1_article;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return LAYOUT_ONE;
        } else {
            return LAYOUT_TWO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =null;
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == LAYOUT_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(layout1, parent, false);
            viewHolder = new ViewHolder1(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(layout2, parent, false);
            viewHolder = new ViewHolder2(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == LAYOUT_ONE) {
            ViewHolder1 viewHolder1 = (ViewHolder1)holder;
            Picasso.get().load(l1_article.getImgUrl()).into(viewHolder1.imageView);
            viewHolder1.titleText.setText(l1_article.getTitle());
            viewHolder1.bodyText.setText(l1_article.getDescription());
        } else {
            Article article = articles.get(position - 1);
            ((ViewHolder2)holder).titleText.setText(article.getTitle());
            ((ViewHolder2)holder).bodyText.setText(article.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return articles.size() + 1;
    }

    //ViewHolder1
    public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleText;
        ImageView imageView;
        TextView bodyText;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(l1_image);
            titleText = itemView.findViewById(l1_title);
            bodyText = itemView.findViewById(l1_body);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String url = l1_article.getArticleUrl();

            context.startActivity(new Intent(context, WebView.class).putExtra("url", url));
        }
    }

    //ViewHolder2
    public class ViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleText;
        TextView bodyText;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(l2_title);
            bodyText = itemView.findViewById(l2_body);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition() - 1;
            String url = articles.get(position).getArticleUrl();

            context.startActivity(new Intent(context, WebView.class).putExtra("url", url));
        }
    }
}
