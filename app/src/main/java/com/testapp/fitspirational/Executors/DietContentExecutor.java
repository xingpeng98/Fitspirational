package com.testapp.fitspirational.Executors;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.fitspirational.Adapters.DietAdapter;
import com.testapp.fitspirational.Models.Article;
import com.testapp.fitspirational.ParseData.Content;
import com.testapp.fitspirational.ParseData.DietContent;
import com.testapp.fitspirational.R;

import java.util.ArrayList;

public class DietContentExecutor {

    int dietType;
    Context context;
    ProgressBar progressBar;
    ArrayList<Article> articles = new ArrayList<>();
    RecyclerView recyclerView;
    DietAdapter adapter;
    private int[] layout1;
    private int[] layout2;

    public DietContentExecutor(int dietType, Context context, ProgressBar progressBar, RecyclerView recyclerView,
                               int[] layout1, int[] layout2) {
        this.dietType = dietType;
        this.context = context;
        this.progressBar = progressBar;
        this.recyclerView = recyclerView;
        this.layout1 = layout1;
        this.layout2 = layout2;
    }

    public void run() {
        String imgUrl = "", title = "", url = "", body = "";
        Article article;
        DietContent dietContent;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        switch (dietType) {
            case 0:
                imgUrl = context.getString(R.string.keto_imageURL);
                title = context.getString(R.string.keto_title);
                url = context.getString(R.string.keto_url);
                body = context.getString(R.string.keto_body);
                break;
            case 1:
                imgUrl = context.getString(R.string.paleo_imageURL);
                title = context.getString(R.string.paleo_title);
                url = context.getString(R.string.paleo_url);
                body = context.getString(R.string.paleo_body);
                break;
            case 2:
                imgUrl = context.getString(R.string.vegetarian_imageURL);
                title = context.getString(R.string.vegetarian_title);
                url = context.getString(R.string.vegetarian_url);
                body = context.getString(R.string.vegetarian_body);
                break;
            case 3:
                imgUrl = context.getString(R.string.mediterranean_imageURL);
                title = context.getString(R.string.mediterranean_title);
                url = context.getString(R.string.mediterranean_url);
                body = context.getString(R.string.mediterranean_body);
                break;
        }

        article = new Article(imgUrl, title, url, body);
        adapter = new DietAdapter(articles, context, layout1, layout2, article);
        dietContent = new DietContent(dietType, progressBar, context, adapter, articles);
        dietContent.execute();
        recyclerView.setAdapter(adapter);
    }
}
