package com.testapp.fitspirational.Executors;

import android.content.Context;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.testapp.fitspirational.Adapters.FitnessArticlesRecyclerViewAdapterInflated;
import com.testapp.fitspirational.MainActivity;
import com.testapp.fitspirational.Models.Article;
import com.testapp.fitspirational.ParseData.Content;

import java.util.ArrayList;

public class FitnessContentExecutorInflated {

    Context context;
    ProgressBar progressBar;
    ArrayList<Article> articles = new ArrayList<>();
    RecyclerView recyclerView;
    FitnessArticlesRecyclerViewAdapterInflated adapter;
    int cardLayout, cardImageId, cardTitleId, cardBodyId;

    public FitnessContentExecutorInflated(Context context, ProgressBar progressBar, RecyclerView recyclerView,
                                          int cardLayout, int cardImageId, int cardTitleId, int cardBodyId) {
        this.context = context;
        this.progressBar = progressBar;
        this.recyclerView = recyclerView;
        this.cardLayout = cardLayout;
        this.cardImageId = cardImageId;
        this.cardTitleId = cardTitleId;
        this.cardBodyId = cardBodyId;
    }

    public void run() {
        recyclerView.setHasFixedSize(true);
        if (context instanceof MainActivity) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        adapter = new FitnessArticlesRecyclerViewAdapterInflated(articles, context, cardLayout, cardImageId, cardTitleId, cardBodyId);
        recyclerView.setAdapter(adapter);

        Content content = new Content(1, progressBar, context, adapter, articles);
        content.execute();
    }
}
