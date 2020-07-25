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

public class Content extends AsyncTask<Void, Void, Void> {

    private int type;
    private ProgressBar progressBar;
    private Context context;
    private RecyclerView.Adapter<?> adapter;
    private ArrayList<Article> articles;

    public Content(int type, ProgressBar progressBar, Context context, RecyclerView.Adapter<?> adapter, ArrayList<Article> articles) {
        this.type = type;
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
            String url = "https://www.bornfitness.com/blog/";
            Document doc = Jsoup.connect(url).get();

            Elements headerData = doc.select("header.entry-header");
            Elements descData = doc.select("div.entry-content.base-content");
            Elements urlData = doc.select("main.site-main.archive-posts").select("p.read-more");

            int size = headerData.size();

            for (int i = 0; i < size; i++) {
                String imgUrl = headerData.select("header.entry-header")
                        .select("img")
                        .eq(i)
                        .attr("src");

                String title = headerData.eq(i).select("h2").text();

                String articleUrl = urlData.select("a").eq(i).attr("href");

                String desc = descData.eq(i).select("p").text();

                if (desc == "") {
                    desc = "Empty";
                } else if (desc == null) {
                    desc = "Not found";
                }

                articles.add(new Article(imgUrl, title, articleUrl, desc));
            }
        } catch (IOException e) {
            Log.d("Parse Data", "IOException: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
