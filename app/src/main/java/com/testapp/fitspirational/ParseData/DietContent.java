package com.testapp.fitspirational.ParseData;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.testapp.fitspirational.Models.Article;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class DietContent extends AsyncTask<Void, Void, Void> {

    private static final String[] DIETS_URL = new String[]{
            "https://www.livestrong.com/search/?search=keto",
            "https://www.livestrong.com/search/?search=paleo",
            "https://www.livestrong.com/search/?search=vegetarian",
            "https://www.livestrong.com/search/?search=mediterranean"
    };

    private int dietType;
    private ProgressBar progressBar;
    private Context context;
    private RecyclerView.Adapter<?> adapter;
    private ArrayList<Article> articles;

    public DietContent(int dietType, ProgressBar progressBar, Context context, RecyclerView.Adapter<?> adapter, ArrayList<Article> articles) {
        this.dietType = dietType;
        this.progressBar = progressBar;
        this.context = context;
        this.adapter = adapter;
        this.articles = articles;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        progressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBar.setVisibility(View.GONE);
        progressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {
            String url = DIETS_URL[dietType];
            Document doc = Jsoup.connect(url).get();

            Elements titleData = doc.select("div.title");
            Elements descData = doc.select("div.summary-container");
            Elements urlData = doc.select("div.url-link");

            int size = titleData.size();

            for (int i = 0; i < size; i++) {

                String title = titleData.eq(i).text();
                String articleUrl = "https://" + urlData.eq(i).text().trim();
                String desc = descData.eq(i).text();

                articles.add(new Article("", title, articleUrl, desc));
                Log.d("Diet", "URL: " + articleUrl);
            }
        } catch (IOException e) {
            Log.d("DietContent", "IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
