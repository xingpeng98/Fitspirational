package com.testapp.fitspirational.FeatureActivities.Fitness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.testapp.fitspirational.Executors.FitnessContentExecutorInflated;
import com.testapp.fitspirational.R;

public class GeneralFitness extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_fitness);

        RecyclerView recyclerView = findViewById(R.id.generalFitness_recyclerView);
        ProgressBar progressBar = findViewById(R.id.generalFitness_progressBar);

        FitnessContentExecutorInflated fitnessContentExecutor = new FitnessContentExecutorInflated(
                this, progressBar, recyclerView, R.layout.general_fitness_item,
                R.id.generalFitness_cardImage, R.id.generalFitness_cardTitle, R.id.generalFitness_cardBody);

        fitnessContentExecutor.run();
    }
}

