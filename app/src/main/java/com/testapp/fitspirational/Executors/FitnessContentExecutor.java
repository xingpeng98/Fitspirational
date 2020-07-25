package com.testapp.fitspirational.Executors;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.fitspirational.Adapters.FitnessArticlesRecyclerViewAdapter;
import com.testapp.fitspirational.MainActivity;
import com.testapp.fitspirational.Models.Article;
import com.testapp.fitspirational.ParseData.Content;

import java.util.ArrayList;

public class FitnessContentExecutor {

    Context context;
    ProgressBar progressBar;
    ArrayList<Article> articles = new ArrayList<>();
    RecyclerView recyclerView;
    FitnessArticlesRecyclerViewAdapter adapter;
    int cardLayout, cardImageId, cardTextId;

    public FitnessContentExecutor(Context context, ProgressBar progressBar, RecyclerView recyclerView,
                                  int cardLayout, int cardImageId, int cardTextId) {
        this.context = context;
        this.progressBar = progressBar;
        this.recyclerView = recyclerView;
        this.cardLayout = cardLayout;
        this.cardImageId = cardImageId;
        this.cardTextId = cardTextId;
    }

    public void run() {
        recyclerView.setHasFixedSize(true);
        if (context instanceof MainActivity) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        adapter = new FitnessArticlesRecyclerViewAdapter(articles, context, cardLayout, cardImageId, cardTextId);
        recyclerView.setAdapter(adapter);

        Content content = new Content(0, progressBar, context, adapter, articles);
        content.execute();
    }

}
